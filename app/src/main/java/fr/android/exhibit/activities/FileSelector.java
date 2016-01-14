package fr.android.exhibit.activities;

import android.content.Intent;
import android.exhibit.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.android.exhibit.adapters.FileDisplayAdapter;

import java.util.ArrayList;
import java.util.List;

public class FileSelector extends AppCompatActivity {
    private View mView;
    private Button mButtonSend;
    private Button mButtonReturn;
    private GridView mFileView;
    private static int mToSend = 0;
    private List<String> mFiles;

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
                String[] parcel = new String[mFiles.size()];
                mFiles.toArray(parcel);
                Intent intent = new Intent(FileSelector.this, FileSave.class);
                intent.putExtra("EXTRA_SELECTED_FILES",parcel);
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

        mFiles = new ArrayList<String>();
    }

    public void onFileSelected(View view){
        CheckBox cb = (CheckBox)view.findViewById(R.id.cbFileSend);
        TextView tv = (TextView)view.findViewById(R.id.tvFileTitle);
        if(!cb.isChecked()) {
            mToSend++;
            cb.setChecked(true);
            mFiles.add(tv.getText().toString());
            mButtonSend.setEnabled(true);
        } else {
            mToSend--;
            cb.setChecked(false);
            mFiles.remove(tv.getText().toString());
            if(mToSend <= 0)
                mButtonSend.setEnabled(false);
        }
    }

}
