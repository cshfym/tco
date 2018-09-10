package com.tcoproject.server.services.trim

import com.google.gson.reflect.TypeToken
import com.tcoproject.server.converters.TrimConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.CarQueryTrimResponse
import com.tcoproject.server.models.external.TrimFetchAndPersistRequest
import com.tcoproject.server.services.common.CarQueryApiService
import com.tcoproject.server.services.make.MakeService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

import java.lang.reflect.Type

@Slf4j
@Service
class TrimService extends CarQueryApiService {

    @Autowired
    MakeService makeService

    @Value('${carqueryapi.trims.path}')
    String CARQUERYAPI_TRIMS_PATH

    final static String FULL_RESULTS = "&full_results=1" // 0 = Partial Results, 1 = Full Results

    // Ex: https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&full_results=1&year=2018&make=Acura

    void doTrimFetchAndPersist(TrimFetchAndPersistRequest request) {

        if (request.endWithYear < 1950) { request.endWithYear = 1950 }

        PersistableMake make = makeService.getMakeByName(request.make)

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
        try {
            persistTrims(trims.collect { TrimConverter.toPersistable(it, year) })
        } catch (Exception ex) {
            log.error "Exception thrown while persisting model collection", ex
        }

        log.info "Fetched and persisted [${trims.size()}] trims for [${make.name}] Models, [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

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

    // TODO: Transactional per model, not the entire batch.
    @Transactional
    void persistTrims(List<PersistableTrim> persistableTrimList) {

        persistableTrimList.each { PersistableTrim model ->
/*
            PersistableTrim existingTrim = trimRepository.findByMakeAndNameAndYear(model.make, model.name, model.year)

            if (!existingTrim) {
                log.info "Persisting model with make [${model.make.name}], model [${model.name}], and year [${model.year}]"
                modelRepository.save(model)
            } else {
                log.info "Bypassing persistence for make [${model.make.name}], model [${model.name}], year [${model.year}] - already exists."
            }
        }
*/
        }
    }

}
