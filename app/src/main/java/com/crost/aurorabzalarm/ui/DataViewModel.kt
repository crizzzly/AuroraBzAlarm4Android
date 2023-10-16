package com.crost.aurorabzalarm.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.repository.SpaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow


// TODO: database @ Some time: https://developer.android.com/reference/androidx/room/package-summary
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


// currentSpaceWeatherLiveData.value -> Object
class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentSpaceWeatherStateFlow = MutableStateFlow(MutableCurrentSpaceWeatherState())
//    val currentSpaceWeatherState: StateFlow<CurrentSpaceWeatherState> = _currentSpaceWeatherStateFlow

    val currentSpaceWeatherLiveData: LiveData<CurrentSpaceWeatherState>  =
        _currentSpaceWeatherStateFlow.asLiveData(Dispatchers.IO)

    private var  spaceWeatherRepository: SpaceWeatherRepository
    private lateinit var db: SpaceWeatherDataBase

    init {
        spaceWeatherRepository = SpaceWeatherRepository(application)
    }

    var outputText: String = ""
    // TODO: Create Color-Set
    var outputTextColor: Color = Color.LightGray


    fun fetchSpaceWeatherData(){
        Log.i("DataViewModel", "fetchSpaceWeatherData")

//        viewModelScope.launch(Dispatchers.IO) {
            try {
                 spaceWeatherRepository.fetchDataAndStoreInDatabase()
            } catch (e: Exception){
                Log.e("DataViewModel fetchData", e.stackTraceToString())
            }
//        }
    }


    private fun updateCurrentSpaceWeatherState(newData: CurrentSpaceWeatherState){
        Log.d("Dvm - updateCurrentSpaceWeatherState", "length: ${newData.weatherData.size} \n" +
                newData.getAsPrintableString()
        )
        _currentSpaceWeatherStateFlow.value.weatherData = newData.weatherData as MutableMap<String, Any>
        
        try {
            outputText = "CurrentDataState: \n" +
//                    "Bz Value:\t ${currentLiveData.value?.get("bzVal")}\n" + // does not update!
                    "Bz Value:\t ${currentSpaceWeatherLiveData.value?.weatherData?.get("bzVal")}\n" + // does not update!
                    "Hemispheric Power: ${currentSpaceWeatherLiveData.value?.get("hpVal")} GW"
        } catch (e: NoSuchElementException){
            outputText = "Error \nProbs with accessing WeatherDataState"
            Log.e("MainComposables - Values", e.stackTraceToString())
            outputTextColor = Color.Red
        }



                as MutableMap<String, Any>

        Log.d("Dvm - currentSpaceWeatherLiveData.value ", currentSpaceWeatherLiveData.value.toString())
    }
}

object ViewModelFactory {
    private lateinit var dataViewModel: DataViewModel

    fun init(application: Application) {
        Log.i("ViewModelFactory", "init")
        dataViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(DataViewModel::class.java)
    }

    fun getDataViewModel(): DataViewModel {
        return dataViewModel
    }
}



