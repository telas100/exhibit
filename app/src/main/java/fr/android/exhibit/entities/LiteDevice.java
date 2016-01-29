package fr.android.exhibit.entities;

import android.database.Cursor;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "Devices")
public class LiteDevice extends Model {
    @Column(name = "gender")
    public Character mGender;

    @Column(name = "dob")
    public Date mDateOfBirth;

    @Column(name = "name")
    public String mName;

    @Column(name = "fname")
    public String mFirstName;

    @Column(name = "email")
    public String mEmail;

    @Column(name = "society")
    public String mSociety;

    @Column(name = "mac")
    public String mAddress;

    public LiteDevice() {
        super();
    }

    public LiteDevice(String address) {
        super();
        this.mAddress = address;
    }

    public LiteDevice(Character mGender, Date mDateOfBirth, String mName,
                      String mFirstName, String mEmail, String mSociety, String mAddress) {
        this.mGender = mGender;
        this.mDateOfBirth = mDateOfBirth;
        this.mName = mName;
        this.mFirstName = mFirstName;
        this.mEmail = mEmail;
        this.mSociety = mSociety;
        this.mAddress = mAddress;
    }

    public void addParameter(Character index, String value) {
        switch(index) {
            case '0':
                if(mGender == null || mDateOfBirth == null)
                    this.setGDOB(value);
                break;
            case '1':
                if(mFirstName == null)
                    mFirstName = value;
                break;
            case '2':
                if(mName == null)
                    mName = value;
                break;
            case '3':
                if(mEmail == null)
                    mEmail = value;
                break;
            case '4':
                if(mSociety == null)
                    mSociety = value;
                break;
            default:
                Log.e("LITEDEVICE.CLASS", "WRONG INDEX");
        }
    }

    private void setGDOB(String value) {
        String[] split = value.split(":",2);
        try {
            mGender = Character.toUpperCase(split[0].charAt(0));
            mDateOfBirth = new SimpleDateFormat("ddMMyyyy").parse(split[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<LiteDevice> getByName(String name) {
        return new Select()
                .from(LiteDevice.class)
                .where("name == ?", name)
                .execute();
    }

    public static List<LiteDevice> getByAddress(String address) {
        return new Select()
                .from(LiteDevice.class)
                .where("mac == ?", address)
                .execute();
    }

    public static List<LiteDevice> getAll() {
        return new Select()
                .all()
                .from(LiteDevice.class)
                .execute();
    }

    public static TreeMap<Integer,Integer> getCountByAge() {
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        TreeMap<Integer,Integer> map = new TreeMap<Integer, Integer>();
        List<LiteDevice> allList = getAll();
        for(LiteDevice ld : allList) {
            Integer age = currentYear - ld.mDateOfBirth.getYear() - 1900;
            if(!map.containsKey(age)) {
                map.put(age,1);
            } else {
                int value = map.get(age);
                map.remove(age);
                map.put(age,value+1);
            }
        }
        return map;
    }

    @Override
    public String toString() {
        return this.getId()+":"+this.mName+":"+this.mAddress;
    }

    public boolean isFullyRegistered() {
        if(mGender == null
                || mDateOfBirth == null
                || mName == null
                || mFirstName == null
                || mEmail == null
                || mSociety == null)
            return false;
        return true;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append((mGender == 'F') ? "Mme. " : "M. " );
        sb.append(mName+" ");
        sb.append(mFirstName);
        return sb.toString();
    }
}
