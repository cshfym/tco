package com.tcoproject.server.converters

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistableTrim
import com.tcoproject.server.models.external.kbb.KBBMakeModelYearResponse
import com.tcoproject.server.models.external.kbb.KBBVehicleWrapper


@Singleton
class PriceDataConverter extends BaseConverter {

    static PersistablePriceData toPersistableFromKBBMakeModelYear(KBBMakeModelYearResponse response,
        PersistableModel persistableModel, PersistableTrim persistableTrim, String priceSource, String priceSourceUrl) {

        List<String> priceData = parsePriceData(response.info.vehicle)

        PersistablePriceData persistablePriceData = new PersistablePriceData()
        persistablePriceData.with {
            model = persistableModel
            trim = persistableTrim
            dateCreated = new Date()
            retailPrice = safeParseDouble(priceData[1], "response.info.vehicle.taggingprice[1]")
            source = priceSource
            sourceUrl = priceSourceUrl
            suggestedPrice = safeParseDouble(priceData[0], "response.info.vehicle.taggingprice[0]")
            trimDisplayName = response.info.vehicle?.trimdisplayname
        }
        persistablePriceData
    }

    private static List<String> parsePriceData(KBBVehicleWrapper vehicleWrapper) {
        vehicleWrapper.taggingprice.tokenize("^")
    }
}
