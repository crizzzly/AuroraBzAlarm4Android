package com.crost.aurorabzalarm.ui.elements//package com.crost.aurorabzalarm.ui
//
//import android.util.Size
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.MaterialTheme.colors
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.dp
//import java.lang.reflect.Modifier
//import kotlin.math.min
//
//
//@Composable
//fun GaugeChart(){
//    val density = LocalDensity.current
//    val strokeWidth = with(density) { 10.dp.toPx() }
//    val gaugeDegrees = 180
//    val startAngle = 180f
//
//// brush with color stops, where each color can have custom proportion
//    val brush = Brush.horizontalGradient(
//        0.1f to colors.secondary,
//        0.2f to colors.primaryVariant,
//        0.5f to colors.primary,
//    )
//
//    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
//
//        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
//        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
//        val canvasSizeDp = with(density) { canvasSize.toDp() }
//
//        Canvas(
//            modifier = Modifier.size(canvasSizeDp),
//            onDraw = {
//
//                drawArc(
//                    brush = brush,
//                    startAngle = startAngle,
//                    sweepAngle = gaugeDegrees.toFloat(),
//                    useCenter = false,
//                    size = size,
//                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
//                )
//
//            }
//        )
//    }
//}