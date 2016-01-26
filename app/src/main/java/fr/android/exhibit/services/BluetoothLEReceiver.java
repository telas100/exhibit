package fr.android.exhibit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fr.android.exhibit.activities.MainActivity;

/**
 * Created by Thibault on 19/01/2016.
 */
public class BluetoothLEReceiver extends BroadcastReceiver {
    public static final String ACTION_BEACON_DISCOVERED
            = "fr.android.exhibit.services.BluetoothLEService.BEACON_DISCOVERED";
    private final MainActivity mActivity;

    public BluetoothLEReceiver(MainActivity context) {
        mActivity = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String beaconName = intent.getStringExtra(BluetoothLEService.EXTRA_BEACON_NAME);
        mActivity.onBeaconReceived(beaconName);
    }
}
