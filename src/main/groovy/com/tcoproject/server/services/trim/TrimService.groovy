package com.tcoproject.server.services.make

import com.tcoproject.server.converters.MakeConverter
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.external.ExternalMake

import com.tcoproject.server.repository.MakeRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Slf4j
@Service
class MakeService {

    @Autowired
    MakeRepository makeRepository

    @Value('${carqueryapi.makes.base.path}')
    String CARQUERYAPI_MAKES_BASE_PATH

    // https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&full_results=1&year=2018&make=Acura

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
