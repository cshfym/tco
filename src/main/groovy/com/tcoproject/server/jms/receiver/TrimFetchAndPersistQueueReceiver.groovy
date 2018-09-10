package com.tcoproject.server.jms.receiver

import com.tcoproject.server.jms.sender.TrimFetchAndPersistQueueSender
import com.tcoproject.server.models.external.TrimFetchAndPersistRequest
import com.tcoproject.server.services.trim.TrimService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class TrimFetchAndPersistQueueReceiver {

    @Autowired
    TrimService trimService

    @JmsListener(destination = TrimFetchAndPersistQueueSender.QUEUE_TRIM_FETCH_PERSIST, containerFactory = "trimFetchAndPersistFactory")
    void receiveRequest(TrimFetchAndPersistRequest request) {

        log.debug "Received [${request}] from queue ${TrimFetchAndPersistQueueSender.QUEUE_TRIM_FETCH_PERSIST}"

        trimService.doTrimFetchAndPersist(request)
    }
}
