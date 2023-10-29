package com.crost.aurorabzalarm.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.crost.aurorabzalarm.viewmodels.DataViewModel

class WebParsingWorker(
    var ctx: Context,
    var viewModel: DataViewModel,
    params: WorkerParameters,
): CoroutineWorker(ctx,params) {
    override suspend fun doWork(): Result {
        Log.d("WebParsingWorker", "running")
            return try {
//                runBlocking {
                    viewModel.fetchSpaceWeatherData()
//                }

//                viewModel.updateSpaceWeatherValues()
                Result.success()
            } catch (throwable: Throwable) {
                Log.e("WebParsingWorker", throwable.stackTraceToString())

                val text = "WebParsingWorker\n${throwable.message}"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()
                Result.failure()
            } catch (e: IllegalStateException){
                Log.e("WebParsingWorker", e.stackTraceToString())

                val text = "WebParsingWorker\n${e.message}"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                Result.failure()
            }
    }
}


class MyWorkerFactory(
    private val viewModel: DataViewModel
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WebParsingWorker::class.java.name -> {
                WebParsingWorker(appContext, viewModel, workerParameters)
            }
            else -> null
        }
    }
}