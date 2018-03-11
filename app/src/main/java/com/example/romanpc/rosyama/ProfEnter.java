package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(ProfEnter.this, ProfAftRegActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent1 = new Intent(ProfEnter.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent2 = new Intent(ProfEnter.this, Info.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
