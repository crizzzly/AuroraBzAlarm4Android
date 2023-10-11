package com.crost.aurorabzalarm.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.data.SpaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Stable
interface CurrentSpaceWeatherState {
    val weatherData: Map<String, Any>

    fun getAsPrintableString(): String{
        var string = ""
        val iter = weatherData.iterator()

        iter.forEach {entry: Map.Entry<String, Any> ->
            string += "${entry.key}: ${entry.value}\n"
        }

        return string
    }

    operator fun get(s: String): Any? {
        return weatherData[s]

    }
}

private class MutableCurrentSpaceWeatherState : CurrentSpaceWeatherState {
    override var weatherData: MutableMap<String, Any> by mutableMapOf<String, Any>()
}

data class SpaceWeatherState(
    override val weatherData: Map<String, Any>,
) : CurrentSpaceWeatherState


class DataViewModel : ViewModel() {
    private val _currentSpaceWeatherStateFlow = MutableStateFlow(MutableCurrentSpaceWeatherState())
//    val currentSpaceWeatherState: StateFlow<CurrentSpaceWeatherState> = _currentSpaceWeatherStateFlow

    val currentSpaceWeatherLiveData: LiveData<CurrentSpaceWeatherState>  =
        _currentSpaceWeatherStateFlow.asLiveData(Dispatchers.IO)

    private val spaceWeatherRepository = SpaceWeatherRepository()


    fun fetchData(){
//        Log.d("dvm fetchData viewModel", this.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newData = spaceWeatherRepository.fetchData()
                Log.e("DataViewModel fetchData key-val", "newData: ${newData.weatherData.keys}, ${newData.weatherData.values}")
                updateData(newData)

            } catch (e: Exception){
                Log.e("DataViewModel fetchData", e.stackTraceToString())
            }
        }
    }

    private fun updateData(newData: CurrentSpaceWeatherState){
        Log.d("Dvm - updateData", newData.getAsPrintableString())
        _currentSpaceWeatherStateFlow.value.weatherData = newData.weatherData as MutableMap<String, Any>

        Log.d("Dvm - currentSpaceWeatherLiveData.value ", currentSpaceWeatherLiveData.value.toString())
    }
}

object ViewModelFactory {
    private lateinit var dataViewModel: DataViewModel

    fun init(application: Application) {
        dataViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(DataViewModel::class.java)
        Log.e("ViewModelFactory", "init")
    }

    fun getDataViewModel(): DataViewModel {
        return dataViewModel
    }
}



