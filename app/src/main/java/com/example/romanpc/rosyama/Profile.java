package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Profile extends AppCompatActivity {

    public static int isAuth = 0;
    private ImageButton btnMap, btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(isAuth == 0){
            Intent intent = new Intent(Profile.this, ProfAftRegActivity.class);
            startActivity(intent);
        }

        btnInfo = (ImageButton)findViewById(R.id.imageButton2);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Info.class);
                startActivity(intent);
            }
        });
        btnMap = (ImageButton)findViewById(R.id.imageButton3);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
