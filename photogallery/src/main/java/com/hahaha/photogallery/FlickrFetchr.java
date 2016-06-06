package com.hahaha.photogallery;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Guang on 2016/5/27.
 */
public class FlickrFetchr {
    private static final String TAG="FlickrFetchr";
    public static final String PREF_SEARCH_QUERY="searchQuery";
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

    public ArrayList<GalleryItem> search(String query){
        Log.d(TAG,"SEARCH:"+query);

        return fetchItems();
    }
    public ArrayList<GalleryItem> fetchItems() {
        ArrayList<GalleryItem> galleryItems=new ArrayList<GalleryItem>();
        String jsonResult = null;
        try {
            //由于此API最多每次返回10条信息，所以，调用10次，返回100条。
            for(int i=0;i<100;i+=10){
                jsonResult = request(httpUrl, httpArg);
                parseItems(galleryItems,jsonResult);
            }
        } catch (IOException e) {
            Log.d(TAG,"failed to fetch items"+e);
        } catch (JSONException e) {
            Log.d(TAG,"failed to parse items"+e);
        }
        Assert.assertNotNull(jsonResult);
        return galleryItems;
    }
 /*   public void parseItems(ArrayList<GalleryItem> list, XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType=parser.next();
       *//* while(eventType!=parser.END_DOCUMENT){
            if(eventType==parser.START_DOCUMENT&&XML_PHOTO.equals(parser.getName())){
                String id=
            }
        }*//*
        GalleryItem itemTemp=null;
        for(int i=0;i<3;i++){
            itemTemp=new GalleryItem();
            itemTemp.setCaption("capiton"+i);
            list.add(itemTemp);
        }
    }*/
    public void parseItems(ArrayList<GalleryItem> list,String jsonResult) throws JSONException {
        JSONObject object=new JSONObject(jsonResult);
        if(object.getInt("code")==200){ //正确代码
            JSONArray array=object.getJSONArray("newslist");
            JSONObject objectTemp=null;
            GalleryItem item=null;
            for(int i=0;i<array.length();i++){
                objectTemp=array.getJSONObject(i);
                item=new GalleryItem();
                item.setCaption(objectTemp.getString("title"));
                item.setCTime(objectTemp.getString("ctime"));
                item.setOriUrl(objectTemp.getString("url"));
                item.setUrl(objectTemp.getString("picUrl"));
                item.setId(String.valueOf(i));
                list.add(item);
            }
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
    public static String request(String httpUrl, String httpArg) throws IOException {
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;
        URL url = new URL(httpUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("apikey",  "ba2d4d0f184210a912ce55443a575031");
        InputStream is = connection.getInputStream();
        if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
            Log.d(TAG,"返回错误"+connection.getResponseCode());
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }

        result = sbf.toString();
        connection.disconnect();
        reader.close();


        return result;
    }

}
