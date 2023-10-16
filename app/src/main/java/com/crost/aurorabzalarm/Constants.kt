package com.crost.aurorabzalarm


object Constants {
    const val DB_NAME = "SpaceWeatherDb"
    const val ACE_TABLE_NAME = "ace_magnetometer"
    const val ACE_URL = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    const val ACE_KEYS = "YR MO DA Time JulianDay SecOfDay S Bx By Bz Bt Lat Long"

    const val HP_TABLE_NAME = "hemispheric_power"
    const val HP_URL = "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"
    const val HP_KEYS = "Observation Forecast HPNorth HPSouth"

    const val ACE_COL_DT = "datetime_ace"
    const val ACE_COL_BX = "bx"
    const val ACE_COL_BY = "by"
    const val ACE_COL_BZ = "bz"
    const val ACE_COL_BT = "bt"

    const val HP_COL_DT = "datetime_hp"
    const val HP_COL_HPN = "hpNorth"
    const val HP_COL_HPS = "hpSouth"
}
