package com.crost.aurorabzalarm.viewmodels

//
//object AuroraViewModelFactory {
//    private lateinit var dataViewModel: DataViewModel
//    private lateinit var settingsViewModel: SettingsViewModel
//
//    fun init(application: Application) {
//        Log.i("AuroraViewModelFactory", "init")
//        dataViewModel =
//            ViewModelProvider.AndroidViewModelFactory(application)
//                .create(DataViewModel::class.java)
//        settingsViewModel =
//            ViewModelProvider.AndroidViewModelFactory(application)
//                .create(SettingsViewModel::class.java)
//    }
//
//    fun getSettingsViewModel(): SettingsViewModel {
//        return settingsViewModel
//    }
//
//    fun getDataViewModel(): DataViewModel {
//        return dataViewModel
//    }
//}