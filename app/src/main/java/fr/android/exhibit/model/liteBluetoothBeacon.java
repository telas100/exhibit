package fr.android.exhibit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "BluetoothBeacons")
public class LiteBluetoothBeacon extends Model {
    @Column(name = "name")
    public String mName;

    @Column(name = "mac")
    public String mAddress;

    @Column(name = "uuid")
    public String mUuid;

    @Column(name = "major")
    public int mMajor;

    @Column(name = "minor")
    public int mMinor;

    public LiteBluetoothBeacon() {
        super();
    }

    public LiteBluetoothBeacon(String name, String address, String uuid, int major, int minor) {
        super();
        this.mName = name;
        this.mAddress = address;
        this.mUuid = uuid;
        this.mMajor = major;
        this.mMinor = minor;
    }

    public static List<LiteBluetoothBeacon> getAll() {
        return new Select()
                .all()
                .from(LiteBluetoothBeacon.class)
                .execute();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
