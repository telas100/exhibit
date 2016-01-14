package fr.android.exhibit.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Thibault on 14/01/2016.
 */
@Table(name = "Files")
public class LiteFile extends Model {
    @Column(name = "name")
    public String mName;

    public LiteFile(){
        super();
    }

    public LiteFile(String name){
        super();
        this.mName = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
