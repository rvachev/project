package com.example.romanpc.rosyama;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ListHelper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_helper);
        ListView listView = (ListView)findViewById(R.id.firstListView);
        ArrayList<String> listOfCities = new ArrayList<>();
        listOfCities.add("Омск");
        listOfCities.add("Москва");
        listOfCities.add("Санкт-Петербург");
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfCities);

        ArrayList<HashMap<String, String>> listOfDishes = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("dishName", "Гуляш");
        hashMap.put("category", "Горячее блюдо");
        listOfDishes.add(hashMap);
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("dishName", "Борщ");
        hashMap1.put("category", "Суп");
        listOfDishes.add(hashMap1);

        String[] from = new String[]{"dishName", "category"};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listOfDishes, android.R.layout.two_line_list_item, from, to);

        listView.setAdapter(stringArrayAdapter);
        listView.setAdapter(simpleAdapter);
    }
}
