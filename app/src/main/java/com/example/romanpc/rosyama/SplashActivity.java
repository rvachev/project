package com.example.romanpc.rosyama;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class SplashActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.copyDataBase();
        //ArrayList<String> list = dataBaseHelper.getCities();

        spinner = (Spinner)findViewById(R.id.spinner);
//        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, ProfAftRegActivity.class));
            }
        });
    }
}
