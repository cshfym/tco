package com.tcoproject.server.repository

import com.tcoproject.server.models.domain.PersistableMake
import org.springframework.data.repository.CrudRepository

interface MakeRepository extends CrudRepository<PersistableMake, String> {

    PersistableMake findByName(String name)

    List<PersistableMake> findAllByIsCommon(boolean isCommon)
}