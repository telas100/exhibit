package fr.android.exhibit.activities;

import android.exhibit.R;

import fr.android.exhibit.model.LiteBluetoothBeacon;
import fr.android.exhibit.model.LiteBluetoothDevice;
import fr.android.exhibit.model.LiteRequest;
import fr.android.exhibit.model.SavedUserFiles;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReturnOnExperience extends AppCompatActivity {
    private TableLayout mTableLayoutEmailFiles;
    private TextView mTextViewElapsedTime;
    private TextView mTextViewRequestCount;
    private TextView mTextViewProximities;
    private TextView mTextViewMostRequestedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_on_experience);
        mTableLayoutEmailFiles = (TableLayout) findViewById(R.id.tbEmailFiles);
        populateRequestedFilesTable();
        mTextViewElapsedTime = (TextView)findViewById(R.id.tvElapsedTime);
        mTextViewRequestCount= (TextView)findViewById(R.id.tvRequestCount);
        mTextViewProximities = (TextView)findViewById(R.id.tvProximities);
        mTextViewMostRequestedFile = (TextView)findViewById(R.id.tvMostRequestedFiles);

        long elapsedTime = System.currentTimeMillis() - MainActivity.START_TIME;
        mTextViewElapsedTime.setText(elapsedTime / 3600000+":"+elapsedTime / 60000+":"+elapsedTime / 1000);

        mTextViewRequestCount.setText(LiteRequest.getRequestCount().toString());

    }

    private void populateRequestedFilesTable(){

        ArrayList<LiteBluetoothDevice> devices = (ArrayList<LiteBluetoothDevice>)LiteBluetoothDevice.getAll();
        if(devices != null) {
            for (LiteBluetoothDevice lbd : devices) {
                Log.e("BLUETOOTHDEVICELITE", lbd.toString());
                if (lbd != null && lbd.mAddress != null) {
                    TableRow tr = new TableRow(this);
                    TextView tvEmail = new TextView(this);
                    TextView tvFiles = new TextView(this);
                    tvEmail.setText(lbd.mName);
                    tvFiles.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tvEmail.setPadding(20,0,0,0);
                    ArrayList<LiteRequest> requests = (ArrayList<LiteRequest>)LiteRequest.getByAddress(lbd.mAddress);
                    StringBuilder files = new StringBuilder();
                    if(requests != null) {
                        for (LiteRequest lr : requests) {
                            files.append(lr.mFile.mName);
                            files.append("\n");
                        }
                        tvFiles.setText(files.toString());
                    }
                    tr.addView(tvEmail);
                    tr.addView(tvFiles);
                    mTableLayoutEmailFiles.addView(tr);
                }
            }
        }
        ArrayList<LiteRequest> requests = (ArrayList<LiteRequest>)LiteRequest.getAll();
        for(LiteRequest request : requests) {
            Log.e("BLUETOOTHREQUESTLITE",request.toString());
        }
    }
}
