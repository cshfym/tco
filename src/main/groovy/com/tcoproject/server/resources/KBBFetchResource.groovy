package com.tcoproject.server.resources

import com.tcoproject.server.jms.sender.PriceDataFetchAndPersistQueueSender
import com.tcoproject.server.models.BasicResponse
import com.tcoproject.server.models.external.PriceDataFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/kbb")
class KBBFetchResource {

    @Autowired
    PriceDataFetchAndPersistQueueSender priceDataFetchAndPersistQueueSender

    @ResponseBody
    @RequestMapping(path="/fetchAndPersist", method=RequestMethod.POST, consumes="application/json")
    BasicResponse fetchAndPersistPriceData(@RequestBody PriceDataFetchAndPersistRequest request) {
        priceDataFetchAndPersistQueueSender.queueRequest(request)
        new BasicResponse(success: true)
    }

}
