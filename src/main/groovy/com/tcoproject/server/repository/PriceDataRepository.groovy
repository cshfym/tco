package com.tcoproject.server.repository

import com.tcoproject.server.models.domain.PersistablePriceData
import org.springframework.data.repository.CrudRepository

interface PriceDataRepository extends CrudRepository<PersistablePriceData, String> {

}