package com.crost.aurorabzalarm.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(): ViewModel() {

    private val _showSettings = MutableLiveData(true)
    val showSettings: LiveData<Boolean> get() = _showSettings



    private var _notificationEnabled = MutableLiveData(false)
    val notificationEnabled get(): LiveData<Boolean> = _notificationEnabled

    private val _hpSliderState = MutableLiveData(50f)
    val hpSliderState get(): LiveData<Float> = _hpSliderState

    private val _bzSliderState = MutableLiveData(0f)
    val bzSliderState get(): LiveData<Float> = _bzSliderState

//    var settingsConfig: Settings = getSettingsConfig(context)

    fun saveConfig(context: Context, settings: Settings) {
        saveSettingsConfig(context, settings)
    }

    fun loadAndReturnConfig(context: Context): Settings {
        return loadSettingsConfig(context)
    }

    fun setSettingsVisible(value: Boolean) {
        _showSettings.value = value
        Log.d("notificationState", value.toString())
    }

    fun updateBzState(value: Float) {
        _bzSliderState.value = value
        Log.d("BzState", value.toString())
        Log.d("_bzSliderState", _bzSliderState.value.toString())
    }

    fun updateHpState(value: Float) {
        _hpSliderState.value = value
        Log.d("HpState", value.toString())
        Log.d("_hpSliderState", _hpSliderState.value.toString())
    }

    fun setNotificationState(value: Boolean) {
        _notificationEnabled.value = value
        Log.d("settings visible", value.toString())
    }
}

