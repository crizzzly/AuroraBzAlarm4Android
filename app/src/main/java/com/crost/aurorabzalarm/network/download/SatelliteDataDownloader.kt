package com.crost.aurorabzalarm.network.download

import android.util.Log
import com.crost.aurorabzalarm.Constants.FILEPATH_ACE_DATA
import com.crost.aurorabzalarm.Constants.FILEPATH_HP_DATA
import com.crost.aurorabzalarm.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.Constants.RETRY_DELAY_MS
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.io.BufferedReader


class DownloadManager() {
    private var retryCount = 0
    suspend fun loadSatelliteDatasheet(url: String): String {
        /*
        * downloads document from https://services.swpc.noaa.gov/
        *
        * @param url: self explaining.
        * */
        Log.d("SatelliteDataDownloader", "loading $url")
        do {
            try {
                val aceDoc = Jsoup.connect(url).get()
                val html = aceDoc.select("body").toString()
                Log.d("SatelliteDataDownloader", "length of doc${html.length}") // 7774 /2335
                saveDataSheetToFile(html, url)
                return html
            } catch (e: Exception) {
                // TODO: make it better somehow
                Log.e("getSatelliteData", "$url\n ${e.stackTraceToString()}")
                retryCount ++
                delay(RETRY_DELAY_MS.toLong())
            }
        } while (retryCount < MAX_RETRY_COUNT)

        Log.e("getSatelliteData", "Failed to download data from $url after $MAX_RETRY_COUNT attempts")
        readDataSheetFromFile(url)
        return "Error"
    }

    private fun saveDataSheetToFile(text: String, url: String){
        val path = if ("ace" in url){
            FILEPATH_ACE_DATA
        } else{
            FILEPATH_HP_DATA
        }
//        PrintWriter(path).use {
//            it.print(text)
//        }
    }

    private fun readDataSheetFromFile(url: String){
        val reader: BufferedReader
        val file = if ("ace" in url){
            FILEPATH_ACE_DATA
        } else{
            FILEPATH_HP_DATA
        }
//        reader = BufferedReader(InputStreamReader(file.byteInputStream()))
    }
}


//
//fun loadSatelliteDatasheet(url: String): String {
//    var retryCount = 0
//    var html: String
//
//    do {
//        try {
//            val aceDoc = Jsoup.connect(url).get()
//            html = aceDoc.select("body").toString()
//            Log.d("SatelliteDataDownloader", "length of doc ${html.length}") // 7774 /2335
//            return html
//        } catch (e: UnknownHostException) {
//            // Handle network errors (UnknownHostException) here
//            Log.e("getSatelliteData", "$url\n${e.stackTraceToString()}")
//            retryCount++
//            Thread.sleep(RETRY_DELAY_MS.toLong()) // Wait before the next retry
//        }
//    } while (retryCount < MAX_RETRY_COUNT)
//
//    // If all retries fail, return an error message
//    Log.e("getSatelliteData", "Failed to download data from $url after $MAX_RETRY_COUNT attempts")
//    return "Error"
//}
//}
