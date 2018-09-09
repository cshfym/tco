package com.tcoproject.server.models.external

/**
 * External request to fetch and persist all models for a given make.
 */
class ModelFetchAndPersistRequest {

    Integer startWithYear
    Integer endWithYear
    String make

    @Override
    String toString() {
        "ModelFetchAndPersistRequest: startWithYear [${startWithYear}], endWithYear [${endWithYear}], make [${make}]"
    }
}
