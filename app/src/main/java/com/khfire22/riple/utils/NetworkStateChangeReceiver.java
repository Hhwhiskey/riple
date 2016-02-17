package com.khfire22.riple.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.khfire22.riple.R;

/**
 * Created by Kevin on 1/19/2016.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {
                Toast.makeText(context, R.string.network_connection_established, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        }
    }


}