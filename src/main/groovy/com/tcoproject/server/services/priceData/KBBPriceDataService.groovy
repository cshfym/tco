package com.tcoproject.server.services.priceData

import com.tcoproject.server.converters.PriceDataConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistablePriceDataOrphan
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.repository.PriceDataOrphanRepository
import com.tcoproject.server.repository.PriceDataRepository
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.services.make.MakeService
import com.tcoproject.server.services.model.ModelService
import com.tcoproject.server.services.trim.TrimService
import com.tcoproject.server.services.trim.TrimUriResolutionFactory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional

@Slf4j
@Service
class KBBPriceDataService extends AbstractExternalApiService {

    @Autowired
    PriceDataRepository priceDataRepository

    @Autowired
    PriceDataOrphanRepository priceDataOrphanRepository

    @Autowired
    MakeService makeService

    @Autowired
    ModelService modelService

    @Autowired
    TrimService trimService

    @Autowired
    TrimUriResolutionFactory trimUriResolutionFactory

    @Value('${kbb.base.url}')
    String kbbBaseUrl

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"

    final String BEGIN_PRICE_DATA_MODEL_DELIMETER = "var data = \""
    final String END_PRICE_DATA_MODEL_DELIMETER = "\""
    final String MAKE_MODEL_NOT_FOUND_RESPONSE = "Sorry, we couldn't find that page."
    final String BASE_STYLE_OPTION = "base-style"

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

        List<PersistableModel> modelsForMakeAndYear = modelService.findAllByMakeAndYear(persistableMake, year)

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
                List<PersistableTrim> trimList = trimService.findAllTrimsForModel(persistableModel)
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
        if (priceDataExists(persistableModel, persistableTrim, runDate)) {
            log.info "Price data already exists for model [${persistableModel.name}], [${year}], trim [${persistableTrim}], " +
                "and date [${runDate}], bypassing!"
            return
        }

        KBBMakeModelYearResponse kbbMakeModelYearResponse

        String requestUri = buildRequestUriForModelAndTrim(persistableMake.name, sanitizeModelName(persistableModel.name), year, persistableTrim)

        try {
            kbbMakeModelYearResponse = fetchAndParsePriceData(requestUri)
        } catch (Exception ex) {
            log.error "Exception thrown in fetchAndParsePriceData", ex
            return // Next model
        }

        if (!kbbMakeModelYearResponse) {
            persistPriceDataOrphan(persistableModel, requestUri)
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

        persistPriceData(kbbMakeModelYearResponse, persistableModel, persistableTrim)
    }

    @Transactional
    boolean persistPriceData(KBBMakeModelYearResponse kbbMakeModelYearResponse, PersistableModel persistableModel, PersistableTrim persistableTrim) {

        // Check for existing data (no time)
        Date today = new Date().clearTime()
        PersistablePriceData existingPriceData =
                priceDataRepository.findByModelAndTrimAndSourceAndDateCreated(persistableModel, persistableTrim, "KBB", today)
        if (existingPriceData) {
            log.info "Existing price data found for model [${persistableModel?.name}], trim [${persistableTrim?.name}], on date [${today}]; bypassing persistence."
            return false
        }

        try {
            PersistablePriceData priceData = PriceDataConverter.toPersistableFromKBBMakeModelYear(kbbMakeModelYearResponse, persistableModel, persistableTrim)
            def persisted = priceDataRepository.save(priceData)
            log.info "Persisted price data [${persisted}]"
        } catch (Exception ex) {
            log.error "Exception thrown while converting and persisting persistablePriceData", ex
            return false
        }

        true
    }

    @Transactional
    void persistPriceDataOrphan(PersistableModel persistableModel, String orphanUri) {

        if (priceDataOrphanRepository.findByModelAndUri(persistableModel, orphanUri)) {
            log.info "Existing orphan record with model [${persistableModel.name}] and uri [${orphanUri}], not persisting."
            return
        }

        PersistablePriceDataOrphan orphan = new PersistablePriceDataOrphan(model: persistableModel,uri: orphanUri)
        log.info "Persisting orphan record with model [${persistableModel.name}] and uri [${orphanUri}]"
        priceDataOrphanRepository.save(orphan)
    }

    boolean priceDataExists(PersistableModel model, PersistableTrim trim, Date date) {
        priceDataRepository.findByModelAndTrimAndSourceAndDateCreated(model, trim, "KBB", date)
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

        kbbGson.fromJson(decodedJsonString, KBBMakeModelYearResponse)
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
    String buildRequestUriForModelAndTrim(String makeName, String modelName, int year, PersistableTrim persistableTrim) {

        String style = persistableTrim ? determineTrimArgument(persistableTrim) : BASE_STYLE_OPTION

        new StringBuffer()
                .append(kbbBaseUrl)
                .append(makeName.toLowerCase()).append("/")
                .append(modelName.toLowerCase()).append("/")
                .append(year).append("/")
                .append(style)
                .append("?")
                .toString()
    }

    private String determineTrimArgument(PersistableModel persistableModel, PersistableTrim persistableTrim) {
        trimUriResolutionFactory.targetUriSegment(persistableModel.name, persistableTrim.name)
    }

    private static String sanitizeModelName(String name) {

        // Replace space with dash
        name.trim().replace(" ","-")
    }
}
