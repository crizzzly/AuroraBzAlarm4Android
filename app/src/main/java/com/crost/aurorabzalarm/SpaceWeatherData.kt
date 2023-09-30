package com.crost.aurorabzalarm

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

class SpaceWeatherData {
    val bzVal by mutableStateOf(-15.0)
    val hemisphericPower by mutableStateOf(54.0)

}