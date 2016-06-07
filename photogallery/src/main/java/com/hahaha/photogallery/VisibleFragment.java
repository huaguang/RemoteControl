package com.hahaha.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Guang on 2016/6/7.
 */
public abstract class VisibleFragment extends Fragment {
    public static final String TAG="VisibleFragment";
    private BroadcastReceiver mOnShowNotification=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(),"Got a broadcast action is "+intent.getAction()
                    ,Toast.LENGTH_LONG).show();
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mOnShowNotification,
                new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION),PollService.PREM_PRIVATE,null);
    }
}
