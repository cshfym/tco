package com.tcoproject.server.repository.resolutionStrategy

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableResolutionStrategy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class ResolutionStrategyRepositoryService {

    @Autowired
    ResolutionStrategyRepository resolutionStrategyRepository

    List<PersistableResolutionStrategy> findAllByMakeAndModelName(PersistableMake make, String model) {
        resolutionStrategyRepository.findAllByMakeAndModelName(make, model)
    }
}
