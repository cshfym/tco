package com.tcoproject.server.models.external.kbb

class KBBVehicleWrapper {

    String id
    String taggingprice    // Format: 39720^42715 = Suggested^Retail
    String primarycategory // i.e. Convertible
    String name
    String trimdisplayname
    String price
    String defaultprice   // MSRP i.e. 42715
    String mpgcity
    String mpghwy
    String mpgrangelow
    String mpgrangehigh
    String chromeid

}
