package com.tcoproject.server.repository.model

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Slf4j
@Service
class ModelRepositoryService {

    @Autowired
    ModelRepository modelRepository

    /**
     * Convenience method to persist a collection of models.
     * @param persistableModelList
     */
    void persistModels(List<PersistableModel> persistableModelList) {

        persistableModelList.each { PersistableModel persistableModel ->
            persistModel(persistableModel)
        }
    }

    @Transactional
    void persistModel(PersistableModel persistableModel) {

        PersistableModel existingModel = modelRepository.findByMakeAndNameAndYear(persistableModel.make, persistableModel.name, persistableModel.year)

        if (!existingModel) {
            log.info "Persisting model with make [${persistableModel.make.name}], model [${persistableModel.name}], and year [${persistableModel.year}]"
            modelRepository.save(persistableModel)
        } else {
            log.info "Bypassing persistence for make [${persistableModel.make.name}], " +
                    "model [${persistableModel.name}], year [${persistableModel.year}] - already exists."
        }
    }

    List<PersistableModel> findAllByMakeAndYear(PersistableMake make, int year) {
        modelRepository.findAllByMakeAndYear(make, year)
    }

    PersistableModel findByMakeAndNameAndYear(PersistableMake make, String name, int year) {
        modelRepository.findByMakeAndNameAndYear(make, name, year)
    }
}
