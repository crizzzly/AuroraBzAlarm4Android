package com.crost.aurorabzalarm.worker

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.viewmodels.DataViewModel


class WebParsingWorker(
    var ctx: Context,
    var viewModel: DataViewModel,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    private val notificationManager =
        ctx.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
    private val fileLogger = FileLogger.getInstance(ctx)


    override suspend fun doWork(): Result {
        return try {
            viewModel.fetchSpaceWeatherData(ctx)

            Result.success()
        } catch (throwable: Throwable) {
            Log.e("WebParsingWorker", throwable.stackTraceToString())

            val text = "WebParsingWorker\n${throwable.message}"
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
            toast.show()
            Result.failure()
        } catch (e: IllegalStateException) {
            fileLogger.writeLogsToInternalStorage(
                ctx, "WebParsingWorker\n${e.stackTraceToString()}"
            )
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