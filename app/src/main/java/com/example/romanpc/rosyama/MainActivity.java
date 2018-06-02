package com.example.romanpc.rosyama;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.romanpc.api.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnCompleteListener<Void> {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_FOR_GET_USER_LOCATION = 1;
    private FloatingActionButton fab;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private static TextToSpeech tts;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView address;
    private static Location mLocation;
    private ImageView imageView;
    private static ArrayList<LatLng> allPits;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Создание объекта класса TextToSpeech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawer = findViewById(R.id.drawerMain);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_viewMain);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.map) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (id == R.id.addPit) {
                    startActivity(new Intent(MainActivity.this, CreateMarker.class));
                } else if (id == R.id.settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
                DrawerLayout drawer = findViewById(R.id.drawerMain);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //Экран не гаснет во время работы приложения
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Текстовое поле "Адрес" в BottomSheet
        address = (TextView) findViewById(R.id.textView16);

        //Создание фрагмента карты
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        //Запрос на разрешение использования местоположения
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_FOR_GET_USER_LOCATION);
            }
        }
        //Кнопка добавления ямы
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateMarker.class);
                startActivity(intent);
                finish();
            }
        });

    }
    //Обработка нажатия кнопки "Назад" в ActionBar
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

    //Разрешение на доступ к местоположению
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FOR_GET_USER_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        }
    }
    //Работа карты
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Определение BottomSheet
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        //Потенциальная фотография ямы
        imageView = (ImageView) findViewById(R.id.imageView);
        //Инициализация карты
        mMap = googleMap;
        //Кнопка местоположения
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        //Запрос на получение текущего местоположения
        createLocationRequest();
        //Массивы для работы с geofence
        allPits = new ArrayList<>();
        mGeofenceList = new ArrayList<>();
        //Начало работы geofence
        final GeofencingClient client = LocationServices.getGeofencingClient(this);
        final GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        //База данных и выгрузка ям
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        ArrayList<ClusterItemImpl> companies = dataBaseHelper.getCompaniesForClaster();
        setMarkersOnMap(companies);
        //Получение последнего местоположения
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        mLocation = location;
                        addGeofence(client, geofencingRequestBuilder);
                    }
                });
        //Отслеживание изменения местоположения
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    addGeofence(client, geofencingRequestBuilder);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    //Кластеризация маркеров
    public void setMarkersOnMap(final ArrayList<ClusterItemImpl> items) {
        //Определение ClusterManager и добавление кластеров
        ClusterManager<ClusterItemImpl> mClusterManager = new ClusterManager<>(MainActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.addItems(items);
        //Обработка нажатия на кластеры
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterItemImpl>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterItemImpl> cluster) {
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
        //Обработка на нажатие элемента кластера
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItemImpl>() {
            @Override
            public boolean onClusterItemClick(ClusterItemImpl clusterItem) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //Получение информации о яме относительно ее id
                DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                final String pitId = clusterItem.getPitId();
                HashMap<String, String> pitsById = dataBaseHelper.getPitsById(pitId);
                String adr = pitsById.get("adr");
                Button button_comp = (Button)findViewById(R.id.button_comp);
                //Отправка запрос на удаление ямы
                button_comp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Ваш запрос отправлен", Toast.LENGTH_SHORT).show();
                        ApiService.getApi("http://kredit55.ru/").complitePit(pitId).enqueue(new Callback<ResponseBody>() {
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
                    }
                });
                //Фотография ямы
                try {
                    String photo = pitsById.get("photo");
                    String photoRepl = photo.replace("|", ";");
                    String[] split = photoRepl.split(";");
                    Picasso.with(MainActivity.this).load(split[0]).into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Адрес ямы
                address.setText(adr);
                return false;
            }
        });
    }

    //PendingIntent для geofence
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    //Запрос на получение текущего местоположения
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1500);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Обработка создания geofence
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            //Toast.makeText(this, "Успешно добавлено", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Возникла проблема", Toast.LENGTH_SHORT).show();
        }
    }

    //Основной метод добавления geofence
    public void addGeofence(GeofencingClient client, GeofencingRequest.Builder geofencingRequestBuilder) {
        mGeofenceList.clear();
        //Получение ям
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<HashMap<String, String>> listPits = dataBaseHelper.getPits();
        if (mLocation != null) {
            int i = 0;
            //Выгрузка координат и создание geofence
            while (i < listPits.size()) {
                if (listPits.get(i).get("stat").equals("Отремонтировано") || listPits.get(i).get("stat").equals("Нет ответа")) {

                } else {
                    if (mLocation != null) {
                        Location newLocation = new Location("newlocation");
                        newLocation.setLatitude(Double.parseDouble(listPits.get(i).get("Lat")));
                        newLocation.setLongitude(Double.parseDouble(listPits.get(i).get("Lng")));
                        float distance = mLocation.distanceTo(newLocation);
                        if(mGeofenceList.size() > 98){
                            mGeofenceList.clear();
                        }
                        if (distance < 500 && mGeofenceList.size() < 100) {
                            double lat = Double.parseDouble(listPits.get(i).get("Lat"));
                            double lng = Double.parseDouble(listPits.get(i).get("Lng"));
                            LatLng latLng = new LatLng(lat, lng);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                            marker.setTag(listPits.get(i).get("_id"));
                            Geofence.Builder geofenceBuilder = new Geofence.Builder();
                            geofenceBuilder.setCircularRegion(lat, lng, 100);
                            geofenceBuilder.setRequestId(listPits.get(i).get("_id"));
                            geofenceBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
                            geofenceBuilder.setExpirationDuration(Geofence.NEVER_EXPIRE);
                            Geofence geofence = geofenceBuilder.build();
                            int x = 0;
                            for(int j = 0; j < allPits.size(); j++){
                                if(latLng.equals(allPits.get(j))){
                                    x++;
                                }
                            }
                            if(x == 0) {
                                mGeofenceList.add(geofence);
                            }
                            allPits.add(new LatLng(lat, lng));
                        }
                    }
                }
                i++;
            }
        }
        //Добавление geofence
        if (mGeofenceList.size() > 0) {
            GeofencingRequest.Builder addGeofence = geofencingRequestBuilder.addGeofences(mGeofenceList);
            GeofencingRequest geofencingRequest = addGeofence.build();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            client.addGeofences(geofencingRequest, getGeofencePendingIntent()).addOnCompleteListener(this);
        }
    }

    //Обработчик на вход в зону geofence
    public static class GeofenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tts.speak("Внимание! Впереди яма!", TextToSpeech.QUEUE_FLUSH, null);
            //Toast.makeText(context, "Вы в зоне", Toast.LENGTH_SHORT).show();
        }
    }
}
