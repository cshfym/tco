package com.tcoproject.server.jms.receiver

import com.tcoproject.server.jms.sender.PriceDataFetchAndPersistQueueSender
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import com.tcoproject.server.services.priceData.KBBPriceDataService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class PriceDataFetchAndPersistQueueReceiver {

    @Autowired
    KBBPriceDataService priceDataService

    @JmsListener(destination = PriceDataFetchAndPersistQueueSender.QUEUE_PRICE_DATA_FETCH_PERSIST, containerFactory = "priceDataFetchAndPersistFactory")
    void receiveRequest(PriceDataFetchAndPersistRequest request) {

        log.info "[] ==========> * Received [${request}] from queue ${PriceDataFetchAndPersistQueueSender.QUEUE_PRICE_DATA_FETCH_PERSIST}"

        priceDataService.doPriceDataFetchAndPersist(request)
    }
}
