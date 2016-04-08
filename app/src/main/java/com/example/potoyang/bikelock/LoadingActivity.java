package com.example.potoyang.bikelock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: com.example.potoyang.bikelock.LoadingActivity
 * @Author: PotoYang
 * @Time: 2016/4/8 21:00
 * @Description: app启动画面
 */
public class LoadingActivity extends AppCompatActivity {

    private LinearLayout ll;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AndroidStudio隐藏标题栏由于是继承至AppCompatActivity不一样
        getSupportActionBar().hide();

        setContentView(R.layout.activity_loading);
        super.onCreate(savedInstanceState);

        ll = (LinearLayout) findViewById(R.id.splash_root);
        tv_version = (TextView) findViewById(R.id.tv_version);

        tv_version.setText(getVersion());
        //设置亮度渐变,从0.0到1.0
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        ll.startAnimation(animation);

        //设置动画监听器，当动画结束的时候，启动新的Activity
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //SharedPreferences存储以键值对的方式存储
                //getSharedPreferences的第一个参数就是文件名称
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

    /**
     * @MethodName: getVersion
     * @Author: PotoYang
     * @Time: 2016/4/8 20:59
     * @Description: 获得app版本信息
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return "Version:" + version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号错误";
        }
    }

    /**
     * @MethodName: startPotocolActivity
     * @Author: PotoYang
     * @Time: 2016/4/8 21:01
     * @Description: 跳转到ProtocolActivity
     */
    private void startPotocolActivity() {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * @MethodName: startMapActivity
     * @Author: PotoYang
     * @Time: 2016/4/8 21:01
     * @Description: 跳转到startMapActivity
     */
    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
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
