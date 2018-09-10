package com.tcoproject.server.jms.sender

import com.tcoproject.server.models.external.TrimFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class TrimFetchAndPersistQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for loading model requests */
    final static String QUEUE_TRIM_FETCH_PERSIST = "com.tcoproject.queue.trim.fetch.persist"

    /**
     * Queues a trim fetch and persist request
     * @param TrimFetchAndPersistRequest
     */
    void queueRequest(TrimFetchAndPersistRequest request) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a trim fetch and persist request: [${request}]"

        jmsTemplate.convertAndSend(QUEUE_TRIM_FETCH_PERSIST, request)
    }

}
