package android.exhibit;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            mDevices = BluetoothLEService.getBluetoothDevices();
            nd.connect();
            NdefMessage ndfm = nd.getNdefMessage();
            NdefRecord ndr = ndfm.getRecords()[0];
            String mime = ndr.toMimeType();
            byte[] byt = ndr.getPayload();
            String msg = new String(byt, StandardCharsets.UTF_16);
            nd.close();
            BluetoothDevice device = BluetoothLEService.matchNFC(msg);
            if(msg.contains(MANAGER_BRACELET))
                mButtonManage.setVisibility(View.VISIBLE);
            else if(msg.contains(RESET_STRING)) {
                createFile();
                mTextViewInfo.setText("Données supprimées");
            } else if(true ||device != null)
                if(saveUser(msg))
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

    private boolean saveUser(String bluetoothName) {
        try
        {
            String[] strFiles = new String[mSelectedFiles.size()];
            mSelectedFiles.toArray(strFiles);
            SavedUserFiles suf = new SavedUserFiles(bluetoothName,strFiles);
            FileOutputStream filestream = null;
            ObjectOutputStream objectstream = null;
            filestream = openFileOutput("user_files.ser",MODE_APPEND);
            objectstream = new ObjectOutputStream(filestream){
                protected void writeStreamHeader() throws IOException {
                    reset();
                }
            };
            objectstream.writeObject(suf);
            objectstream.close();
            return true;
        }catch(IOException i) {
            i.printStackTrace();
        }
        return false;
    }

    private void createFile() {
        deleteFile("user_files.ser");
        FileOutputStream output = null;
        try {
            output = openFileOutput("user_files.ser",MODE_WORLD_READABLE);
            ObjectOutputStream objectstream = null;
            objectstream = new ObjectOutputStream(output);
            SavedUserFiles header = new SavedUserFiles("BORNE "+MANAGER_BORNE,new String[]{MANAGER_BRAND, MANAGER_NAME});
            SavedUserFiles suf = new SavedUserFiles("ID BRACELET ",new String[]{"FILES REQUIRED"});
            objectstream.writeObject(header);
            objectstream.writeObject(suf);
            objectstream.reset();
            objectstream.close();
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
