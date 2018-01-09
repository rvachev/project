package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Info extends AppCompatActivity {

    private ImageButton btnProf, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        btnProf = (ImageButton)findViewById(R.id.imageButton);
        btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info.this, Profile.class);
                startActivity(intent);
            }
        });
        btnMap = (ImageButton)findViewById(R.id.imageButton3);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
