package com.tcoproject.server.converters

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.CarQueryTrimResponse
import groovy.util.logging.Slf4j

@Slf4j
@Singleton
class TrimConverter {

    static PersistableTrim toPersistable(CarQueryTrimResponse trimResponse, PersistableModel persistableModel) {
        PersistableTrim persistableTrim = new PersistableTrim()
        persistableTrim.with {
            active = true
            model = persistableModel
            name = trimResponse.modelTrim
            body = trimResponse.modelBody
            co2 = trimResponse.modelCo2
            drive = trimResponse.modelDrive
            doors = safeParseInteger(trimResponse.modelDoors, "trimResponse.modelDoors")
            engineBoreMm = safeParseDouble(trimResponse.modelEngineBoreMm, "trimResponse.modelEngineBoreMm")
            engineCc = safeParseInteger(trimResponse.modelEngineCc, "trimResponse.modelEngineCc")
            engineCompression = trimResponse.modelEngineCompression
            engineCylinders = safeParseInteger(trimResponse.modelEngineCyl, "trimResponse.modelEngineCyl")
            engineFuel = trimResponse.modelEngineFuel
            enginePosition = trimResponse.modelEnginePosition
            enginePowerPs = safeParseInteger(trimResponse.modelEnginePowerPs, "trimResponse.modelEnginePowerPs")
            enginePowerRpm = safeParseInteger(trimResponse.modelEnginePowerRpm, "trimResponse.modelEnginePowerRpm")
            engineTorqueNm = safeParseInteger(trimResponse.modelEngineTorqueNm, "trimResponse.modelEngineTorqueNm")
            engineTorqueRpm = safeParseInteger(trimResponse.modelEngineTorqueRpm, "trimResponse.modelEngineTorqueRpm")
            engineType = trimResponse.modelEngineType
            engineValvesPerCylinder = safeParseInteger(trimResponse.modelEngineValvesPerCyl, "trimResponse.modelEngineValvesPerCyl")
            engineStrokeMm = safeParseDouble(trimResponse.modelEngineStrokeMm, "trimResponse.modelEngineStrokeMm")
            fuelCap = safeParseDouble(trimResponse.modelFuelCap1, "trimResponse.modelFuelCap1")
            zeroToHundredKph = safeParseDouble(trimResponse.model0to100Kph, "trimResponse.model0to100Kph")
            oneKmHwy = safeParseDouble(trimResponse.model1kmHwy, "trimResponse.model1kmHwy")
            oneKmMixed = safeParseDouble(trimResponse.model1kmMixed, "trimResponse.model1kmMixed")
            oneKmCity = safeParseDouble(trimResponse.model1kmCity, "trimResponse.model1kmCity")
            seats = safeParseInteger(trimResponse.modelSeats, "trimResponse.modelSeats")
            soldInUs = safeParseBoolean(trimResponse.modelSoldInUs, "trimResponse.modelSoldInUs")
            topSpeedKph = safeParseInteger(trimResponse.modelTopSpeedKph, "trimResponse.modelTopSpeedKph")
            transmissionType = trimResponse.modelTransmissionType
            weightKg = safeParseDouble(trimResponse.modelWeightKg, "trimResponse.modelWeightKg")
            lengthMm = safeParseDouble(trimResponse.modelLengthMm, "trimResponse.modelLengthMm")
            widthMm = safeParseDouble(trimResponse.modelWidthMm, "trimResponse.modelWidthMm")
            heightMm = safeParseDouble(trimResponse.modelHeightMm, "trimResponse.modelHeightMm")
            wheelbaseMm = safeParseDouble(trimResponse.modelWheelbaseMm, "trimResponse.modelWheelBaseMm")
        }

        persistableTrim
    }

    static Integer safeParseInteger(String value, String field) {
        if (!value) { return 0 }
        try {
            return Integer.parseInt(value)
        } catch (Exception ex) {
            log.warn "Could not parse integer value [${value}] for field [${field}]"
            return 0
        }
    }

    static Double safeParseDouble(String value, String field) {
        if (!value) { return 0.0 }
        try {
            return Double.parseDouble(value)
        } catch (Exception ex) {
            log.warn "Could not parse double value [${value}] for field [${field}]"
            return 0.0
        }
    }

    static Boolean safeParseBoolean(String value, String field) {
        if (!value) { return Boolean.FALSE }
        try {
            return Boolean.valueOf(value)
        } catch (Exception ex) {
            return Boolean.FALSE
        }
    }
}
