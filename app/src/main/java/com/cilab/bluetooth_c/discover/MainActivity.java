package com.cilab.bluetooth_c.discover;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;

import android.os.Bundle;

import com.cilab.bluetooth_c.ChatActivity;
import com.cilab.bluetooth_c.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private DevicesAdapter adapter;
    private ArrayList<BluetoothDevice> list;
    private ScanDevices mScanDevices;

    public static final String[] permissions = {
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.BLUETOOTH_PRIVILEGED" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(permissions,1);
        }

        //初始化python環境
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python python=Python.getInstance();

        //調用hello_python.py裡面的Python_say_Hello函式
        int x = 8;

        PyObject pyObject=python.getModule("hello_python");
        x = pyObject.callAttr("Python_say_Hello", x).toJava(int.class);

        System.out.println(x);

        init();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listview);
        list = new ArrayList<>();
        adapter = new DevicesAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(icl);
        mScanDevices = new ScanDevices();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    public void onClick(View view){ //单击扫描按钮
        list.clear();
        list.addAll(mScanDevices.getBondedDevices()); //添加已绑定的设备列表
        adapter.notifyDataSetChanged();
        mScanDevices.startDiscovery(); //搜索周围蓝牙设备，并通过广播返回
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND: //接收搜索到的蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String address = device.getAddress();
                    for (int i = 0; i < list.size(); i++) { //避免接收重复的设备
                        if (address == null || address.equals(list.get(i).getAddress())) {
                            return;
                        }
                    }
                    list.add(device);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    AdapterView.OnItemClickListener icl = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String address = list.get(position).getAddress();
            startChat(address);
        }
    };

    private void startChat(String remoteAddress) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("remoteAddress", remoteAddress);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}