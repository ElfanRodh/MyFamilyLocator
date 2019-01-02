package xyz.elfanrodhian.myfamilylocator.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import xyz.elfanrodhian.myfamilylocator.utils.NetworkState;
import xyz.elfanrodhian.myfamilylocator.utils.NetworkStateEvent;


public class NetworkStateReceiver extends BroadcastReceiver {

    private final EventBus eventBus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = NetworkState.getConnectivityStatus(context);

        // Post the event with this line
        eventBus.post(new NetworkStateEvent(status));
    }
}
