package com.tcoproject.server.models.constants

/**
 * For specifying the target URI segment
 */
enum ResolutionInspectionTypeEnum {

    INSPECTION_TYPE_CONTAINS("contains"),
    INSPECTION_TYPE_STARTSWITH("startsWith")

    String value

    ResolutionInspectionTypeEnum(String inspectionType) {
        value = inspectionType
    }

}
