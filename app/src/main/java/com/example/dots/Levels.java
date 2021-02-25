package com.example.dots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Levels extends AppCompatActivity {

    public static final String LEVEL_KEY = "level";
    public static final String LEVEL_NUM_KEY = "levelNum";
    public static String LEVEL_NUM_VALUE;
    public static int MAX_LEVEL = 1;
    private TableLayout tableLayout;
    private List<ImageView> levelsImageButtonsList = new ArrayList<>();
    private List<LinearLayout> levelsLinearLayoutsList = new ArrayList<>();
    private DB db = new DB(this);
    private ArrayList<ArrayList<ArrayList<String>>> levels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Menu.IS_RIGHT_TO_LEFT)
            setContentView(R.layout.levels_heb);
        else
            setContentView(R.layout.levels);

        Menu.MOVED_TO_ACTIVITY = false;
        tableLayout = findViewById(R.id.tableLayout);
        levels = getLevels();
        createTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTable();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onStart();
        if (Game.HOME_BUTTON_CLICKED) {
            Game.HOME_BUTTON_CLICKED = false;
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onPause();
    }

    public void createTable(){
        int level = 1;
        int tableRows;
        int tableCols = 3;

        if (MAX_LEVEL % 2 == 0)
            tableRows = MAX_LEVEL/3;
        else
            tableRows = MAX_LEVEL/3 + 1;

        for (int i = 0; i<tableRows; i++) {
            if (MAX_LEVEL % 2 != 0 && i == tableRows - 1)
                tableCols = 1;

            TableRow tableRow = new TableRow(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            tableRow.setPadding(0, 20, 0, 0);
            tableRow.setGravity(Gravity.CENTER);
            LinearLayout layout = new LinearLayout(this);

            for (int j = 0; j < tableCols; j++) {
                LinearLayout layoutInner = new LinearLayout(this);
                layoutInner.setOrientation(LinearLayout.VERTICAL);
                ImageView btn = new ImageView(this);
                TextView txt = new TextView(this);
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (j == 2 || tableCols == 1)
                    params1.setMarginEnd(0);
                else
                    params1.setMarginEnd(30);

                txt.setTextSize(20);
                txt.setTextColor(ContextCompat.getColor(this, (R.color.colorPrimaryDark)));
                Typeface typeface = ResourcesCompat.getFont(this, R.font.bangers);
                txt.setTypeface(typeface);
                txt.setText(String.valueOf(level++));
                txt.setGravity(Gravity.CENTER);


                layoutInner.setLayoutParams(params1);
                layoutInner.setGravity(Gravity.CENTER);
                btn.setImageResource(R.drawable.lock);

                layout.setGravity(Gravity.CENTER);
                layoutInner.addView(btn);
                layoutInner.addView(txt);
                layoutInner.setBackgroundResource(R.drawable.test);
                levelsLinearLayoutsList.add(layoutInner);
                layout.addView(layoutInner);
                levelsImageButtonsList.add(btn);
            }
            tableRow.addView(layout);
            tableLayout.addView(tableRow);
        }
    }

    private void updateTable(){
        LEVEL_NUM_VALUE = String.valueOf(db.read("LEVELS").get(0));
        for (int i = 0; i < Integer.parseInt(LEVEL_NUM_VALUE); i++) {
            levelsImageButtonsList.get(i).setImageResource(R.drawable.play_level);
            final int finalI = i;
            levelsLinearLayoutsList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Levels.this, Game.class);
                    intent.putExtra(LEVEL_KEY, levels.get(finalI));
                    intent.putExtra(LEVEL_NUM_KEY, String.valueOf(finalI+1));
                    Menu.MOVED_TO_ACTIVITY = true;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return false;
    }

    public void home(View view) {
        finish();
    }

    public List<TypedArray> getMultiTypedArray(Context context, String key){

        List<TypedArray> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            int lvlNum = 1;
            Field field;
            do {
                field = res.getField(key + "_" + lvlNum++);
                array.add(context.getResources().obtainTypedArray(field.getInt(null)));
            } while (field != null);
        }
        catch (NoSuchFieldException | IllegalAccessException ignored){}
        return array;
    }

    @SuppressLint("ResourceType")
    public ArrayList<ArrayList<ArrayList<String>>> getLevels(){

        List<TypedArray> levelsTypedArray = getMultiTypedArray(Levels.this, LEVEL_KEY);
        MAX_LEVEL = levelsTypedArray.size();

        for (int lvl = 0; lvl < MAX_LEVEL; lvl++) {
            String lvlNum = String.valueOf(lvl);
            TypedArray levelArr = levelsTypedArray.get(Integer.parseInt(lvlNum));
            ArrayList<ArrayList<String>> level = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < levelArr.length(); i++) {
                String str = levelArr.getString(i);
                String color = str.split("-")[0];
                String first = str.split("-")[1];
                String second = str.split("-")[2];
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(color);
                temp.add(first);
                temp.add(second);
                level.add(temp);
            }
            levels.add(level);
        }
        return levels;
    }
}
