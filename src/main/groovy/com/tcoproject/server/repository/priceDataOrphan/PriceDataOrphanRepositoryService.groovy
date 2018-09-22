package com.tcoproject.server.repository.priceDataOrphan

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceDataOrphan
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class PriceDataOrphanRepositoryService {


    @Transactional
    void persistPriceDataOrphan(PersistableModel persistableModel, String orphanUri) {

        if (priceDataOrphanRepository.findByModelAndUri(persistableModel, orphanUri)) {
            log.info "Existing orphan record with model [${persistableModel.name}] and uri [${orphanUri}], not persisting."
            return
        }

        PersistablePriceDataOrphan orphan = new PersistablePriceDataOrphan(model: persistableModel,uri: orphanUri)
        log.info "Persisting orphan record with model [${persistableModel.name}] and uri [${orphanUri}]"
        priceDataOrphanRepository.save(orphan)
    }

}
