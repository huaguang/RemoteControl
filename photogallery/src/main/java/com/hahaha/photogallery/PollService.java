package com.hahaha.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;


public class PollService extends IntentService {
    private static final String TAG="PollService";
    private static final int POLL_INTERVAL=10;
    public static final String PREF_IS_ALARM_ON="isOn";
    public static final String ACTION_SHOW_NOTIFICATION="android.photo.SHOW_NOTIFICATION";
    public static final String PREM_PRIVATE="android.photogallery.PRIVATE";
    private static int label=1;

    public PollService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable=(cm.getBackgroundDataSetting()&&cm.getActiveNetworkInfo()!=null);
        if(!isNetworkAvailable)
            return ;
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        String lastResultId=sp.getString(FlickrFetchr.PREF_LAST_RESULT_ID,null);
        String query=sp.getString(FlickrFetchr.PREF_SEARCH_QUERY,null);
        ArrayList<GalleryItem> list=null;

        if(query==null){
            list=new FlickrFetchr().fetchItems();
        }else{
            list=new FlickrFetchr().search(query);
        }
        if(list.size()==0)
            return;

        String resultId=list.get(0).getUrl();
        if(resultId.equals(lastResultId)){
            Log.d(TAG,"Got a old result "+ resultId);
        }else{
            Log.d(TAG,"Got a new Result "+resultId);
            sp.edit().putString(FlickrFetchr.PREF_LAST_RESULT_ID,resultId).commit();
            Resources r=getResources();
            PendingIntent pi=PendingIntent.getActivity(this,0,new Intent(this,PhotoGalleryActivity.class),0);
            Notification notification= new NotificationCompat.Builder(this)
                    .setTicker(r.getString(R.string.new_picture_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(r.getString(R.string.new_picture_title))
                    .setContentText(r.getString(R.string.new_picture_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
          /*  NotificationManager nm= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0,notification);
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION),PREM_PRIVATE);*/
            showBackgroundNotification(0,notification);


        }
        Log.d(TAG,"现在的label为："+label++);
       // Log.d(TAG,"receive a intent:"+intent);
    }

    public static void setServiceAlarm(Context context,boolean isOn){
        Intent i=new Intent(context,PollService.class);
        PendingIntent pi=PendingIntent.getService(context,0,i,0);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(isOn){
            am.setRepeating(AlarmManager.RTC,System.currentTimeMillis(),POLL_INTERVAL,pi);
        }else{
            am.cancel(pi);
            pi.cancel();
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON,isOn).commit();

    }
    public static boolean isServiceAlarmOn(Context context){
        Intent i=new Intent(context,PollService.class);
        PendingIntent pi=PendingIntent.getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
    }

    public void showBackgroundNotification(int requestCode,Notification notification){
        Intent i=new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE",requestCode);
        i.putExtra("NOTIFICATION",notification);
        sendOrderedBroadcast(i,PREM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }
}
