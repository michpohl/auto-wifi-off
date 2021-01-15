package com.example.myapplication

import timber.log.Timber
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

class ShellFish {

    var isWifiOff = false

    fun turnOnWifi() {
        sudo("svc wifi enable")
        isWifiOff = false
    }

    fun turnOffWifi() {
        sudo("svc wifi disable")
        isWifiOff = true
    }

    fun isWifiConnected(ssid: String): Boolean {
        val connectionCheckResult = execute("busybox")
        return connectionCheckResult?.let {
            it.contains(ssid)
        } ?: false
    }

    private fun execute(string: String): String? {
        try {
            val process = Runtime.getRuntime().exec(string)
            val bufferedReader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            val result = StringBuilder()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                Timber.d("reading...$line")
                result.append(line)
            }
            Timber.d("result: ${result}")
            return result.toString()
        } catch (e: IOException) {
            Timber.d("Catching")
            e.printStackTrace()
        }
        return null
    }

    private fun sudo(vararg strings: String) {
        try {
            val su = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(su.outputStream)
            for (s in strings) {
                outputStream.writeBytes(
                    """
                    $s
                    
                    """.trimIndent()
                )
                outputStream.flush()
            }
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            try {
                su.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun runAsRoot(cmds: Array<String>): String? {
        val p = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(p.outputStream)
        val `is` = p.inputStream
        for (tmpCmd in cmds) {
            os.writeBytes(
                """
                $tmpCmd
                
                """.trimIndent()
            )
            var readed = 0
            val buff = ByteArray(4096)
            val cmdRequiresAnOutput = true
            if (cmdRequiresAnOutput) {
                while (`is`.available() <= 0) {
                    try {
                        Thread.sleep(5000)
                    } catch (ex: Exception) {
                    }
                }
                while (`is`.available() > 0) {
                    readed = `is`.read(buff)
                    if (readed <= 0) break
                    val seg = String(buff, 0, readed)
                    return seg
                }
            }
        }
        return null
    }

    fun exists() {
        Timber.d("${execute("echo hello world")}")
    }
}
