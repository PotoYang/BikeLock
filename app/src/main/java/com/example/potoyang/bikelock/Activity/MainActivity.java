package com.example.potoyang.bikelock.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.potoyang.bikelock.Fragment.InfoFragment;
import com.example.potoyang.bikelock.Fragment.LocationFragment;
import com.example.potoyang.bikelock.Fragment.SportFragment;
import com.example.potoyang.bikelock.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout ll_bottom01, ll_bottom02, ll_bottom03;

    private ImageView iv_bottom01, iv_bottom02, iv_bottom03;

    private Fragment fg_sport, fg_location, fg_info;

    //滑动菜单
    private SlidingMenu slidingMenu = null;

    private ImageView iv_headphoto;
    private TextView tv_title;
    private TextView map_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化布局
        initView();

        //初始化侧滑菜单
        initSlidingMenu();

        //初始化点击事件
        initEvent();

        //切换选项
        selectItem(0);
    }

    /**
     * @MethodName: initView
     * @author: PotoYang
     * @time: 2016/4/13 18:45
     * @params: null
     * @return: void
     * @Description: 初始化布局
     */
    private void initView() {
        ll_bottom01 = (LinearLayout) findViewById(R.id.ll_bottom01);
        ll_bottom02 = (LinearLayout) findViewById(R.id.ll_bottom02);
        ll_bottom03 = (LinearLayout) findViewById(R.id.ll_bottom03);

        iv_bottom01 = (ImageView) findViewById(R.id.iv_bottom01);
        iv_bottom02 = (ImageView) findViewById(R.id.iv_bottom02);
        iv_bottom03 = (ImageView) findViewById(R.id.iv_bottom03);

        iv_headphoto = (ImageView) findViewById(R.id.iv_headphoto);
        tv_title = (TextView) findViewById(R.id.tv_title);
        map_clear = (TextView) findViewById(R.id.map_clear);
    }

    /**
     * @MethodName: initEvent
     * @author: PotoYang
     * @time: 2016/4/13 18:46
     * @params: null
     * @return: void
     * @Description: 初始化点击事件
     */
    private void initEvent() {
        ll_bottom01.setOnClickListener(this);
        ll_bottom02.setOnClickListener(this);
        ll_bottom03.setOnClickListener(this);

        iv_headphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.showMenu();
            }
        });
    }

    /**
     * @MethodName: selectItem
     * @author: PotoYang
     * @time: 2016/4/13 19:59
     * @params: item
     * @return: void
     * @Description: Fragment选择
     */
    private void selectItem(int item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fmt = fm.beginTransaction();
        hideFragment(fmt);

        switch (item) {
            case 0:
                if (fg_sport == null) {
                    fg_sport = new SportFragment();
                    fmt.add(R.id.fl_content, fg_sport);
                } else {
                    fmt.show(fg_sport);
                }
                tv_title.setText("运动");
                map_clear.setVisibility(View.INVISIBLE);
                iv_bottom01.setImageResource(R.mipmap.ic_bottom01_press);
                break;

            case 1:
                if (fg_location == null) {
                    fg_location = new LocationFragment();
                    fmt.add(R.id.fl_content, fg_location);
                } else {
                    fmt.show(fg_location);
                }
                tv_title.setText("地图");
                map_clear.setVisibility(View.VISIBLE);
                map_clear.setText("清空");
                iv_bottom02.setImageResource(R.mipmap.ic_bottom02_press);
                break;

            case 2:
                if (fg_info == null) {
                    fg_info = new InfoFragment();
                    fmt.add(R.id.fl_content, fg_info);
                } else {
                    fmt.show(fg_info);
                }
                tv_title.setText("消息");
                map_clear.setVisibility(View.INVISIBLE);
                iv_bottom03.setImageResource(R.mipmap.ic_bottom03_press);
                break;
            default:
                break;
        }

        fmt.commit();
    }

    /**
     * @MethodName: initSlidingMenu
     * @author: PotoYang
     * @time: 2016/4/12 22:36
     * @params: null
     * @return: void
     * @Description: 初始化SlidingMenu
     */
    private void initSlidingMenu() {
        // View view=LayoutInflater.from(R.layout.left_drawer);
        slidingMenu = new SlidingMenu(this);

        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); // 触摸边界拖出菜单
        slidingMenu.setMenu(R.layout.left_drawer);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 将抽屉菜单与主页面关联起来
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    }


    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (fg_sport != null) {
            fragmentTransaction.hide(fg_sport);
        }
        if (fg_location != null) {
            fragmentTransaction.hide(fg_location);
        }
        if (fg_info != null) {
            fragmentTransaction.hide(fg_info);
        }
    }

    @Override
    public void onClick(View v) {
        resetImage();
        switch (v.getId()) {
            case R.id.ll_bottom01:
                selectItem(0);
                break;
            case R.id.ll_bottom02:
                selectItem(1);
                break;
            case R.id.ll_bottom03:
                selectItem(2);
                break;
            default:
                break;
        }
    }

    private void resetImage() {
        iv_bottom01.setImageResource(R.mipmap.ic_bottom01_normal);
        iv_bottom02.setImageResource(R.mipmap.ic_bottom02_normal);
        iv_bottom03.setImageResource(R.mipmap.ic_bottom03_normal);
    }
}