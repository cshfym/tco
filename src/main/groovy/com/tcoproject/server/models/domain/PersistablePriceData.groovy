package com.tcoproject.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity(name="price_data")
class PersistablePriceData {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    PersistableModel model

    @ManyToOne
    @JoinColumn(name="trim_id")
    PersistableTrim trim

    @Temporal(TemporalType.DATE)
    Date dateCreated

    Double retailPrice

    String source

    Double suggestedPrice

    String trimDisplayName

    @Override
    String toString() {
      "PersistablePriceData: id [${id}], trim [${trim?.name}], dateCreated [${dateCreated}], " +
              "retailPrice [${retailPrice}], suggestedPrice [${suggestedPrice}], source [${source}], trimDisplayName [${trimDisplayName}]"
    }
}
