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
import androidx.appcompat.app.AlertDialog

class InternetBroadcaster : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("네트워크 연결 실패")
            .setMessage("네트워크 연결이 되지 않은 상태입니다.")
            .setNeutralButton("재시도") { dialog, which ->
                Thread.sleep(2000)
            }
            .create()
        while (!isNetworkConnected(context)) {
            alertDialog.show()
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