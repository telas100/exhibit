package fr.android.exhibit.activities;

import android.content.Intent;
import android.exhibit.R;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.android.exhibit.adapters.FileDisplayAdapter;
import fr.android.exhibit.model.LiteFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSelector extends AppCompatActivity {
    private View mView;
    private Button mButtonSend;
    private Button mButtonReturn;
    private GridView mFileView;
    private static int mToSend = 0;
    private List<String> mFilesString;
    private static File mDirectory;
    private static List<File> mFiles;

    private static String FILE_LOCATION = "";

    private String[] mAvailableFiles={
            "File1", "File2",
            "File3", "File4",
            "File5"
    };

    private Integer[] mFileImageRessources = {
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);
        mView = findViewById(R.id.rlFileSelector);

        mButtonSend = (Button)findViewById(R.id.btnSend);
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

        mButtonReturn = (Button)findViewById(R.id.btnReturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileSelector.this.finish();
            }
        });

        mFileView = (GridView)findViewById(R.id.gvFiles);
        mFileView.setAdapter(new FileDisplayAdapter(this, mAvailableFiles, mFileImageRessources));

        mFilesString = new ArrayList<String>();
        mDirectory = new File(Environment.getExternalStorageDirectory().getPath()
                + String.format("/Android/data/?/files","com.android.exhibit"));
        mFiles = new ArrayList<File>();
    }

    public void onFileSelected(View view){
        CheckBox cb = (CheckBox)view.findViewById(R.id.cbFileSend);
        TextView tv = (TextView)view.findViewById(R.id.tvFileTitle);
        if(!cb.isChecked()) {
            mToSend++;
            cb.setChecked(true);
            mFilesString.add(tv.getText().toString());
            mButtonSend.setEnabled(true);
        } else {
            mToSend--;
            cb.setChecked(false);
            mFilesString.remove(tv.getText().toString());
            if(mToSend <= 0)
                mButtonSend.setEnabled(false);
        }
    }

    private static void retreiveFilesFromDrive() {
        File files[] = mDirectory.listFiles();
        for(File file : files){

        }

    }

}
