package com.crost.aurorabzalarm.utils

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

const val FILE_NAME = "logs.log"
class Logger private constructor(context: Context) {
    private val logFile: File by lazy {
        File(context.filesDir, FILE_NAME)
    }

    companion object {
        private var instance: Logger? = null

        fun getInstance(context: Context): Logger {
            return instance ?: synchronized(this) {
                instance ?: Logger(context.applicationContext).also { instance = it }
            }
        }
    }

    fun writeLogsToInternalStorage(context: Context, logString: String) {
        try {
            val outputStream = context.openFileOutput(FILE_NAME,  Context.MODE_APPEND,)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(logString)
            outputStreamWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
        }
    }
}