package com.cilab.bluetooth_c.discover;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ScanDevices {
    private BluetoothAdapter mBluetoothAdapter;

    ScanDevices() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public ArrayList<BluetoothDevice> getBondedDevices() { //獲取使用過的設備
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        ArrayList bondedDevices = new ArrayList();
        Iterator<BluetoothDevice> iterator = devices.iterator();
        while (iterator.hasNext()) {
            bondedDevices.add(iterator.next());
        }
        return bondedDevices;
    }

    public void startDiscovery() { //搜尋周圍設備並回傳
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable(); // 開啟藍芽
            return;
        }
        mBluetoothAdapter.startDiscovery();
    }
}
