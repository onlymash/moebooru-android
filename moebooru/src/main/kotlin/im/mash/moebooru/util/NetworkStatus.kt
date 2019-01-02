package im.mash.moebooru.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * 检查网络连接状态
 */
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