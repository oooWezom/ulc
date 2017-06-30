package com.wezom.ulcv2.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created: Zorin A.
 * Date: 30.08.2016.
 */
public class ConnectionManager {
    public static boolean isWiFiEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected() &&wifiInfo.isAvailable();
    }
}
