package com.crost.aurorabzalarm.ui.panels

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SpeedometerOriginalScreen( ) {
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
        SpeedometerOriginal(progress.value.toInt())
    }
}

@Preview
@Composable
fun PreviewSpeedometerOriginal(){
    SpeedometerOriginal(50)
}


@Composable
fun SpeedometerOriginal(
    progress: Int,
) {
    val arcDegrees = 275
    val startArcAngle = 135f
    val startStepAngle = -45 //
    val numberOfMarkers = 98// 55
    val degreesMarkerStep = arcDegrees / numberOfMarkers

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
                val (mainColor, secondaryColor) = when {
                    progress < 20 -> // Red
                        Color(0xFFD32F2F) to Color(0xFFFFCDD2)
                    progress < 40 -> // Orange
                        Color(0xFFF57C00) to Color(0xFFFFE0B2)
                    else -> // Green
                        Color(0xFF388E3C) to Color(0xFFC8E6C9)
                }
                val paint = Paint().apply {
                    color = mainColor
                }
                val centerArcSize = Size(w / 2f, h / 2f)
                val centerArcStroke = Stroke(20f, 0f, StrokeCap.Round)
                drawArc(
                    secondaryColor,
                    startArcAngle,
                    arcDegrees.toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing Center Arc progress
                drawArc(
                    mainColor,
                    startArcAngle,
                    (degreesMarkerStep * progress).toFloat(),
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
                    paint.color = Color.Blue
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
                    if (counter == progress) {
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
            }
        }
    )
}