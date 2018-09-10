package com.tcoproject.server.resources

import com.tcoproject.server.jms.sender.ModelFetchAndPersistQueueSender
import com.tcoproject.server.models.BasicResponse
import com.tcoproject.server.models.external.ModelFetchAndPersistRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/model")
class ModelResource {

    @Autowired
    ModelFetchAndPersistQueueSender modelFetchAndPersistQueueSender

    @ResponseBody
    @RequestMapping(path="/fetchAndPersist", method=RequestMethod.POST, consumes="application/json")
    BasicResponse fetchAndPersistModel(@RequestBody ModelFetchAndPersistRequest request) {
        modelFetchAndPersistQueueSender.queueRequest(request)
        new BasicResponse(success: true)
    }

}
