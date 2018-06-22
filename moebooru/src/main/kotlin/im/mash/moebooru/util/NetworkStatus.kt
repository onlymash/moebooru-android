package im.mash.moebooru.util

import android.content.Context
import android.net.ConnectivityManager

val Context.isNetworkConnected: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val allNetworks = connectivityManager.allNetworks
        allNetworks.forEach { network ->
            if (connectivityManager.getNetworkInfo(network).isConnected)
                return true
        }
        return false
    }