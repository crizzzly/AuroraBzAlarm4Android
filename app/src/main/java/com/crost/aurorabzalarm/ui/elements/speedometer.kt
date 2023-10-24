package com.crost.aurorabzalarm.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.crost.aurorabzalarm.R

const val COMPONENT_COUNT = 3

fun mapValueToRange(value: Double, fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double {
    return ((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin)
}



@Preview
@Composable
fun PreviewAllCharts(
    bz: Double = -15.6,
    hp: Double = 35.0,
    speed: Double = 476.4,
    density: Double = 202.0,
    temp: Double = 123.0
){
    ShowAllCharts(
        bz,
        hp,
        speed,
        density,
        temp,
    )
}


@Composable
fun ShowAllCharts(
    bz: Double,
    hp:Double,
    speed:Double,
    density: Double,
    temp:Double
){
    val padding_s = dimensionResource(R.dimen.padding_small)
    val padding_m = dimensionResource(R.dimen.padding_middle)
    val padding_l = dimensionResource(R.dimen.padding_large)

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val canvasWidth =( screenWidth / COMPONENT_COUNT) /2.5
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreviewBz(
                progress = bz,
                modifier = Modifier
                    .size(canvasWidth.dp, canvasWidth.dp)
                    .padding(padding_s)
//                .align(BottomStart)
            )

            PreviewHemisphericPower(
                progress = hp,
                modifier = Modifier
                    .size(canvasWidth.dp, canvasWidth.dp)
                    .padding(padding_s)
//                .align(BottomCenter)
            )
        }
        Row {
            PreviewSpeed(
                progress = speed,
                modifier = Modifier
                    .size(canvasWidth.dp, canvasWidth.dp)
                    .padding(padding_s)
//                .align(BottomEnd)
            )
        }
    }
}

@Composable
fun PreviewBz(
    progress: Double  = -17.0,
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
    Speedometer(
        progress = progress,
        valueRangeFrom.toDouble(),
        valueRangeTo.toDouble(),
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
    )
}

@Composable
fun PreviewHemisphericPower(
    progress:Double = 25.0,
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
    Speedometer(
        progress,
        valueRangeFrom.toDouble(),
        valueRangeTo.toDouble(),
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit,
        modifier
        )
}


@Composable
fun PreviewSpeed(
    progress: Double = 400.0,
    modifier:  Modifier
){

    val valueRangeFrom: Double = 0.0
    val valueRangeTo: Double = 999.9
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
    val unit = "km/s"
    Speedometer(
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
fun Speedometer(
    progress: Double,
    valueRangeFrom: Double = 0.0,
    valueRangeTo: Double = 100.0,
    mainColor: Color,
    secondaryColor: Color,
    drawProgressArcFromTop: Boolean,
    unit: String,
    modifier:  Modifier

) {
    val arcDegrees = 270
    val startArcAngle = 135f
    val startStepAngle = -45 //
    val numberOfMarkers = 68
    val degreesMarkerStep = arcDegrees / numberOfMarkers

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val canvasWidth = screenWidth / COMPONENT_COUNT

    // valRanges: from 0 to 90 - -100 - 100
    val mappedProgress = mapValueToRange(progress, valueRangeFrom, valueRangeTo, 0.0, 90.0)

    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val string = "$progress $unit"


    Canvas(
        modifier = modifier.size(canvasWidth.dp, canvasWidth.dp),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val centerOffset = Offset(w / 2f, h / 2f)
                val quarterOffset = Offset(w / 4f, h / 4f)

                // Drawing Center Arc background
//                val (mainColor, secondaryColor) = when {
//                    progress < 50 -> // Red
//                        Color(0xFF388E3C) to Color(0xFFC8E6C9)
////                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
//                    progress in 50..70 -> // Orange
//                        Color(0xFFF57C00) to Color(0xFFFFE0B2)
//                    else -> // Green
//                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
//                }
                val paint = Paint().apply {
                    color = mainColor
                }
                val centerArcSize = Size(w / 2f, h / 2f)
                val centerArcStroke = Stroke(20f, 0f, StrokeCap.Butt)
                drawArc(
                    secondaryColor,
                    startArcAngle,
                    arcDegrees.toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing Right Half Center Arc mappedProgress
                val angle2: Float
                val angle1: Float
                    if (drawProgressArcFromTop){
                        angle1 = 270f
                        angle2 = (degreesMarkerStep * mappedProgress -135f).toFloat()
                } else {
                        angle1 = startArcAngle
                        angle2 = (degreesMarkerStep * mappedProgress).toFloat()
                }
                drawArc(
                    mainColor,
                    angle1, //270f
                    angle2,
//                    (degreesMarkerStep * mappedProgress -235).toDouble(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing the pointer circle
                drawCircle(mainColor, 30f, centerOffset)
                drawCircle(Color.White, 20f, centerOffset)
                drawCircle(Color.Black, 15f, centerOffset)

                // Drawing Line Markers
                for ((counter, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = 35f
                    paint.color = Color.Cyan
                    val lineStartX = if (counter % 5 == 0) {
                        paint.strokeWidth = 3f
                        0f
                    } else {
                        paint.strokeWidth = 1f
                        lineEndX * .2f
                    }
                    canvas.save()
                    canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
                    canvas.drawLine(
                        Offset(lineStartX, h / 2f),
                        Offset(lineEndX, h / 2f),
                        paint
                    )
                    // Drawing Pointer
                    if (counter == mappedProgress.toInt()) {
                        paint.color = Color.Red
                        canvas.drawPath(
                            Path().apply {
                                moveTo(w / 2, (h / 2) - 10)
                                lineTo(w / 2, (h / 2) + 10)
                                lineTo(w / 15f, h / 2)
                                lineTo(w / 2, (h / 2) - 10)
                                close()
                            },
                            paint
                        )
                    }
                    canvas.restore()
                }

                val offset = Offset(w / 2.6f, h / 4f*3)
                val offsetTxt = Offset(w / 2.6f, h / 4f*3+15)
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(string),
                            constraints = Constraints
                                .fixed((size.width* 1f / 3f).toInt(), (size.height* 1f /4f).toInt()),
                        style = androidx.compose.ui.text.TextStyle(
                            color = mainColor, //Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                        )
                    )
                val offsetT = Offset(w/2 - measuredText.size.width/2, h / 4f*3)
                drawRect(Color.DarkGray, size = measuredText.size.toSize(),topLeft =offsetT)
                drawText(measuredText, topLeft =offsetT)

            }
        }
    )
}