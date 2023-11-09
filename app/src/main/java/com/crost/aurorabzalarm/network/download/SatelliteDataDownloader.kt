package com.crost.aurorabzalarm.network.download

import android.content.Context
import android.os.Environment
import android.util.Log
import com.crost.aurorabzalarm.utils.Constants.ACE_URL
import com.crost.aurorabzalarm.utils.Constants.FILEPATH_ACE_DATA
import com.crost.aurorabzalarm.utils.Constants.FILEPATH_EPAM_DATA
import com.crost.aurorabzalarm.utils.Constants.FILEPATH_HP_DATA
import com.crost.aurorabzalarm.utils.Constants.HP_URL
import com.crost.aurorabzalarm.utils.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.utils.Constants.RETRY_DELAY_MS
import com.crost.aurorabzalarm.utils.FileLogger
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream


class DownloadManager(context: Context) {
    private val con = context
    private var retryCount = 0
    private val fileLogger = FileLogger.getInstance(context)
    suspend fun loadSatelliteDatasheet(url: String): String {
        /*
        * downloads document from https://services.swpc.noaa.gov/
        *
        * @param url: self explaining.
        * */
//        Log.d("SatelliteDataDownloader", "loading $url")
        do {
            try {
                val aceDoc = Jsoup.connect(url).get()
                val html = aceDoc.select("body").toString()
//                Log.d("SatelliteDataDownloader", "length of doc${html.length}") // 7774 /2335
//                saveDataSheetToFile(html, url)
                return html
            } catch (e: Exception) {
                val msg = "$url\n ${e.stackTraceToString()}"

                fileLogger.writeLogsToInternalStorage(
                    con,
                    "getSatelliteData" +msg
                )

                Log.e("getSatelliteData", msg)
                retryCount++
                delay(RETRY_DELAY_MS.toLong())
            }
        } while (retryCount < MAX_RETRY_COUNT)

        Log.e(
            "getSatelliteData",
            "Failed to download data from $url after $MAX_RETRY_COUNT attempts"
        )
//        readDataSheetFromFile(url)
        return "Error"
    }

    private fun saveDataSheetToFile(text: String, url: String) {
        var path = when (url) {
            ACE_URL -> FILEPATH_ACE_DATA
            HP_URL -> FILEPATH_HP_DATA
            else -> FILEPATH_EPAM_DATA
//            else -> {}
        }

        // Get the external storage directory
        val directory = Environment.getExternalStorageDirectory()
        val file = try {
            File(path)
        } catch (e: Exception){
            fileLogger.writeLogsToInternalStorage(
                con,
                "saveDataSheetToFile\n" +e.message
            )
            Log.e("saveDataSheetToFile", e.message!!)
            null
        }

        try {
            // Open a file output stream to write to the file in append mode
            val outputStream = FileOutputStream(file)

            // Write the content to the file
            outputStream.write(text.toByteArray())

            // Close the file output stream
            outputStream.close()

            // File has been written successfully
            Log.d("FileWrite", "File has been written successfully!")

        } catch (e: Exception) {
            fileLogger.writeLogsToInternalStorage(
                con,
                "FileWrite\nError writing to file: ${e.message}"
            )
            // Handle any exceptions that occur during the file write process
            Log.e("FileWrite", "Error writing to file: ${e.message}")
        }
    }
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
