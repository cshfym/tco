package com.tcoproject.server.models.domain

import org.hibernate.annotations.GenericGenerator
import org.joda.time.DateTime

import javax.persistence.*

@Entity(name="price_data")
class PersistablePriceData {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    @JoinColumn(name="trim_id")
    PersistableTrim trim

    DateTime dateCreated

    Boolean isBaseModelPrice

    Double retailPrice

    String source

    Double suggestedPrice

}
