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

public class ProfReg extends AppCompatActivity {

    private ImageButton btnProf, btnMap, btnInfo;
    private Button btnReg;
    //private EditText edt_email, edt_nick, edt_pass, edt_pass_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_reg);

        final Profile prof = new Profile();
        if(prof.isAuth == 1){
            Intent intent = new Intent(ProfReg.this, Profile.class);
            startActivity(intent);
        }

//        edt_email = (EditText) findViewById(R.id.editText3);
//        edt_nick = (EditText) findViewById(R.id.editText5);
//        edt_pass = (EditText) findViewById(R.id.editText6);
//        edt_pass_check = (EditText) findViewById(R.id.editText7);

        btnReg = (Button)findViewById(R.id.button5);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfReg.this, Profile.class);
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
                        Intent intent = new Intent(ProfReg.this, ProfAftRegActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent1 = new Intent(ProfReg.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent2 = new Intent(ProfReg.this, Info.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
