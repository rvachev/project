package com.example.romanpc.rosyama;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper{

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context){
        super(context, "database.db", null, 1);
    }

    //Выполняется при создании базы данных, нужно написать код для создания таблиц
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery1 = "CREATE TABLE pits (\n" +
                "    _id       INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                      NOT NULL,\n" +
                "    latitude  REAL,\n" +
                "    longitude REAL,\n" +
                "    city_id   INTEGER\n" +
                "    exist INTEGER\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery1); //Этот метод позволяет выполнить любой SQL-запрос

        String sqlQuery2 = "CREATE TABLE cities (\n" +
                "    _id  INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                 NOT NULL,\n" +
                "    city TEXT\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery2);

        String sqlQuery3 = "CREATE TABLE photos (\n" +
                "    _id   INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                  NOT NULL,\n" +
                "    photo TEXT\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPit(double lat, double lng){
        //Объект, позволяющий записывать БД
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "INSERT INTO pits (latitude, longitude) VALUES ("+lat+", "+lng+")";
        writableDatabase.execSQL(sql);
    }

    public int getId(){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT _id FROM pits";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        int id = 0;
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return id;
    }

    public ArrayList<HashMap<String,String>> getPits(){
        //получение объекта, с помощью которого вы можете выполнять запросы к БД
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT _id, latitude, longitude FROM pits";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        ArrayList<HashMap<String,String>> pitsList = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            HashMap<String, String> pit = new HashMap<>();
            pit.put("_id", Integer.toString(id));
            pit.put("Lat", Double.toString(latitude));
            pit.put("Lng", Double.toString(longitude));
            pitsList.add(pit);
        }
        return pitsList;
    }
}
