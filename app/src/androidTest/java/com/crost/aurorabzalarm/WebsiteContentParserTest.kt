package com.crost.aurorabzalarm

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class WebsiteContentParserTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.crost.aurorabzalarm", appContext.packageName)

        val parser = WebsiteContentParser(appContext)

        // Call the method you want to test
        val content = parser.getAceSatelliteData()

        // Add assertions to validate the behavior of the extractAceData method
        // For example, you can assert on properties or behaviors affected by extractAceData
        // assertEquals(expectedValue, actualValue)

        // You can also use Logcat to log messages for debugging purposes
        println(content)
        Log.d("WebsiteContentParserTest", content)

    }
}