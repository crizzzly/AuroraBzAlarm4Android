package com.crost.aurorabzalarm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crost.aurorabzalarm.parser.SatelliteDataParser

class DataFetchWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val sdp = SatelliteDataParser(context)

    override fun doWork(): Result {
        val success = sdp.parseSatelliteData()

        if (success) {
            return Result.success()
        }
        return Result.failure()


        // Perform data fetching here
        // You can access application context using applicationContext
        // Return Result.success() on success, Result.failure() on failure

    }
}