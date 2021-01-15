package com.example.myapplication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import timber.log.Timber
import java.util.*
import kotlin.coroutines.suspendCoroutine

class WifiWatcher(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val shell = ShellFish()

    override suspend fun doWork()= suspendCoroutine<Result> {
        Timber.d("Work starts. ${Date()}")

        val isConnected = networks.getActiveNetworkSsid()?.contains("the landing strip") == true
        Timber.d("Is connected to the landing strip? $isConnected")

        if (isConnected) {
            shell.turnOffWifi()
            Timber.d("Wifi should be off now")
        } else {
            Timber.d("Wifi is not connected to the landing strip")
        }


        Timber.d("Work ends")
        Result.success()
    }
}


