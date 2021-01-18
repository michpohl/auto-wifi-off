package com.example.myapplication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WifiWatcher(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val shell = ShellFish()

    override suspend fun doWork() = suspendCoroutine<Result> {
        //        Timber.d("Work starts. ${Date()}")
        //
        //        val isConnected = networks.getActiveNetworkSsid()?.contains("the landing strip") == true
        //        Timber.d("Is connected to the landing strip? $isConnected")
        //
        //        if (isConnected) {
        //            shell.turnOffWifi()
        //            Timber.d("Wifi should be off now")
        //        } else {
        //            Timber.d("Wifi is not connected to the landing strip")
        //        }

        while (true) {
            val request = getSingleWatch()
            workManager.enqueue(request)
            Thread.sleep(2000)
        }

        Timber.d("Work ends")
        it.resume(Result.success())
    }

    private fun getSingleWatch(): WorkRequest {
        return OneTimeWorkRequestBuilder<SingleWatch>().build()
    }
}

class SingleWatch(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Work starts. ${Date()}")

        //        val isConnected = networks.getActiveNetworkSsid()?.contains("the landing strip") == true
        //        Timber.d("Is connected to the landing strip? $isConnected")
        //
        //        if (isConnected) {
        //            shell.turnOffWifi()
        //            Timber.d("Wifi should be off now")
        //        } else {
        //            Timber.d("Wifi is not connected to the landing strip")
        //        }
        //
        //
        //        Timber.d("Work ends")
        return Result.success()
    }
}




