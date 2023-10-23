package com.crost.aurorabzalarm.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.crost.aurorabzalarm.ui.DataViewModel

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
                Result.failure()
            } catch (e: IllegalStateException){
                Log.e("WebParsingWorker", e.stackTraceToString())
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