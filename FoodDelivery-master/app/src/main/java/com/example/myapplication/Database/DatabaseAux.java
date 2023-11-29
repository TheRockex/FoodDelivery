package com.example.myapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseAux extends SQLiteOpenHelper {
    private static final String DB_NAME = "FoodDelivery";
    private static final int DB_VERSION = 1;
    public DatabaseAux(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(25) NOT NULL," +
                "password VARCHAR(25) NOT NULL," +
                "date VARCHAR(25) NOT NULL," +
                "email VARCHAR(25) NOT NULL," +
                "telephone VARCHAR(25) NOT NULL," +
                "direction VARCHAR(25) NOT NULL)");
        db.execSQL("CREATE TABLE pedido " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombreproduct VARCHAR(25) NOT NULL," +
                "precioproduct double NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
