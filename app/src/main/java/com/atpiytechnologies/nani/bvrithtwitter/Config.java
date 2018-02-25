package com.atpiytechnologies.nani.bvrithtwitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Config {

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

}
