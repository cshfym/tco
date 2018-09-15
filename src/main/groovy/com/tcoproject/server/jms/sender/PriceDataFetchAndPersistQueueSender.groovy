package com.tcoproject.server.jms.sender

import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class PriceDataFetchAndPersistQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for loading requests */
    final static String QUEUE_PRICE_DATA_FETCH_PERSIST = "com.tcoproject.queue.pricedata.fetch.persist"

    /**
     * Queues a price data fetch and persist request
     * @param TrimFetchAndPersistRequest
     */
    void queueRequest(PriceDataFetchAndPersistRequest request) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "==========> [*] Queueing a price data fetch and persist request: [${request}]"

        jmsTemplate.convertAndSend(QUEUE_PRICE_DATA_FETCH_PERSIST, request)
    }

}
