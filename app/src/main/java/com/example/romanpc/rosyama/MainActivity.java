package com.example.romanpc.rosyama;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnCompleteListener<Void> {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_FOR_GET_USER_LOCATION = 1;
    FloatingActionButton fab;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private static TextToSpeech tts;
    private ClusterManager<ClusterItemImpl> mClusterManager;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView address;
    private static Location mLocation;
    private float distance;
    private ImageView imageView;
    private static ArrayList<Geofence> allPits;
    private static LocationManager locationManager;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationCallback mLocationCallback;
    private static LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        address = (TextView) findViewById(R.id.textView16);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_FOR_GET_USER_LOCATION);
            }
        }
        mapFragment.getMapAsync(this);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateMarker.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FOR_GET_USER_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        imageView = (ImageView) findViewById(R.id.imageView);


        mMap = googleMap;

        allPits = new ArrayList<>();
        mGeofenceList = new ArrayList<>();

        final GeofencingClient client = LocationServices.getGeofencingClient(this);

        final GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);

        createLocationRequest();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        ArrayList<ClusterItemImpl> companies = dataBaseHelper.getCompaniesForClaster();
        setMarkersOnMap(companies);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        mLocation = location;
                        addGeofence(client, geofencingRequestBuilder);
                    }
                });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    for (int i = 0; i < mGeofenceList.size(); i++) {
                        allPits.add(mGeofenceList.get(i));
                    }
                    client.removeGeofences(getGeofencePendingIntent());
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

    public void setMarkersOnMap(final ArrayList<ClusterItemImpl> items) {
        mClusterManager = new ClusterManager<>(MainActivity.this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.addItems(items);
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

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItemImpl>() {
            @Override
            public boolean onClusterItemClick(ClusterItemImpl clusterItem) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                String pitId = clusterItem.getPitId();
                HashMap<String, String> pitsById = dataBaseHelper.getPitsById(pitId);
                String adr = pitsById.get("adr");
                try {
                    String photo = pitsById.get("photo");
                    String photoRepl = photo.replace("|", ";");
                    String[] split = photoRepl.split(";");
                    Picasso.with(MainActivity.this).load(split[0]).into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                address.setText(adr);
                return false;
            }
        });

    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1500);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            //Toast.makeText(this, "Успешно добавлено", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Возникла проблема", Toast.LENGTH_SHORT).show();
        }
    }

    public static class GeofenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tts.speak("Внимание! Впереди яма!", TextToSpeech.QUEUE_FLUSH, null);
//            Toast.makeText(context, "Вы в зоне", Toast.LENGTH_SHORT).show();
        }
    }

    public void addGeofence(GeofencingClient client, GeofencingRequest.Builder geofencingRequestBuilder) {
        mGeofenceList.clear();
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<HashMap<String, String>> listPits = dataBaseHelper.getPits();
        if (mLocation != null) {
            int i = 0;
            while (i < listPits.size()) {
                if (listPits.get(i).get("stat").equals("Отремонтировано") || listPits.get(i).get("stat").equals("Нет ответа")) {

                } else {
                    if (mLocation != null) {
                        Location newLocation = new Location("newlocation");
                        newLocation.setLatitude(Double.parseDouble(listPits.get(i).get("Lat")));
                        newLocation.setLongitude(Double.parseDouble(listPits.get(i).get("Lng")));
                        distance = mLocation.distanceTo(newLocation);
                        if (distance < 500 && mGeofenceList.size() < 100) {
                            double lat = Double.parseDouble(listPits.get(i).get("Lat"));
                            double lng = Double.parseDouble(listPits.get(i).get("Lng"));
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                            marker.setTag(listPits.get(i).get("_id"));
                            Geofence.Builder geofenceBuilder = new Geofence.Builder();
                            geofenceBuilder.setCircularRegion(lat, lng, 100);
                            geofenceBuilder.setRequestId(listPits.get(i).get("_id"));
                            geofenceBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
                            geofenceBuilder.setExpirationDuration(Geofence.NEVER_EXPIRE);
                            Geofence geofence = geofenceBuilder.build();
                            int x = 0;
                            for (int j = 0; j < allPits.size(); j++) {
                                if (geofence.equals(allPits.get(j))) {
                                    x++;
                                }
                            }
                            if (x == 0) {
                                mGeofenceList.add(geofence);
                            }
                        }
                    }
                }
                i++;
            }
        }
        if (mGeofenceList.size() > 0) {
            GeofencingRequest.Builder addGeofence = geofencingRequestBuilder.addGeofences(mGeofenceList);
            GeofencingRequest geofencingRequest = addGeofence.build();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            client.addGeofences(geofencingRequest, getGeofencePendingIntent())
                    .addOnCompleteListener(this);
        }
    }
}
