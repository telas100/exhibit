package android.exhibit;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;

/**
 * Created by Thibault on 08/01/2016.
 */
public class BluetoothLEService extends IntentService {
    /*
    MAC ADRESSES
    D0:39:72:C8:C7:00

     */

    private Handler mThread;
    private static List<BluetoothDevice> mBluetoothDevices;
    private static BluetoothAdapter mAdapter;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE && !lookForDevice(device)) {
                                mBluetoothDevices.add(device);
                                Map<Integer,String> record = parseRecord(scanRecord);
                                Log.e("BLEDEVICE"+device.getName(), "RECORD: " + recordToString(record));
                                for(byte rec : scanRecord) {
                                    Log.e("|--BLEDEVICE", "--------: " + Byte.toString(rec));
                                }
                            }
                        }
                    });
                }
            };
    static public  Map <Integer,String>  parseRecord(byte[] scanRecord) {
        Map<Integer, String> ret = new HashMap<Integer, String>();
        int index = 0;
        while (index < scanRecord.length) {
            int length = scanRecord[index++];
            if (length == 0) break;

            int type = scanRecord[index];
            if (type == 0) break;

            byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);
            if (data != null && data.length > 0) {
                StringBuilder hex = new StringBuilder(data.length * 2);
                for (int bb = data.length - 1; bb >= 0; bb--) {
                    hex.append(String.format("%02X", data[bb]));
                }
                ret.put(type, hex.toString());
            }
            index += length;
        }

        return ret;
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public String recordToString(Map<Integer, String> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<Integer, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, String> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();
    }

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

    @Override
    protected void onHandleIntent(Intent intent) {
        int i = 1;
        while(true) {
            try {
                synchronized (this) {
                    Log.e("LESCAN","RUNNING LESCAN N"+String.valueOf(i));
                    mAdapter.startLeScan(null, mLeScanCallback);
                    wait(20000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                i++;
                Log.e("LESCAN","ENDING LESCAN N"+String.valueOf(i));
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
