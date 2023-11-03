package com.nightscout.nightviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.net.NetworkInfo
import android.widget.Toast
class InternetBroadcaster : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        while (!isNetworkConnected(context)) {
            Toast.makeText(context, "네트워크에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            Thread.sleep(2000);
        }
    }
    private fun isNetworkConnected(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isNetworkAvailable(context)
        } else {
            isNetworkConnectedLegacy(context)
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    @Suppress("DEPRECATION")
    private fun isNetworkConnectedLegacy(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

}