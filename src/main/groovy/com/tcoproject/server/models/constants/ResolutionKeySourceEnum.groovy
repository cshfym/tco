package com.tcoproject.server.models.constants

/**
 * For specifying the target URI segment
 */
enum ResolutionKeySourceEnum {

    KEY_SOURCE_MODEL("Model"),
    KEY_SOURCE_TRIM("Trim")

    String value

    ResolutionKeySourceEnum(String keySource) {
        value = keySource
    }

}
