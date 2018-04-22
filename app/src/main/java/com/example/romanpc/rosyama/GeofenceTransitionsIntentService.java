package com.example.romanpc.rosyama;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.JobIntentService;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Locale;


public class GeofenceTransitionsIntentService extends JobIntentService implements TextToSpeech.OnInitListener {
    TextToSpeech textToSpeech;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsIntentService.class, 573, intent);
    }

    // ...
    protected void onHandleWork(Intent intent) {
        textToSpeech = new TextToSpeech(this, this);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(this, "Вы в зоне", Toast.LENGTH_SHORT).show();
//            if(!textToSpeech.isSpeaking()){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    textToSpeech.speak("Внимание! Впереди яма.", textToSpeech.QUEUE_ADD, null, "1");
//                }else{
//                    HashMap<String, String> stringStringHashMap = new HashMap<>();
//                    stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Внимание! Впереди яма.");
//                    textToSpeech.speak("Внимание! Впереди яма.", textToSpeech.QUEUE_ADD, stringStringHashMap);
//                }
//            }else{
//                textToSpeech.stop();
//            }
            //String text = "Внимание! впереди яма.";
            //textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            int result = textToSpeech.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            }
        } else {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
