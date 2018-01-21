package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfAftRegActivity extends AppCompatActivity {

    private Button btnEnt, btnReg;
    private ImageButton btnProf, btnMap, btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_aft_reg);

        Profile prof = new Profile();
        if(prof.isAuth == 1){
            Intent intent = new Intent(ProfAftRegActivity.this, Profile.class);
            startActivity(intent);
        }

        btnReg = (Button)findViewById(R.id.button4);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, ProfReg.class);
                startActivity(intent);
            }
        });

        btnEnt = (Button)findViewById(R.id.button3);
        btnEnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, ProfEnter.class);
                startActivity(intent);
            }
        });

        btnProf = (ImageButton)findViewById(R.id.imageButton);
        btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, ProfAftRegActivity.class);
                startActivity(intent);
            }
        });
        btnInfo = (ImageButton)findViewById(R.id.imageButton2);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, Info.class);
                startActivity(intent);
            }
        });
        btnMap = (ImageButton)findViewById(R.id.imageButton3);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}