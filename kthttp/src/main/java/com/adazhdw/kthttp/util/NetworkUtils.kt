package com.adazhdw.kthttp.util

import android.Manifest.permission
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

/**
 * author：daguozhu
 * date-time：2020/11/16 18:00
 * description：
 **/

object NetworkUtils {

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isConnected(context: Context): Boolean {
        val cm = getConnectivityManager(context) ?: return false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
            return isConnectedOver23(capabilities)
        } else {
            val networkInfo = cm.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    /**
     *
     */
    @RequiresApi(android.os.Build.VERSION_CODES.M)
    fun isConnectedOver23(capabilities: NetworkCapabilities): Boolean {
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private fun getConnectivityManager(context: Context): ConnectivityManager? {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    }
}