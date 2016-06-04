package com.hahaha.photogallery;

/**
 * Created by Guang on 2016/5/27.
 */
public class GalleryItem {
    private String mCaption;    //title
    private String mId;
    private String mUrl;    //picUrl    图片地址
    private String mCTime;
    private String mOriUrl; //url      图片原网址

    public String getCTime() {
        return mCTime;
    }

    public void setCTime(String CTime) {
        mCTime = CTime;
    }

    public String getOriUrl() {
        return mOriUrl;
    }

    public void setOriUrl(String oriUrl) {
        mOriUrl = oriUrl;
    }
    public String toString(){
        return mId+mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
