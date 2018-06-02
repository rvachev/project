package com.example.romanpc.rosyama;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.romanpc.api.ApiService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;
    private String region;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawer = findViewById(R.id.drawerSettings);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_viewSettings);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.map) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                } else if (id == R.id.addPit) {
                    startActivity(new Intent(SettingsActivity.this, CreateMarker.class));
                } else if (id == R.id.settings) {
                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                }
                DrawerLayout drawer = findViewById(R.id.drawerSettings);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //Спиннер городов
        spinner = (Spinner)findViewById(R.id.spinner2);
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        ArrayList<String> list = dataBaseHelper.getCities();
        //Заполнение спиннера
        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Обработчик на выбранный элемент спиннера
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region = parent.getItemAtPosition(position).toString();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.remove("region");
                sharedPreferencesEditor.putString("region", region);
                sharedPreferencesEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Выбор города
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progBar);
                progressBar.setVisibility(ProgressBar.VISIBLE);
                //Запрос на получение ям с сервера
                ApiService.getApi("http://kredit55.ru/").getPitsByRegionName(region).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        InputStream inputStream = responseBody.byteStream();
                        Scanner scanner = new Scanner(inputStream);
                        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(SettingsActivity.this);
                        dataBaseHelper1.cleanTable();
                        while (scanner.hasNextLine()){
                            String row = scanner.nextLine();
                            String[] split = row.split(";");
                            String status = null, photo = null;
                            if(split.length > 5){
                                status = split[5];
                                if(split.length > 6){
                                    photo = split[6];
                                }
                            }
                            try {
                                dataBaseHelper1.addPit(Integer.parseInt(split[0]), Double.parseDouble(split[3]), Double.parseDouble(split[4]), split[2], status, photo);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        button.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
