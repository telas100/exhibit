package fr.android.exhibit.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import fr.android.exhibit.model.LiteBluetoothBeacon;
import fr.android.exhibit.model.LiteBluetoothDevice;
import fr.android.exhibit.model.LiteFile;
import fr.android.exhibit.model.LiteProximityRecord;
import fr.android.exhibit.model.LiteRequest;
import fr.android.exhibit.services.NFCForegroundUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.exhibit.R;
import fr.android.exhibit.model.SavedUserFiles;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;

import fr.android.exhibit.services.BluetoothLEService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSave extends AppCompatActivity {
    NFCForegroundUtil nfcForegroundUtil = null;
    private List<BluetoothDevice> mDevices;
    private List<String> mSelectedFiles;
    private Button mButtonManage;
    private TextView mTextViewInfo;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static final String MANAGER_BRAND = "DECATHLON";
    private static final String MANAGER_NAME = "JEAN-MARC DESPANIER";
    private static final String MANAGER_BRACELET = "DEDE";
    private static final String MANAGER_BORNE = "N124";
    private static final String RESET_STRING = "RESET";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_save);
        mTextViewInfo = (TextView)findViewById(R.id.tvSaveMessage);
        mButtonManage = (Button)findViewById(R.id.btnManage);
        mButtonManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileSave.this,ReturnOnExperience.class);
                startActivity(intent);
            }
        });
        nfcForegroundUtil = new NFCForegroundUtil(this);
        mDevices = new ArrayList<BluetoothDevice>();
        String[] files = (String[])this.getIntent().getExtras().get("EXTRA_SELECTED_FILES");
        mSelectedFiles = new ArrayList<String>();
        mSelectedFiles.addAll(Arrays.asList(files));
    }

    public void onPause() {
        super.onPause();
        nfcForegroundUtil.disableForeground();
    }

    public void onResume() {
        super.onResume();
        nfcForegroundUtil.enableForeground();
    }

    public void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef nd = Ndef.get(tag);
        try {
            nd.connect();
            NdefMessage ndfm = nd.getNdefMessage();
            NdefRecord ndr = ndfm.getRecords()[0];
            String mime = ndr.toMimeType();
            byte[] byt = ndr.getPayload();
            String msg = new String(byt, StandardCharsets.UTF_16);
            nd.close();
            if (msg.contains(MANAGER_BRACELET))
                mButtonManage.setVisibility(View.VISIBLE);
            else if (msg.contains(RESET_STRING)) {
                this.resetDatabase();
                mTextViewInfo.setText("Données supprimées, veuillez relancer l'application.");
            } else {
                Pattern pat = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
                Matcher mat = pat.matcher(msg);
                if (mat.find()) {
                    msg = mat.group();
                    LiteBluetoothDevice device = BluetoothLEService.matchNFC(msg);
                    if (device != null) {
                        saveRequest(device.mAddress);
                        mTextViewInfo.setText("Informations bien enregistrées");
                    } else
                        mTextViewInfo.setText("Périphérique Bluetooth introuvable");
                } else
                    mTextViewInfo.setText("Erreur dans le paramétrage de la puce NFC");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private void saveRequest(String bluetoothName) {
        for(String filename : mSelectedFiles) {
            LiteFile lf = LiteFile.getByName(filename).get(0);
            LiteRequest lr = new LiteRequest(bluetoothName, lf);
            lr.save();
        }
    }

    private static void resetDatabase() {
        new Delete().from(LiteRequest.class).execute();
        new Delete().from(LiteProximityRecord.class).execute();
        new Delete().from(LiteBluetoothBeacon.class).execute();
        new Delete().from(LiteBluetoothDevice.class).execute();
        new Delete().from(LiteFile.class).execute();
    }


}
