package com.tcoproject.server.services.priceData

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.repository.TrimRepository
import com.tcoproject.server.services.make.MakeService
import com.tcoproject.server.services.model.ModelService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class PriceDataService {

    @Autowired
    TrimRepository trimRepository

    @Autowired
    MakeService makeService

    @Autowired
    ModelService modelService

    void doPriceDataFetchAndPersist(PriceDataFetchAndPersistRequest request) {

        log.info "PriceDataFetchAndPersistRequest [${request}]"

        if (request.endWithYear < 1960) { request.endWithYear = 1960 }

        PersistableMake make = makeService.getMakeByName(request.make)

        // Iterating through start and stop with year arguments
        (request.startWithYear..request.endWithYear).each { int year ->
            doFetchAndPersistForModelAndYear(make, year)
        }
    }

    void doFetchAndPersistForModelAndYear(PersistableMake make, int year) {

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



        // log.info "Fetched and persisted [${trims.size()}] trims for [${make.name}], year [${year}] in [${System.currentTimeMillis() - startStopwatch}] ms"

        Thread.sleep(1000)
    }
}
