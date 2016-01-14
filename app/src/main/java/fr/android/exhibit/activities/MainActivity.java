package fr.android.exhibit.activities;

import android.content.Intent;
import android.exhibit.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import fr.android.exhibit.services.BluetoothLEService;

public class MainActivity extends AppCompatActivity {
    private View mView;
    private Intent mBLEService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(R.id.flWelcome);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileSelector.class);
                startActivity(intent);
            }
        });


        if(true || BluetoothLEService.launchBluetoothToast(this)) {
            mBLEService = new Intent(MainActivity.this, BluetoothLEService.class);
            startService(mBLEService);
        } else {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mBLEService);
    }
}
