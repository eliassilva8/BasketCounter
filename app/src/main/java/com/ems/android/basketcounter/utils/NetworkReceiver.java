package com.ems.android.basketcounter.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Elias on 14/08/2018.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private NetworkReceiverListener networkReceiverListener;

    public interface NetworkReceiverListener {
        void onConnectionChange(boolean isConnected);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        networkReceiverListener.onConnectionChange(isConnected(context));
    }

    public boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conn != null;
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        return (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE));
    }

    public void setNetworkReceiverListener(NetworkReceiverListener networkReceiverListener) {
        this.networkReceiverListener = networkReceiverListener;
    }
}
