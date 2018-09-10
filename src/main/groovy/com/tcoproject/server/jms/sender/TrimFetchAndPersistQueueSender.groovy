package com.tcoproject.server.jms.sender

import com.tcoproject.server.models.external.ModelFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class ModelFetchAndPersistQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for loading model requests */
    final static String QUEUE_MODEL_FETCH_PERSIST = "com.tcoproject.queue.model.fetch.persist"

    /**
     * Queues a model fetch and persist request
     * @param ModelFetchAndPersistRequest
     */
    void queueRequest(ModelFetchAndPersistRequest request) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a model fetch and persist: [${request}]"

        jmsTemplate.convertAndSend(QUEUE_MODEL_FETCH_PERSIST, request)
    }

}
