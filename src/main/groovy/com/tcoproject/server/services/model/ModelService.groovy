package com.tcoproject.server.services.model

import com.google.common.base.Utf8
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tcoproject.server.config.AvailableYears
import com.tcoproject.server.converters.ModelConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.CarQueryModelResponse
import com.tcoproject.server.repository.ModelRepository
import com.tcoproject.server.services.common.HTTPConnectionService
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
class ModelService {

    @Autowired
    MakeService makeService

    @Autowired
    ModelRepository modelRepository

    @Value('${carqueryapi.v3.base.url}')
    String CARQUERYAPI_V3_BASE_URL
    String CARQUERYAPI_MODELS_PATH = "&cmd=getModels"

    static final String CARQUERY_ACCEPT_HEADER = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
    static final String CARQUERY_CACHE_CONTROL_HEADER = "max-age=0"
    static final String CARQUERY_USER_AGENT_HEADER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"

    // Override request headers for carqueryapi
    static final Map<String,String> requestHeaders = [
            "Accept": CARQUERY_ACCEPT_HEADER,
            "cache-control": CARQUERY_CACHE_CONTROL_HEADER,
            "user-agent": CARQUERY_USER_AGENT_HEADER
    ]


    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient()
            .create()

    @Autowired
    HTTPConnectionService connectionService

    /**
     * Fetch all models available from the Car Query API and persist.
     */
    void fetchAndPersistModelsAllMakesAllYears() {

        // Iterating all years and makes
        (AvailableYears.MINIMUM_YEAR..AvailableYears.MAXIMUM_YEAR).each { int year ->
            makeService.allMakes.each { PersistableMake make ->
                doFetchAndPersistForMakeAndYear(make, year)
            }
        }
    }

    void doFetchAndPersistForMakeAndYear(PersistableMake make, int year) {

        def startStopwatch = System.currentTimeMillis()

        String encodedMakeName = URLEncoder.encode(make.name, "UTF-8")
        String makeAndYearUri = CARQUERYAPI_V3_BASE_URL + CARQUERYAPI_MODELS_PATH + "&make=${encodedMakeName}&year=${year}"

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
            throw ex
        }

        log.info "Fetched and persisted [${models.size()}] models for [${make.name}], [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(2000)
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
}
