package com.crost.aurorabzalarm.utils.constants


object SpaceWeatherDataConstants {
    // TODO: Move to packages
    const val DB_NAME = "SpaceWeatherData"
    const val ACE_TABLE_NAME = "ace_magnetometer"
    const val ACE_URL = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"



    // Hemispheric Power
    const val HP_TABLE_NAME = "hemispheric_power"
    const val HP_URL = "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"



    // Ace SWEPAM
    const val EPAM_TABLE_NAME = "ace_epam"
    const val EPAM_URL = "https://services.swpc.noaa.gov/text/ace-swepam.txt"



    const val ALERTS_PSEUDO_TABLE_NAME = "alerts"


    // worker repeat interval in sec
    const val WORKER_REPEAT_INTERVAL = (300L*5L)

    // Notification Service
    const val CHANNEL_ID = "aurora_notification"
    const val CHANNEL_ID_NOAA = "noaa_alerts"

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
    const val ACE_BT_TITLE = "ACE\nBt"

    // Padding Values
    const val PADDING_XS = 4
    const val PADDING_S = 8
    const val PADDING_M = 16
    const val PADDING_L = 24
}
