package com.tcoproject.server.converters

import com.tcoproject.server.models.Make
import com.tcoproject.server.models.domain.PersistableMake

@Singleton
class MakeConverter {

    static PersistableMake toPersistable(Make make) {
        PersistableMake persistableMake = new PersistableMake()
        persistableMake.with {
            country = make.make_country
            isCommon = make.make_is_common
            name = make.make_display
        }
        persistableMake
    }

}
