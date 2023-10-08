package com.crost.aurorabzalarm.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val bzVal: Float?
    val hpVal: Float?

    fun getAsPrintableString(): String{
        return "Bz: $bzVal, HP: $hpVal"
    }
}

private class MutableCurrentSpaceWeatherState : CurrentSpaceWeatherState {
    override var bzVal: Float? by mutableStateOf(-15f)
    override var hpVal: Float? by mutableStateOf(35f)

}

data class SpaceWeatherState(
    override var bzVal: Float?,
    override var hpVal: Float?
) : CurrentSpaceWeatherState


class DataViewModel : ViewModel() {
    private val _currentSpaceWeatherStateFlow = MutableStateFlow(MutableCurrentSpaceWeatherState())
//    val currentSpaceWeatherState: StateFlow<CurrentSpaceWeatherState> = _currentSpaceWeatherStateFlow

    val currentSpaceWeatherLiveData: LiveData<CurrentSpaceWeatherState>  =
        _currentSpaceWeatherStateFlow.asLiveData(Dispatchers.IO)

    private val spaceWeatherRepository = SpaceWeatherRepository()


    fun fetchData(){
        Log.d("dvm fetchData viewModel", this.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newData = spaceWeatherRepository.fetchData()
                Log.e("DataViewModel fetchData", "newData: ${newData.bzVal}, ${newData.hpVal}")
                updateData(newData)

            } catch (e: Exception){
                Log.e("DataViewModel fetchData", e.stackTraceToString())
            }
        }
    }

    private fun updateData(newData: CurrentSpaceWeatherState){
        Log.d("Dvm - updateData", newData.getAsPrintableString())
        _currentSpaceWeatherStateFlow.value.hpVal = newData.hpVal
        _currentSpaceWeatherStateFlow.value.bzVal = newData.bzVal

        Log.d("Dvm - currentSpaceWeatherLiveData", currentSpaceWeatherLiveData.value?.bzVal.toString())
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



