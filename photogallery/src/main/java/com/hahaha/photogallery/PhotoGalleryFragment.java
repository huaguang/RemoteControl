package com.hahaha.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Guang on 2016/5/27.
 */
public class PhotoGalleryFragment extends Fragment {
    private static  final String TAG="PhotoGalleryFragment";
    private ArrayList<GalleryItem> mGalleryItems;
    private GridView mGridView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView=(GridView)v.findViewById(R.id.gridView);
        setUpAdapter();
        return v;
    }
    private void setUpAdapter(){
        if(getActivity()==null||mGridView==null){
            return ;
        }
        if(mGalleryItems!=null){
            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
                    android.R.layout.simple_list_item_1,mGalleryItems
            ));
        }else{
            mGridView.setAdapter(null);
        }
    }
    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>{
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
           return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mGalleryItems=galleryItems;
            setUpAdapter();
        }
    }
}
