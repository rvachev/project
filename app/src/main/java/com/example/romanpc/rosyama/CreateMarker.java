package com.example.romanpc.rosyama;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.romanpc.api.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMarker extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static double lat, lng;
    private int hasMarker = 0;
    private static String address;
    private static TextView adr;
    private static String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_marker);
        //Кнопка "Назад на ActionBar"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Создание MapView и инициализция
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        //Текстовое поле Адрес
        adr = (TextView)findViewById(R.id.textView21);
        //Получение региона из SplashActivity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        region = sharedPreferences.getString("region", "");
        //Кнопка добавления ямы
        Button btn = (Button) findViewById(R.id.button7);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat != 0.0 && lng != 0.0) {
                    //Добавление ямы
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(CreateMarker.this);
                    Random random = new Random();
                    int i = random.nextInt(1000000) + 200000;
                    dataBaseHelper.addPit(i, lat, lng, address, null, null);
                    //Отправка запроса на сервер на добавление ямы
                    ApiService.getApi("http://kredit55.ru/").savePit(region, String.valueOf(lat), String.valueOf(lng), address).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ResponseBody responseBody = response.body();
                            InputStream inputStream = responseBody.byteStream();
                            Scanner scanner = new Scanner(inputStream);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    Intent intent = new Intent(CreateMarker.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CreateMarker.this, "Введены не все данные", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Обработчик нажатия на кнопку "Назад"
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Основная работа карты
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Инициализация карты
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Кнопка местоположения
        mMap.setMyLocationEnabled(true);
        //Обработчик нажатия на карту
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Добавление маркера
                if(hasMarker == 0){
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    hasMarker++;
                }else{
                    mMap.clear();
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                }
                lat = latLng.latitude;
                lng = latLng.longitude;
                //Работа Геокодера(Получение адреса)
                Locale myLocale = new Locale("ru","RU");
                Geocoder geocoder = new Geocoder(CreateMarker.this, myLocale);
                try {
                    List<Address> fromLocation = geocoder.getFromLocation(lat, lng, 1);
                    address = fromLocation.get(0).getAddressLine(0);
                    if(address != null){
                        adr.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //Получение последнего местоположения
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20));
                    }
                });
    }
}
