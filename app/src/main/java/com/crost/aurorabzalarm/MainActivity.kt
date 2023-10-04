package com.crost.aurorabzalarm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crost.aurorabzalarm.ui.theme.AuroraBzAlarmTheme
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionManager: PermissionManager
    var showContent = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("MainActivity", "All permissions granted, displaying ImageGallery")
                // All permissions are granted, proceed with your logic
                // TODO: Start TimedWorkerThread
                showContent = true
            } else {
                Log.d("MainActivity", "Some or all permissions denied, showing WelcomeScreen")
                // Some or all permissions are denied, handle the case where the permission is required
            }
        }
        permissionManager = PermissionManager()

        val repeatInterval = 1L // 15 minutes
        val timeUnit = TimeUnit.MINUTES

        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(
            repeatInterval, timeUnit
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "fetchDataWork",
            ExistingPeriodicWorkPolicy.UPDATE, // Choose the appropriate policy
            workRequest
        )



        setContent {
            AuroraBzAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable(permissionManager, permissionLauncher)
                }
            }
        }
    }
}

@Composable
fun Values(spaceWeatherData: CurrentSpaceWeatherData, modifier: Modifier = Modifier){
    Text(
        text = "Bz Value:\t ${spaceWeatherData.bzVal}\n" +
                "Hemispheric Power: ${spaceWeatherData.hemisphericPower} GW",
        modifier = modifier,
    )
}

@Composable
fun MainComposable(permissionManager: PermissionManager, permissionLauncher: ActivityResultLauncher<Array<String>>) {
    val permissionState by permissionManager.permissionState.collectAsState()
    val context = LocalContext.current

    Values(spaceWeatherData = CurrentSpaceWeatherData())

    Log.d("setContent - Surface", "checking permission")
    if (! permissionState) {
        if (permissionManager.hasPermission(context)) {
            permissionManager.onPermissionGranted()
            val aUrl = stringResource(R.string.aceValsUrl)
            //readUrlPages(aUrl)
            Text(text = "Reading Webpages")
        } else {
            Text(text = "No Permissions")
            permissionManager.requestPermission(context, permissionLauncher)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ValuesPreview() {
    AuroraBzAlarmTheme {
        Values(CurrentSpaceWeatherData())
    }
}