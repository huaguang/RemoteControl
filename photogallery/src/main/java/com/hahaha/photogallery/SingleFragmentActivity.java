package com.hahaha.photogallery;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * Created by Guang on 2016/5/18.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment CreateFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);
        if(fragment==null){
            fragment=CreateFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }

    }



    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }
}
