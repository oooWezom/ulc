package com.wezom.ulcv2.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.OnNetworkStateEvent;
import com.wezom.ulcv2.managers.ConnectionManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 30.08.2016.
 */
public class ConnectionListener extends BroadcastReceiver {
    @Inject
    EventBus mBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        ULCApplication.mApplicationComponent.inject(this);

        if (Utils.isNetworkAvailable(context)) {
            mBus.post(new OnNetworkStateEvent(true));
        } else {
            mBus.post(new OnNetworkStateEvent(false));
        }
    }
}
