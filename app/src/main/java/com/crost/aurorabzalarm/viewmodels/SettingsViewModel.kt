package com.crost.aurorabzalarm.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crost.aurorabzalarm.ui.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel: ViewModel() {
    private val _settingsState = MutableLiveData(SettingsState(
        true, false, 0, 30
    ))
    val settingsState: LiveData<SettingsState> get() = _settingsState

    private var _visibilityState = MutableStateFlow(false)
    val visibilityState get(): StateFlow<Boolean> = _visibilityState

    private val _hpSliderState = MutableStateFlow(50f)
    val hpSliderState get(): StateFlow<Float> = _hpSliderState

    private val _bzSliderState = MutableStateFlow(0f)
    val bzSliderState get(): StateFlow<Float> = _bzSliderState



    fun setNotificationsState(value: Boolean){
        _settingsState.value!!.notificationsEnabled = value
        Log.d("notificationState", value.toString())
    }

    fun updateBzState(value: Float) {
        _settingsState.value!!.bzThreshold = value.toInt()
        Log.d("BzState", value.toString())
    }

    fun updateHpState(value: Float) {
        _settingsState.value!!.hpThreshold = value.toInt()
        Log.d("HpState", value.toString())
    }

    fun setSettingsVisible(value: Boolean){
        _visibilityState.value = value
        Log.d("settings visible", value.toString())
    }
}