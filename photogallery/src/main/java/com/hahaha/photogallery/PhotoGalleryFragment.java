package com.hahaha.photogallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Guang on 2016/5/27.
 */
public class PhotoGalleryFragment extends Fragment {
    private static  final String TAG="PhotoGalleryFragment";
    private ArrayList<GalleryItem> mGalleryItems;
    private GridView mGridView;
    private ThumbnailDownLoader<ImageView>  mThumbnailThread;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        mThumbnailThread=new ThumbnailDownLoader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownLoader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()){
                    imageView.setImageBitmap(thumbnail);
                    Log.d(TAG,"SetImageBitMap():  current Thread"+Thread.currentThread().getName());
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.d(TAG,"mThumbnailThread Starts");
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
            mGridView.setAdapter(new GalleryItemAdapter(mGalleryItems));
        }else{
            mGridView.setAdapter(null);
        }
    }
    //返回图片list,内含url.
    //第三个参数为doInBackground返回结果类型。
    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>{
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            ArrayList<GalleryItem> list=new FlickrFetchr().fetchItems();
            System.out.println("已返回"+list.size());
            return list;
        }
        //此方法在主线程中运行，在doInBackground之后运行。
        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mGalleryItems=galleryItems;
            setUpAdapter();
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{
        //为什么必须调用super(getActivity(),0,list);????
        public GalleryItemAdapter(ArrayList<GalleryItem> list){
            super(getActivity(),0,list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }
            ImageView imageView= (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.mipmap.ic_launcher);
            GalleryItem item=getItem(position);
            mThumbnailThread.queueThumbnail(imageView,item.getUrl());
            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();    //退出子线程；
        Log.d(TAG,"mThumbnailThread destroyed");

    }
}
