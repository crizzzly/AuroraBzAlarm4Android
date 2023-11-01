package com.crost.aurorabzalarm.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.crost.aurorabzalarm.settings.SettingsViewModel

object AuroraViewModelFactory {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    fun init(application: Application) {
        Log.i("AuroraViewModelFactory", "init")
        dataViewModel =
            ViewModelProvider.AndroidViewModelFactory(application)
                .create(DataViewModel::class.java)
        settingsViewModel =
            ViewModelProvider.AndroidViewModelFactory(application)
                .create(SettingsViewModel::class.java)
    }

    fun getSettingsViewModel(): SettingsViewModel {
        return settingsViewModel
    }

    fun getDataViewModel(): DataViewModel {
        return dataViewModel
    }
}