package com.tcoproject.server.models.external

/**
 * External request to fetch and persist all trims for a given model .
 */
class TrimFetchAndPersistRequest {

    Integer startWithYear
    Integer endWithYear
    String make

    @Override
    String toString() {
        "ModelFetchAndPersistRequest: startWithYear [${startWithYear}], endWithYear [${endWithYear}], make [${make}]"
    }
}
