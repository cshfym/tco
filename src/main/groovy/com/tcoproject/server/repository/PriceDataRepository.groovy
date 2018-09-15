package com.tcoproject.server.repository

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistableTrim
import org.springframework.data.repository.CrudRepository

interface PriceDataRepository extends CrudRepository<PersistablePriceData, String> {

    PersistablePriceData findByModelAndTrimAndSourceAndDateCreated(PersistableModel model, PersistableTrim trim, String source, Date date)

}