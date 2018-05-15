package com.example.romanpc.rosyama;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.romanpc.api.ApiService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;
    private String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        ArrayList<String> list = dataBaseHelper.getCities();

        spinner = (Spinner)findViewById(R.id.spinner);
        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService.getApi("http://kredit55.ru/").getPitsByRegionName(region).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        InputStream inputStream = responseBody.byteStream();
                        Scanner scanner = new Scanner(inputStream);
                        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(SplashActivity.this);
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
                            dataBaseHelper1.addPit(Integer.parseInt(split[0]), Double.parseDouble(split[3]), Double.parseDouble(split[4]), split[2], status, photo);
                            //System.out.println(row);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });
    }
}