package com.example.romanpc.rosyama;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.romanpc.api.ApiService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

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
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private static LatLng myCoords;
    private static Marker marker;
    private String mapsApiKey = "AIzaSyDMQlmGLJUZykFgEnVSgwk_JAPPSSw4uq0";
    private static final int CAMERA_REQUEST = 0;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_marker);

        ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Кнопка "Назад на ActionBar"
        drawer = findViewById(R.id.drawerCreate);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_viewCreate);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.map) {
                    startActivity(new Intent(CreateMarker.this, MainActivity.class));
                } else if (id == R.id.settings) {
                    startActivity(new Intent(CreateMarker.this, SettingsActivity.class));
                }
                DrawerLayout drawer = findViewById(R.id.drawerCreate);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //Создание MapView и инициализция
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        imageView = (ImageView)findViewById(R.id.imageView3);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                imageButton.setVisibility(View.GONE);
            }
        });
        //Текстовое поле Адрес
//        adr = (TextView)findViewById(R.id.textView21);
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
                    finish();
                }else{
                    Toast.makeText(CreateMarker.this, "Введены не все данные", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnailBitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    //Обработчик нажатия на кнопку "Назад"
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //Получение последнего местоположения
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myCoords = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), (float) 17.5));
                    }
                });
        //Обработчик нажатия на карту
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Добавление маркера
                if(hasMarker == 0){
                    marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    hasMarker++;
                }else{
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(latLng));
                }
                lat = latLng.latitude;
                lng = latLng.longitude;
                //Работа Геокодера(Получение адреса)
                Locale myLocale = new Locale("ru","RU");
                Geocoder geocoder = new Geocoder(CreateMarker.this, myLocale);
                try {
                    List<Address> fromLocation = geocoder.getFromLocation(lat, lng, 1);
                    address = fromLocation.get(0).getAddressLine(0);
//                    if(address != null){
//                        adr.setText(address);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                //постороение маршрута
//                GeoApiContext geoApiContext = new GeoApiContext.Builder()
//                        .apiKey(mapsApiKey)
//                        .build();
//                //Маршрут
//                DirectionsResult result = null;
//                try {
//                    result = DirectionsApi.newRequest(geoApiContext)
//                            .mode(TravelMode.DRIVING)
//                            .origin(String.valueOf(myCoords))
//                            .destination(String.valueOf(new LatLng(lat, lng)))
//                            .await();
//                    List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();
//                    PolylineOptions line = new PolylineOptions();
//                    LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
//                    for (int i = 0; i < path.size(); i++) {
//                        line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
//                        latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
//                    }
//                    line.width(16f).color(Color.RED);
//                    mMap.addPolyline(line);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        });
    }
}
