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

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name ="Requests")
public class LiteRequest extends Model {
    @Column(name = "mac")
    public String mAddress;

    @Column(name = "id_file")
    public LiteFile mFile;

    public LiteRequest() {
        super();
    }

    public LiteRequest(String address, LiteFile file) {
        super();
        this.mAddress = address;
        this.mFile = file;
    }

    public static List<LiteRequest> getByAddress(String address) {
        return new Select()
                .all()
                .from(LiteRequest.class)
                .where("mac == ?", address)
                .execute();
    }

    public static List<LiteRequest> getAll() {
        return new Select()
                .all()
                .from(LiteRequest.class)
                .execute();
    }

    public static Integer getRequestCount() {
        return new Select("mac")
                .distinct()
                .from(LiteRequest.class)
                .groupBy("mac")
                .execute()
                .size();
    }

    public static Map<String,Integer> getMostRequestedFiles() {
        Map<String,Integer> map = new HashMap<String, Integer>();
        Cursor proximities = ActiveAndroid.getDatabase()
                .rawQuery("SELECT name, COUNT(*) AS total " +
                        "FROM Requests r " +
                        "LEFT JOIN Files f " +
                        "ON r.id_file == f.id " +
                        "GROUP BY name " +
                        "ORDER BY total; ", null);
        proximities.moveToFirst();
        while(!proximities.isAfterLast()){
            String filename = proximities.getString(0);
            Integer count = proximities.getInt(1);
            map.put(filename,count);
            proximities.moveToNext();
        }
        return map;
    }


    @Override
    public String toString() {
        return this.getId()+":"+this.mAddress+":["+this.mFile.toString()+"]";
    }
}
