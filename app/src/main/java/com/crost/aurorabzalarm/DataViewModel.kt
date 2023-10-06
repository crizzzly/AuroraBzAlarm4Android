package com.crost.aurorabzalarm

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@Stable
interface CurrentSpaceWeatherState {
    val bzVal: Float?
    val hpVal: Float?
}

private class MutableCurrentSpaceWeatherState : CurrentSpaceWeatherState {
    override var bzVal: Float? by mutableStateOf(-15f)
    override var hpVal: Float? by mutableStateOf(35f)
}


class DataViewModel() : ViewModel() {
    private val _currentSpaceWeather = MutableStateFlow(MutableCurrentSpaceWeatherState())
    val currentSpaceWeather: StateFlow<CurrentSpaceWeatherState> get() = _currentSpaceWeather
    val uniqueId: String = UUID.randomUUID().toString()

    // updated by SatelliteDataParser
    fun updateSpaceWeatherState(newSpaceWeatherState: List<Float>) {
        _currentSpaceWeather.value.bzVal = newSpaceWeatherState[0]
        _currentSpaceWeather.value.hpVal = newSpaceWeatherState[1]
    }

    companion object {
        fun provideFactory(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return DataViewModel() as T
                }
            }
    }
}

