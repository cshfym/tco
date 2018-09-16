package com.tcoproject.server.services.model

import org.springframework.stereotype.Component

/**
 * Class to resolve the model-based URI segment
 */
@Component
class ModelUriResolutionFactory {


    static String targetUriSegment(String makeName, String modelName, String trimName) {

        if (modelName.equals("Frontier")) {
            if (trimName?.contains("Crew Cab")) { return "frontier-crew-cab" }
            if (trimName?.contains("King Cab")) { return "frontier-king-cab" }
        }

        modelName.toLowerCase()
    }


}
