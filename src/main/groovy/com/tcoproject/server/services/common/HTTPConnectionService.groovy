package com.tcoproject.server.services.common

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Generic, all-purpose HTTP connection service.
 */
@Slf4j
@Service
class HTTPConnectionService {

    final static String  EMPTY_STRING = ""

    static String getData(String address, RequestMethod method, String base64EncodedAuthorization = null, Map<String,String> requestHeaders = [:]) {

        def response = null

        def startStopwatch = System.currentTimeMillis()

        HttpURLConnection conn = null

        try {
            URL url = new URL(address)
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod(method.toString())

            // conn.setRequestProperty("Accept", "application/json")

            requestHeaders.each { k, v ->
                conn.setRequestProperty(k, v)
            }

            log.info "Issuing requst to [${address}] as [${method.name()}] with request headers: [${requestHeaders}]"

            if (base64EncodedAuthorization) {
                conn.setRequestProperty("Authorization", "Basic " + base64EncodedAuthorization)
            }

            conn.connect()

            def responseCode = conn.responseCode

            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.inputStream))
                    StringBuilder sb = new StringBuilder()
                    String line
                    while ((line = br.readLine()) != null) { sb.append(line) }
                    br.close()
                    response = sb.toString()
                    break
                case 401:
                    throw new RuntimeException("Getting 401 response from address [${url.text}] with request headers [${requestHeaders}]")
                    break
                case 403:
                    throw new RuntimeException("Getting 401 response from address [${url.text}] with request headers [${requestHeaders}]")
                    return EMPTY_STRING
                    break
                case 404:
                    log.warn("404 Not Found at address [${url.text}] with request headers [${requestHeaders}]")
                    return EMPTY_STRING
                    break
                case 429:
                    log.warn("429 Access Denied - too many requests at [${url.text}] with request headers [${requestHeaders}]")
                    return EMPTY_STRING
                    break
                default:
                    throw new RuntimeException("Failed : HTTP error code [${responseCode}] with request headers [${requestHeaders}]")
            }
            log.trace "Slurped data at [${url.path}]: [${response}]"
        } catch (Exception ex) {
            log.error("Exception caught handling uri [${address}] with request headers [${requestHeaders}]: [${ex.message}]")
            return EMPTY_STRING
        } finally {
            conn?.disconnect()
        }

        log.info "Retrieved data from [${address}] in [${System.currentTimeMillis() - startStopwatch} ms]"

        response
    }
}
