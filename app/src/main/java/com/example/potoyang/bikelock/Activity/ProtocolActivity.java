package com.example.potoyang.bikelock.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.potoyang.bikelock.R;

public class ProtocolActivity extends AppCompatActivity {

    private Button btn_protocol_agree, btn_protocol_disagree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        btn_protocol_agree = (Button) findViewById(R.id.btn_protocol_agree);
        btn_protocol_disagree = (Button) findViewById(R.id.btn_protocol_disagree);

        btn_protocol_disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_protocol_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProtocolActivity.this, AnimationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}
