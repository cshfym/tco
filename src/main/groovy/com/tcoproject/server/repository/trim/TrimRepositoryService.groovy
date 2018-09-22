package com.tcoproject.server.repository.trim

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistableTrim
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class TrimRepositoryService {

    @Autowired
    TrimRepository trimRepository

    @Transactional
    void persistTrim(PersistableTrim persistableTrim) {

        PersistableTrim existingTrim = trimRepository.findByModelAndName(persistableTrim.model, persistableTrim.name)

        if (!existingTrim) {
            log.info "Persisting trim for model [${persistableTrim.model.name}], year [${persistableTrim.model.year}] and trim [${persistableTrim.name}]"
            trimRepository.save(persistableTrim)
        } else {
            log.info "Bypassing persistence for trim - model [${persistableTrim.model.name}], year [${persistableTrim.model.year}] and trim [${persistableTrim.name}] - already exists!"
        }

    }

    List<PersistableTrim> findAllTrimsForModel(PersistableModel model) {
        trimRepository.findAllByModel(model)
    }

}
