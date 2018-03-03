package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Scanner;

public class ProfEnter extends AppCompatActivity {

    private ImageButton btnProf, btnMap, btnInfo;
    private Button btnEnt;
    //private EditText edt_email, edt_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_enter);

        final Profile prof = new Profile();
        if(prof.isAuth == 1){
            Intent intent = new Intent(ProfEnter.this, Profile.class);
            startActivity(intent);
        }

//        edt_email = (EditText) findViewById(R.id.editText);
//        edt_pass = (EditText) findViewById(R.id.editText2);

        btnEnt = (Button)findViewById(R.id.button2);
        btnEnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfEnter.this, Profile.class);
                startActivity(intent);
                prof.isAuth = 1;
            }
        });

        btnProf = (ImageButton)findViewById(R.id.imageButton);
        btnProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfEnter.this, ProfAftRegActivity.class);
                startActivity(intent);
            }
        });
        btnInfo = (ImageButton)findViewById(R.id.imageButton2);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfEnter.this, Info.class);
                startActivity(intent);
            }
        });
        btnMap = (ImageButton)findViewById(R.id.imageButton3);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfEnter.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
