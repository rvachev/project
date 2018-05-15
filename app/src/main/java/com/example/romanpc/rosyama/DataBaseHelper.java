package com.example.romanpc.rosyama;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataBaseHelper extends SQLiteOpenHelper{

    private Context context;

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context) {
        super(context, "database.db", null, 1);
        this.context = context;
    }

    //Выполняется при создании базы данных, нужно написать код для создания таблиц
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery1 = "CREATE TABLE pits (\n" +
                "    _id       INTEGER PRIMARY KEY\n" +
                "                      NOT NULL,\n" +
                "    latitude  REAL,\n" +
                "    longitude REAL,\n" +
                "    address   TEXT,\n" +
                "    status TEXT,\n" +
                "    photo TEXT\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery1); //Этот метод позволяет выполнить любой SQL-запрос

        String sqlQuery4 = "CREATE TABLE user (\n" +
                "    _id   INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                  NOT NULL,\n" +
                "    name TEXT,\n" +
                "    photo TEXT\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery4);

        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("regions.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()){
                String sql = scanner.nextLine();
                sqLiteDatabase.execSQL(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPit(int id, double lat, double lng, String address, String status, String photo){
        //Объект, позволяющий записывать БД
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "INSERT INTO pits (_id, latitude, longitude, address, status, photo) VALUES ("+id+", "+lat+", "+lng+", '"+address+"', '"+status+"', '"+photo+"')";
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
        String sql = "SELECT _id, latitude, longitude, status FROM pits";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        ArrayList<HashMap<String,String>> pitsList = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            String status = cursor.getString(3);
            HashMap<String, String> pit = new HashMap<>();
            pit.put("_id", Integer.toString(id));
            pit.put("Lat", Double.toString(latitude));
            pit.put("Lng", Double.toString(longitude));
            pit.put("stat", status);
            pitsList.add(pit);
        }

        return pitsList;
    }

    public HashMap<String, String> getPitsById(String id){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT latitude, longitude, address, status FROM pits WHERE _id = ?";
        Cursor cursor = readableDatabase.rawQuery(sql, new String[]{id});
        HashMap<String, String> hashMap = new HashMap<>();
        while(cursor.moveToNext()) {
            double lat = cursor.getDouble(0);
            double lng = cursor.getDouble(1);
            String address = cursor.getString(2);
            String status = cursor.getString(3);
            hashMap.put("lat", Double.toString(lat));
            hashMap.put("lng", Double.toString(lng));
            hashMap.put("adr", address);
            hashMap.put("stat", status);
        }
        return hashMap;
    }

    public ArrayList<String> getCities(){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT region FROM regions ORDER BY region";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        ArrayList<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            String region = cursor.getString(0);
            list.add(region);
        }
        return list;
    }

    public void cleanTable(){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "DELETE FROM pits";
        writableDatabase.execSQL(sql);
    }

    public int dataExist(){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM pits";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        int i = cursor.getInt(0);
        return i;
    }
}
