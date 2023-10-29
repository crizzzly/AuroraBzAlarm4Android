package com.crost.aurorabzalarm

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.crost.aurorabzalarm.ui.MainComposable
import com.crost.aurorabzalarm.ui.theme.AuroraBzAlarmTheme
import com.crost.aurorabzalarm.utils.AuroraNotificationService
import com.crost.aurorabzalarm.utils.PermissionManager
import com.crost.aurorabzalarm.viewmodels.ViewModelFactory


class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionManager: PermissionManager
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelFactory.getDataViewModel()
//        Log.d("MainActivity viewModel", viewModel.toString())

        val bzState = viewModel.latestAceState.value

        permissionManager = PermissionManager()
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("MainActivity", "All permissions granted, displaying ImageGallery")
                // All permissions are granted, proceed with your logic
                // TODO: Start TimedWorkerThread
            } else {
                Log.d("MainActivity", "Some or all permissions denied, showing WelcomeScreen")
                // Some or all permissions are denied, handle the case where the permission is required
            }
        }

        val notificationService = AuroraNotificationService(this)

        setContent {
            AuroraBzAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (bzState!!.bz < 1.0){// && bzState.bz > -900){
                        Log.d("MainActivity", "showing Notification")
                        notificationService.showBasicNotification()
                    }
                    MainComposable(
                        viewModel,
                        permissionManager,
                        permissionLauncher
                    )
                }
            }
        }

    // TODO: setup foreground service:
    //  https://developer.android.com/guide/components/fg-service-types#special-use

    }
}
