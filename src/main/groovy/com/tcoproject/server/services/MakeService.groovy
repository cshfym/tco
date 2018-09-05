package com.tcoproject.server.services

import com.tcoproject.server.converters.MakeConverter
import com.tcoproject.server.models.Make

import com.tcoproject.server.repository.MakeRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class MakeService {

    @Autowired
    MakeRepository makeRepository

    // Convert to async method?
    void saveMakeRequest(List<Make> makeList) {
        makeList.each { Make make ->
            makeRepository.save(MakeConverter.toPersistable(make))
        }

    }

}
