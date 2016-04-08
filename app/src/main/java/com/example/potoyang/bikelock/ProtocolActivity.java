package com.example.potoyang.bikelock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class ProtocolActivity extends AppCompatActivity {

    CheckBox cb_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        cb_agree = (CheckBox) findViewById(R.id.cb_agree);

        cb_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProtocolActivity.this, AnimationActivity.class);
                startActivity(intent);
            }
        });
    }
}
