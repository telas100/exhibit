package fr.android.exhibit.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;

import fr.android.exhibit.services.BluetoothLEReceiver;
import fr.android.exhibit.services.BluetoothLEService;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mLayout;
    private TextView mTvName;
    private TextView mTvContinue;
    private ProgressBar mPbSpinner;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private Animation mBlink;
    private Intent mBluetoothService;
    private LocalBroadcastManager mBroadcastManager;
    private BluetoothLEReceiver mReceiver;
    private String mCurrentName = "";

    protected void fadeOutName() {
        mPbSpinner.setAlpha(1.0f);
        mPbSpinner.startAnimation(mFadeIn);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTvName.setAlpha(0.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mTvName.startAnimation(mFadeOut);
    }

    protected void fadeInName() {
        mTvName.setAlpha(1.0f);
        mFadeIn.setDuration(20);
        mTvName.startAnimation(mFadeIn);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPbSpinner.setAlpha(0.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mPbSpinner.startAnimation(mFadeOut);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ActiveAndroid.dispose();
        ActiveAndroid.initialize(this);

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLayout = (RelativeLayout)findViewById(R.id.ma_rl_layout);

        mTvName = (TextView)findViewById(R.id.ma_tv_name);
        mTvContinue = (TextView)findViewById(R.id.ma_tv_continue);
        mPbSpinner = (ProgressBar)findViewById(R.id.ma_pb_spinner);

        mBlink = new AlphaAnimation(0.0f, 1.0f);
        mBlink.setDuration(1000);
        mBlink.setStartOffset(20);
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(Animation.INFINITE);

        mFadeIn = new AlphaAnimation(0.0f, 1.0f);
        mFadeIn.setDuration(1000);
        mFadeIn.setStartOffset(20);

        mFadeOut = new AlphaAnimation(1.0f, 0.0f);
        mFadeOut.setDuration(1000);
        mFadeOut.setStartOffset(20);

        mBluetoothService = new Intent(MainActivity.this, BluetoothLEService.class);
        this.startService(mBluetoothService);

        mTvContinue.startAnimation(mBlink);
        mTvName.setAlpha(0.0f);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilePicker.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter broadcastFilter
                = new IntentFilter(BluetoothLEReceiver.ACTION_BEACON_DISCOVERED);
        mReceiver = new BluetoothLEReceiver(this);
        mBroadcastManager.registerReceiver(mReceiver, broadcastFilter);
        this.startService(mBluetoothService);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBroadcastManager.unregisterReceiver(mReceiver);
        // this.stopService(mBluetoothService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mReceiver);
        // this.stopService(mBluetoothService);
    }

    public void onBeaconReceived(String beaconName) {
        mTvName.setText(beaconName);
        if(beaconName.equals("") && mTvName.getAlpha() > 0)
            fadeOutName();
        if(!beaconName.equals("") && mTvName.getAlpha() == 0)
            fadeInName();
    }
}
