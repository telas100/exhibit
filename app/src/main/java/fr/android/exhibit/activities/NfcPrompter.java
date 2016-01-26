package fr.android.exhibit.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.activeandroid.query.Delete;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.android.exhibit.entities.LiteBeacon;
import fr.android.exhibit.entities.LiteDevice;
import fr.android.exhibit.entities.LiteFile;
import fr.android.exhibit.entities.LiteRecord;
import fr.android.exhibit.entities.LiteRequest;
import fr.android.exhibit.services.BluetoothLEService;
import fr.android.exhibit.services.NFCForegroundUtil;

public class NfcPrompter extends AppCompatActivity {

    private static final CharSequence MANAGER_BRACELET = "DEDE";
    private static final CharSequence RESET_STRING = "RESET";
    private static AlphaAnimation mBlink;
    private static TextView mPrompter;
    private NFCForegroundUtil nfcForegroundUtil;
    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<String> mSelectedFiles;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_prompter);

        mPrompter = (TextView)findViewById(R.id.nfcp_tv_prompter);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NfcPrompter.this, ManagerReport.class);
                startActivity(intent);
            }
        });
        mFab.setVisibility(View.INVISIBLE);

        mBlink = new AlphaAnimation(0.0f, 1.0f);
        mBlink.setDuration(2000);
        mBlink.setStartOffset(20);
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(Animation.INFINITE);

        mPrompter.startAnimation(mBlink);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            String mac = new String(byt, StandardCharsets.UTF_16);
            nd.close();
            if (mac.contains(MANAGER_BRACELET)) {
                mFab.setVisibility(View.VISIBLE);
                mPrompter.setText("Cliquez sur le bouton pour accéder à l'interface manager.");
                mPrompter.clearAnimation();
                mFab.startAnimation(mBlink);
            } else if (mac.contains(RESET_STRING)) {
                this.resetDatabase();
                mPrompter.setText("Données supprimées, veuillez relancer l'application.");
            } else {
                Pattern pat = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
                Matcher mat = pat.matcher(mac);
                if (mat.find()) {
                    mac = mat.group();
                    List<LiteDevice> bleDevices = LiteDevice.getByAddress(mac);
                    if (!bleDevices.isEmpty()) {
                        saveRequest(mac);
                        mPrompter.setText("Informations bien enregistrées, merci de votre visite !");
                    } else
                        mPrompter.setText("Périphérique Bluetooth introuvable.");
                } else
                    mPrompter.setText("Erreur dans le paramétrage de la puce NFC.");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private void saveRequest(String bluetoothName) {
        for(String filename : mSelectedFiles) {
            List<LiteFile> lfs = LiteFile.getByName(filename);
            if(lfs.isEmpty()) {
                LiteFile lf = new LiteFile(filename);
                lf.save();
                lfs.add(lf);
            }
            LiteRequest lr = new LiteRequest(bluetoothName, lfs.get(0));
            lr.save();
        }
    }

    private static void resetDatabase() {
        new Delete().from(LiteRequest.class).execute();
        new Delete().from(LiteRecord.class).execute();
        new Delete().from(LiteBeacon.class).execute();
        new Delete().from(LiteDevice.class).execute();
        new Delete().from(LiteFile.class).execute();
    }

}
