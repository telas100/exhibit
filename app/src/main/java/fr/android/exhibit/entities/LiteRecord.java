package fr.android.exhibit.entities;

import android.database.Cursor;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "Records")
public class LiteRecord extends Model {
    @Column(name = "id_beacon")
    public LiteBeacon mBeacon;

    @Column(name = "rssi")
    public int mRssi;

    @Column(name = "proximity")
    public int mProximity;

    @Column(name = "accuracy")
    public double mAccuracy;

    @Column(name = "tx_power")
    public int mTxPower;

    public LiteRecord() {
        super();
    }

    public LiteRecord(LiteBeacon beacon, int rssi, int proximity, double accuracy, int txPower) {
        super();
        this.mBeacon = beacon;
        this.mRssi = rssi;
        this.mProximity = proximity;
        this.mAccuracy = accuracy;
        this.mTxPower = txPower;
    }

    public static Map<Integer,Integer> getProximitiesCountsByAddress() {
        Map<Integer,Integer> map = new HashMap<Integer, Integer>();
        Cursor proximities = ActiveAndroid.getDatabase()
                .rawQuery("SELECT proximity, COUNT(*) AS total " +
                        "FROM Records " +
                        "LEFT JOIN (" +
                        "SELECT bb.id as id, bb.name as beacon, bd.mac, bd.name AS device " +
                        "FROM BluetoothBeacons bb " +
                        "LEFT JOIN BluetoothDevices bd " +
                        "ON bb.mac == bd.mac) AS bbd " +
                        "ON id_beacon == bbd.id " +
                        "GROUP BY proximity,mac " +
                        "ORDER BY total; ", null);
        proximities.moveToFirst();
        while(!proximities.isAfterLast()){
            Integer proximity = proximities.getInt(0);
            Integer count = proximities.getInt(1);
            map.put(proximity,count);
            proximities.moveToNext();
        }
        return map;
    }

    public static List<LiteRecord> getAll() {
        return new Select()
                .all()
                .from(LiteRecord.class)
                .execute();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static TreeMap<Integer,Integer> getProximityMatrix() {
        List<LiteRecord> records = getAll();
        TreeMap<Integer,Integer> map = new TreeMap<Integer, Integer>();
        for (LiteRecord lr : records) {
            int acc = (int)lr.mAccuracy;
            if(!map.containsKey(acc)) {
                map.put(acc,1);
            } else {
                int value = map.get(acc);
                map.remove(acc);
                map.put(acc,value+1);
            }
        }
        return map;
    }

}
