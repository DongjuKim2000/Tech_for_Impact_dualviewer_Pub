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
    private var isConnected: Boolean = false
    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkConnected(context)) {
            if(isConnected == true)
                return;

            isConnected = true
        }
        else{
            if(isConnected == false)
                return;
            isConnected = false
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