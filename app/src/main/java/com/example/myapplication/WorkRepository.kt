package com.example.myapplication

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class WorkRepository(networks: ConfiguredNetworksHandler) {
    val oneTimeWatchWifiWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<WifiWatcher>()
            .build()




    val periodicWatchWifiWorkRequest = PeriodicWorkRequest.Builder(
        WifiWatcher::class.java, // Your work r class
        15, // repeating interval
        TimeUnit.MINUTES,
        15, // flex interval - worker will run somewhen within this period of time, but at the end of repeating interval
        TimeUnit.MINUTES
    ).build()


    fun watchWifi(context: Context) {
        WorkManager
            .getInstance(context)
            .enqueue(periodicWatchWifiWorkRequest)
    }

}
