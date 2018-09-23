package com.tcoproject.server.repository.priceData

import com.tcoproject.server.models.domain.PersistableModel
import com.tcoproject.server.models.domain.PersistablePriceData
import com.tcoproject.server.models.domain.PersistableTrim
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class PriceDataRepositoryService {

    @Autowired
    PriceDataRepository priceDataRepository

    @Transactional
    boolean persistPriceData(PersistablePriceData persistablePriceData) {

        // Check for existing data (no time)
        Date today = new Date().clearTime()
        PersistablePriceData existingPriceData =
                priceDataRepository.findByModelAndTrimAndSourceAndDateCreated(persistablePriceData.model,
                        persistablePriceData.trim, persistablePriceData.source, today)
        if (existingPriceData) {
            log.info "Existing price data found for model [${persistablePriceData?.model?.name}], " +
                    "trim [${persistablePriceData?.trim?.name}], on date [${today}]; bypassing persistence."
            return false
        }

        try {
            def persisted = priceDataRepository.save(persistablePriceData)
            log.info "Persisted price data [${persisted}]"
        } catch (Exception ex) {
            log.error "Exception thrown while converting and persisting persistablePriceData", ex
            return false
        }

        true
    }

    boolean priceDataExists(PersistableModel model, PersistableTrim trim, Date date, String source) {
        priceDataRepository.findByModelAndTrimAndSourceAndDateCreated(model, trim, source, date)
    }

}
