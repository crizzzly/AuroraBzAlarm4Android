package com.crost.aurorabzalarm

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.data.SatelliteDataParser
import com.crost.aurorabzalarm.data.SpaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Stable
interface CurrentSpaceWeatherState {
    val bzVal: Float?
    val hpVal: Float?
}

private class MutableCurrentSpaceWeatherState : CurrentSpaceWeatherState {
    override var bzVal: Float? by mutableStateOf(-15f)
    override var hpVal: Float? by mutableStateOf(35f)
}

data class SpaceWeatherState(
    override var bzVal: Float?,
    override var hpVal: Float?
) : CurrentSpaceWeatherState


class DataViewModel() : ViewModel() {
    private val _currentSpaceWeatherStateFlow = MutableStateFlow(MutableCurrentSpaceWeatherState())
    val currentSpaceWeatherStateFlow: StateFlow<CurrentSpaceWeatherState> get() = _currentSpaceWeatherStateFlow
    private val parser = SatelliteDataParser()
    private val spaceWeatherRepository = SpaceWeatherRepository(parser)


    fun fetchData(){
        viewModelScope.launch(Dispatchers.IO) {
                Log.e("DataViewModel fetchData", "Launching dispatcher")
            try {
                val newData = spaceWeatherRepository.getSpaceWeatherData()
                Log.e("DataViewModel fetchData", "newData: ${newData.bzVal}, ${newData.hpVal}")
                _currentSpaceWeatherStateFlow.value.hpVal = newData.hpVal
                _currentSpaceWeatherStateFlow.value.bzVal = newData.bzVal
            } catch (e: Exception){
                Log.e("DataViewModel fetchData", e.stackTraceToString())
            }
        }
    }
}

object ViewModelFactory {
    private lateinit var dataViewModel: DataViewModel

    fun init(application: Application) {
        dataViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(DataViewModel::class.java)
    }

    fun getDataViewModel(): DataViewModel {
        return dataViewModel
    }
}



