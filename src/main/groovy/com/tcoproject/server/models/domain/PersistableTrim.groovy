package com.tcoproject.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name="trim")
class PersistableTrim {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    Boolean active

    @ManyToOne
    @JoinColumn(name="model_id")
    PersistableModel model


    String body
    String co2
    String drive
    Integer doors
    Double engineBoreMm
    Integer engineCc
    String engineCompression
    Integer engineCylinders
    String engineFuel
    String enginePosition
    Integer enginePowerPs
    Integer enginePowerRpm
    Integer engineTorqueNm
    Integer engineTorqueRpm
    String engineType
    Integer engineValvesPerCylinder
    Double engineStrokeMm
    String fuelCap
    String name // Model trim
    Double oneKmHwy
    Double oneKmMixed
    Double oneKmCity
    Double zeroToHundredKph
    Integer seats
    Boolean soldInUs
    Integer topSpeedKph
    String transmissionType
    Double weightKg
    Double lengthMm
    Double widthMm
    Double heightMm
    Double wheelbaseMm

}
