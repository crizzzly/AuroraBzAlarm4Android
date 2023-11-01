package com.crost.aurorabzalarm.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    // TODO: Move all the file access stuff to FileManager (or sth)
    private val con = application.applicationContext

    private val _showSettings = MutableLiveData(true)
    val showSettings: LiveData<Boolean> get() = _showSettings

    private var _notificationEnabled = MutableLiveData(false)
    val notificationEnabled get(): LiveData<Boolean> = _notificationEnabled

    private val _hpSliderState = MutableLiveData(50f)
    val hpSliderState get(): LiveData<Float> = _hpSliderState

    private val _bzSliderState = MutableLiveData(0f)
    val bzSliderState get(): LiveData<Float> = _bzSliderState

    var settingsConfig: Settings = getSettingsConfig(con)

    fun saveConfigToFile(settings: Settings){
        saveSettingsConfig(con, settings)
    }

    fun setSettingsVisible(value: Boolean){
        _showSettings.value = value
        Log.d("notificationState", value.toString())
    }

    fun updateBzState(value: Float) {
        _bzSliderState.value = value
        Log.d("BzState", value.toString())
    }

    fun updateHpState(value: Float) {
        _hpSliderState.value = value
        Log.d("HpState", value.toString())
        Log.d("_hpSliderState", _hpSliderState.value.toString())
    }

    fun setNotificationState(value: Boolean){
        _notificationEnabled.value = value
        Log.d("settings visible", value.toString())
    }
}