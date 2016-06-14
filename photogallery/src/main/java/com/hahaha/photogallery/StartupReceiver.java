package com.hahaha.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Guang on 2016/6/7.
 */
//重启设备时，重新启动服务。
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG="StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceiver  intentAction:"+intent.getAction());
        boolean isOn= PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PollService.PREF_IS_ALARM_ON,false);
        PollService.setServiceAlarm(context,isOn);
    }


}
