package fr.android.exhibit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "Records")
public class LiteProximityRecord extends Model {
    @Column(name = "id_beacon")
    public LiteBluetoothBeacon mBeacon;

    @Column(name = "rssi")
    public int mRssi;

    @Column(name = "proximity")
    public int mProximity;

    @Column(name = "accuracy")
    public double mAccuracy;

    @Column(name = "tx_power")
    public int mTxPower;

    public LiteProximityRecord() {
        super();
    }

    public LiteProximityRecord(LiteBluetoothBeacon beacon, int rssi, int proximity, double accuracy, int txPower) {
        super();
        this.mBeacon = beacon;
        this.mRssi = rssi;
        this.mProximity = proximity;
        this.mAccuracy = accuracy;
        this.mTxPower = txPower;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
