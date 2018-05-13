package com.example.romanpc.rosyama;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
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
                "    _id       INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                      NOT NULL,\n" +
                "    latitude  REAL,\n" +
                "    longitude REAL,\n" +
                "    city_id   INTEGER,\n" +
                "    rating   REAL,\n" +
                "    exist INTEGER\n" +
                ")";
        sqLiteDatabase.execSQL(sqlQuery1); //Этот метод позволяет выполнить любой SQL-запрос

        String addPit = "INSERT INTO pits (latitude, longitude) VALUES (55.567332, 73.126748)";
        sqLiteDatabase.execSQL(addPit);

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

    public void addPit(double lat, double lng, double rating){
        //Объект, позволяющий записывать БД
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "INSERT INTO pits (latitude, longitude, rating) VALUES ("+lat+", "+lng+", "+rating+")";
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
        String sql = "SELECT _id, latitude, longitude, rating FROM pits";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        ArrayList<HashMap<String,String>> pitsList = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            float rating = cursor.getFloat(3);
            HashMap<String, String> pit = new HashMap<>();
            pit.put("_id", Integer.toString(id));
            pit.put("Lat", Double.toString(latitude));
            pit.put("Lng", Double.toString(longitude));
            pit.put("rat", Double.toString(rating));
            pitsList.add(pit);
        }
        return pitsList;
    }

    public HashMap<String, String> getPitsById(String id){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT latitude, longitude, rating FROM pits WHERE _id = ?";
        Cursor cursor = readableDatabase.rawQuery(sql, new String[]{id});
        HashMap<String, String> hashMap = new HashMap<>();
        while(cursor.moveToNext()) {
            double lat = cursor.getDouble(0);
            double lng = cursor.getDouble(1);
            float rating = cursor.getFloat(2);
            hashMap.put("lat", Double.toString(lat));
            hashMap.put("lng", Double.toString(lng));
            hashMap.put("rat", Float.toString(rating));
        }
        return hashMap;
    }

    public void writeUserInfo(String username, String photo){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "INSERT INTO user (name, photo) VALUES (?, ?)";
        writableDatabase.execSQL(sql, new String[]{username, photo});
    }

    public HashMap<String, String> readUserInfo(){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String sql = "SELECT name, photo FROM user";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        HashMap<String, String> hashMap = new HashMap<>();
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            String photo = cursor.getString(1);
            hashMap.put("name", name);
            hashMap.put("photo", photo);
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
}
