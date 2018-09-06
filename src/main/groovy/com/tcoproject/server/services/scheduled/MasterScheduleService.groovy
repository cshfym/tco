package com.tcoproject.server.services.scheduled

import com.tcoproject.server.services.model.ModelService
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class MasterScheduleService {

    @Autowired
    ModelService modelService

    @Scheduled(fixedDelay = 43200000L, initialDelay = 1000L) // Every 12 hours.
    void scheduledModelFetchCron() {

        log.info "Starting scheduledModelFetchCron at [${new DateTime()}]"

        modelService.fetchAndPersistModelsAllMakesAllYears()

        log.info "Finished scheduledModelFetchCron at [${new DateTime()}]"
    }

}
