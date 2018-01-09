package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfReg extends AppCompatActivity {

    private ImageButton btnProf, btnMap, btnInfo;
    private Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_reg);

        final Profile prof = new Profile();
        if(prof.isAuth == 1){
            Intent intent = new Intent(ProfReg.this, Profile.class);
            startActivity(intent);
        }

        btnReg = (Button)findViewById(R.id.button5);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfReg.this, Profile.class);
                startActivity(intent);
                prof.isAuth = 1;
            }
        });

        btnProf = (ImageButton)findViewById(R.id.imageButton);
        btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfReg.this, ProfAftRegActivity.class);
                startActivity(intent);
            }
        });
        btnInfo = (ImageButton)findViewById(R.id.imageButton2);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfReg.this, Info.class);
                startActivity(intent);
            }
        });
        btnMap = (ImageButton)findViewById(R.id.imageButton3);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfReg.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
