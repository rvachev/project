package com.example.romanpc.rosyama;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnCompleteListener<Void> {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_FOR_GET_USER_LOCATION = 1;
    FloatingActionButton fab;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                Intent intent = new Intent(MainActivity.this, ProfAftRegActivity.class);
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
                        Toast.makeText(this, "Пользователь включил определение местоположеия", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
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


        mMap = googleMap;


        mGeofenceList = new ArrayList<>();

        GeofencingClient client = LocationServices.getGeofencingClient(this);

        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);

//        Geofence.Builder geofenceBuilder = new Geofence.Builder();
//        geofenceBuilder.setCircularRegion(55.041082, 73.323116, 100);
//        geofenceBuilder.setRequestId("111");
//        geofenceBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
//        geofenceBuilder.setExpirationDuration(Geofence.NEVER_EXPIRE);
//        Geofence geofence = geofenceBuilder.build();

//        GeofencingRequest.Builder addGeofence = geofencingRequestBuilder.addGeofence(geofence);
//        GeofencingRequest geofencingRequest = addGeofence.build();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        client.addGeofences(geofencingRequest, getGeofencePendingIntent())
//                .addOnCompleteListener(this);

        final DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<HashMap<String, String>> listPits = dataBaseHelper.getPits();
        int i = 0;
        while (i < listPits.size()) {
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
            mGeofenceList.add(geofence);
            i++;
        }
        GeofencingRequest.Builder addGeofence = geofencingRequestBuilder.addGeofences(mGeofenceList);
        GeofencingRequest geofencingRequest = addGeofence.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnCompleteListener(this);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TextView lat = (TextView)findViewById(R.id.textView21);
                TextView lng = (TextView)findViewById(R.id.textView22);
                String pitId = (String)marker.getTag();
                DataBaseHelper dataBaseHelper1 = new DataBaseHelper(MainActivity.this);
                HashMap<String, String> pitInfo = dataBaseHelper1.getPitsById(pitId);
                lat.setText(pitInfo.get("lat"));
                lng.setText(pitInfo.get("lng"));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return false;
            }
        });


        createLocationRequest();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    }
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1500);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(this, "AAAAAA", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "BBBBBB", Toast.LENGTH_SHORT).show();
        }
    }

    public static class GeofenceBroadcastReceiver extends BroadcastReceiver implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
        TextToSpeech textToSpeech;

        @Override
        public void onReceive(Context context, Intent intent) {
            textToSpeech = new TextToSpeech(context, this);
            if(!textToSpeech.isSpeaking()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak("Внимание! Впереди яма", textToSpeech.QUEUE_ADD, null, "1");
                }else{
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Внимание! Впереди яма.");
                    textToSpeech.speak("Внимание! Впереди яма", textToSpeech.QUEUE_ADD, stringStringHashMap);
                }
            }else{
                onDestroy();
            }
            Toast.makeText(context, "Вы в зоне", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInit(int status) {
            textToSpeech.setOnUtteranceCompletedListener(this);
        }

        @Override
        public void onUtteranceCompleted(String utteranceId) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(, "Completed!", Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        protected void onDestroy(){
            if(textToSpeech != null){
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            }
        }
    }
}
