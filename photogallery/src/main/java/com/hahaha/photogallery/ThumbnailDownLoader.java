package com.hahaha.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import junit.framework.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guang on 2016/6/5.
 */
public class ThumbnailDownLoader<Token> extends HandlerThread {
    private static final String TAG="ThumbnailDawnLoader";
    private static final int MESSAGE_DOWNLOAD=0;

    private Handler mHandler;
    private Map<Token,String> requestMap= Collections.synchronizedMap(new HashMap<Token, String>());

    private Handler mResponseHandler;   //接受主线程传递过来的mHandler，以完成信息传递。
    private Listener<Token> mListener;  //消息

    private LruCache<String,Bitmap> mLruCache;

    public interface Listener<Token>{
        void onThumbnailDownloaded(Token token,Bitmap  thumbnail);
    }
    public void setListener(Listener<Token> listener){
        mListener=listener;
    }

    public ThumbnailDownLoader(Handler handler) {
        super(TAG);
        mResponseHandler=handler;
        mLruCache=new LruCache<String,Bitmap>(50);/*{
            @Override
            protected int sizeOf(String key, Bitmap value) {
               // Assert.assertEquals(1520000,value.getByteCount());
              //  Log.d(TAG,"Size:"+value.getByteCount());
               // return value.getByteCount();
            }
        };*/
    }

    //在Looper第一次检查消息队列之前调用此方法。
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==MESSAGE_DOWNLOAD){
                    @SuppressWarnings("unchecked")
                    Token token=(Token)msg.obj;
                //    Log.i(TAG,"got a request from url:"+requestMap.get(token));
                    handleRequest(token);

                }
            }
        };
    }
    private void handleRequest(final Token token){
        try {
            final String url= requestMap.get(token);
            if(url==null){
                return;
            }
            //好蠢的做法啊！！！！。。
            Bitmap bitmapT=null;
            if((bitmapT=mLruCache.get(url))==null){
                byte[] bitmapByte=new FlickrFetchr().getUrlBytes(url);
                bitmapT= BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
                mLruCache.put(url,bitmapT);
            }
            Log.d(TAG,"hitCount:"+ mLruCache.hitCount()+"\tmissCount:"+mLruCache.missCount()+"mLruSize:"+mLruCache.size());
            final Bitmap bitmap=bitmapT;
            //  Log.i(TAG,"bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token)!=url){
                        return;
                    }
                    requestMap.remove(token);
                //    Log.d(TAG,"Run():  current Thread"+Thread.currentThread().getName());
                    mListener.onThumbnailDownloaded(token,bitmap);
                }
            });

        } catch (IOException e) {
            Log.i(TAG,"download image error:"+e);
        }


    }
    //使用自己的handler，从公共消息池里获取message，然后发送到自己的信箱中。
    public void queueThumbnail(Token token, String url){
     //   Log.d(TAG,"Got an url:"+url);
        requestMap.put(token,url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD,token).sendToTarget();
    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
