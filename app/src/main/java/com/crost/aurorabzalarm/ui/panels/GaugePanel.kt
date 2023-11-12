package com.crost.aurorabzalarm.ui.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.crost.aurorabzalarm.utils.Constants
import java.math.RoundingMode
import java.text.DecimalFormat


@Preview
@Composable
fun PreviewPanel(){
    GaugePanel(Constants.EPAM_SPEED_TITLE, 600.0f)
}


@Composable
fun GaugePanel(text: String, value: Float){
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val panelWidth =( screenWidth / COMPONENT_COUNT) /3
    val panelHeight = panelWidth * 1.7

    // TODO: variable sizes depending on screenSize
    Card(
        colors = CardColors(Color.DarkGray, Color.LightGray, Color.DarkGray, Color.LightGray),
        modifier = Modifier
            .size(panelWidth.dp, panelHeight.dp)
//            .align(Alignment.Center))
            .border(
                BorderStroke(
                0.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Cyan, Color.DarkGray, Color.Red))),
                shape = RoundedCornerShape(10.dp)
            )

    ) {

        val previewModifier = Modifier
            .size(panelWidth.dp, panelWidth.dp)
            .padding(Constants.PADDING_S.dp, Constants.PADDING_S.dp)
            .aspectRatio(1f)
            .align(Alignment.CenterHorizontally)

        Text(
            text,
            textAlign = TextAlign.Center,
            lineHeight = 1.5.em,
            modifier = Modifier
                .padding( Constants.PADDING_S.dp)
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
            Constants.ACE_BZ_TITLE -> BzPanel(value, modifier = previewModifier)
            Constants.ACE_BT_TITLE -> BtPanel(value, modifier = previewModifier)
            Constants.EPAM_SPEED_TITLE -> SpeedPanel(value, modifier = previewModifier)
            Constants.EPAM_DENS_TITLE -> DensityPanel(value, modifier = previewModifier)
            Constants.EPAM_TEMP_TITLE -> TempPanel(value, modifier = previewModifier)

        }
    }
}




// TODO: Handle DisplayRotation
// TODO: set markers for values
@Composable
fun Gauge(
    progress: Float,
    valueRangeFrom: Float,
    valueRangeTo: Float,
    mainColor: Color,
    secondaryColor: Color,
    drawProgressArcFromTop: Boolean,
    unit: String,
    modifier: Modifier

) {
    val arcDegrees = 270
    val startArcAngle = 135f
    val startStepAngle = -45
    val numberOfMarkers = 68
    val degreesMarkerStep = arcDegrees / numberOfMarkers

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val cardWidth = screenWidth / COMPONENT_COUNT - (2 * Constants.PADDING_L)
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
                        style = TextStyle(
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