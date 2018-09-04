package com.arcsoft.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MyIntentService myIntentService;
    Messenger mService = null;
    boolean mIsBound1 = false;
    boolean mIsBound2 = false;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyRemoteService.MSG_SET_VALUE:
                    System.out.println("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = new Messenger(service);
            System.out.println("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        MyRemoteService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        MyRemoteService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);

                msg = Message.obtain(null,
                        MyRemoteService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            System.out.println("Disconnected.");

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    void doUnbindService() {
        if (mIsBound2) {
            if (mService != null) {
                System.out.println("doUnbindService");
                unbindService(mConnection);
                mIsBound2 = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mIsBound1)
            unbindService(connection);
        if(mIsBound2)
            unbindService(mConnection);
        super.onDestroy();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功后回调方法
            MyService.LocalBinder downloadBinder = (MyService.LocalBinder) service;
            downloadBinder.startDownload();
            downloadBinder.print(4);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //服务异常调用
        }
    };

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                System.out.println("button click");
                Intent startIntent = new Intent(this,MyService.class);
                startService(startIntent);     //启动服务
                break;
            case R.id.button2:
                Intent stopIntent = new Intent(this,MyService.class);
                stopService(stopIntent);   //停止服务
                break;
            case R.id.button3:
                System.out.println("button3 click");
                if(myIntentService==null)
                    myIntentService = new MyIntentService();
                myIntentService.startActionFoo(this,"HJ ","CY");     //启动服务
                break;
            case R.id.button4:
                System.out.println("button4 click");
                if(myIntentService==null)
                    myIntentService = new MyIntentService();
                myIntentService.startActionBaz(this,"HJ ","CY");     //启动服务
                break;
            case R.id.button5:
                System.out.println("button5 click");
                Intent bindIntent = new Intent(this,MyService.class);
                bindService(bindIntent,connection, Context.BIND_AUTO_CREATE);
                mIsBound1 = true;
                break;
            case R.id.button6:
                System.out.println("button6 click");
                if(mIsBound1)
                     unbindService(connection);
                mIsBound1 = false;
                break;
            case R.id.button7:
                Intent bindIntent2 = new Intent(this,MyRemoteService.class);
                bindService(bindIntent2,mConnection,Context.BIND_AUTO_CREATE);
                mIsBound2 = true;
                break;
            case R.id.button8:
                doUnbindService();
            default:
                break;
        }
    }
}
