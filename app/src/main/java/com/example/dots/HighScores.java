package com.example.dots;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HighScores extends AppCompatActivity {
    private ListView scoreTable;
    private DB db = new DB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Menu.IS_RIGHT_TO_LEFT)
            setContentView(R.layout.high_scores_heb);
        else
            setContentView(R.layout.high_scores);

        Menu.MOVED_TO_ACTIVITY = false;
        scoreTable = findViewById(R.id.scoreTable);

        showHighScores();
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

    public void home(View view) {
        finish();
    }

    public void showHighScores() {
        ArrayList<Integer> scores = db.read("SCORES");
        ArrayAdapter<Integer> adapter = null;
        if (scores != null) {
            adapter = new ArrayAdapter<Integer>(this,
                    R.layout.list_view_row, android.R.id.text1, scores);
            scoreTable.setAdapter(adapter);
        }
        else {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                    R.layout.list_view_row, android.R.id.text1, new String[]{"No Scores"});
            scoreTable.setAdapter(adapter1);
        }
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
}