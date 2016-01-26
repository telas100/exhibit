package fr.android.exhibit.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

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
        return this.getId()+":"+this.mName;
    }

    public static List<LiteFile> getByName(String filename){
        return new Select()
                .from(LiteFile.class)
                .where("name == ?",filename)
                .execute();
    }

    public static List<LiteFile> getAll() {
        return new Select()
                .all()
                .from(LiteFile.class)
                .execute();
    }
}
