package com.crost.aurorabzalarm.ui.elements

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.DataViewModel
import kotlinx.coroutines.launch

fun mapValueToRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return ((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin)
}
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SpeedometerScreen( ) {
    val viewModel: DataViewModel = viewModel()
//    var targetValue = viewModel.currentSpaceWeatherLiveData.observeAsState().value?.bzVal!!
    var targetValue by remember {
        mutableStateOf(0f)
    }
    val progress = remember(targetValue) { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(16.dp)) {
        Slider(value = targetValue, onValueChange = { targetValue = it })
        val intValue = targetValue * 55
        Text(text = "${intValue.toInt()}")
        Button(onClick = {
            scope.launch {
                progress.animateTo(
                    targetValue = intValue,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutLinearInEasing,
                    )
                )
            }
        }) {
            Text(text = "Go")
        }
//        Speedometer(progress.value.toInt())
    }
}

@Preview
@Composable
fun PreviewBz(){
    val progress = -17f
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
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit
    )
}

@Preview
@Composable
fun PreviewHemisphericPower(){
    val progress = 25f
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
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit
        )
}


@Preview
@Composable
fun PreviewSpeed(){
    val progress = 400f
    val valueRangeFrom: Float = 0f
    val valueRangeTo: Float = 999.9f
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
        valueRangeFrom,
        valueRangeTo,
        mainColor,
        secondaryColor,
        drawProgressArcFromTop,
        unit
    )
}


@Composable
fun Speedometer(
    progress: Float,
    valueRangeFrom: Float = 0f,
    valueRangeTo: Float = 100f,
    mainColor: Color,
    secondaryColor: Color,
    drawProgressArcFromTop: Boolean,
    unit: String

) {
    val arcDegrees = 270
    val startArcAngle = 135f
    val startStepAngle = -45 //
    val numberOfMarkers = 68
    val degreesMarkerStep = arcDegrees / numberOfMarkers

//    val startAngleDrawArc = 270f
//    val EndAngleDrawArc = (degreesMarkerStep * mappedProgress-135).toFloat(),


    // valRanges: from 0 to 90 - -100 - 100
    val mappedProgress = mapValueToRange(progress, valueRangeFrom, valueRangeTo, 0f, 90f)

    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val string = "$progress $unit"


    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
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
//                    (degreesMarkerStep * mappedProgress -235).toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing the pointer circle
                drawCircle(mainColor, 50f, centerOffset)
                drawCircle(Color.White, 25f, centerOffset)
                drawCircle(Color.Black, 20f, centerOffset)

                // Drawing Line Markers
                for ((counter, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = 80f
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
                                .fixed((size.width* 1f / 2.5f).toInt(), (size.height* 1f /6f).toInt()),
                        style = androidx.compose.ui.text.TextStyle(
                            color = mainColor, //Color.LightGray,
                            fontSize = 50.sp,
                            textAlign = TextAlign.Center,
                        )
                    )
                val offsetT = Offset(w/2 - measuredText.size.width/2, h / 4f*3)
                drawRect(Color.DarkGray, size = measuredText.size.toSize(),topLeft =offsetT)
                drawText(measuredText, topLeft =offsetT)

//                this.runCatching {
//
////                    onDrawBehind {
//
////                    }
//                }

            }
        }
    )
}