package com.tcoproject.server.resources

import com.tcoproject.server.models.BasicResponse
import com.tcoproject.server.models.Make
import com.tcoproject.server.services.MakeService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/makes")
class MakeResource {

    @Autowired
    MakeService makeService

    @ResponseBody
    @RequestMapping(method=RequestMethod.POST, consumes="application/json")
    BasicResponse postMakeRequest(@RequestBody List<Make> request) {
        makeService.saveMakeRequest(request)
        new BasicResponse(success: true)
    }

}
