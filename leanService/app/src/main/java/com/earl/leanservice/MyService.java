package com.earl.leanservice;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {

    private boolean commitFrist = false;
    private String data = "默认信息";
    private int i;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //        throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public void setData(String data) {
            MyService.this.data = data;
        }

        public MyService getMyService(){
            return MyService.this;
        }
    }

    //服务一开启就运行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        data = intent.getStringExtra("data");//接受传值
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        commitFrist = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                i = 0;
                while (commitFrist) {
                    i++;
                    System.out.println(i+":"+data);

                    //把数据回传到外部
                    if(callback != null){
                        callback.onDataChange(i+":"+data);
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        commitFrist = false;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        commitFrist = false;
    }

    /**
     * 以下创建回调函数
     */
    private Callback callback = null;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    public interface Callback {
        void onDataChange(String data);
    }
}
