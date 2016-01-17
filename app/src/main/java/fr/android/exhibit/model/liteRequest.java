package fr.android.exhibit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

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
                .where("mac == ?",address)
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

    @Override
    public String toString() {
        return this.getId()+":"+this.mAddress+":["+this.mFile.toString()+"]";
    }
}
