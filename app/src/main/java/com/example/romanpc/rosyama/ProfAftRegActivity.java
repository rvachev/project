package com.example.romanpc.rosyama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfAftRegActivity extends AppCompatActivity {

    private Button btnEnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_aft_reg);

        btnEnt = (Button)findViewById(R.id.button3);
        btnEnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfAftRegActivity.this, ProfEnter.class);
                startActivity(intent);
            }
        });
    }
}
