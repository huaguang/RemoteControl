package com.hahaha.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment CreateFragment() {
        return new PhotoGalleryFragment();
    }


}
