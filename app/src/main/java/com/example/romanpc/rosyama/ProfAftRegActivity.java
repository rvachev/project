package com.example.romanpc.rosyama;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent = new Intent(ProfAftRegActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent1 = new Intent(ProfAftRegActivity.this, Info.class);
                        startActivity(intent1);
                        break;
                }
                return false;
            }
        });
    }
}
