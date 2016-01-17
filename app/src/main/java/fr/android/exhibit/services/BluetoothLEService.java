package fr.android.exhibit.services;

import android.app.Activity;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import fr.android.exhibit.model.IBeacon;
import fr.android.exhibit.model.LiteBluetoothBeacon;
import fr.android.exhibit.model.LiteBluetoothDevice;
import fr.android.exhibit.model.LiteProximityRecord;

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
                                LiteBluetoothDevice bleDevice = new LiteBluetoothDevice(device.getName(),device.getAddress());
                                bleDevice.save();
                                Log.e("BLEDEVICE","BLUETOOTH LE : "+device.getName()+" : "+device.getAddress());
                            } else if(beacon != null && device.getName() != null) {
                                LiteBluetoothBeacon beaconDevice = new LiteBluetoothBeacon(device.getName(),beacon.getBluetoothAddress(),
                                        beacon.getProximityUuid(), beacon.getMajor(), beacon.getMinor());
                                beaconDevice.save();
                                LiteProximityRecord beaconRecord = new LiteProximityRecord(beaconDevice, beacon.getRssi(),
                                        beacon.getProximity(), beacon.getAccuracy(), beacon.getTxPower());
                                beaconRecord.save();
                                Log.e("BLEDEVICE ", "BLUETOOTH iBEACON : " + device.getName() + " : " + beacon.getProximity());
                            }
                        }
                    });
                }
            };

    public BluetoothLEService() {
        super(".BluetoothLEService");
        mThread = new Handler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothLEService(String name) {
        super(name);
        mThread = new Handler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
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

    public static List<LiteBluetoothDevice> getBluetoothDevices() {
        return LiteBluetoothDevice.getAll();
    }

    // public static List<IBeacon> getBluetoothBeacons() { return mBluetoothBeacons; }

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
        for(LiteBluetoothDevice bd : LiteBluetoothDevice.getAll()){
            Log.e("COMPARING", bd.mAddress + " with " + device.getAddress() + bd.mAddress.equals(device.getAddress()));
            if (bd.mAddress.equals(device.getAddress()))
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BLESERVICE_CLASS","Service ended.");
    }

    public static LiteBluetoothDevice matchNFC(String nfcContent) {
        List<LiteBluetoothDevice> devices = LiteBluetoothDevice.getAll();
        if(devices!= null && !devices.isEmpty()) {
            for(LiteBluetoothDevice bd : devices) {
                if(bd.mAddress.equals(nfcContent))
                    return bd;
            }
            return null;
        }
        else
            return null;
    }
}
