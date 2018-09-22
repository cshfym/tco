package com.tcoproject.server.jms.receiver

import com.tcoproject.server.jms.sender.ModelFetchAndPersistQueueSender
import com.tcoproject.server.models.external.ModelFetchAndPersistRequest
import com.tcoproject.server.services.model.CarQueryModelService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ModelFetchAndPersistQueueReceiver {

    @Autowired
    CarQueryModelService modelService

    @JmsListener(destination = ModelFetchAndPersistQueueSender.QUEUE_MODEL_FETCH_PERSIST, containerFactory = "modelFetchAndPersistFactory")
    void receiveRequest(ModelFetchAndPersistRequest request) {

        log.debug "Received [${request}] from queue ${ModelFetchAndPersistQueueSender.QUEUE_MODEL_FETCH_PERSIST}"

        modelService.doModelFetchAndPersist(request)
    }
}
