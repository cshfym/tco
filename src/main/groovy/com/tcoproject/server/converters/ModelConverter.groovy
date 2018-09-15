package com.tcoproject.server.converters

import com.tcoproject.server.models.domain.PersistableMake
import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.external.CarQueryModelResponse

@Singleton
class ModelConverter extends BaseConverter {

    static PersistableModel toPersistable(CarQueryModelResponse model, PersistableMake persistableMake, int inputYear) {
        PersistableModel persistableModel = new PersistableModel()
        persistableModel.with {
            make = persistableMake
            year = inputYear
            name = model.modelName
            active = true
        }
        persistableModel
    }

}
