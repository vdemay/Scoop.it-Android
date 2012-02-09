package com.odeval.scoopit.OAuth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.odeval.scoopit.R;

public class OAuthWindowActivity  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.oauth_window_activity);
        
        WebView wv = (WebView)findViewById(R.id.oauth_window_webView);
        
        String url = getIntent().getData().toString();
        
        wv.loadUrl(url);
        wv.setVisibility(View.GONE);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith("x-oauthflow")) {
                    return true;
                }
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
           }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               view.setVisibility(View.VISIBLE);
            }
        });

    }

}
