package com.example.dots;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class BackgroundSoundService extends Service {

    private MediaPlayer player;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.mp3);
        player.setLooping(true);
        player.setVolume(1,1);
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        Menu.serviceStarted = true;
        return 1;
    }

    public void mute(){
        player.setVolume(0,0);
    }

    public void unmute(){
        player.setVolume(1,1);
    }

    public void onStart() {player.start();}

    public void onPause() {player.pause();}

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {}

    public class LocalBinder extends Binder {
        BackgroundSoundService getService() {
            return BackgroundSoundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
