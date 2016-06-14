package com.hahaha.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Guang on 2016/6/14.
 */
public class PhotoPageFragment extends VisibleFragment {
    private String mUrl;
    private WebView mWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl=getActivity().getIntent().getData().toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_page,container,false);
        final ProgressBar progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        final TextView titleTextView=(TextView)v.findViewById(R.id.title_textView);
        mWebView=(WebView)v.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleTextView.setText(title);
            }
        });

      /*  mWebView.addJavascriptInterface(new Object(){
            public void send(String message){
                Log.i("PhotoPageFragment","Receive Message:"+message);
            }
        },"androidObject");*/



        mWebView.loadUrl(mUrl);
        return v;
    }
}
