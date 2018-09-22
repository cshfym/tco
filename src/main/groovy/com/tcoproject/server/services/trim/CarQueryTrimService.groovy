package com.tcoproject.server.services.trim

import com.google.gson.reflect.TypeToken
import com.tcoproject.server.converters.TrimConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.CarQueryTrimResponse
import com.tcoproject.server.models.external.TrimFetchAndPersistRequest
import com.tcoproject.server.repository.model.ModelRepositoryService
import com.tcoproject.server.repository.trim.TrimRepositoryService
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.repository.make.MakeRepositoryService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import java.lang.reflect.Type

@Slf4j
@Service
class CarQueryTrimService extends AbstractExternalApiService {

    @Autowired
    MakeRepositoryService makeService

    @Autowired
    ModelRepositoryService modelRepositoryService

    @Autowired
    TrimRepositoryService trimRepositoryService

    @Value('${carqueryapi.v3.base.url}')
    String CARQUERYAPI_V3_BASE_URL

    @Value('${carqueryapi.trims.path}')
    String CARQUERYAPI_TRIMS_PATH

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"

    final static String FULL_RESULTS = "&full_results=1" // 0 = Partial Results, 1 = Full Results

    // Ex: https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&full_results=1&year=2018&make=Acura

    void doTrimFetchAndPersist(TrimFetchAndPersistRequest request) {

        if (request.endWithYear < 1960) { request.endWithYear = 1960 }

        PersistableMake make = makeService.getMakeByName(request.make)

        if (!make) {
            def message = "Could not find make with TrimFetchAndPersistRequest - [${request.make}]"
            log.error message
            throw new IllegalArgumentException(message)
        }

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            doFetchAndPersistForModelAndYear(make, year)
        }
    }

    void doFetchAndPersistForModelAndYear(PersistableMake make, int year) {

        def startStopwatch = System.currentTimeMillis()

        String encodedMakeName = URLEncoder.encode(make.name, "UTF-8")
        String makeAndYearUri = CARQUERYAPI_V3_BASE_URL + CARQUERYAPI_TRIMS_PATH + FULL_RESULTS + "&make=${encodedMakeName}&year=${year}"

        // Override X-Forwarded-For header to randomize IP address
        requestHeaders.put(X_FORWARDED_FOR_HEADER as String, randomizedIpAddress as String)

        // Call API
        String trimResponseJson = connectionService.getData(makeAndYearUri, RequestMethod.GET, null, requestHeaders)

        // Strip out garbage - sanitize
        trimResponseJson = trimResponseJson?.replace("?({\"Trims\":", "")?.replace("});", "")

        if (trimResponseJson) {
            log.info "Fetched [${trimResponseJson.size()}] bytes at [${makeAndYearUri}] in [${System.currentTimeMillis() - startStopwatch}] ms."
        } else {
            log.info "No data response from query at [${makeAndYearUri}] - potentially no data available for arguments."
            return
        }

        // Convert json to collection of models
        List<CarQueryTrimResponse> trims = parseTrimResponseJson(trimResponseJson)

        // Save all
        trims.each { CarQueryTrimResponse trimResponse ->
            PersistableModel model = modelRepositoryService.findByMakeAndNameAndYear(make, trimResponse.modelName, year)
            if (!model) {
                log.error "Could not find model by make [${make.name}], trimResponse.modelName [${trimResponse.modelName}], and year [${year}]!"
                return // Next response
            }
            try {
                trimRepositoryService.persistTrim(TrimConverter.toPersistable(trimResponse, model))
            } catch (Exception ex) {
                log.error "Exception thrown while persisting trim", ex
            }
        }

        log.info "Fetched and persisted [${trims.size()}] trims for [${make.name}], year [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(1000)
    }

    List<CarQueryTrimResponse> parseTrimResponseJson(String response) {

        try {
            Type collectionType = new TypeToken<Collection<CarQueryTrimResponse>>(){}.type
            return gson.fromJson(response, collectionType)
        } catch (Exception ex) {
            log.error "Exception", ex
            log.error "Payload in error: [${response}]"
            throw ex
        }
    }

}
