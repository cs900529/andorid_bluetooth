package com.cilab.bluetooth_c;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.UUID;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;
public class BluetoothClient {
    final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private InputStream in;
    private PrintWriter out;
    private Object lock = new Object();

    BluetoothClient(Handler handler, String remoteAddress) {
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
        try {
            mSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StringToArray(String str) {

        /*Gson gson = new Gson();
        int[][] twoDArray = gson.fromJson(str, int[][].class);

        System.out.println(Arrays.deepToString(twoDArray));
        System.out.println(twoDArray.length);
        System.out.println(twoDArray[0].length);*/
        System.out.println(str);
        Python python=Python.getInstance();

        PyObject pyObject=python.getModule("heatmap");
        pyObject.callAttr("get_heatmap", str);
        send_msg("done");
    }

    private void connect(){
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (mSocket.isConnected()) {
                            return;
                        }
                        mSocket.connect(); //c t
                        in = mSocket.getInputStream();
                        out = new PrintWriter(mSocket.getOutputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void begin_listen() {
        while (!mSocket.isConnected()) {
            connect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    while (mSocket.isConnected()) {
                        byte[] bt = new byte[1024];
                        in.read(bt);
                        String content = new String (bt, "UTF-8" );
                        if (content!=null && !content.equals("")) {
                            String[] x = content.split("!");
                            StringToArray(x[0]);
                            Message msg = new Message();
                            msg.obj = content;
                            mHandler.sendMessage(msg);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message msg = new Message();
                    msg.obj = "失去連線";
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void send_msg(final String content) {
        new Thread() {
            @Override
            public void run() {
                out.print(content);
                out.flush ();
            }
        }.start();
    }
}
