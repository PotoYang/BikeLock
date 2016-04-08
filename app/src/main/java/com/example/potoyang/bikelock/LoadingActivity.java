package com.example.potoyang.bikelock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         *AndroidStudio隐藏标题栏由于是继承至AppCompatActivity不一样
         */
        getSupportActionBar().hide();
        final View view = View.inflate(this, R.layout.activity_loading, null);

        /*
         *设置亮度渐变,从0.0到1.0
         */
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        view.startAnimation(animation);

        setContentView(view);

        /*
         *设置动画监听器，当动画结束的时候，启动新的Activity
         */
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /*
                 * SharedPreferences存储以键值对的方式存储
                 * getSharedPreferences的第一个参数就是文件名称
                 */
                SharedPreferences sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
                boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isFirstRun) {
                    editor.putBoolean("isFirstRun", false);
                    editor.commit();
                    startPotocolActivity();
                } else {
                    startMapActivity();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startPotocolActivity() {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     *屏蔽返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}
