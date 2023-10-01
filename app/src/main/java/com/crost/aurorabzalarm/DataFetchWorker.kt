package com.crost.aurorabzalarm

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.net.URL

class DataFetchWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val wcp = WebsiteContentParser(context)
    override fun doWork(): Result {
        val success = wcp.parseAceSatelliteData()

        if (success) {
            return Result.success()
        }
        return Result.failure()


        // Perform data fetching here
        // You can access application context using applicationContext
        // Return Result.success() on success, Result.failure() on failure

    }

    fun readUrlPages(url: String){
        Log.d("reading url pages", url)
        var aceValuesUrl: URL
        try {
            aceValuesUrl = URL(url)
            try {
                var aceValues = aceValuesUrl.readText(Charsets.UTF_8)
                Log.d("readUrlPages", aceValues)
            } catch ( e: Exception){
                Log.e("readUrlPages", e.stackTraceToString())
            }
        } catch (e: Exception){
            Log.e("readUrlPages", e.stackTraceToString())
        }
    }
}