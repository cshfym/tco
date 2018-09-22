package com.tcoproject.server.repository.priceDataOrphan

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceDataOrphan
import org.springframework.data.repository.CrudRepository

interface PriceDataOrphanRepository extends CrudRepository<PersistablePriceDataOrphan, String> {

    PersistablePriceDataOrphan findByModelAndUri(PersistableModel model, String uri)
}