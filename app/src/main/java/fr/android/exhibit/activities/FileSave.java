package fr.android.exhibit.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import fr.android.exhibit.model.LiteFile;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;

import fr.android.exhibit.services.BluetoothLEService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSave extends AppCompatActivity {
    NFCForegroundUtil nfcForegroundUtil = null;
    private List<BluetoothDevice> mDevices;
    private List<LiteFile> mSelectedFiles;
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
            mDevices = BluetoothLEService.getBluetoothDevices();
            nd.connect();
            NdefMessage ndfm = nd.getNdefMessage();
            NdefRecord ndr = ndfm.getRecords()[0];
            String mime = ndr.toMimeType();
            byte[] byt = ndr.getPayload();
            String msg = new String(byt, StandardCharsets.UTF_16);
            nd.close();
            BluetoothDevice device = BluetoothLEService.matchNFC(msg);
            if (msg.contains(MANAGER_BRACELET))
                mButtonManage.setVisibility(View.VISIBLE);
            else if (msg.contains(RESET_STRING)) {
                // @TODO: clear databases
                mTextViewInfo.setText("Données supprimées");
            } else if (true || device != null)
                if (saveRequest(msg))
                    mTextViewInfo.setText("Informations bien enregistrées");
                else
                    mTextViewInfo.setText("Informations non sauvegardées");
            else
                mTextViewInfo.setText("Périphérique Bluetooth LE introuvable");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private boolean saveRequest(String bluetoothName) {
        LiteFile
        LiteRequest request = new LiteRequest(bluetoothName, )
    }


}
