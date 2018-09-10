package com.tcoproject.server.resources

import com.tcoproject.server.jms.sender.ModelFetchAndPersistQueueSender
import com.tcoproject.server.jms.sender.TrimFetchAndPersistQueueSender
import com.tcoproject.server.models.BasicResponse
import com.tcoproject.server.models.external.ModelFetchAndPersistRequest
import com.tcoproject.server.models.external.TrimFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/trim")
class TrimResource {

    @Autowired
    TrimFetchAndPersistQueueSender trimFetchAndPersistQueueSender

    @ResponseBody
    @RequestMapping(path="/fetchAndPersist", method=RequestMethod.POST, consumes="application/json")
    BasicResponse fetchAndPersistTrim(@RequestBody TrimFetchAndPersistRequest request) {
        trimFetchAndPersistQueueSender.queueRequest(request)
        new BasicResponse(success: true)
    }

}
