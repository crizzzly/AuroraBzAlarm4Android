package com.crost.aurorabzalarm.settings

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crost.aurorabzalarm.utils.FileLogger

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val fileLogger = FileLogger.getInstance(application.applicationContext)
    private val settingsParser = SettingsParser(application.applicationContext)

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
        settingsParser.saveSettingsConfig(context, settings)
    }

    fun loadAndReturnConfig(context: Context): Settings {
        return settingsParser.loadSettingsConfig(context)
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

