package com.tcoproject.server.converters

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.models.external.kbb.KBBVehicleWrapper


@Singleton
class PriceDataConverter extends BaseConverter {

    static PersistablePriceData toPersistableFromKBBMakeModelYear(KBBMakeModelYearResponse response, boolean isBaseModel,
        PersistableModel persistableModel, PersistableTrim persistableTrim = null) {

        List<String> priceData = parsePriceData(response.info.vehicle)

        PersistablePriceData persistablePriceData = new PersistablePriceData()
        persistablePriceData.with {
            model = persistableModel
            trim = persistableTrim
            dateCreated = new Date()
            isBaseModelPrice = isBaseModel
            retailPrice = safeParseDouble(priceData[1], "response.info.vehicle.taggingprice[1]")
            source = "KBB"
            suggestedPrice = safeParseDouble(priceData[0], "response.info.vehicle.taggingprice[0]")
        }
        persistablePriceData
    }

    private static List<String> parsePriceData(KBBVehicleWrapper vehicleWrapper) {
        vehicleWrapper.taggingprice.tokenize("^")
    }
}
