package com.example.dots;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    public DB(@Nullable Context context) {
        super(context, "dots.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTableStatement = "CREATE TABLE SCORES (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, SCORE INTEGER)";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement = "CREATE TABLE LEVELS (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, LEVEL INTEGER)";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement = "CREATE TABLE CURRENT_SCORE (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, SCORE INTEGER)";
        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(int num, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert;

        if (table.equals("LEVELS"))
            cv.put("LEVEL", num);
        else
            cv.put("SCORE", num);

        insert = db.insert(table, null ,cv);

        return insert != -1;
    }

    public ArrayList<Integer> read(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + table;
        ArrayList<Integer> list = new ArrayList<>();

        if (table.equals("LEVELS")){
            Cursor temp = db.rawQuery(query, null);
            if (!temp.moveToFirst())
                addOne(1, table);
        }
        else if (table.equals("CURRENT_SCORE")){
            Cursor temp = db.rawQuery(query, null);
            if (!temp.moveToFirst())
                addOne(0, table);
        }
        else {
            Cursor temp = db.rawQuery(query, null);
            if (!temp.moveToFirst()) {
                for (int i=0; i < 5; i++)
                    addOne(0, "SCORES");
            }
            query += " ORDER BY SCORE DESC LIMIT 5";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                list.add(cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        else
            return null;

        return list;
    }

    public boolean delete(String table){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(table, null, null) > 0;
    }

    public boolean updateLevel(int lvl){
        String table = "LEVELS";
        if (delete(table)) {
            return addOne(lvl + 1, table);
        }
        else
            return false;
    }

    public boolean updateScore(int prevScore, int score){
        String table = "CURRENT_SCORE";
        if (delete(table)) {
            return addOne(score, table);
        }
        else
            return false;
    }
}

