package com.tcoproject.server.models.external

/**
 * External request to fetch and persist all price data for a given model .
 */
class PriceDataFetchAndPersistRequest {

    Integer startWithYear
    Integer endWithYear
    String make
    String source // KBB, etc.
    Boolean baseModelOnly

    @Override
    String toString() {
        "PriceDataFetchAndPersistRequest: startWithYear [${startWithYear}], endWithYear [${endWithYear}], make [${make}], source [${source}], baseModelOnly [${baseModelOnly}]"
    }
}
