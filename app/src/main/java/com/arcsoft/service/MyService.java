package com.arcsoft.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/****
 *  Service运行在UI线程中
 */
public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        System.out.println("创建服务");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("启动服务...");
        for (int i=0;i<10;i++){
            System.out.println("i="+i);
            try {
                Thread.sleep(200);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("停止服务");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("已经解除绑定");
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
        public void  startDownload(){
            System.out.println("start download");
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("download success");
        }
        public void  print(int i){
            for(int m=0;m<i;++m){
                System.out.println(m);
                try {
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
