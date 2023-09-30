package com.crost.aurorabzalarm

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup
import java.net.URL

class DataFetchWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    var aceUrl = ContextCompat.getString(context, R.string.aceValsUrl) //URL(R.string.aceValsUrl.toString())
    override fun doWork(): Result {

        try {
            //WebsiteContentParser.getAceSatteliteData(context)
            val aceDoc = Jsoup.connect(aceUrl).get()
            val html = aceDoc.body()
            Log.d("readUrl - ACE", html.data())

        } catch (e: Exception) {
            Log.e("DataFetchWorker", e.stackTraceToString())
        }

        // Perform data fetching here
        // You can access application context using applicationContext
        // Return Result.success() on success, Result.failure() on failure
    return Result.success()
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