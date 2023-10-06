package com.crost.aurorabzalarm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.data.SatelliteDataParser
import com.crost.aurorabzalarm.ui.theme.AuroraBzAlarmTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val WEBPARSING_JOB_ID = 1

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionManager: PermissionManager
    private lateinit var satelliteDataParser: SatelliteDataParser
    private var showContent = false
    private val viewModel: DataViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = applicationContext//: MainActivity = application as MainActivity

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

        satelliteDataParser = SatelliteDataParser(viewModel)
        CoroutineScope(Dispatchers.Default).launch {
            satelliteDataParser.parseSatelliteData()
        }


        // TODO: Get It To Work!
//        val workManager = WorkManager.getInstance(application)
//        var parsingWorkRequest = PeriodicWorkRequestBuilder<WebParsingWorker>(60, TimeUnit.SECONDS)
//            .build()
//        workManager.enqueue(parsingWorkRequest)


//        Log.d("MainActivity", "starting JobService")
//        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//        val jobId = WEBPARSING_JOB_ID
//        val extras = PersistableBundle().apply {
//            // Put your ViewModel instance in the extras bundle
//            putString("dataViewModel", viewModel.uniqueId)
//        }
//        val jobInfo = JobInfo.Builder(
//            jobId,
//            ComponentName(this, WebParsingJobService::class.java)
//        )
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//            // .setPeriodic(30 * 1000) // 30 seconds interval
//            .setPersisted(true)
//            .setExtras(extras)
//
//            .build()
//
//        jobScheduler.schedule(jobInfo)



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
fun Values(viewModel: DataViewModel = viewModel(), modifier: Modifier = Modifier){
    val currentData: CurrentSpaceWeatherState by viewModel.currentSpaceWeather.collectAsState()

    Text(
        text = "Bz Value:\t ${currentData.bzVal}\n" + // does not update!
                "Hemispheric Power: ${currentData.hpVal} GW",
        modifier = modifier,
    )
}

@Composable
fun MainComposable(permissionManager: PermissionManager, permissionLauncher: ActivityResultLauncher<Array<String>>) {
    val permissionState by permissionManager.permissionState.collectAsState()
    val context = LocalContext.current

    Values()

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
        Values()
    }
}