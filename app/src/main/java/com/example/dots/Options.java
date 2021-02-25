package com.example.dots;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Options extends AppCompatActivity {

    private DB db = new DB(Options.this);
    private TextView muteUnmute;
    private TextView resetLevels;
    private TextView resetScore;
    private TextView deleteScores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Menu.IS_RIGHT_TO_LEFT)
            setContentView(R.layout.options_heb);
        else
            setContentView(R.layout.options);

        Menu.MOVED_TO_ACTIVITY = false;
        muteUnmute = findViewById(R.id.muteUnmute);
        resetLevels = findViewById(R.id.reset_levels);
        resetScore = findViewById(R.id.reset_score);
        deleteScores = findViewById(R.id.delete_scores);
        if (Menu.VOLUME.equals("mute"))
            muteUnmute.setText(R.string.unmute);
        else
            muteUnmute.setText(R.string.mute);
    }

    public void deleteScores(View view) throws InterruptedException {
        db.delete("SCORES");
        clickColorChange(deleteScores);
    }

    public void resetScore(View view){
        int currentScore = db.read("CURRENT_SCORE").get(0);
        db.updateScore(currentScore, 0);
        clickColorChange(resetScore);
    }

    public void resetLevels(View view){
        int currentScore = db.read("CURRENT_SCORE").get(0);
        db.updateScore(currentScore, 0);
        db.updateLevel(0);
        clickColorChange(resetLevels);
    }

    public void muteUnmute(View view){
        if (muteUnmute.getText().equals(getResources().getString(R.string.mute))) {
            Menu.mBoundService.mute();
            Menu.VOLUME = "mute";
            muteUnmute.setText(R.string.unmute);
        }
        else {
            Menu.mBoundService.unmute();
            Menu.VOLUME = "unmuted";
            muteUnmute.setText(R.string.mute);
        }
    }

    public void home(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onPause();
    }

    private void clickColorChange(final TextView textView) {
        textView.setTextColor(ContextCompat.getColor(this, (R.color.colorAccent)));
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            textView.setTextColor(ContextCompat.getColor(Options.this,
                                    (R.color.colorPrimaryDark)));
                        } catch (InterruptedException ignored) {}
                    }
                });
            }
        });
    }
}
