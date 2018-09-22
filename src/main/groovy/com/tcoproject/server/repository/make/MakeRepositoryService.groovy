package com.tcoproject.server.repository.make

import com.tcoproject.server.converters.MakeConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.external.ExternalMake
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class MakeRepositoryService {

    @Autowired
    MakeRepository makeRepository

    void saveMakeRequest(List<ExternalMake> makeList) {
        makeList.each { ExternalMake make ->
            if (!makeExists(make)) {
                makeRepository.save(MakeConverter.toPersistable(make))
            }
        }
    }

    PersistableMake getMakeByName(String name) {
        makeRepository.findByName(name)
    }

    boolean makeExists(ExternalMake make) {
        makeRepository.findByName(make.make_display)
    }

    List<PersistableMake> getAllMakes() {
        makeRepository.findAll()
    }

    List<PersistableMake> getAllCommonMakes() {
        makeRepository.findAllByIsCommon(true)
    }
}
