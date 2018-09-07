package com.tcoproject.server.config

import java.text.SimpleDateFormat

@Singleton
class AvailableYears {

    static final int MINIMUM_YEAR = 1936
    static final int MAXIMUM_YEAR = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.instance.time)) + 1

}
