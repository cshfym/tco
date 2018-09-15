package com.tcoproject.server.services.priceData

import com.google.gson.reflect.TypeToken
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.CarQueryModelResponse
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.models.external.kbb.KBBInfoWrapper
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.repository.TrimRepository
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.services.common.HTTPConnectionService
import com.tcoproject.server.services.make.MakeService
import com.tcoproject.server.services.model.ModelService
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import java.lang.reflect.Type

@Slf4j
@Service
class PriceDataService extends AbstractExternalApiService {

    @Autowired
    MakeService makeService

    @Autowired
    ModelService modelService

    @Value('${kbb.base.url}')
    String kbbBaseUrl

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"

    final String BEGIN_PRICE_DATA_MODEL_DELIMETER = "var data = \""
    final String END_PRICE_DATA_MODEL_DELIMETER = "\""

    void doPriceDataFetchAndPersist(PriceDataFetchAndPersistRequest request) {

        log.info "PriceDataFetchAndPersistRequest [${request}]"

        if (request.endWithYear < 1960) { request.endWithYear = 1960 }

        PersistableMake make = makeService.getMakeByName(request.make)

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            doFetchAndPersistForModelAndYear(make, year, request.baseModelOnly)
        }
    }

    void doFetchAndPersistForModelAndYear(PersistableMake make, int year, boolean baseModelOnly) {

        if (!baseModelOnly) {
            // TODO Implement
            return
        }

        def startStopwatch = System.currentTimeMillis()

        /*
           Psuedo:
             1. Fetch all models for make
             2. If fetching base model only from request, construct base model URL
             3. Issue GET request at URL
             4. Snag block beginning with "var data =[URLEncoded Data]"
             5. URL decode block of data
             6. Parse decoded block into object from JSON
             7. Convert object into PersistablePriceData
             7. Persist price data
         */

        List<PersistableModel> modelsForMakeAndYear = modelService.findAllByMakeAndYear(make, year)

        modelsForMakeAndYear.each { PersistableModel model ->

            String requestUri = buildRequestUriForModel(make.name, model.name, year)

            // Override X-Forwarded-For header to randomize IP address
            requestHeaders.put(X_FORWARDED_FOR_HEADER as String, randomizedIpAddress as String)

            // Call API
            String response = connectionService.getData(requestUri, RequestMethod.GET, null, requestHeaders)

            if (!response.contains(BEGIN_PRICE_DATA_MODEL_DELIMETER)) {
                log.warn "Response from [${requestUri}] does not contain marker [var data = \"] to extract price data; bypassing!"
                return
            }

            int beginIdx = response.indexOf(BEGIN_PRICE_DATA_MODEL_DELIMETER) + BEGIN_PRICE_DATA_MODEL_DELIMETER.length()

            // Trim off everything in front of index i
            String trimmed = response.substring(beginIdx)

            int endIdx = trimmed.indexOf(END_PRICE_DATA_MODEL_DELIMETER)

            String urlEncodedData = trimmed.substring(0, endIdx)

            String decodedJsonString = URLDecoder.decode(urlEncodedData, "UTF-8")

            //KBBMakeModelYearResponse infoWrapper = new JsonSlurper().parseText(decodedJsonString) as KBBMakeModelYearResponse

            Type type = new TypeToken<KBBMakeModelYearResponse>(){}.type
            KBBMakeModelYearResponse wrapper = kbbGson.fromJson(decodedJsonString, type)


            log.info "Response from [${requestUri}]: ${decodedJsonString}"
        }


        // log.info "Fetched and persisted [${trims.size()}] trims for [${make.name}], year [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(1000)
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
}
