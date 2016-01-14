package fr.android.exhibit.services;

import android.app.Activity;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import fr.android.exhibit.data.IBeacon;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Thibault on 08/01/2016.
 */
public class BluetoothLEService extends IntentService {
    private static final long LESCAN_DURATION = 2000;
    private static final long LESCAN_NUMBER = 2;
    private static final long LESCAN_SWITCH = 2000;
    /*
    MAC ADRESSES
    D0:39:72:C8:C7:00 - IBEACON Bean - BLE Bean270
    B4:99:4C:1E:BC:07
     */

    private Handler mThread;
    private static List<BluetoothDevice> mBluetoothDevices;
    private static List<IBeacon> mBluetoothBeacons;
    private static BluetoothAdapter mAdapter;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            IBeacon beacon = IBeacon.fromScanData(scanRecord,rssi,device);
                            if(beacon == null && device.getName() != null
                                    && device.getType() == BluetoothDevice.DEVICE_TYPE_LE && !lookForDevice(device)) {
                                mBluetoothDevices.add(device);
                                Log.e("BLEDEVICE","UN BLUETOOTH LE !!!"+device.getName());
                            } else if(beacon != null && device.getName() != null){
                                mBluetoothBeacons.add(beacon);
                                Log.e("BLEDEVICE ","UN BLUETOOTH iBEACON : "+device.getName()+" PROX : "+beacon.getProximity());
                            }
                        }
                    });
                }
            };

    public BluetoothLEService() {
        super(".BluetoothLEService");
        mThread = new Handler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevices = new ArrayList<BluetoothDevice>();
    }

    public BluetoothLEService(String name) {
        super(name);
        mThread = new Handler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevices = new ArrayList<BluetoothDevice>();
    }

    public static boolean launchBluetoothToast(Activity context) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            Toast.makeText(context, "Ce périphérique ne permet pas l'utilisation du Bluetooth.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (!mAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBtIntent, 100);
                return true;
            } else {
                Toast.makeText(context, "La fonction Bluetooth n'a pu être activée.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    public static List<BluetoothDevice> getBluetoothDevices() {
        return mBluetoothDevices;
    }

    public static List<IBeacon> getBluetoothBeacons() {
        return mBluetoothBeacons;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // @TODO: 14/01/2016 Si nécessaire, threader le startlescan/stoplescan et en lancer
        // @TODO:             plusieurs à Xsec d'intervalle sur une longue durée

        int i = 1;
        while(true) {
            try {
                synchronized (this) {
                    Log.e("LESCAN","RUNNING LESCAN N"+String.valueOf(i));
                    mAdapter.startLeScan(null, mLeScanCallback);
                    wait(LESCAN_DURATION);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Log.e("LESCAN","ENDING LESCAN N"+String.valueOf(i));
                i++;
                mAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    private void runOnThread(Runnable runnable){
        mThread.post(runnable);
    }

    private boolean lookForDevice(BluetoothDevice device){
        for(BluetoothDevice bd : mBluetoothDevices){
            if(bd.getAddress() == device.getAddress())
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BLESERVICE_CLASS","Service ended.");
    }

    public static BluetoothDevice matchNFC(String nfcContent) {
        List<BluetoothDevice> tempBluetoothDevices = getBluetoothDevices();
        if(tempBluetoothDevices!= null && !tempBluetoothDevices.isEmpty()) {
            for(BluetoothDevice bd : tempBluetoothDevices) {
                if(bd.getName().equals(nfcContent))
                    return bd;
            }
            return null;
        }
        else
            return null;
    }
}
