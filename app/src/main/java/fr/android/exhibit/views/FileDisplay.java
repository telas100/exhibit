package fr.android.exhibit.views;

/**
 * Created by Thibault on 20/01/2016.
 */
public class FileDisplay {
    public String mFileName;
    public Integer mImageRessource;

    public FileDisplay(String mFileName) {
        this.mFileName = mFileName;
        this.mImageRessource = android.R.drawable.ic_input_get;
    }

    public FileDisplay(String mFileName, Integer mImageRessource) {
        this.mFileName = mFileName;
        this.mImageRessource = mImageRessource;
    }
}
