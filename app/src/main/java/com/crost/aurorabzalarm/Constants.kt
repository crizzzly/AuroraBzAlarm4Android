package com.crost.aurorabzalarm


object Constants {
    const val DB_NAME = "SpaceWeatherDb"
    const val ACE_TABLE_NAME = "ace_magnetometer"
    const val ACE_URL = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    const val ACE_KEYS = "YR MO DA Time JulianDay SecOfDay S Bx By Bz Bt Lat Long"

    // table columns
    const val ACE_COL_DT = "datetime_ace"
    const val ACE_COL_BX = "bx-nT"
    const val ACE_COL_BY = "by-nT"
    const val ACE_COL_BZ = "bz-nT"
    const val ACE_COL_BT = "bt-nT"

    // Hemispheric Power
    const val HP_TABLE_NAME = "hemispheric_power"
    const val HP_URL = "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"
    const val HP_KEYS = "Observation Forecast HPNorth HPSouth"

    // table
    const val HP_COL_DT = "datetime_hp"
    const val HP_COL_HPN = "hpNorth-GW"
    const val HP_COL_HPS = "hpSouth-GW"

    // Ace SWEPAM
    const val EPAM_TABLE_NAME = "ace_swepam"
    const val EPAM_URL = "https://services.swpc.noaa.gov/text/ace-swepam.txt"
    const val EPAM_KEYS = "YR MO DA Time JulianDay SecOfDay S ProtonDensity BulkSpeed IonTemperature"

    // table
    const val EPAM_COL_DT = "datetime_epam"
    const val EPAM_COL_DENSITY = "protonDensity-p/cc"
    const val EPAM_COL_SPEED = "bulkSpeed-km/s"
    const val EPAM_COL_TEMP = "ionTemperature-K"


    // download worker time in sec
    const val WORKER_REPEAT_INTERVAL = 60L

    // retries for time-consuming functions
    const val MAX_RETRY_COUNT = 3
    const val RETRY_DELAY_MS = 300

    // SatelliteDataDownloader
    const val FILEPATH_ACE_DATA = "spaceDataDocuments/ace.txt"
    const val FILEPATH_HP_DATA = "spaceDataDocuments/hp.txt"

    // GaugeCard
    const val ACE_BZ_TITLE = "ACE Magnetometer\nBz"
}
