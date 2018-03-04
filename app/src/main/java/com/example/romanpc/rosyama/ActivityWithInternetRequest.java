package com.example.romanpc.rosyama;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ActivityWithInternetRequest extends AppCompatActivity {

    private String site = "https://ya.ru";
    private String siteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestData requestData = new RequestData();
        requestData.execute(site);
    }

    private class RequestData extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String siteName = strings[0];
            try {
                URL url = new URL(siteName);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                while (true){
                    String line = bufferedReader.readLine();
                    if(line == null){
                        break;
                    }
                    siteContent += line;
                }
                System.out.print(siteContent);
                inputStreamReader.close();
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
