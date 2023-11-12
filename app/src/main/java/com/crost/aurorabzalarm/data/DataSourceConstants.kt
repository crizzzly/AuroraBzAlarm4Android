package com.crost.aurorabzalarm.data

object DataSourceConstants{
    const val HP_URL = "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"
    //const val EPAM_URL = "https://services.swpc.noaa.gov/text/ace-swepam.txt"
    const val EPAM_JSON_DAILY = "https://services.swpc.noaa.gov/products/solar-wind/mag-1-day.json"
    const val EPAM_JSON_5MINUTELY = "https://services.swpc.noaa.gov/products/solar-wind/mag-5-minute.json"
    const val ACE_URL = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    const val ACE_JSON_5MINUTELY = "https://services.swpc.noaa.gov/products/solar-wind/plasma-5-minute.json"
    const val ALERTS_URL = "https://services.swpc.noaa.gov/products/alerts.json"
    const val ALERTS_JSON = "https://services.swpc.noaa.gov/products/alerts.json"
    const val K_INDEX_JSON = "https://services.swpc.noaa.gov/products/noaa-planetary-k-index.json"
}