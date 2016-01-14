package fr.android.exhibit.model;

import java.io.Serializable;

/**
 * Created by Thibault on 12/01/2016.
 */
public class SavedUserFiles implements Serializable {
    private String mUser;
    private String[] mFiles;

    public SavedUserFiles(String user, String[] files) {
        mUser = user;
        mFiles = files;
    }

    @Override
    public String toString() {
        String ret = "";
        ret = ret.concat(mUser);
        for (String file : mFiles)
            ret = ret.concat(":"+file);
        return ret;
    }

    public String getUser() {
        return mUser;
    }

    public String[] getFiles() {
        return mFiles;
    }
}