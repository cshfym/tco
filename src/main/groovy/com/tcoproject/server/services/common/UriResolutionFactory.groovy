package com.tcoproject.server.services.common

import com.tcoproject.server.models.constants.ResolutionKeySourceEnum
import com.tcoproject.server.models.constants.ResolutionTargetTypeEnum
import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistableResolutionStrategy
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.repository.resolutionStrategy.ResolutionStrategyRepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Class to resolve the model-based URI segment
 */
@Component
class UriResolutionFactory {

    @Autowired
    ResolutionStrategyRepositoryService resolutionStrategyRepositoryService

    /**
     * Returns the target URI segment given vehicle information and target type.
     * @param make
     * @param model
     * @param trim
     * @param targetType {@link ResolutionTargetTypeEnum}
     * @return
     */
    String targetUriSegment(PersistableMake make, PersistableModel model, PersistableTrim trim, ResolutionTargetTypeEnum targetType,
        ResolutionKeySourceEnum keySource) {

        String uriSegment = ""

        List<PersistableResolutionStrategy> resolutionStrategies =
                resolutionStrategyRepositoryService.findAllByMakeAndModelName(make, model.name
                )?.findAll {
                    (it.uriTargetType == targetType.value)
                }?.findAll {
                    (it.keySource == keySource.value)
                }

        resolutionStrategies.each { PersistableResolutionStrategy strategy ->
            if (uriSegment) { return }
            if ((strategy.keySource == ResolutionKeySourceEnum.KEY_SOURCE_TRIM.value) && trim.name."${strategy.inspectionType}"(strategy.key)) {
                uriSegment = strategy.value
                return
            }
            if ((strategy.keySource == ResolutionKeySourceEnum.KEY_SOURCE_MODEL.value) && model.name."${strategy.inspectionType}"(strategy.key)) {
                uriSegment = strategy.value
                return
            }
        }

        uriSegment.toLowerCase()
    }

}
