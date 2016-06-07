package com.hahaha.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by Guang on 2016/6/7.
 */
public class MyServiceUtil extends Service{
    private static final String TAG="MyServiceUtil";
    private IBinder myBinder=new Binder() {
        public String getInterfaceDescriptor(){
            return "MyService class";
        }

    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind:intent:"+intent);
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind:intent:"+intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG,"onRebind:intent:"+intent);
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand:Intent"+intent+"\tflags"+flags+"\tstartId:"+startId);
        return super.onStartCommand(intent, flags, startId);
    }
}
