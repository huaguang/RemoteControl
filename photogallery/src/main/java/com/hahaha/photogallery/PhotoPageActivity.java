package com.hahaha.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Guang on 2016/6/14.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    protected Fragment CreateFragment() {
        return new PhotoPageFragment();
    }
}
