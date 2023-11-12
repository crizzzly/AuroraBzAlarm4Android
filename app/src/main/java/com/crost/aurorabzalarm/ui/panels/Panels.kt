package com.crost.aurorabzalarm.ui.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_BT_TITLE
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_BZ_TITLE
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_DENS_TITLE
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_SPEED_TITLE
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_TEMP_TITLE
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.PADDING_L
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.PADDING_S

const val COMPONENT_COUNT = 3


fun mapValueToRange(value: Float, fromMin: Float, fromMax: Float, toMin: Double, toMax: Double): Double {
    return ((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin)
}

val redIsh =  Color(0xFFD32F2F) to Color(0xFFFFCDD2)
val orangeIsh = Color(0xFFF57C00) to Color(0xFFFFE0B2)
val yellowIsh = Color(0xFFFFFF00) to Color(0xFFDDD791)
val greenIsh = Color(0xFF388E3C) to Color(0xFFC8E6C9)


@Composable
fun ShowAllPanels(
    bz: Double,
    bt: Double,
    speed: Double,
    density: Double,
    temp: Double
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING_S.dp, PADDING_L.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Row( // 1st Row
            modifier = Modifier
                .padding(PADDING_S.dp)
                .fillMaxWidth(),
//            verticalAlignment = Alignment,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            GaugePanel(ACE_BZ_TITLE, bz.toFloat())
            GaugePanel(text = ACE_BT_TITLE, value = bt.toFloat())

        }
        Row(
            modifier = Modifier
                .padding(PADDING_S.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.
        ) {
            GaugePanel(EPAM_SPEED_TITLE, speed.toFloat())
            GaugePanel(EPAM_DENS_TITLE, density.toFloat())
            GaugePanel(EPAM_TEMP_TITLE, temp.toFloat())
        }
    }
}


@Preview
@Composable
fun PreviewAllPanels(
    bz: Double = -15.6,
    bt: Double = 20.0,
    speed: Double = 476.4,
    density: Double = 10.0,
    temp: Double = 8.64e+05
){
    ShowAllPanels(
        bz,
        bt,
        speed,
        density,
        temp,
    )
}


@Composable
fun BzPanel(
    progress: Float = -17.0f,
    modifier:  Modifier
){

    val valueRangeFrom= -100f
    val valueRangeTo = 100f
    val (mainColor, secondaryColor) = when {
        progress < -15 -> // Red
            redIsh  //Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in -15f..-11f -> // Orange
            orangeIsh //Color(0xFFF57C00) to Color(0xFFFFE0B2)
        progress in -10f .. 0f -> // yellow
            yellowIsh  //Color(0xFFFFFF00) to Color(0xFFFFF78E)
        else -> // Green
            greenIsh  //Color(0xFF388E3C) to Color(0xFFC8E6C9)
    }

//    val startAngleDrawArc = 270f
//    val EndAngleDrawArc = (degreesMarkerStep * mappedProgress-135).toFloat(),
    val drawProgressArcFromTop = true
    val unit = "nT"
    Gauge(
        progress = progress,
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
    )
}

@Composable
fun BtPanel(
    progress: Float = 17.0f,
    modifier:  Modifier
){
    val valueRangeFrom= 0f
    val valueRangeTo = 70f
    val (mainColor, secondaryColor) = when {
        progress < 10 -> // Red
            greenIsh
        progress in 10f..19f -> // Orange
            yellowIsh
        progress in 20f .. 29f ->
            orangeIsh
        else -> // Green
            redIsh
    }

//    val startAngleDrawArc = 270f
//    val EndAngleDrawArc = (degreesMarkerStep * mappedProgress-135).toFloat(),
    val drawProgressArcFromTop = false
    val unit = "nT"
    Gauge(
        progress = progress,
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
    )
}


@Composable
fun SpeedPanel(
    progress: Float = 400.0f,
    modifier:  Modifier
){
    val valueRangeFrom = 100.0f
    val valueRangeTo = 999.9f
    val (mainColor, secondaryColor) = when {
        progress > 700 -> // Red
            redIsh
        progress in 500f..700f -> // Orange
            orangeIsh
        progress in 350f .. 499f ->
            yellowIsh
        else -> // Green
            greenIsh
    }
    val drawProgressArcFromTop = false
    val unit = "km/s"
    Gauge(
        progress,
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
    )
}


@Composable
fun DensityPanel(
    progress: Float = 4.0f,
    modifier:  Modifier
){
    val valueRangeFrom = 0.0f
    val valueRangeTo = 30.0f
    val (mainColor, secondaryColor) = when {
        progress > 15 -> // Red
            redIsh
        progress in 10f..15f -> // Orange
            orangeIsh
        progress in 5f .. 10f ->
            yellowIsh
        else -> // Green
            greenIsh
    }
    val drawProgressArcFromTop = false
    val unit = "p/cc"
    Gauge(
        progress,
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
    )
}



@Composable
fun TempPanel(
    progress: Float,
    modifier:  Modifier
){

    val valueRangeFrom = 0.0f
    val valueRangeTo = 1e+06f
    val (mainColor, secondaryColor) = when {
        progress < 5.0e+05 -> // Red
            greenIsh
        progress in 5.0e+05..0.9e+06 -> // Orange
            orangeIsh
        else -> // Green
            redIsh
    }
    val drawProgressArcFromTop = false
    val unit = "°K"
    Gauge(
        progress = progress,
        valueRangeFrom = valueRangeFrom,
        valueRangeTo = valueRangeTo,
        mainColor = mainColor,
        secondaryColor = secondaryColor,
        drawProgressArcFromTop = drawProgressArcFromTop,
        unit = unit,
        modifier = modifier
    )
}

