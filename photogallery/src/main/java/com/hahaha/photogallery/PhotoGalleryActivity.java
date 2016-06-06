package com.hahaha.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    private static  final String TAG="PhotoGalleryActivity";
    @Override
    protected Fragment CreateFragment() {
        return new PhotoGalleryFragment();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        PhotoGalleryFragment fragment=(PhotoGalleryFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query=intent.getExtras().getString(SearchManager.QUERY);
            Log.d(TAG,"Received a query:"+query);
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(FlickrFetchr.PREF_SEARCH_QUERY,query).commit();
        }
        fragment.updateItems();

    }

}
