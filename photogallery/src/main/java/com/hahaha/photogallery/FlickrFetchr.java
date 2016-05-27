package com.hahaha.photogallery;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Guang on 2016/5/27.
 */
public class FlickrFetchr {
    private static final String TAG="FlickrFetchr";
    private static final String XML_PHOTO="photo";
    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url=new URL(urlSpec);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        try{
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
                return null;
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally{
            connection.disconnect();
        }
    }
    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public ArrayList<GalleryItem> fetchItems()  {
        ArrayList<GalleryItem> galleryItems=new ArrayList<GalleryItem>();
        String jsonResult =request(httpUrl, httpArg);
        try {
            parseItems(galleryItems,null);
        } catch (IOException e) {
            Log.d(TAG,"错误11"+e);
            return null;
        } catch (XmlPullParserException e) {
            Log.d(TAG,"错误12"+e);
            return null;
        }
        assert jsonResult != null;
       // String result=jsonResult.substring(0,2400);
        Log.d(TAG,jsonResult.substring(0,200));
        return galleryItems;
    }
    public void parseItems(ArrayList<GalleryItem> list, XmlPullParser parser) throws IOException, XmlPullParserException {
       /* int eventType=parser.next();
        while(eventType!=parser.END_DOCUMENT){
            if(eventType==parser.START_DOCUMENT&&XML_PHOTO.equals(parser.getName())){
                String id=
            }
        }*/
        GalleryItem itemTemp=null;
        for(int i=0;i<3;i++){
            itemTemp=new GalleryItem();
            itemTemp.setCaption("capiton"+i);
            list.add(itemTemp);
        }
    }



    String httpUrl = "http://apis.baidu.com/txapi/mvtp/meinv";
    String httpArg = "num=10";
    /**
     * @param httpUrl
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            Log.d(TAG,"错误1："+e);
            return null;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.d(TAG,"错误2："+e);
            return null;
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.d(TAG,"错误3："+e);
            return null;
        }
        // 填入apikey到HTTP header
        connection.setRequestProperty("apikey",  "ba2d4d0f184210a912ce55443a575031");
        try {
            connection.connect();
        } catch (IOException e) {
            Log.d(TAG,"错误1："+e);
            return null;
        }
        InputStream is = null;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            Log.d(TAG,"错误4："+e);
            return null;
        }
        try {
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                Log.d(TAG,"错误9："+connection.getResponseCode());
                return null;
            }
        } catch (IOException e) {
            Log.d(TAG,"错误10："+e);
            return null;
        }
        try {
            int code=connection.getResponseCode();
        } catch (IOException e) {
            Log.d(TAG,"错误5："+e);
            return null;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG,"错误6："+e);
            return null;
        }
        String strRead = null;
        try {
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
        } catch (IOException e) {
            Log.d(TAG,"错误7："+e);
            return null;
        }
        try {
            reader.close();
        } catch (IOException e) {
            Log.d(TAG,"错误8："+e);
            return null;
        }
        result = sbf.toString();
        if(connection!=null){
            connection.disconnect();
        }

        return result;
    }

}
