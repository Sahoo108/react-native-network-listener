package com.networklistener

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule

@ReactModule(name = NetworkListenerModule.NAME)
class NetworkListenerModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    init {
        registerNetworkState()
    }

    companion object {
        const val NAME = "NetworkListenerModule"
        const val CONNECTION_STATE = "onNetworkStateChange"
        const val NETWORK_AVAILABLE = "AVAILABLE"
        const val NETWORK_UNAVAILABLE = "UNAVAILABLE"
        const val NETWORK_LOST = "LOST"
        const val NETWORK_LOSING = "LOSING"
        const val NETWORK_BLOCK_STATE_CHANGE = "BLOCK_STATE_CHANGE"
    }

    private fun registerNetworkState() {

        val connectivityManagerNetworkCallback = object :
            ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                emitNetworkEvent(Arguments.createMap().apply {
                    putString("state", NETWORK_AVAILABLE)

                })
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                emitNetworkEvent(Arguments.createMap().apply {
                    putString("state", NETWORK_LOST)
                })

            }

            override fun onUnavailable() {
                super.onUnavailable()
                emitNetworkEvent(Arguments.createMap().apply {
                    putString("state", NETWORK_UNAVAILABLE)
                })
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                emitNetworkEvent(Arguments.createMap().apply {
                    putString("state", NETWORK_LOSING)
                    putInt("maxMsToLive", maxMsToLive)
                })
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                super.onBlockedStatusChanged(network, blocked)
                emitNetworkEvent(Arguments.createMap().apply {
                    putString("state", NETWORK_BLOCK_STATE_CHANGE)
                    putBoolean("isBlocked", blocked)
                })
            }

        }

        /*Android os version callback*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager = reactContext.getSystemService(ConnectivityManager::class.java)
            connectivityManager?.registerDefaultNetworkCallback(connectivityManagerNetworkCallback)
        } else{
            val connectivityManager = reactContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkChangeFilter = NetworkRequest.Builder().build()
            connectivityManager.registerNetworkCallback(networkChangeFilter, connectivityManagerNetworkCallback)
        }
    }



    /*Send connection state to JS Layer*/
    private fun emitNetworkEvent(params: WritableMap?) {
        try {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                ?.emit(CONNECTION_STATE, params)
        } catch (ignore: Exception) {
        }
    }

    override fun getName(): String {
        return NAME
    }

    override fun getConstants(): MutableMap<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put(NETWORK_AVAILABLE, NETWORK_AVAILABLE)
            put(NETWORK_UNAVAILABLE, NETWORK_UNAVAILABLE)
            put(NETWORK_LOST, NETWORK_LOST)
            put(NETWORK_LOSING, NETWORK_LOSING)
            put(NETWORK_BLOCK_STATE_CHANGE, NETWORK_BLOCK_STATE_CHANGE)
        }
    }

}
