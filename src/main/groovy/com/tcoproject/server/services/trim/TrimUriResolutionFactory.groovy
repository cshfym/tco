package com.tcoproject.server.services.trim

import com.tcoproject.server.models.constants.TCOConstants
import org.springframework.stereotype.Component

/**
 * Class to resolve the trim-based URI segment
 */
@Component
class TrimUriResolutionFactory {


    static String targetUriSegment(String makeName, String modelName, String trimName) {

        if (makeName.equals("Nissan")) {
            if (modelName.equals("Frontier")) {
                if (trimName.startsWith("Desert Runner")) { return "desert-runner" }
                if (trimName.startsWith("PRO-4X")) { return "pro-4x" }
                if (trimName.startsWith("S ")) { return "s" }
                if (trimName.startsWith("SL ")) { return "sl" }
                if (trimName.startsWith("SV ")) { return "sv" }
            }
            if (modelName.equals("Titan")) {
                if (trimName.startsWith("PRO-4X")) { return "pro-4x" }
                if (trimName.startsWith("S ")) { return "s" }
                if (trimName.startsWith("SL ")) { return "sl" }
                if (trimName.startsWith("SV ")) { return "sv" }
            }
            if (modelName.equals("Xterra")) {
                if (trimName.startsWith("Desert Runner")) { return "desert-runner" }
                if (trimName.startsWith("PRO-4X")) { return "pro-4x" }
                if (trimName.startsWith("X ")) { return "s" }
                if (trimName.startsWith("SL ")) { return "sl" }
                if (trimName.startsWith("SV ")) { return "sv" }
            }
            if (modelName.equals("Juke")) {
                if (trimName.startsWith("NISMO")) { return "nismo" }
                if (trimName.startsWith("S ")) { return "s" }
                if (trimName.startsWith("SL ")) { return "sl" }
                if (trimName.startsWith("SV ")) { return "sv" }
            }
            if (modelName.equals("Armada")) {
                if (trimName.startsWith("Platinum")) { return "platinum" }
                if (trimName.startsWith("S ")) { return "s" }
                if (trimName.startsWith("SL ")) { return "sl" }
                if (trimName.startsWith("SV ")) { return "sv" }
            }

        }

        TCOConstants.BASE_STYLE_OPTION
    }


}
