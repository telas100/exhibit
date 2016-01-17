package fr.android.exhibit.activities;

import android.content.Intent;
import android.exhibit.R;
import android.graphics.Path;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import fr.android.exhibit.adapters.FileDisplayAdapter;
import fr.android.exhibit.model.LiteFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSelector extends AppCompatActivity {
    private View mView;
    private Button mButtonSend;
    private Button mButtonReturn;
    private GridView mFileView;
    private static int mToSend = 0;
    private static List<String> mFilesString;
    private static File mDirectory;
    private static List<File> mFiles;
    private static List<File> mAvailableFiles;
    private static List<Integer> mImageRessources;

    private final static String FILE_DIRECTORY = "/Android/data/?/files";
    private final static String FILE_PACKAGE = "fr.android.exhibit";
    private final static List<String> README_TXT =
            Arrays.asList("INSTRUCTIONS FOR SETTING UP THE EXHIBIT APP -->",
                    "PUT YOUR FILES IN THE CURRENT FOLDER",
                    "FORMAT SUPPORTED : JPEG, PDF, DOC, DOCX");
    private final static Map<String, Integer> IMAGE_RESSOURCES = new HashMap<String, Integer>() {
        {
            put("pdf", R.mipmap.ic_launcher);
            put("jpeg", R.mipmap.ic_launcher);
            put("jpg", R.mipmap.ic_launcher);
            put("doc", R.mipmap.ic_launcher);
            put("docx", R.mipmap.ic_launcher);
            put("default", R.mipmap.ic_launcher);
        }
    };


    /*private String[] mAvailableFiles={
            "File1", "File2",
            "File3", "File4",
            "File5"
    };*/

    /*private Integer[] mFileImageRessources = {
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);
        mView = findViewById(R.id.rlFileSelector);

        mButtonSend = (Button) findViewById(R.id.btnSend);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parcel = new String[mFilesString.size()];
                mFilesString.toArray(parcel);
                Intent intent = new Intent(FileSelector.this, FileSave.class);
                intent.putExtra("EXTRA_SELECTED_FILES", parcel);
                startActivity(intent);
            }
        });
        mButtonSend.setEnabled(false);

        mButtonReturn = (Button) findViewById(R.id.btnReturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileSelector.this.finish();
            }
        });

        mAvailableFiles = new ArrayList<File>();
        mImageRessources = new ArrayList<Integer>();
        this.retreiveFilesFromDrive();
        Integer[] mImages = new Integer[mImageRessources.size()];
        mImageRessources.toArray(mImages);

        mFileView = (GridView) findViewById(R.id.gvFiles);

        if(!mAvailableFiles.isEmpty() && !mImageRessources.isEmpty())
            mFileView.setAdapter(new FileDisplayAdapter(this, mAvailableFiles, mImages));
        else
            Toast.makeText(this,"Dossier vide",Toast.LENGTH_LONG);

        mFilesString = new ArrayList<String>();
        mFiles = new ArrayList<File>();
    }

    public void onFileSelected(View view) {
        CheckBox cb = (CheckBox) view.findViewById(R.id.cbFileSend);
        TextView tv = (TextView) view.findViewById(R.id.tvFileTitle);
        if (!cb.isChecked()) {
            mToSend++;
            cb.setChecked(true);
            mFilesString.add(tv.getText().toString());
            mButtonSend.setEnabled(true);
        } else {
            mToSend--;
            cb.setChecked(false);
            mFilesString.remove(tv.getText().toString());
            if (mToSend <= 0)
                mButtonSend.setEnabled(false);
        }
    }

    private static void retreiveFilesFromDrive() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            mDirectory = new File(Environment.getExternalStorageDirectory(),"/Android/data/fr.android.exhibit/files");
        else
            mDirectory = null;

        if (!mDirectory.exists()) {
            try {
                mDirectory.mkdirs();
                PrintWriter readme = new PrintWriter(mDirectory.getPath() + "/readme.txt");
                for (String str : README_TXT)
                    readme.println(str);
                readme.close();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File files[] = mDirectory.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    if (!file.getName().equals("readme.txt")) {
                        mAvailableFiles.add(file);
                        mImageRessources.add(IMAGE_RESSOURCES.get(getFileExtension(file)));
                        LiteFile lf = new LiteFile(file.getName());
                        lf.save();
                    }
                }
            }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "default";
    }
}
