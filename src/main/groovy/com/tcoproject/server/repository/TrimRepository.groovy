package com.tcoproject.server.repository

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistableTrim
import org.springframework.data.repository.CrudRepository

interface TrimRepository extends CrudRepository<PersistableTrim, String> {

    PersistableTrim findByModelAndName(PersistableModel model, String name)

    List<PersistableTrim> findAllByModel(PersistableModel model)
}