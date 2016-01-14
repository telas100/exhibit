package fr.android.exhibit.activities;

import android.exhibit.R;

import fr.android.exhibit.model.LiteBluetoothBeacon;
import fr.android.exhibit.model.LiteBluetoothDevice;
import fr.android.exhibit.model.SavedUserFiles;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class ReturnOnExperience extends AppCompatActivity {
    private TextView mTextViewReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_on_experience);
        mTextViewReturn = (TextView) findViewById(R.id.tvReturnOnExperience);
        mTextViewReturn.setText(retreiveData());
        mTextViewReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewReturn.setText(retreiveData2());
            }
        });
    }

    private String retreiveData() {
        StringBuilder sb = new StringBuilder();
        ArrayList<LiteBluetoothDevice> devices = (ArrayList<LiteBluetoothDevice>)LiteBluetoothDevice.getAll();
        for(LiteBluetoothDevice lbd : devices)
            sb.append(lbd.toString());
        return sb.toString();
    }

    private String retreiveData2() {
        StringBuilder sb = new StringBuilder();
        ArrayList<LiteBluetoothBeacon> devices = (ArrayList<LiteBluetoothBeacon>) LiteBluetoothBeacon.getAll();
        for(LiteBluetoothBeacon lbd : devices)
            sb.append(lbd.toString());
        return sb.toString();
    }
}
