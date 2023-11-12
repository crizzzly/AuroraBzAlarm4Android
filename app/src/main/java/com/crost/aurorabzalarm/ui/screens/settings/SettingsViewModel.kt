package com.crost.aurorabzalarm.ui.screens.settings

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val settings = loadSettingsConfig(application.applicationContext)

    private val _showSettings = MutableLiveData(false)
    val showSettings: LiveData<Boolean> get() = _showSettings



    private var _notificationEnabled = MutableLiveData(settings.notificationEnabled)
    val notificationEnabled get(): LiveData<Boolean> = _notificationEnabled

    private val _hpSliderState = MutableLiveData(settings.hpWarningLevel.currentValue)
    val hpSliderState get(): LiveData<Float> = _hpSliderState

    private val _bzSliderState = MutableLiveData(settings.bzWarningLevel.currentValue)
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
//        Log.d("BzState", value.toString())
//        Log.d("_bzSliderState", _bzSliderState.value.toString())
    }

    fun updateHpState(value: Float) {
        _hpSliderState.value = value
//        Log.d("HpState", value.toString())
//        Log.d("_hpSliderState", _hpSliderState.value.toString())
    }

    fun setNotificationState(value: Boolean) {
        _notificationEnabled.value = value
//        Log.d("settings visible", value.toString())
    }
}

