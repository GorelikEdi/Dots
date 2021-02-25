package com.example.dots;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    public static boolean MOVED_TO_ACTIVITY = false;
    public static String VOLUME = "unmuted";
    public static boolean IS_RIGHT_TO_LEFT;
    public static boolean serviceStarted = false;
    public static BackgroundSoundService mBoundService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((BackgroundSoundService.LocalBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IS_RIGHT_TO_LEFT = getResources().getBoolean(R.bool.is_right_to_left);

        if (Menu.IS_RIGHT_TO_LEFT)
            setContentView(R.layout.menu_heb);
        else
            setContentView(R.layout.menu);

        Intent svc = new Intent(this, BackgroundSoundService.class);
        svc.setAction("com.example.BackgroundSoundService");
        startService(svc);
        bindService(svc, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void play(View view){
        Intent intent = new Intent(this, Levels.class);
        MOVED_TO_ACTIVITY = true;
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void highScores(View view){
        Intent intent = new Intent(this, HighScores.class);
        MOVED_TO_ACTIVITY = true;
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void options(View view) {
        Intent intent = new Intent(this, Options.class);
        MOVED_TO_ACTIVITY = true;
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serviceStarted && !MOVED_TO_ACTIVITY)
            mBoundService.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (serviceStarted && !MOVED_TO_ACTIVITY) {
            mBoundService.onStart();
        }
    }
}
