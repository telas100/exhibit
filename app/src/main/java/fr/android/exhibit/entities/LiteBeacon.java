package fr.android.exhibit.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "Beacons")
public class LiteBeacon extends Model {
    @Column(name = "name")
    public String mName;

    @Column(name = "uuid")
    public String mUuid;

    @Column(name = "major")
    public int mMajor;

    @Column(name = "minor")
    public int mMinor;

    @Column(name = "mac")
    public String mAddress;

    public LiteBeacon() {
        super();
    }

    public LiteBeacon(String name, String address, String uuid, int major, int minor) {
        super();
        this.mName = name;
        this.mAddress = address;
        this.mUuid = uuid;
        this.mMajor = major;
        this.mMinor = minor;
    }

    public static List<LiteBeacon> getAll() {
        return new Select()
                .all()
                .from(LiteBeacon.class)
                .execute();
    }

    public static List<LiteBeacon> getByAddress(String mac){
        return new Select()
                .from(LiteBeacon.class)
                .where("mac == ?", mac)
                .execute();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
