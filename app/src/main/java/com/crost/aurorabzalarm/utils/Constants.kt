package com.crost.aurorabzalarm.utils


object Constants {
    // TODO: Move to packages
    const val DB_NAME = "SpaceWeatherData"
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
    const val EPAM_TABLE_NAME = "ace_epam"
    const val EPAM_URL = "https://services.swpc.noaa.gov/text/ace-swepam.txt"
    const val EPAM_KEYS = "YR MO DA Time JulianDay SecOfDay S ProtonDensity BulkSpeed IonTemperature"

    // table
    const val EPAM_COL_DT = "datetime_epam"
    const val EPAM_COL_DENSITY = "protonDensity-p/cc"
    const val EPAM_COL_SPEED = "bulkSpeed-km/s"
    const val EPAM_COL_TEMP = "ionTemperature-K"

    const val ALERTS_PSEUDO_TABLE_NAME = "alerts"


    // worker repeat interval in sec
    const val WORKER_REPEAT_INTERVAL = 300L*5L

    // Notification Service
    const val CHANNEL_ID = "aurora_notification"

    // retries for time-consuming functions
    const val MAX_RETRY_COUNT = 3
    const val RETRY_DELAY_MS = 200L

    // SatelliteDataDownloader
    const val FILEPATH_ACE_DATA = "spaceDataDocuments/ace.txt"
    const val FILEPATH_HP_DATA = "spaceDataDocuments/hp.txt"
    const val FILEPATH_EPAM_DATA = "spaceDataDocuments/epam.txt"

    // GaugeCardTitles
    const val ACE_BZ_TITLE = "ACE\nBz"
    const val HP_TITLE = "Hemispheric Power"
    const val EPAM_SPEED_TITLE = "ACE EPAM\nSpeed"
    const val EPAM_DENS_TITLE = "ACE EPAM\nDensity"
    const val EPAM_TEMP_TITLE = "ACE EPAM\nTemperature"

    // Padding Values
    const val PADDING_XS = 4
    const val PADDING_S = 8
    const val PADDING_M = 16
    const val PADDING_L = 24
}
