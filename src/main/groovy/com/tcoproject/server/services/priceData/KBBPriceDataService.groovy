package com.tcoproject.server.services.priceData

import com.tcoproject.server.converters.PriceDataConverter
import com.tcoproject.server.models.constants.TCOConstants
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.repository.priceData.PriceDataRepositoryService
import com.tcoproject.server.repository.model.ModelRepositoryService
import com.tcoproject.server.repository.priceDataOrphan.PriceDataOrphanRepositoryService
import com.tcoproject.server.repository.trim.TrimRepositoryService
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.repository.make.MakeRepositoryService
import com.tcoproject.server.services.model.ModelUriResolutionFactory
import com.tcoproject.server.services.trim.TrimUriResolutionFactory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

@Slf4j
@Service
class KBBPriceDataService extends AbstractExternalApiService {

    @Autowired
    PriceDataRepositoryService priceDataRepositoryService

    @Autowired
    PriceDataOrphanRepositoryService priceDataOrphanRepositoryService

    @Autowired
    MakeRepositoryService makeService

    @Autowired
    ModelRepositoryService modelRepositoryService

    @Autowired
    TrimRepositoryService trimRepositoryService

    @Autowired
    TrimUriResolutionFactory trimUriResolutionFactory

    @Autowired
    ModelUriResolutionFactory modelUriResolutionFactory

    @Value('${kbb.base.url}')
    String kbbBaseUrl

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"

    final String BEGIN_PRICE_DATA_MODEL_DELIMETER = "var data = \""
    final String END_PRICE_DATA_MODEL_DELIMETER = "\""
    final String MAKE_MODEL_NOT_FOUND_RESPONSE = "Sorry, we couldn't find that page."

    void doPriceDataFetchAndPersist(PriceDataFetchAndPersistRequest request) {

        log.info "PriceDataFetchAndPersistRequest [${request}]"

        def startStopwatch = System.currentTimeMillis()

        int updateCount = 0

        if (request.endWithYear < 1960) { request.endWithYear = 1960 }

        PersistableMake make = makeService.getMakeByName(request.make)

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            updateCount += doFetchAndPersistForMakeAndModelAndYear(make, request.model, year, request.baseModelOnly)
        }

