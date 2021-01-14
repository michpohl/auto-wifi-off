package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import timber.log.Timber

class ConfiguredNetworksHandler(
    private val application: Application,
    private val wifiManager: WifiManager,
) {
    private val WIFI_ADDED_NETWORKS_KEY = "wifi-availableBahnWifis"
    private val connectivityManager =
        application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var networkIdsMap = mutableMapOf<String, Int>()

    /**
     * Returns true if the phone's wifi is on
     */
    fun isWifiEnabled(): Boolean {
        return wifiManager.isWifiEnabled
    }

    fun getActiveNetworkInfo(): NetworkInfo? {
        return connectivityManager.activeNetworkInfo
    }

    fun getIpAddress(): Int {
        return wifiManager.connectionInfo.ipAddress
    }

    private fun getNetworkCapabilities(): NetworkCapabilities? {
        if (Build.VERSION.SDK_INT >= 23) {
            val activeNetwork = connectivityManager.activeNetwork
            return connectivityManager.getNetworkCapabilities(activeNetwork)
        }
        return null
    }

    fun isActiveNetworkVpn(): Boolean? {
        return getNetworkCapabilities()?.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    fun getActiveNetworkSsid(): String? {
        return getInfoForCurrentConnection()?.ssid
    }

    fun getUpAndDownstreamBandwidth(): Pair<Int, Int>? {
        val upStream = getNetworkCapabilities()?.linkUpstreamBandwidthKbps
        val downStream = getNetworkCapabilities()?.linkDownstreamBandwidthKbps
        return if (upStream != null && downStream != null) Pair(upStream, downStream) else null
    }

    /**
     * starts a broadcast that notifies the receiver when an update on wifi states comes in (but also sometimes
     * maybe not, it's up to the operating system when and how often this happens)
     */
    fun startScanningForWifis(receiver: BroadcastReceiver) {
        if (wifiManager.startScan()) {
            application.applicationContext!!.registerReceiver(
                receiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
        }
    }

    /**
     * Manually requests the latest information from the [WifiManager]
     */
    fun getScanResults(): MutableList<ScanResult>? {
        return wifiManager.scanResults
    }

    /**
     * @return a WifiInfo object containing the specs of the currently active connection
     */
    fun getInfoForCurrentConnection(): WifiInfo? {
        return wifiManager.connectionInfo
    }

    fun getSignalStrength(info: WifiInfo? = getInfoForCurrentConnection(), numberOfLevels: Int = 10): Int? {

        return info?.let { WifiManager.calculateSignalLevel(it.rssi, numberOfLevels) }
    }

//    /**
//     * Tells the system to add the specified SSID to the known networks, then saves the current status internally
//     * (because the app needs to persist the network IDs to be able to identify wifis added from outside the app)
//     */
//    fun addSingleNetwork(ssid: String) {
//        val escapedSsid = ssid.asEscapedSSID()
//        val config = WifiConfiguration()
//        config.SSID = escapedSsid
//        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
//
//        if (!isWifiAdded(ssid)) {
//            val id = wifiManager.addNetwork(config)
//            wifiManager.enableNetwork(id, true)
//            wifiManager.reconnect()
//            networkIdsMap[ssid] = id
//        } else {
//            Timber.w("Cannot add wifi, because this SSID has already been added (by the system probably)")
//        }
//        saveAddedNetworks()
//    }

//    /**
//     * Tells the system to remove the specified SSID from the known networks if the corresponding networkID is present.
//     * @return true if wifi could be forgotten
//     */
//    @SuppressLint("MissingPermission")
//    fun forgetSingleNetwork(ssid: String): Boolean {
//        var wasRemoved = false
//        val escapedSsid = ssid.asEscapedSSID()
//
//        wifiManager.configuredNetworks?.find { it.SSID == escapedSsid }?.let {
//            if (networkIdsMap.values.contains(it.networkId)) {
//                wasRemoved = wifiManager.removeNetwork(it.networkId)
//                networkIdsMap.remove(ssid)
//                saveAddedNetworks()
//            }
//        }
//        saveAddedNetworks()
//        return wasRemoved
//    }

//    /**
//     * Loads the Map of added wifi SSIDS and their network IDs into [networkIdsMap]
//     */
//    fun loadAddedNetworks() {
//        sharedPrefsManager.getString(WIFI_ADDED_NETWORKS_KEY, null)?.let { json ->
//            val savedAddedNetworks =
//                Moshi.Builder().build().adapter(AddedNetworks::class.java).fromJson(json)
//            networkIdsMap =
//                savedAddedNetworks?.addedNetworks?.toMutableMap() ?: mutableMapOf()
//        }
//    }

//    /**
//     * Stores the added wifis' SSIDS with their corresponding network IDs in SharedPreferences. The network IDs need
//     * to be persisted to know if a wifi was added by the app or not.
//     */
//    private fun saveAddedNetworks() {
//        val addedNetworks = AddedNetworks(networkIdsMap)
//        val moshi = Moshi.Builder().build()
//        sharedPrefsManager.putString(
//            WIFI_ADDED_NETWORKS_KEY,
//            moshi.adapter(AddedNetworks::class.java).toJson(addedNetworks)
//        )
//    }

//    /**
//     *  the system
//     */
//    @SuppressLint("MissingPermission")
//    fun isWifiAdded(ssid: String): Boolean {
//        return wifiManager.configuredNetworks?.find { it.SSID == ssid.asEscapedSSID() } != null
//    }

    /**
     * Returns true if the phone currently has access to the internet via wifi
     */
    fun hasInternetAccessViaWifi(): Boolean {
        application.applicationContext?.let { context ->
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            connectivityManager?.activeNetworkInfo?.let {
                if (it.isConnected && it.type == ConnectivityManager.TYPE_WIFI) {
                    return true
                }
            } ?: Timber.w("Checking if a wifi is connected that does not seem to exist!")
        }
        return false
    }
}
