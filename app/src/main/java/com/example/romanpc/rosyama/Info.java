package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class Info extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        Intent intent = new Intent(Info.this, SettingsActivity.class);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.navigation_dashboard:
//                        Intent intent1 = new Intent(Info.this, MainActivity.class);
//                        startActivity(intent1);
//                        finish();
//                        break;
//                    case R.id.navigation_notifications:
//                        break;
//                }
//                return false;
//            }
//        });
    }
}
