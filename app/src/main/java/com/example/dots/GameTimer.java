package com.example.dots;

import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class GameTimer extends TimerTask {

    private static int mins = 0;
    private static int secs = 0;
    private String time;
    private TextView timerTextView;
    private static Timer timer;
    public GameTimer(TextView timerText){
        this.timerTextView = timerText;
    }

    public void startTimer() {
       timer = new Timer(true);
       GameTimer gameTimer = new GameTimer(timerTextView);
       timer.scheduleAtFixedRate(gameTimer, 0, 1000);
    }

    @Override
    public void run() {
       String minsStr;
       String secsStr;
        if (secs < 10)
            secsStr = "0" + String.valueOf(secs);
        else
            secsStr = String.valueOf(secs);
        if (mins < 10)
            minsStr = "0" + String.valueOf(mins);
        else
            minsStr = String.valueOf(mins);
        time = minsStr + ":" + secsStr;
        timerTextView.setText(time);
        secs++;
        if (secs == 59){
            secs = 0;
            mins++;
        }
    }

    public void pauseTimer(){
        timer.cancel();
    }

    public void resumeTimer(){
        startTimer();
    }

    public void stopTimer(){
       mins = 0;
       secs = 0;
       timer.cancel();
    }
}
