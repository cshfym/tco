package com.tcoproject.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity(name="price_data_orphan")
class PersistablePriceDataOrphan {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    PersistableModel model

    String uri

    @Override
    String toString() {
      "PersistablePriceData: id [${id}], trim [${uri}]"
    }
}
