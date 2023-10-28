package com.crost.aurorabzalarm.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.crost.aurorabzalarm.Constants.ACE_BZ_TITLE
import com.crost.aurorabzalarm.Constants.EPAM_DENS_TITLE
import com.crost.aurorabzalarm.Constants.EPAM_SPEED_TITLE
import com.crost.aurorabzalarm.Constants.EPAM_TEMP_TITLE
import com.crost.aurorabzalarm.Constants.HP_TITLE
import com.crost.aurorabzalarm.Constants.PADDING_L
import com.crost.aurorabzalarm.Constants.PADDING_S
import java.math.RoundingMode
import java.text.DecimalFormat

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
    temp: Double = 8.64e+05
){
    ShowAllCharts(
        bz,
        hp,
        speed,
        density,
        temp,
    )
}

@Preview
@Composable
fun PreviewCard(){
    GaugeCard(EPAM_SPEED_TITLE, 600.0)
}


@Composable
fun GaugeCard(text: String, value: Double){
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val cardWidth =( screenWidth / COMPONENT_COUNT) /3
    val cardHeight = cardWidth * 1.7

    // TODO: variable sizes depending on screenSize
    Card(
        colors = CardColors(Color.DarkGray, Color.LightGray, Color.DarkGray, Color.LightGray),
        modifier = Modifier
            .size(cardWidth.dp, cardHeight.dp)
//            .align(Alignment.Center))
            .border(BorderStroke(
                0.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Cyan, Color.DarkGray, Color.Red))),
                shape = RoundedCornerShape(10.dp)
            )

    ) {

        val previewModifier = Modifier
            .size(cardWidth.dp, cardWidth.dp)
            .padding(PADDING_S.dp, PADDING_S.dp)
            .aspectRatio(1f)
            .align(Alignment.CenterHorizontally)

        Text(
            text,
            textAlign = TextAlign.Center,
            lineHeight = 1.5.em,
            modifier = Modifier
                .padding( PADDING_S.dp)
                .width(IntrinsicSize.Max)
                .align(Alignment.CenterHorizontally)
        )
        HorizontalDivider( // -> where r u?
            thickness = 3.dp,
            color = Color.Black,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(3.dp)
                .border(2.dp, Color.Black)

        )

        when (text){
            ACE_BZ_TITLE -> PreviewBz(value, modifier = previewModifier)
            HP_TITLE -> PreviewHp(value, modifier = previewModifier)
            EPAM_SPEED_TITLE -> PreviewSpeed(value, modifier = previewModifier)
            EPAM_DENS_TITLE -> PreviewDensity(value, modifier = previewModifier)
            EPAM_TEMP_TITLE -> PreviewTemp(value, modifier = previewModifier)

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
fun ShowAllCharts(
    bz: Double,
    hp:Double,
    speed:Double,
    density: Double,
    temp:Double
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
            GaugeCard(ACE_BZ_TITLE, bz)



            GaugeCard(HP_TITLE, hp)

        }
        Row(
            modifier = Modifier
                .padding(PADDING_S.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.
        ) {
            GaugeCard(EPAM_SPEED_TITLE, speed)
            GaugeCard(EPAM_DENS_TITLE, density)
            GaugeCard(EPAM_TEMP_TITLE, temp)
        }
    }
}




@Composable
fun PreviewHp(
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

    val valueRangeFrom = 0.0
    val valueRangeTo = 999.9
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
fun PreviewDensity(
    progress: Double = 400.0,
    modifier:  Modifier
){

    val valueRangeFrom = 0.0
    val valueRangeTo = 800.0
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
fun PreviewTemp(
    progress: Double,
    modifier:  Modifier
){

    val valueRangeFrom = 0.0
    val valueRangeTo = 1.5e+06
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
    Speedometer(
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



// TODO: Handle DisplayRotation
// TODO: set markers for values
@Composable
fun Speedometer(
    progress: Double,
    valueRangeFrom: Double,
    valueRangeTo: Double,
    mainColor: Color,
    secondaryColor: Color,
    drawProgressArcFromTop: Boolean,
    unit: String,
    modifier:  Modifier

) {
    val arcDegrees = 270
    val startArcAngle = 135f
    val startStepAngle = -45
    val numberOfMarkers = 68
    val degreesMarkerStep = arcDegrees / numberOfMarkers

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val cardWidth = screenWidth / COMPONENT_COUNT - (2 * PADDING_L)
//    val cardHeight = cardWidth * 1.7
    // valRanges: from 0 to 90 - -100 - 100
    val mappedProgress = mapValueToRange(progress, valueRangeFrom, valueRangeTo, 0.0, 90.0)

    val df: DecimalFormat =  if (valueRangeTo > 1.0e+04){
        DecimalFormat("#")
    } else{
        DecimalFormat("#.##")
    }
    df.roundingMode = RoundingMode.CEILING

    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val string = "${df.format(progress)}\n$unit"


    Canvas(
        modifier = modifier
            .size(cardWidth.dp, cardWidth.dp),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val centerOffset = Offset(w / 2f, h / 2f)
                val quarterOffset = Offset(w / 4f, h / 4f)


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
                        angle2 = (degreesMarkerStep * mappedProgress -136.5f).toFloat()
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
                    paint.color = Color.Black//(0xFF16181F)// 226, 26, 13
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
                                moveTo(w / 2, (h / 2) - 5)
                                lineTo(w / 2, (h / 2) + 5)
                                lineTo(w / 10f, h / 2)
                                lineTo(w / 2, (h / 2) - 5)
                                close()
                            },
                            paint
                        )
                    }
                    canvas.restore()
                }

                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(string),
                            constraints = Constraints
                                .fixed((size.width* 1f / 3f).toInt(), (size.height* 1f /4f).toInt()),
                        style = androidx.compose.ui.text.TextStyle(
                            color = mainColor, //Color.LightGray,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                        )
                    )
                val offsetT = Offset(w/2 - measuredText.size.width/2, (h / 4f*2.7).toFloat())
                drawRect(Color.DarkGray, size = measuredText.size.toSize(),topLeft =offsetT)
                drawText(measuredText, topLeft =offsetT)

            }
        }
    )
}