package fr.android.exhibit.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.android.exhibit.entities.IBeacon;
import fr.android.exhibit.entities.LiteBeacon;
import fr.android.exhibit.entities.LiteDevice;
import fr.android.exhibit.entities.LiteRecord;

/**
 * Created by Thibault on 08/01/2016.
 */
public class BluetoothLEService extends IntentService {
    private static final long LESCAN_DURATION = 2000;
    private static final long RUNS_BEFORE_EMPTY_MESSAGE = 5;
    public static final String EXTRA_BEACON_NAME = "beacon_name";
    private static final String LIGHTBLUEBEAN_UUID = "";
    private static final int LIGHTBLUEBEAN_MAJOR = 0;
    private static final int LIGHTBLUEBEAN_MINOR = 0;
    private static int count = 0;

    /*
    MAC ADRESSES
    D0:39:72:C8:C7:00 - IBEACON Bean - BLE Bean270
    B4:99:4C:1E:BC:07
     */
;
    private boolean mInterrupted = false;
    private Handler mThread;
    private static final String EMPTY_BEAN = "BeanVoid"; // name when no data
    private LocalBroadcastManager mBroadcastManager;
    private IBeacon mClosestBeacon;
    private LiteDevice mClosestDevice;
    private static BluetoothAdapter mAdapter;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            IBeacon beacon = IBeacon.fromScanData(scanRecord, rssi, device);
                            if(device.getName() != null)
                                Log.e("BLESERVICE DEVICE","FOUND ! "+device.getName());
                            if(device.getName()!= null && beacon != null)
                                Log.e("BLESERVICE BEACON","ACCURACY "+beacon.getAccuracy());
                            if (beacon == null && device.getName() != null
                                    && device.getName() != EMPTY_BEAN
                                    && isLightBlueBeanBluetooth(device)) { // #1 - If the device is running on normal BLE mode
                                Pattern pat = Pattern.compile("[0-4]{1}[:]");
                                Matcher mat = pat.matcher(device.getName());
                                if (mat.find()) {
                                    List<LiteDevice> bleDevices = LiteDevice.getByAddress(device.getAddress());
                                    LiteDevice bleDevice = null;
                                    if (bleDevices.size() > 0) // #1.1 - The device exists within the database
                                        bleDevice = bleDevices.get(0);
                                    else // #1.2 - The device is new
                                        bleDevice = new LiteDevice(device.getAddress());
                                    if (!bleDevice.isFullyRegistered()) { // #1.3 - The device lacks some parameters
                                        String[] split = device.getName().split(":", 2);
                                        bleDevice.addParameter(split[0].charAt(0), split[1]);
                                        bleDevice.save();
                                    }
                                }
                            } else if (beacon != null && device.getName() != null
                                    && device.getName() != EMPTY_BEAN
                                    && isLightBlueBeanBeacon(beacon)) { // #2 - If the device is running on beacon mod
                                List<LiteBeacon> bleBeacons = LiteBeacon.getByAddress(device.getAddress());
                                LiteBeacon bleBeacon = null;
                                if (bleBeacons.size() > 0) // #2.1 - The beacon exists within the database
                                    bleBeacon = bleBeacons.get(0);
                                else { // #2.2 - The beacon is new
                                    bleBeacon = new LiteBeacon(device.getName(), device.getAddress()
                                            , beacon.getProximityUuid(), beacon.getMajor(), beacon.getMinor());
                                    bleBeacon.save();
                                }
                                LiteRecord bleRecord = new LiteRecord(bleBeacon, beacon.getRssi(),
                                        beacon.getProximity(), beacon.getAccuracy(), beacon.getTxPower());
                                bleRecord.save();
                                if (mClosestBeacon == null || beacon.getAccuracy() < mClosestBeacon.getAccuracy()) {
                                    List<LiteDevice> bleDevices = LiteDevice.getByAddress(device.getAddress());
                                    if (bleDevices.size() > 0
                                            && bleDevices.get(0).isFullyRegistered()) {
                                        mClosestDevice = bleDevices.get(0);
                                        mClosestBeacon = beacon;
                                        count = 0;
                                    }
                                }
                            }
                        }
                    });
                }
            };

    private boolean isLightBlueBeanBluetooth(BluetoothDevice device) {
        if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE)
            if (!LiteBeacon.getByAddress(device.getAddress()).isEmpty())
                return true;
        return false;
    }

    private boolean isLightBlueBeanBeacon(IBeacon beacon) {
        if (beacon.getProximityUuid() == LIGHTBLUEBEAN_UUID
                && beacon.getMajor() == LIGHTBLUEBEAN_MAJOR
                && beacon.getMinor() == LIGHTBLUEBEAN_MINOR)
            return true;
        return false;
    }

    public BluetoothLEService() {
        super("BluetoothLEService");
        mThread = new Handler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int i = 1;
        mInterrupted = false;
        mClosestDevice = null;
        while(!mInterrupted) {
            try {
                synchronized (this) {
                    Log.e("LESCAN", "RUNNING LESCAN N" + String.valueOf(i));
                    mClosestBeacon = null;
                    mAdapter.startLeScan(null, mLeScanCallback);
                    wait(LESCAN_DURATION);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Log.e("LESCAN", "ENDING LESCAN N" + String.valueOf(i));
                i++;
                mAdapter.stopLeScan(mLeScanCallback);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(BluetoothLEReceiver.ACTION_BEACON_DISCOVERED);
                if(mClosestDevice != null)
                    broadcastIntent.putExtra(EXTRA_BEACON_NAME, mClosestDevice.getFullName());
                else
                    broadcastIntent.putExtra(EXTRA_BEACON_NAME, "");
                mBroadcastManager.sendBroadcast(broadcastIntent);
                if(count >= RUNS_BEFORE_EMPTY_MESSAGE)
                    mClosestDevice = null;
                else
                    count++;
            }
        }
    }

    private void runOnThread(Runnable runnable){
        mThread.post(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInterrupted = true;
        mAdapter.stopLeScan(mLeScanCallback);
        Log.e("BLESERVICE_CLASS", "Service ended.");
    }
}
