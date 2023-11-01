package com.crost.aurorabzalarm.settings

import android.content.Context
import com.google.gson.Gson
import java.io.IOException
import java.io.OutputStreamWriter


const val FILE_NAME = "settingsConfig.json" // root/settings/

const val JSON_SETTINGS_STRING = "{" +
    "'hpWarningLevel': {" +
    "'minValue': 0," +
   "'maxValue': 100," +
    "'currentValue': 30,"+
"},"+
    "'bzWarningLevel': {"+
    "'minValue': -50," +
    "'maxValue': 0," +
    "'currentValue': -10"  +
"}" +
"}"


data class WarningLevel(
    var minValue: Float,
    var maxValue: Float,
    var currentValue: Float
)


data class Settings(
    var hpWarningLevel: WarningLevel,
    var bzWarningLevel: WarningLevel,
    var notificationEnabled: Boolean
)

fun saveSettingsConfig(context: Context, settings: Settings){
    val gson = Gson()
    val jsonString = gson.toJson(settings)
    writeSettingsToInternalStorage(context, jsonString)
}
fun getSettingsConfig(context: Context): Settings {
    val gson = Gson()
    val jsonString = readSettingsFromInternalStorage(context)
    return try {
        gson.fromJson(jsonString, Settings::class.java)
    } catch (e: NullPointerException) {
        gson.fromJson(JSON_SETTINGS_STRING, Settings::class.java)
    }
}


private fun readSettingsFromAssets(context: Context): String {
    val jsonString = context.assets.open(FILE_NAME).bufferedReader().use {
        it.readText()
    }
    return jsonString
}

private fun writeSettingsToInternalStorage(context: Context, jsonString: String) {
    try {
        val outputStreamWriter = OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))
        outputStreamWriter.write(jsonString)
        outputStreamWriter.close()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
    }
}


private fun readSettingsFromInternalStorage(context: Context): String {
    return context.openFileInput(FILE_NAME).bufferedReader().use {
        it.readText()
    }
}


//private fun readSettingsFromFile(): String {
//    val file = File(FILE_NAME)
//    return if (file.exists() && file.isFile) {
//        file.readText()
//    } else {
//        // Handle the case when the file does not exist or is not a regular file
//        ""
//    }
//}



//private fun loadSettingsFile(): String {
//    val currentDir = System.getProperty("user.dir")
//    Log.d("loadSettingsFile", "currentDir: $currentDir")
//
////    val filePath = "root/settings/settingsConfig.json"
////    return readSettingsFromFile(filePath)
////    println("JSON String from file: $jsonString")
//}
//private fun exampleUsage(){
//    val settings = parseSettingsConfig()
//    println(settings.bzWarningLevel.currentValue)
//}