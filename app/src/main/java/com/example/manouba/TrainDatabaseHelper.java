package com.example.manouba;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TrainDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "manouba.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "train";

    private Context context;

    public TrainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            getReadableDatabase(); // create empty database
            close();
            try {
                InputStream inputStream = context.getAssets().open(DATABASE_NAME);
                OutputStream outputStream = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // We already have a pre-filled database, so nothing to create here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public List<Train> getAllTrains() {
        List<Train> trainList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Train train = new Train();
                train.setDirection(cursor.getString(cursor.getColumnIndexOrThrow("direction")));
                train.setTunis(cursor.getString(cursor.getColumnIndexOrThrow("tunis")));
                train.setSaiidaManoubia(cursor.getString(cursor.getColumnIndexOrThrow("saiidaManoubia")));
                train.setMellassine(cursor.getString(cursor.getColumnIndexOrThrow("mellassine")));
                train.setErraoudha(cursor.getString(cursor.getColumnIndexOrThrow("erraoudha")));
                train.setLeBardo(cursor.getString(cursor.getColumnIndexOrThrow("leBardo")));
                train.setElBortal(cursor.getString(cursor.getColumnIndexOrThrow("elBortal")));
                train.setManouba(cursor.getString(cursor.getColumnIndexOrThrow("manouba")));
                train.setLesOrangers(cursor.getString(cursor.getColumnIndexOrThrow("lesOrangers")));
                train.setGobaa(cursor.getString(cursor.getColumnIndexOrThrow("gobaa")));
                train.setGobaaVille(cursor.getString(cursor.getColumnIndexOrThrow("gobaaVille")));
                trainList.add(train);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trainList;
    }
}

