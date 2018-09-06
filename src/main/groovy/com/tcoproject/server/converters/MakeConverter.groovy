package com.tcoproject.server.converters

import com.tcoproject.server.models.external.ExternalMake
import com.tcoproject.server.models.domain.PersistableMake

@Singleton
class MakeConverter {

    static PersistableMake toPersistable(ExternalMake make) {
        PersistableMake persistableMake = new PersistableMake()
        persistableMake.with {
            country = make.make_country
            isCommon = make.make_is_common
            name = make.make_display
            active = true
        }
        persistableMake
    }

}
