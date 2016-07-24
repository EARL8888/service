package com.earl.leanservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private Intent intent;
    private EditText edText;
    private MyService.MyBinder binder = null;
    private TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this, MyService.class);
        findViewById(R.id.btStarService).setOnClickListener(this);
        findViewById(R.id.btStopService).setOnClickListener(this);
        findViewById(R.id.btBindService).setOnClickListener(this);
        findViewById(R.id.btUnBindService).setOnClickListener(this);
        findViewById(R.id.btSyncServiceData).setOnClickListener(this);
        edText = (EditText) findViewById(R.id.edText);
        tvOut = (TextView) findViewById(R.id.tvOut);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btStarService://开启服务传值
                intent.putExtra("data", edText.getText().toString().trim());
                startService(intent);
                break;
            case R.id.btStopService:
                stopService(intent);
                break;
            case R.id.btBindService:
                bindService(intent, MainActivity.this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btUnBindService:
                unbindService(MainActivity.this);
                break;
            case R.id.btSyncServiceData:
                if (binder != null) {
                    binder.setData(edText.getText().toString().trim());
                }
                break;
        }
    }

    //绑定成功后执行
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = (MyService.MyBinder) iBinder;

        //获取服务中变化的数据
        binder.getMyService().setCallback(new MyService.Callback() {
            @Override
            public void onDataChange(String data) {
                Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putString("data",data);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    //服务所在的进程崩掉后或者被杀掉后执行
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    //创建Handler显示服务传递过来的数据
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvOut.setText(msg.getData().getString("data"));
        }
    };
}
