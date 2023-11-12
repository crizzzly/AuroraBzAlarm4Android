package com.crost.aurorabzalarm

//import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory
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
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.PermissionManager


class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionManager: PermissionManager
    private lateinit var fileLogger: FileLogger


    private fun getPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("MainActivity", "All permissions granted, displaying ImageGallery")
//                fileLogger.writeLogsToInternalStorage(this, "MainActivity\nAll permissions granted, displaying ImageGallery")
                // All permissions are granted, proceed with your logic
                // TODO: Start TimedWorkerThread
            } else {
                Log.d("MainActivity", "Some or all permissions denied, showing WelcomeScreen")
                // Some or all permissions are denied, handle the case where the permission is required
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileLogger = FileLogger.getInstance(this)

        permissionManager = PermissionManager()
        permissionLauncher = getPermissionLauncher()

        setContent {
            AuroraBzAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable(
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
