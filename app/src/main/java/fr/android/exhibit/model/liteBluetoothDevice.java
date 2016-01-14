package fr.android.exhibit.model;

import android.bluetooth.BluetoothDevice;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "BluetoothDevices")
public class LiteBluetoothDevice extends Model {
    @Column(name = "name")
    public String mName;

    @Column(name = "mac")
    public String mAddress;

    public LiteBluetoothDevice() {
        super();
    }

    public LiteBluetoothDevice(String name, String address) {
        super();
        this.mName = name;
        this.mAddress = address;
    }

    public static List<LiteBluetoothDevice> getByName(String name) {
        return new Select()
                .from(LiteBluetoothDevice.class)
                .where("name == ?", name)
                .execute();
    }

    public static List<LiteBluetoothDevice> getByAddress(String address) {
        return new Select()
                .from(LiteBluetoothDevice.class)
                .where("mac == ?", address)
                .execute();
    }

    public static List<LiteBluetoothDevice> getAll() {
        return new Select()
                .all()
                .from(LiteBluetoothDevice.class)
                .execute();
    }

    @Override
    public String toString() {
        return this.getId()+":"+this.mName+":"+this.mAddress;
    }
}
