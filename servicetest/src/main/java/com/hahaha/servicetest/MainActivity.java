package com.hahaha.servicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private Button startBut;
    private Button stopBut;
    private Button bindBut;
    private Button unbindBut;
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                Log.d(TAG,"connect successfully: descriptor="+service.getInterfaceDescriptor()
                        +"\tComponentName:"+name+"\tservice:"+service);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"disconnected    ComponentName:"+name);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        startBut=(Button)this.findViewById(R.id.start_but);
        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startService(new Intent(MainActivity.this,MyServiceUtil.class));
            }
        });

        stopBut=(Button)this.findViewById(R.id.stop_but);
        stopBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.stopService(new Intent(MainActivity.this,MyServiceUtil.class));
            }
        });

        bindBut=(Button)this.findViewById(R.id.bind_but);
        bindBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.bindService(new Intent(MainActivity.this,MyServiceUtil.class)
                        ,mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        unbindBut=(Button)this.findViewById(R.id.unbind_but);
            unbindBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.this.unbindService(mServiceConnection);
                }
        });


    }
}
