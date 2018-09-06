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

    boolean active

    @ManyToOne
    @JoinColumn(name="model_id")
    PersistableModel model

    String body
    int co2
    String drive
    int doors
    int engineBoreMm
    int engineCc
    String engineCompression
    int engineCylinders
    String engineFuel
    String enginePosition
    int enginePowerPs
    int enginePowerRpm
    int engineTorqueNm
    int engineTorqueRpm
    String engineType
    int engineValvesPerCylinder
    int engineStrokeMm
    String fuelCap
    int oneKmHwy
    int oneKmMixed
    int oneKmCity
    int seats
    boolean soldInUs
    int topSpeedKph
    String transmissionType
    int weightKg
    int lengthMm
    int widthMm
    int heightMm
    int wheelbaseMm

}
