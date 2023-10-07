package com.crost.aurorabzalarm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class WebParsingWorker(
    ctx: Context,
    params: WorkerParameters,
): CoroutineWorker(ctx,params) {
    override suspend fun doWork(): Result {
        var viewModel = ViewModelFactory.getDataViewModel()
        return try {
//            setForeground(getForegroundInfo())
            viewModel.fetchData()

            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        } catch (e: IllegalStateException){
            Result.failure()
        }
    }
//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        return ForegroundInfo(
//        )
//    }
}


class MyWorkerFactory() : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WebParsingWorker::class.java.name -> {
                WebParsingWorker(appContext, workerParameters)
            }
            else -> null
        }
    }
}