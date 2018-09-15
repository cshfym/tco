package com.tcoproject.server.services.common

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

abstract class AbstractExternalApiService {

    @Autowired
    HTTPConnectionService connectionService

    Random randomizer = new Random()

    static final int MAX_IP_ADDRESS_NODE = 255

    final String ACCEPT_HEADER_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
    final String CACHE_CONTROL_HEADER_VALUE = "max-age=0"
    final String USER_AGENT_HEADER_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"

    // Override request headers
    Map<String,String> requestHeaders = [
            "Accept": ACCEPT_HEADER_VALUE,
            "cache-control": CACHE_CONTROL_HEADER_VALUE,
            "user-agent": USER_AGENT_HEADER_VALUE
    ]

    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setLenient()
            .create()

    Gson kbbGson = new GsonBuilder()
            .setLenient()
            .create()

    String getRandomizedIpAddress() {
        new StringBuilder()
                .append(randomizer.nextInt(MAX_IP_ADDRESS_NODE + 1))
                .append(".")
                .append(randomizer.nextInt(MAX_IP_ADDRESS_NODE + 1))
                .append(".")
                .append(randomizer.nextInt(MAX_IP_ADDRESS_NODE + 1))
                .append(".")
                .append(randomizer.nextInt(MAX_IP_ADDRESS_NODE + 1))
                .toString()
    }

}
