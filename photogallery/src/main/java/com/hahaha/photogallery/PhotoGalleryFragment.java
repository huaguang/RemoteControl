package com.hahaha.photogallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Created by Guang on 2016/5/27.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private static  final String TAG="PhotoGalleryFragment";
    private ArrayList<GalleryItem> mGalleryItems;
    private GridView mGridView;
    private ThumbnailDownLoader<ImageView>  mThumbnailThread;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        updateItems();

        mThumbnailThread=new ThumbnailDownLoader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownLoader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()){
                    imageView.setImageBitmap(thumbnail);
                  //  Log.d(TAG,"SetImageBitMap():  current Thread"+Thread.currentThread().getName());
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        /*Intent i=new Intent(getActivity(),PollService.class);
        getActivity().startService(i);*/
  //      PollService.setServiceAlarm(getActivity(),true);
    }
    public void updateItems(){
         new FetchItemsTask().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView=(GridView)v.findViewById(R.id.gridView);
        setUpAdapter();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item=mGalleryItems.get(position);

                Uri uri= Uri.parse(item.getOriUrl());
               // Intent i=new Intent(Intent.ACTION_VIEW,uri);  //这是调用隐式Activity
                Intent i=new Intent(getActivity(),PhotoPageActivity.class);
                i.setData(uri);
                startActivity(i);
            }
        });
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
            Activity activity=getActivity();
            if(activity==null)
                return new ArrayList<GalleryItem>();

            String query= PreferenceManager.getDefaultSharedPreferences(activity)
                        .getString(FlickrFetchr.PREF_SEARCH_QUERY,null);
            if(query==null){
                return new FlickrFetchr().fetchItems();
            }else{
                return new FlickrFetchr().search(query);
            }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            MenuItem searchItem=menu.findItem(R.id.menu_item_search);
            SearchView searchView=(SearchView) searchItem.getActionView();
            SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
            ComponentName name=getActivity().getComponentName();
            SearchableInfo searchableInfo=searchManager.getSearchableInfo(name);
            searchView.setSearchableInfo(searchableInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:{
                getActivity().onSearchRequested();
             //   getActivity().startSearch("Search",false,null,false);
                return true;
            }
            case R.id.menu_item_clear:{
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putString(FlickrFetchr.PREF_SEARCH_QUERY,null).commit();
                updateItems();
                return true;
            }
            case R.id.menu_item_toggle_poll:{
                boolean shouldStartAlarm=!PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),shouldStartAlarm);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //每次菜单需要重新配置时调用
    //在3.0版本以前，每次显示菜单时自动调用，在3.0及以后，需要在invalidateOptionMenu()调用后，才会调用。
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item=menu.findItem(R.id.menu_item_toggle_poll);
        if(PollService.isServiceAlarmOn(getActivity())){
            item.setTitle(R.string.stop_poll);
        }else{
            item.setTitle(R.string.start_poll);
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
    //    Log.d(TAG,"mThumbnailThread destroyed");

    }
}
