package com.tcoproject.server.models.constants

/**
 * For specifying the target URI segment
 */
enum ResolutionTargetTypeEnum {

    TARGET_TYPE_MODEL("Model"),
    TARGET_TYPE_TRIM("Trim")

    String value

    ResolutionTargetTypeEnum(String targetType) {
        value = targetType
    }

}
