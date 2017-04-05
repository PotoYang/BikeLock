package com.example.potoyang.bikelock.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.potoyang.bikelock.Bluetooth.BluetoothUnlockActivity;
import com.example.potoyang.bikelock.R;
import com.example.potoyang.bikelock.SendDataToServer;
import com.example.potoyang.bikelock.view.satallite_view.SatelliteMenu;
import com.example.potoyang.bikelock.view.satallite_view.SatelliteMenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment {

    private String strlat, strlon;//向服务器发送的数据

    //一般控件声明
    private TextView tv_lon, tv_lat;
    private Button btn_location;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    //定位相关
    private LocationClient mLocationClient = null;
    private double mLatitude, mLongitude;
    boolean isFirstLoc = true;// 是否首次定位

    //卫星菜单
    private SatelliteMenu satelliteMenu = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_location, null, false);

        initData(view);

        initSatelliteMenu();

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });

        satelliteMenu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {

            public void eventOccured(int id) {
                switch (id) {
                    case 1:
                        Intent intent = new Intent(view.getContext(), BluetoothUnlockActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;

                }
            }
        });

        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        return view;
    }

    /**
     * @MethodName: initData
     * @author: PotoYang
     * @time: 2016/4/12 22:37
     * @params: null
     * @return: void
     * @Description: 初始化Data
     */
    private void initData(View view) {
        tv_lon = (TextView) view.findViewById(R.id.tv_lon);
        tv_lat = (TextView) view.findViewById(R.id.tv_lat);
        btn_location = (Button) view.findViewById(R.id.btn_location);
        satelliteMenu = (SatelliteMenu) view.findViewById(R.id.menu);
        mMapView = (MapView) view.findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(myListener);
        this.setLocationOption();
        mLocationClient.start();

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
        items.add(new SatelliteMenuItem(6, R.mipmap.ic_item01));
        items.add(new SatelliteMenuItem(5, R.mipmap.ic_item02));
        items.add(new SatelliteMenuItem(4, R.mipmap.ic_item03));
        items.add(new SatelliteMenuItem(3, R.mipmap.ic_item04));
        items.add(new SatelliteMenuItem(2, R.mipmap.ic_item05));
        items.add(new SatelliteMenuItem(1, R.mipmap.ic_item06));
        satelliteMenu.addItems(items);

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

    //取得SendDataToServer的数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SendDataToServer.SEND_SUCCESS:
                    break;
                case SendDataToServer.SEND_FAIL:
                    Toast.makeText(getContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    // 三个状态实现地图生命周期管理
    @Override
    public void onDestroy() {
        //退出时销毁定位
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    public void onResume() {
//        /**
//         * 设置为横屏
//        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public static void getCityFromLngAndlat() throws IOException {
        //通过修改这里的location（经纬度）参数，即可得到相应经纬度的详细信息
        String url2 = "http://api.map.baidu.com/geocoder/v2/?ak=pmCgmADsAsD9rEXkqWNcTzjd&location=22.75424,112.76535&output=json&pois=1 ";
        URL myURL2 = null;
        URLConnection httpsConn2 = null;
        try {
            myURL2 = new URL(url2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr2 = null;
        BufferedReader br2 = null;
        try {
            httpsConn2 = (URLConnection) myURL2.openConnection();// 不使用代理
            if (httpsConn2 != null) {
                insr2 = new InputStreamReader(httpsConn2.getInputStream(), "UTF-8");
                br2 = new BufferedReader(insr2);
                String data2 = br2.readLine();
                try {
                    //解析得到的json格式数据
                    JSONObject dataJson = new JSONObject(data2);
                    JSONObject result = dataJson.getJSONObject("result");
                    JSONObject addressComponent = result.getJSONObject("addressComponent");
                    String city = addressComponent.getString("city");
                    System.out.println("city = " + city);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr2 != null) {
                insr2.close();
            }
            /*if (br != null) {
                br2.close();
            }*/
        }
    }
}
