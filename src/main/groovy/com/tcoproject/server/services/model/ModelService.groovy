package com.tcoproject.server.services.model

import com.google.gson.reflect.TypeToken
import com.tcoproject.server.converters.ModelConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.CarQueryModelResponse
import com.tcoproject.server.models.external.ModelFetchAndPersistRequest
import com.tcoproject.server.repository.ModelRepository
import com.tcoproject.server.services.common.AbstractExternalApiService
import com.tcoproject.server.services.make.MakeService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional
import java.lang.reflect.Type

@Slf4j
@Service
class ModelService extends AbstractExternalApiService {

    @Autowired
    MakeService makeService

    @Autowired
    ModelRepository modelRepository

    @Value('${carqueryapi.v3.base.url}')
    String CARQUERYAPI_V3_BASE_URL

    @Value('${carqueryapi.models.path}')
    String CARQUERYAPI_MODELS_PATH

    final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For"


    void doModelFetchAndPersist(ModelFetchAndPersistRequest request) {

        if (request.endWithYear < 1950) { request.endWithYear = 1950 }

        PersistableMake make = makeService.getMakeByName(request.make)

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            doFetchAndPersistForMakeAndYear(make, year)
        }
    }

    /*
    void fetchAndPersistModelsAllCommonMakesAllYears(Integer startWithYear, Integer stopWithYear) {
        List<PersistableMake> commonMakes = makeService.getAllCommonMakes()

        // Iterating all years and common makes
        (AvailableYears.MAXIMUM_YEAR..AvailableYears.MINIMUM_YEAR).each { int year ->

            if (year > startWithYear || year < stopWithYear) { return }

            commonMakes.each { PersistableMake make ->
                doFetchAndPersistForMakeAndYear(make, year)
            }
        }
    }
    */

    void doFetchAndPersistForMakeAndYear(PersistableMake make, int year) {

        def startStopwatch = System.currentTimeMillis()

        String encodedMakeName = URLEncoder.encode(make.name, "UTF-8")
        String makeAndYearUri = CARQUERYAPI_V3_BASE_URL + CARQUERYAPI_MODELS_PATH + "&make=${encodedMakeName}&year=${year}"

        // Override X-Forwarded-For header to randomize IP address
        requestHeaders.put(X_FORWARDED_FOR_HEADER as String, randomizedIpAddress as String)

        // Call API
        String modelResponseJson = connectionService.getData(makeAndYearUri, RequestMethod.GET, null, requestHeaders)

        // Strip out garbage - sanitize
        modelResponseJson = modelResponseJson?.replace("?({\"Models\":", "")?.replace("});", "")

        if (modelResponseJson) {
            log.info "Fetched [${modelResponseJson.size()}] bytes at [${makeAndYearUri}] in [${System.currentTimeMillis() - startStopwatch}] ms."
        } else {
            log.info "No data response from query at [${makeAndYearUri}] - potentially no data available for arguments."
            return
        }

        // Convert json to collection of models
        List<CarQueryModelResponse> models = parseModelResponseJson(modelResponseJson)

        // Save all
        try {
            persistModels(models.collect { ModelConverter.toPersistable(it, make, year) })
        } catch (Exception ex) {
            log.error "Exception thrown while persisting model collection", ex
        }

        log.info "Fetched and persisted [${models.size()}] models for [${make.name}], [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(1000)
    }

    // TODO: Transactional per model, not the entire batch.
    @Transactional
    void persistModels(List<PersistableModel> persistableModelList) {

        persistableModelList.each { PersistableModel model ->

            PersistableModel existingModel = modelRepository.findByMakeAndNameAndYear(model.make, model.name, model.year)

            if (!existingModel) {
                log.info "Persisting model with make [${model.make.name}], model [${model.name}], and year [${model.year}]"
                modelRepository.save(model)
            } else {
                log.info "Bypassing persistence for make [${model.make.name}], model [${model.name}], year [${model.year}] - already exists."
            }
        }

    }

    List<CarQueryModelResponse> parseModelResponseJson(String response) {

        try {
            Type collectionType = new TypeToken<Collection<CarQueryModelResponse>>(){}.type
            return gson.fromJson(response, collectionType)
        } catch (Exception ex) {
            log.error "Exception", ex
            log.error "Payload in error: [${response}]"
            throw ex
        }
    }

    List<PersistableModel> findAllByMakeAndYear(PersistableMake make, int year) {
        modelRepository.findAllByMakeAndYear(make, year)
    }

    PersistableModel findByMakeAndNameAndYear(PersistableMake make, String name, int year) {
        modelRepository.findByMakeAndNameAndYear(make, name, year)
    }
}
