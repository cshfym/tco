package com.tcoproject.server.repository

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import org.springframework.data.repository.CrudRepository

interface ModelRepository extends CrudRepository<PersistableModel, String> {

    PersistableModel findByMakeAndNameAndYear(PersistableMake make, String name, int year)

    List<PersistableModel> findAllByMakeAndYear(PersistableMake make, int year)

}