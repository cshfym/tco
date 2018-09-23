package com.tcoproject.server.repository.resolutionStrategy

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableResolutionStrategy
import org.springframework.data.repository.CrudRepository

interface ResolutionStrategyRepository extends CrudRepository<PersistableResolutionStrategy, String> {

    List<PersistableResolutionStrategy> findAllByMakeAndModelName(PersistableMake make, String modelName)

}