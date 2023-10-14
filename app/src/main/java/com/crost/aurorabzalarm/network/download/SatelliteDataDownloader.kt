package com.crost.aurorabzalarm.network.download

import android.util.Log
import org.jsoup.Jsoup
import java.net.UnknownHostException

class DownloadManager() {
    fun loadSatelliteDatasheet(url: String): String {
        /*
        * downloads document from https://services.swpc.noaa.gov/
        *
        * @param url: self explaining.
        * */
        Log.d("SatelliteDataDownloader", "loading $url")
        return try {
            val aceDoc = Jsoup.connect(url).get()
            val html = aceDoc.select("body").toString()
//            Log.d("readUrl - ACE", html)
            html
        } catch (e: UnknownHostException) {
            // TODO: make it better somehow
            Log.e("getSatelliteData", "$url\n ${e.stackTraceToString()}")
            "Error"
        }
    }
}