        log.info "Persisted [${updateCount}] for make [${make.name}] in [${System.currentTimeMillis() - startStopwatch}] ms"
    }

    int doFetchAndPersistForMakeAndModelAndYear(PersistableMake persistableMake, String filterByModel, int year, boolean baseModelOnly) {

        int updateCount = 0

        List<PersistableModel> modelsForMakeAndYear = modelRepositoryService.findAllByMakeAndYear(persistableMake, year)

        // Filter models if specified in argument
        if (filterByModel) {
            modelsForMakeAndYear = modelsForMakeAndYear.findAll { it.name == filterByModel }
        }

        // Iterate models
        modelsForMakeAndYear.each { PersistableModel persistableModel ->

            if (baseModelOnly) {
                boolean persisted = doProcessWithModelAndTrimAndYear(persistableMake, persistableModel, null, year)
                if (persisted) { updateCount++ }
            } else {
                List<PersistableTrim> trimList = trimRepositoryService.findAllTrimsForModel(persistableModel)
                trimList.each { PersistableTrim persistableTrim ->
                    boolean persisted = doProcessWithModelAndTrimAndYear(persistableMake, persistableModel, persistableTrim, year)
                    if (persisted) { updateCount++ }
                }
            }
        }

        updateCount
    }

    boolean doProcessWithModelAndTrimAndYear(PersistableMake persistableMake, PersistableModel persistableModel, PersistableTrim persistableTrim, int year) {

        // Check for previous
        Date runDate = new Date().clearTime()
        if (priceDataRepositoryService.priceDataExists(persistableModel, persistableTrim, runDate, "KBB")) {
            log.info "Price data already exists for model [${persistableModel.name}], [${year}], trim [${persistableTrim}], " +
                "and date [${runDate}], bypassing!"
            return
        }

        KBBMakeModelYearResponse kbbMakeModelYearResponse

        String requestUri = buildRequestUriForModelAndTrim(persistableMake.name, sanitizeModelName(persistableModel.name), year, persistableTrim?.name)

        try {
            kbbMakeModelYearResponse = fetchAndParsePriceData(requestUri)
        } catch (Exception ex) {
            log.error "Exception thrown in fetchAndParsePriceData", ex
            return // Next model
        }

        if (!kbbMakeModelYearResponse) {
            priceDataOrphanRepositoryService.persistPriceDataOrphan(persistableModel, requestUri)
            return // Couldn't fetch and parse for non-exception reason (404, etc.), next model
        }

        if (!kbbMakeModelYearResponse.info?.vehicle) {
            log.warn "Could not extract info.vehicle response JSON from fetch at [${requestUri}]; bypassing!"
            return
        }

        // Reconcile year returned by kbbMakeModelYearResponse.info.yearid against year specified
        if (kbbMakeModelYearResponse.info.yearid != year) {
            log.warn "Year returned in response [${kbbMakeModelYearResponse.info.yearid}] does not match year argument [${year}]; bypassing!"
            return
        }

        // Prevent saving a "base-style" option when a trim is specified
        if (persistableTrim && requestUri.contains(TCOConstants.BASE_STYLE_OPTION)) {
            log.warn "Cannot persist response when trim specified and base-model returned, trim: [${persistableTrim.name}]"
            return
        }

        priceDataRepositoryService.persistPriceData(
                PriceDataConverter.toPersistableFromKBBMakeModelYear(kbbMakeModelYearResponse, persistableModel, persistableTrim),
                "KBB")
    }

    KBBMakeModelYearResponse fetchAndParsePriceData(String requestUri) {

        // Override X-Forwarded-For header to randomize IP address
        requestHeaders.put(X_FORWARDED_FOR_HEADER as String, randomizedIpAddress as String)

        // Call API
        String response = connectionService.getData(requestUri, RequestMethod.GET, null, requestHeaders)

        if (!response || response.contains(MAKE_MODEL_NOT_FOUND_RESPONSE)) {
            log.warn "Response from [${requestUri}] - Sorry, we couldn't find that page; bypassing!"
            return null
        }

        if (!response.contains(BEGIN_PRICE_DATA_MODEL_DELIMETER)) {
            log.warn "Response from [${requestUri}] does not contain marker [var data = \"] to extract price data; bypassing!"
            return null
        }

        int beginIdx = response.indexOf(BEGIN_PRICE_DATA_MODEL_DELIMETER) + BEGIN_PRICE_DATA_MODEL_DELIMETER.length()

        // Trim off everything in front of index i
        String trimmed = response.substring(beginIdx)

        int endIdx = trimmed.indexOf(END_PRICE_DATA_MODEL_DELIMETER)

        String decodedJsonString = URLDecoder.decode(trimmed.substring(0, endIdx), "UTF-8")

        try {
            kbbGson.fromJson(decodedJsonString, KBBMakeModelYearResponse)
        } catch (Exception ex) {
            log.error "Exception during JSON parse at [${requestUri}]", ex
        }

    }

    /**
     * Builds out the full request URI for the given model, trim, and year.
     * @param makeName
     * @param modelName
     * @param year
     * @param persistableTrim
     * @return URI
     * Format: https://www.kbb.com/<make_name>/<model_name>/<year>/style?options
     */
    String buildRequestUriForModelAndTrim(String makeName, String modelName, int year, String trimName) {

        String derivedModelName = determineModelArgument(makeName, modelName, trimName)
        String derivedTrimName = determineTrimArgument(makeName, modelName, trimName)

        new StringBuffer()
                .append(kbbBaseUrl)
                .append(makeName.toLowerCase()).append("/")
                .append(derivedModelName.toLowerCase()).append("/")
                .append(year).append("/")
                .append(derivedTrimName)
                .append("?")
                .toString()
    }

    private String determineTrimArgument(String makeName, String modelName, String trimName) {
        trimUriResolutionFactory.targetUriSegment(makeName, modelName, trimName)
    }

    private String determineModelArgument(String makeName, String modelName, String trimName) {
        modelUriResolutionFactory.targetUriSegment(makeName, modelName, trimName)
    }

    private static String sanitizeModelName(String name) {
        // Replace space with dash
        name.trim().replace(" ","-")
    }
}
