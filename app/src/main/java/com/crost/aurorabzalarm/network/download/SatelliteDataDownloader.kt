package com.crost.aurorabzalarm.network.download

import android.content.Context
import android.os.Environment
import android.util.Log
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_URL
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_ACE_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_EPAM_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_HP_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.HP_URL
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.RETRY_DELAY_MS
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class DownloadManager(context: Context) {
    private val con = context
    private var retryCount = 0
    private val exceptionHandler = ExceptionHandler.getInstance(context)
    private val fileLogger = FileLogger.getInstance(context)
    suspend fun loadSatelliteDatasheet(url: String): String {
        /*
        * downloads document from @param url
        *
        * @param url: self explaining.
        * @return: downloaded data as string
        * */
//        Log.d("SatelliteDataDownloader", "loading $url")
        val loggerTag = "loadSatelliteDatasheet"

        do {
            if("json" in url){
                Log.d(loggerTag, "downloading json")
                return fetchDataFromUrl(url)

            }
            try {
                val aceDoc = Jsoup.connect(url).get()
                val html = aceDoc.select("body").toString()
                return html
            } catch (e: UnsupportedMimeTypeException) {
                val msg = "$url\n ${e.stackTraceToString()}"

                exceptionHandler.handleExceptions(con, loggerTag, msg)
                retryCount++
                delay(RETRY_DELAY_MS.toLong())
            }
        } while (retryCount < MAX_RETRY_COUNT)

        val msg = "Failed to download data from $url after $MAX_RETRY_COUNT attempts"
        exceptionHandler.handleExceptions(con, loggerTag, msg)

        // TODO: Handle this ;)
        return "Error"
    }


    private fun fetchDataFromUrl(urlString: String): String {
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            stringBuilder.toString()
        } finally {
            connection.disconnect()
        }
    }

    private fun saveDataSheetToFile(text: String, url: String) {
        val path = when (url) {
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
            exceptionHandler.handleExceptions(
                con, "FileWrite", "Error writing to file: ${e.message}"
            )
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
