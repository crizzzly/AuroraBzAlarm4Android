package com.crost.aurorabzalarm.utils

import android.content.Context
import android.util.Log


// TODO: Use this!
class ExceptionHandler private constructor(con: Context) {
    private val logger = FileLogger.getInstance(con)

    companion object{
        private var instance: ExceptionHandler? = null

        fun getInstance(context: Context): ExceptionHandler{
            return instance?: ExceptionHandler(context.applicationContext).also {
                instance = it
            }
        }
    }

    fun handleExceptions(context: Context, tag: String, msg: String){
        logger.writeLogsToInternalStorage(context, "$tag\n$msg")
        Log.e(tag, msg)

//        Toast
//            .makeText(context, tag+"\n"+msg, Toast.LENGTH_LONG)
//            .show()
    }


}