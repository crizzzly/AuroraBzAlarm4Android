package com.crost.aurorabzalarm.network.download

import android.content.Context
import android.os.Environment
import android.util.Log
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_URL
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_ACE_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_EPAM_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.FILEPATH_HP_DATA
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.HP_URL
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.RETRY_DELAY_MS
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

private const val DEBUG = false

class DownloadManager(context: Context) {
    private val con = context
    private var retryCount = 0
    private val exceptionHandler = ExceptionHandler.getInstance(context)
    private val fileLogger = FileLogger.getInstance(context)
    val loggerTag = "loadSatelliteDatasheet"
    suspend fun loadSatelliteDatasheet(url: String): String {
        /*
        * downloads document from @param url
        *
        * @param url: self explaining.
        * @return: downloaded data as string
        * */

        val now = LocalDateTime.now()

        do {
            if("json" in url){
               Log.d("loadSatelliteDatasheet", "loadingNr $retryCount: $url")

                return fetchDataFromUrl(url)

            }
            try {
                val aceDoc = Jsoup.connect(url).get()
                val html = aceDoc.select("body").toString()
                return html
            } catch (e: Exception) {
                val msg = "$url\n ${e.stackTraceToString()}"

                exceptionHandler.handleExceptions(con, loggerTag, msg)
                retryCount++
                delay(RETRY_DELAY_MS.toLong())
            }
        } while (retryCount < MAX_RETRY_COUNT)

        val msg = "Failed to download data from $url after $MAX_RETRY_COUNT attempts"
        exceptionHandler.handleExceptions(con, loggerTag, msg)
        return "Error"
    }


    private fun fetchDataFromUrl(urlString: String): String {
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            if (DEBUG)Log.d(loggerTag, "${LocalDateTime.now()} - ${url.path}")
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            stringBuilder.toString()
        }  finally {
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

