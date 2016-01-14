package fr.android.exhibit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name ="Requests")
public class LiteRequest extends Model {
    @Column(name = "mac")
    public String mAdress;

    @Column(name = "id_file")
    public LiteFile mFile;

    public LiteRequest() {
        super();
    }

    public LiteRequest(String adress, LiteFile file) {
        super();
        this.mAdress = adress;
        this.mFile = file;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
