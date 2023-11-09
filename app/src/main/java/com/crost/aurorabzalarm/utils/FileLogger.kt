package com.crost.aurorabzalarm.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.LocalDateTime

const val FILE_NAME = "logs.txt"
class FileLogger private constructor(context: Context) {
    private val logFile: File by lazy {

        File(context.filesDir, FILE_NAME)
    }

    companion object {
        private var instance: FileLogger? = null

        fun getInstance(context: Context): FileLogger {
//            Log.d("FileLogger", "dir: ${context.filesDir}") // /data/user/0/com.crost.aurorabzalarm/files
            return instance ?: synchronized(this) {
                instance ?: FileLogger(context.applicationContext).also { instance = it }
            }
        }
    }

    fun writeLogsToInternalStorage(context: Context, logString: String) {
        val currentDt = LocalDateTime.now()
        val logMsg = "$currentDt\n$logString\n\n"
        Log.d("writeLogsToInternalStorage", "datetime: $currentDt")
        try {
            val outputStream = context.openFileOutput(FILE_NAME,  Context.MODE_APPEND,)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(logMsg)
            outputStreamWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
        }
    }

    fun getLogFileContent(context: Context): String{
        return if (logFile.exists()) {
            logFile.readText()
            // Use logContent as needed
        } else {
            "No Logfile Created"
            // Log file does not exist or is empty
        }
    }
}