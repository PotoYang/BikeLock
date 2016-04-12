package com.example.potoyang.bikelock.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.potoyang.bikelock.R;
import com.example.potoyang.bikelock.SendDataToServer;
import com.example.potoyang.bikelock.view.satallite_view.SatelliteMenu;
import com.example.potoyang.bikelock.view.satallite_view.SatelliteMenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MapActivity
 * @author: Yuchuan Yang
 * @time: 2016/4/12 22:39
 * @Description: 显示地图
 */
public class MapActivity extends AppCompatActivity {

    private String strlat, strlon;//向服务器发送的数据

    //一般控件声明
    private TextView tv_lon, tv_lat;
    private Button btn_location;
    private ImageView iv_headphoto;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    //定位相关
    private LocationClient mLocationClient = null;
    private double mLatitude, mLongitude;
    boolean isFirstLoc = true;// 是否首次定位

    //滑动菜单
    private SlidingMenu slidingMenu = null;

    //卫星菜单
    private SatelliteMenu satelliteMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_map);

        initData();

        iv_headphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.showMenu();
            }
        });

        initSlidingMenu();

        initSatelliteMenu();

        satelliteMenu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {

            public void eventOccured(int id) {
                Toast.makeText(MapActivity.this, "" + id, Toast.LENGTH_SHORT).show();
            }
        });

        initMap();

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });

    }

    //取得SendDataToServer的数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SendDataToServer.SEND_SUCCESS:
                    // Toast.makeText(MapActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    break;
                case SendDataToServer.SEND_FAIL:
                    Toast.makeText(MapActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @MethodName: initData
     * @author: PotoYang
     * @time: 2016/4/12 22:37
     * @params: null
     * @return: void
     * @Description: 初始化Data
     */
    private void initData() {
        iv_headphoto = (ImageView) findViewById(R.id.iv_headphoto);
        satelliteMenu = (SatelliteMenu) findViewById(R.id.menu);

        mMapView = (MapView) findViewById(R.id.bmapView);
        tv_lon = (TextView) findViewById(R.id.tv_lon);
        tv_lat = (TextView) findViewById(R.id.tv_lat);
        btn_location = (Button) findViewById(R.id.btn_location);
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
        // View view=LayoutInflater.from(R.layout.right_drawer);
        slidingMenu = new SlidingMenu(this);

        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); // 触摸边界拖出菜单
        slidingMenu.setMenu(R.layout.right_drawer);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 将抽屉菜单与主页面关联起来
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    }

    /**
     * @MethodName: initSatelliteMenu
     * @author: PotoYang
     * @time: 2016/4/12 22:35
     * @params: null
     * @return: void
     * @Description: 初始化SatelliteMenu
     */
    private void initSatelliteMenu() {
        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(1, R.mipmap.ic_item01));
        items.add(new SatelliteMenuItem(2, R.mipmap.ic_item02));
        items.add(new SatelliteMenuItem(3, R.mipmap.ic_item03));
        items.add(new SatelliteMenuItem(4, R.mipmap.ic_item04));
        items.add(new SatelliteMenuItem(5, R.mipmap.ic_item05));
        items.add(new SatelliteMenuItem(6, R.mipmap.ic_item06));
        satelliteMenu.addItems(items);

    }

    /**
     * @MethodName: initMap
     * @author: PotoYang
     * @time: 2016/4/12 22:38
     * @params: null
     * @return: void
     * @Description: 初始化Map
     */
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        this.setLocationOption();
        mLocationClient.start();
    }

    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
    }

    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不再处理新接收的位置
            if (location == null || mMapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据

            // 更新经纬度
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            //向服务器发送获取到的经纬度信息
            strlon = Double.toString(mLongitude);
            strlat = Double.toString(mLatitude);
            new SendDataToServer(handler).SendDataToServer(strlon, strlat);

            //显示当前经纬度
            tv_lon.setText("当前经度:" + location.getLongitude());
            tv_lat.setText("当前纬度:" + location.getLatitude());

            if (isFirstLoc) {
                isFirstLoc = false;

                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 18); //设置地图中心点以及缩放级别
                mBaiduMap.animateMapStatus(u);
            }
        }
    };

    // 返回到我的位置
    private void getMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    // 三个状态实现地图生命周期管理
    @Override
    protected void onDestroy() {
        //退出时销毁定位
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
//        /**
//         * 设置为横屏
//         */
//        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        // TODO Auto-generated method stub
//        Toast.makeText(MapActivity.this, "initOverlay", Toast.LENGTH_SHORT).show();
//        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//            LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//        }
//        return false;
//    }

}
