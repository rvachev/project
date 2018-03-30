package com.example.romanpc.rosyama;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.HashMap;


@SuppressLint("Registered")
public class GeofenceTransitionsIntentService extends IntentService {
    TextToSpeech textToSpeech;

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    // ...
    protected void onHandleIntent(Intent intent) {
        textToSpeech = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if(!textToSpeech.isSpeaking()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak("Внимание! Впереди яма.", textToSpeech.QUEUE_ADD, null, "1");
                }else{
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Внимание! Впереди яма.");
                    textToSpeech.speak("Внимание! Впереди яма.", textToSpeech.QUEUE_ADD, stringStringHashMap);
                }
            }else{
                textToSpeech.stop();
            }
        }
    }
}