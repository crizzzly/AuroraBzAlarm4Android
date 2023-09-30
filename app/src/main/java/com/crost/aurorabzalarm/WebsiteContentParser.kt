package com.crost.aurorabzalarm

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebsiteContentParser {

    val hpValuesUrl = R.string.hpValsUrl.toString()
    lateinit var aceValues: String
    lateinit var hpValues: String
    lateinit var aceDoc: Document
    lateinit var hpDoc: Document

    fun getAceSatteliteData(context: Context){
        val aceUrl = ContextCompat.getString(context, R.string.aceValsUrl) //URL(R.string.aceValsUrl.toString())
        val aceDoc = Jsoup.connect(aceUrl).get()
        val html = aceDoc.body()
        Log.d("readUrl - ACE", html.data())
    }

}