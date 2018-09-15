package com.tcoproject.server.converters

import groovy.util.logging.Slf4j

@Slf4j
class BaseConverter {

    static Integer safeParseInteger(String value, String field) {
        if (!value) { return 0 }
        try {
            return Integer.parseInt(value)
        } catch (Exception ex) {
            log.warn "Could not parse integer value [${value}] for field [${field}]"
            return 0
        }
    }

    static Double safeParseDouble(String value, String field) {
        if (!value) { return 0.0 }
        try {
            return Double.parseDouble(value)
        } catch (Exception ex) {
            log.warn "Could not parse double value [${value}] for field [${field}]"
            return 0.0
        }
    }

    static Boolean safeParseBoolean(String value, String field) {
        if (!value) { return Boolean.FALSE }
        try {
            return Boolean.valueOf(value)
        } catch (Exception ex) {
            return Boolean.FALSE
        }
    }
}
