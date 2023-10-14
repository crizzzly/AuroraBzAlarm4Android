package com.crost.aurorabzalarm.repository
//
//import com.crost.aurorabzalarm.network.download.DownloadManager
//import kotlinx.coroutines.test.runTest
////import org.junit.jupiter.api.Assertions
////import org.junit.runner.RunWith
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.`when`
////import org.mockito.junit.MockitoJUnitRunner
//import java.io.File
//import java.nio.charset.Charset
//import org.junit.jupiter.api.Test
//
//import kotlinx.coroutines.test.TestResult as TestResult1
//
//
////@RunWith(MockitoJUnitRunner::class)
//
//class SpaceWeatherRepositoryTest {
//
//    @Test
////    @Test
//    fun testFetchDataAndStoreInDatabase() = runTest {
//        val mockedDownloadManager = mock(DownloadManager::class.java)
//
//        `when`(mockedDownloadManager.loadSatelliteDatasheet("abc")).thenAnswer {
//            loadTestData()
//        }
////
////        val parser = mock(DocumentParser::class.java)
////        // Set up parser mock behavior if needed...
////
////        val dataShaper = mock(DataShaper::class.java)
////        // Set up dataShaper mock behavior if needed...
//
//        val repository = SpaceWeatherRepository()
//
//        // Call the method you want to test
//        val result = repository.fetchDataAndStoreInDatabase()
//        val latestDataMap = result[0][result[0].size-1]
//        val bzVal = latestDataMap["Bz"]
////        assert(bzVal is Float)
////        Assertions.assertTrue(bzVal is Float)
//
//
//    }
//
//    fun loadTestData(): String {
//        val path = "src/test/resources/ace-magnetometer"
//        val file = File(path)
//        val content = file.readText(Charset.defaultCharset())
//        return "<body>\n$content\n</body>"
//    }
//}