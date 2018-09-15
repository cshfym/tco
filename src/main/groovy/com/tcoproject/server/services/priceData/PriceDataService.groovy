package com.tcoproject.server.services.priceData

import com.tcoproject.server.converters.PriceDataConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.repository.PriceDataRepository
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.services.make.MakeService
import com.tcoproject.server.services.model.ModelService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional

@Slf4j
@Service
class PriceDataService extends AbstractExternalApiService {

    @Autowired
    PriceDataRepository priceDataRepository

    @Autowired
    MakeService makeService

    @Autowired
    ModelService modelService

    @Value('${kbb.base.url}')
    String kbbBaseUrl

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"

    final String BEGIN_PRICE_DATA_MODEL_DELIMETER = "var data = \""
    final String END_PRICE_DATA_MODEL_DELIMETER = "\""
    final String MAKE_MODEL_NOT_FOUND_RESPONSE = "Sorry, we couldn't find that page."

    void doPriceDataFetchAndPersist(PriceDataFetchAndPersistRequest request) {

        log.info "PriceDataFetchAndPersistRequest [${request}]"

        if (request.endWithYear < 1960) { request.endWithYear = 1960 }

        PersistableMake make = makeService.getMakeByName(request.make)

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            doFetchAndPersistForMakeAndModelAndYear(make, request.model, year, request.baseModelOnly)
        }
    }

    void doFetchAndPersistForMakeAndModelAndYear(PersistableMake make, String filterByModel, int year, boolean baseModelOnly) {

        if (!baseModelOnly) {
            // TODO Implement
            return
        }

        def startStopwatch = System.currentTimeMillis()

        int priceDataPersistCount = 0

        List<PersistableModel> modelsForMakeAndYear = modelService.findAllByMakeAndYear(make, year)

        // Filter models if specified in argument
        if (filterByModel) {
            modelsForMakeAndYear = modelsForMakeAndYear.findAll { it.name == filterByModel }
        }

        modelsForMakeAndYear.each { PersistableModel persistableModel ->

            KBBMakeModelYearResponse kbbMakeModelYearResponse

            String requestUri = buildRequestUriForModel(make.name, sanitizeModelName(persistableModel.name), year)

            try {
                kbbMakeModelYearResponse = fetchAndParsePriceData(requestUri)
            } catch (Exception ex) {
                log.error "Exception thrown in fetchAndParsePriceData", ex
                return // Next model
            }

            if (!kbbMakeModelYearResponse) {
                return // Couldn't fetch and parse for non-exception reason, next model
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

            if (persistPriceData(kbbMakeModelYearResponse, baseModelOnly, persistableModel, null)) {
                priceDataPersistCount++
            }

        }

        log.info "Fetched [${modelsForMakeAndYear.size()}] and persisted [${priceDataPersistCount}] price data for [${make.name}], " +
                "model [${filterByModel}], and year [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(1000)
    }

    @Transactional
    boolean persistPriceData(KBBMakeModelYearResponse kbbMakeModelYearResponse, boolean baseModelOnly, PersistableModel persistableModel, PersistableTrim persistableTrim) {

        // Check for existing data (no time)
        Date today = new Date().clearTime()
        PersistablePriceData existingPriceData = priceDataRepository.findByModelAndTrimAndSourceAndDateCreated(persistableModel, persistableTrim, "KBB", today)
        if (existingPriceData) {
            log.info "Existing price data found for model [${persistableModel?.name}], trim [${persistableTrim?.name}], on date [${today}]; bypassing persistence."
            return false
        }

        try {
            PersistablePriceData priceData = PriceDataConverter.toPersistableFromKBBMakeModelYear(kbbMakeModelYearResponse, baseModelOnly, persistableModel, persistableTrim)
            def persisted = priceDataRepository.save(priceData)
            log.info "Persisted price data [${persisted}]"
        } catch (Exception ex) {
            log.error "Exception thrown while converting and persisting persistablePriceData", ex
            return false
        }

        true
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

    // Format: https://www.kbb.com/<make_name>/<model_name>/<year>/base-style
    String buildRequestUriForModel(String makeName, String modelName, int year) {

        new StringBuffer()
                .append(kbbBaseUrl)
                .append(makeName.toLowerCase()).append("/")
                .append(modelName.toLowerCase()).append("/")
                .append(year).append("/")
                .append("base-style")
                .toString()
    }

    private static String sanitizeModelName(String name) {

        // Replace space with dash
        name.trim().replace(" ","-")

    }
}
