package com.crost.aurorabzalarm.utils.constants


object ReadoutKeys{
    const val ALERTS_KEYS = "id issue_datetime issue_datetime"
}

// NOAA Alerts
object NoaaAlertConstants {


    const val MAX_MINUTES_BETWEEN_ALERT_AND_NOW = 20

    // https://services.swpc.noaa.gov/products/animations/geospace/
    private const val KP4_ALERT_ID = "K04A"
    private const val KP4_WARNING_ID = "K04W"

    private const val KP5_ALERT_ID = "K05A"
    private const val KP5_WARNING_ID = "K05W"

    private const val KP6_ALERT_ID = "K06A"
    private const val KP6_WARNING_ID = "K06W"

    private const val KP7_ALERT_ID = "K07A"
    private const val KP7_WARNING_ID = "K07W"

    private const val KP8_ALERT_ID = "K08A"
    private const val KP8_WARNING_ID = "K08W"

    private const val KP9_ALERT_ID = "K09A"
    private const val KP9_WARNING_ID = "K09W"

    private const val G5_ALERT_ID = "A60F"
    private const val G4_ALERT_ID = "A50F"
    private const val G3_ALERT_ID = "A40F"
    private const val G2_ALERT_ID = "A30F"
    private const val G1_ALERT_ID = "A20F"

    private const val GEOMAGNETIC_SUDDEN_IMPULSE_EXPECTED = "SGIW"
    private const val GEOMAGNETIC_SUDDEN_IMPULSE_OBSERVED = "MSIS"

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
