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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.theme.AuroraBzAlarmTheme


const val WEBPARSING_JOB_ID = 1

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

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
            } else {
                Log.d("MainActivity", "Some or all permissions denied, showing WelcomeScreen")
                // Some or all permissions are denied, handle the case where the permission is required
            }
        }

        setContent {
            AuroraBzAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable()
                }
            }
        }
    }
}


@Composable
fun Values(viewModel: DataViewModel = viewModel(), modifier: Modifier = Modifier){
    val currentData: CurrentSpaceWeatherState by viewModel.currentSpaceWeatherStateFlow.collectAsState()

    Text(
        text = "Bz Value:\t ${currentData.bzVal}\n" + // does not update!
                "Hemispheric Power: ${currentData.hpVal} GW",
        modifier = modifier,
    )
}


@Composable
fun MainComposable(
    viewModel: DataViewModel = viewModel(),
) {
    val context = LocalContext.current

    Values()

//    Log.d("setContent - Surface", "checking permission")
//    if (! permissionState) {
//        if (permissionManager.hasPermission(context)) {
//            permissionManager.onPermissionGranted()
//            val aUrl = stringResource(R.string.aceValsUrl)
//            //readUrlPages(aUrl)
//            Text(text = "Reading Webpages")
//        } else {
//            Text(text = "No Permissions")
//            permissionManager.requestPermission(context, permissionLauncher)
//        }
//    }
}


@Preview(showBackground = true)
@Composable
fun ValuesPreview() {
    AuroraBzAlarmTheme {
        Values()
    }
}