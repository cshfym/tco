package com.tcoproject.server.converters

import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.CarQueryTrimResponse

@Singleton
class TrimConverter {

    static PersistableTrim toPersistable(CarQueryTrimResponse trimResponse, int inputYear) {
        PersistableTrim persistableTrim = new PersistableTrim()
        persistableTrim.with {
            /*
            make = persistableMake
            year = inputYear
            name = model.modelName
            active = true
            */
        }

        persistableTrim
    }

}
