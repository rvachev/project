package com.example.romanpc.rosyama;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();
        String photo = intent.getStringExtra("photo");
        URL newurl = null;
        try {
            newurl = new URL(photo);
            Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            imageView.setImageBitmap(bitmap);
        }catch (IOException e) {
            e.printStackTrace();
        }

        textView = (TextView)findViewById(R.id.textView7);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        HashMap<String, String> hashMap = dataBaseHelper.readUserInfo();
        textView.setText(hashMap.get("name"));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(Profile.this, ProfAftRegActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent1 = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent2 = new Intent(Profile.this, Info.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
