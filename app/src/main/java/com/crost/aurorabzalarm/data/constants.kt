package com.crost.aurorabzalarm.data


object constants {
    const val ACE_TABLE_NAME = "ace_magnetometer"
    const val ACE_URL = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    const val ACE_KEYS = "YR MO DA Time JulianDay SecOfDay S Bx By Bz Bt Lat Long"

    const val HP_TABLE_NAME = "hemispheric_power"
    const val HP_URL = "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"
    const val HP_KEYS = "Observation Forecast HP-North HP-South"
}
