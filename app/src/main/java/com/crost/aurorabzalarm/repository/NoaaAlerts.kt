package com.crost.aurorabzalarm.repository

// TODO: Implement NOAA Basic Warnings



object ReadoutKeys{
    const val ALERTS_KEYS = "id issue_datetime issue_datetime"
}

// NOAA Alerts
object NoaaAlerts {

    // https://services.swpc.noaa.gov/products/animations/geospace/
    const val KP4_ALERT_ID = "K04A"
    const val KP4_WARNING_ID = "K04W"

    const val KP5_ALERT_ID = "K05A"
    const val KP5_WARNING_ID = "K05W"

    const val KP6_ALERT_ID = "K06A"
    const val KP6_WARNING_ID = "K06W"

    const val KP7_ALERT_ID = "K07A"
    const val KP7_WARNING_ID = "K07W"

    const val KP8_ALERT_ID = "K08A"
    const val KP8_WARNING_ID = "K08W"

    const val KP9_ALERT_ID = "K09A"
    const val KP9_WARNING_ID = "K09W"

    const val G5_ALERT_ID = "A60F"
    const val G4_ALERT_ID = "A50F"
    const val G3_ALERT_ID = "A40F"
    const val G2_ALERT_ID = "A30F"
    const val G1_ALERT_ID = "A20F"

    const val GEOMAGNETIC_SUDDEN_IMPULSE_EXPECTED = "SGIW"
    const val GEOMAGNETIC_SUDDEN_IMPULSE_OBSERVED = "MSIS"

    val GEO_STORM_ALERT_IDs = listOf(
        G5_ALERT_ID, G4_ALERT_ID, G3_ALERT_ID, G2_ALERT_ID
    )

    val GEO_IMPULSE_IDs = listOf(
        GEOMAGNETIC_SUDDEN_IMPULSE_OBSERVED,
        GEOMAGNETIC_SUDDEN_IMPULSE_EXPECTED
    )


    val KP_ALERT_IDs = listOf(
        KP9_ALERT_ID, KP8_ALERT_ID, KP7_ALERT_ID,
        KP6_ALERT_ID, KP5_ALERT_ID, KP4_ALERT_ID
    )

    val KP_WARNING_IDs = listOf(
        KP9_WARNING_ID, KP8_WARNING_ID, KP7_WARNING_ID,
        KP6_WARNING_ID, KP5_WARNING_ID, KP4_WARNING_ID
        )
}
