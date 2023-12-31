package com.cilab.bluetooth_c;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {
    private BluetoothClient client;
    private EditText et_msg;
    private TextView tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init() {
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        Intent intent = getIntent();
        String remoteAddress = intent.getStringExtra("remoteAddress");
        client = new BluetoothClient(mHandler, remoteAddress);
        client.begin_listen();
    }

    public void onClick(View v) { //點擊發送訊息
        String content = et_msg.getText().toString().trim();
        if (content!=null && !content.equals("")) {
            client.send_msg(content);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
            tv_msg.setText(content);
        }
    };
}
