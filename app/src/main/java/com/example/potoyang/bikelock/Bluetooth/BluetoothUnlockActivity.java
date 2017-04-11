package com.example.potoyang.bikelock.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potoyang.bikelock.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import cz.msebera.android.httpclient.Header;

public class BluetoothUnlockActivity extends AppCompatActivity {

    private Button btn_lock, btn_unlock, btn_blu_link;

    private BluetoothSPP bluetoothSPP;

    private TextView textStatus;

    private final String BLU_LOCK = "{cmd_blu_lock}";
    private final String BLU_UNLOCK = "{cmd_blu_unlock}";

    private final String WIFI_LOCK = "{cmd_wifi_lock}";
    private final String WIFI_UNLOCK = "{cmd_wifi_unlock}";

    int reverse = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_unlock);

        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);
        btn_blu_link = (Button) findViewById(R.id.btn_blu_link);

        textStatus = (TextView) findViewById(R.id.textStatus);
    }

    private void BluetoothLink() {
        if (!bluetoothSPP.isBluetoothAvailable()) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_SHORT).show();
            finish();
        }

        btn_blu_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothSPP.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                Toast.makeText(getBaseContext(), "连接", Toast.LENGTH_SHORT).show();
            }
        });

        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Status : Connect to " + name);
            }

            @Override
            public void onDeviceDisconnected() {
                textStatus.setText("Status : Not connect!");
            }

            @Override
            public void onDeviceConnectionFailed() {
                textStatus.setText("Status : Connection failed");
            }
        });
    }

    private void WifiLink() {
        btn_lock.setText("Wifi Lock");
        btn_unlock.setText("Wifi Unlock");
        btn_blu_link.setVisibility(View.GONE);

        textStatus.setText("Status : wifi connected");

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams param2 = new RequestParams();
                param2.put("lock", WIFI_UNLOCK);
                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://192.168.1.108:8888/Train/lock_unlock.php", param2,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String getStr = new String(responseBody);
                                Toast.makeText(BluetoothUnlockActivity.this, "发送成功!  " + getStr, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
            }
        });

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams param2 = new RequestParams();
                param2.put("lock", WIFI_LOCK);
                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://192.168.1.108:8888/Train/lock_unlock.php", param2,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String getStr = new String(responseBody);
                                Toast.makeText(BluetoothUnlockActivity.this, "发送成功!  " + getStr, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
            }
        });

        Toast.makeText(this, "哈哈哈,你用了我们的wifi去开我们设计锁!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothSPP.stopService();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkWifiStatus(getBaseContext())) {
            Toast.makeText(this, "Wifi未连接!请使用蓝牙连接。", Toast.LENGTH_SHORT).show();

            bluetoothSPP = new BluetoothSPP(this);

            if (!bluetoothSPP.isBluetoothEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {
                if (!bluetoothSPP.isServiceAvailable()) {
                    bluetoothSPP.setupService();
                    bluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
                    setup();
                }
            }

            BluetoothLink();

        } else {

            WifiLink();

        }


    }

    public void setup() {
        btn_lock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (int i = 0; i < BLU_LOCK.length(); i++) {
                    bluetoothSPP.send(String.valueOf(BLU_UNLOCK.charAt(i)).getBytes(), false);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (int i = 0; i < BLU_UNLOCK.length(); i++) {
                    bluetoothSPP.send(String.valueOf(BLU_UNLOCK.charAt(i)).getBytes(), false);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetoothSPP.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothSPP.setupService();
                bluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //judge network status is connecting or not
    private boolean checkNetworkConnected(Context context) {
        if (null != context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    //judge Wifi status is connecting or not
    private boolean checkWifiStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != networkInfo
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

}
