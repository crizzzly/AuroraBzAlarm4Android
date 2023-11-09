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
import com.crost.aurorabzalarm.utils.Constants.ACE_BZ_TITLE
import com.crost.aurorabzalarm.utils.Constants.EPAM_DENS_TITLE
import com.crost.aurorabzalarm.utils.Constants.EPAM_SPEED_TITLE
import com.crost.aurorabzalarm.utils.Constants.EPAM_TEMP_TITLE
import com.crost.aurorabzalarm.utils.Constants.HP_TITLE
import com.crost.aurorabzalarm.utils.Constants.PADDING_L
import com.crost.aurorabzalarm.utils.Constants.PADDING_S

const val COMPONENT_COUNT = 3


fun mapValueToRange(value: Float, fromMin: Float, fromMax: Float, toMin: Double, toMax: Double): Double {
    return ((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin)
}


@Composable
fun ShowAllPanels(
    bz: Float,
    hp: Float,
    speed: Float,
    density: Float,
    temp: Float
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
            GaugePanel(ACE_BZ_TITLE, bz)



            GaugePanel(HP_TITLE, hp)

        }
        Row(
            modifier = Modifier
                .padding(PADDING_S.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.
        ) {
            GaugePanel(EPAM_SPEED_TITLE, speed)
            GaugePanel(EPAM_DENS_TITLE, density)
            GaugePanel(EPAM_TEMP_TITLE, temp)
        }
    }
}




@Preview
@Composable
fun PreviewAllPanels(
    bz: Float = -15.6f,
    hp: Float = 35.0f,
    speed: Float = 476.4f,
    density: Float = 202.0f,
    temp: Float = 8.64e+05f
){
    ShowAllPanels(
        bz,
        hp,
        speed,
        density,
        temp,
    )
}

@Composable
fun BzChart(
    progress: Float = -17.0f,
    modifier:  Modifier
){

    val valueRangeFrom= -100f
    val valueRangeTo = 100f
    val (mainColor, secondaryColor) = when {
        progress < 0 -> // Red
            Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in 0f..70f -> // Orange
            Color(0xFFF57C00) to Color(0xFFFFE0B2)
        else -> // Green
            Color(0xFF388E3C) to Color(0xFFC8E6C9)
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
fun HpPanel(
    progress: Float = 25.0f,
    modifier:  Modifier
){
    val valueRangeFrom= 0f
    val valueRangeTo = 100f
    val (mainColor, secondaryColor) = when {
        progress < 50 -> // Red
            Color(0xFF388E3C) to Color(0xFFC8E6C9)
//                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in 50f..70f -> // Orange
            Color(0xFFF57C00) to Color(0xFFFFE0B2)
        else -> // Green
            Color(0xFFD32F2F) to Color(0xFFFFCDD2)
    }
    val drawProgressArcFromTop = false
    val unit = "GW"
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
fun SpeedPanel(
    progress: Float = 400.0f,
    modifier:  Modifier
){

    val valueRangeFrom = 0.0f
    val valueRangeTo = 999.9f
    val (mainColor, secondaryColor) = when {
        progress < 400 -> // Red
            Color(0xFF388E3C) to Color(0xFFC8E6C9)
//                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in 400f..600f -> // Orange
            Color(0xFFF57C00) to Color(0xFFFFE0B2)
        else -> // Green
            Color(0xFFD32F2F) to Color(0xFFFFCDD2)
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
    val valueRangeTo = 100.0f
    val (mainColor, secondaryColor) = when {
        progress < 80 -> // Red
            Color(0xFF388E3C) to Color(0xFFC8E6C9)
//                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in 80f..130f -> // Orange
            Color(0xFFF57C00) to Color(0xFFFFE0B2)
        else -> // Green
            Color(0xFFD32F2F) to Color(0xFFFFCDD2)
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
            Color(0xFF388E3C) to Color(0xFFC8E6C9)
//                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
        progress in 5.0e+05..0.9e+06 -> // Orange
            Color(0xFFF57C00) to Color(0xFFFFE0B2)
        else -> // Green
            Color(0xFFD32F2F) to Color(0xFFFFCDD2)
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

