package com.crost.aurorabzalarm.ui.dataplots

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.crost.aurorabzalarm.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


var src = "https://services.swpc.noaa.gov/images/ace-mag-swepam-24-hour.gif"
private const val DEBUG = false


@Composable
fun AceDataPlot() {
    Image(
        painter = rememberAsyncImagePainter(src),
        contentDescription = null,
        modifier = Modifier.size(400.dp)
    )
}


@Composable
fun WsaEnlilPlot(urlList: List<String>) {
    val imageUrls by remember { mutableStateOf(urlList) }

    var alpha = 1f

    var painters = listOf<AsyncImagePainter>()
    val loadedPainters = mutableListOf<AsyncImagePainter>()
    for (url in imageUrls) {
        val painter = rememberAsyncImagePainter(model = url, onState = { state ->
            alpha = if (state is AsyncImagePainter.State.Success) 1f else 0.5f
        })

        loadedPainters.add(painter)
    }

    painters = loadedPainters
    Enlil(imagePainters = painters, alphaVal = alpha)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Enlil(
    imagePainters: List<AsyncImagePainter>,
    alphaVal: Float
){
    val painters = remember {
        mutableStateOf(imagePainters)
    }
    var currentImageIndex by remember{ mutableStateOf(0) }
    val alpha = remember { mutableStateOf(alphaVal) }
    var playAnimation by remember { mutableStateOf(true) }
    val listSize = painters.value.size
    Image(
        painter = painters.value[currentImageIndex],
        contentDescription = null,
        modifier = Modifier
            .size(400.dp)
            .alpha(alpha.value))
    Row {
        val sliderState = remember {
            SliderState(
                valueRange = 0f .. (listSize-1).toFloat(),
                steps = listSize,
                initialValue = currentImageIndex.toFloat(),
                initialOnValueChange = {
                    if (playAnimation){
                        playAnimation = false
                    }
                    currentImageIndex = it.toInt()

                }
            )
        }
        sliderState.value = currentImageIndex.toFloat()

        val icon = if (playAnimation) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play_buttton)
        FloatingActionButton(
            onClick = { playAnimation = !playAnimation }
        ) {
            Icon(
                painter = icon,
                contentDescription = "Stop/play Animation",
                modifier = Modifier.size(16.dp)
            )
        }
        Slider(state = sliderState)
    }
    LaunchedEffect(currentImageIndex, playAnimation) {
        this.launch {
            while (playAnimation) {
//                Log.d("WsaEnlil", "index: $currentImageIndex")
                delay(70) // Adjust the delay between images as needed
                currentImageIndex = (currentImageIndex + 1) % listSize
            }
        }
    }
}