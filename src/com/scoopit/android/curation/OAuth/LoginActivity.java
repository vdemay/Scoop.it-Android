package com.scoopit.android.curation.OAuth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.signature.HmacSha1MessageSigner;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.scoopit.android.curation.Constants;
import com.scoopit.android.curation.PrivateConstants;
import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ui.topic.CuratedTopicListActivity;

/**
 * Prepares a OAuthConsumer and OAuthProvider
 * 
 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.
 * 
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the
 * request.
 * 
 * After the request is authorized, a callback is made here.
 * 
 */
public class LoginActivity extends Activity {

    private OAuthConsumer consumer;
    private OAuthProvider provider;
    
    private LinearLayout loadIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            System.setProperty("debug", "true");
            this.consumer = new CommonsHttpOAuthConsumer(PrivateConstants.CONSUMER_KEY, PrivateConstants.CONSUMER_SECRET);
            this.consumer.setMessageSigner(new HmacSha1MessageSigner());

            this.provider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL, Constants.ACCESS_URL,
                    Constants.AUTHORIZE_URL);

        } catch (Exception e) {
            Log.e(getClass().getName(), "Error creating consumer / provider", e);
        }

        setContentView(R.layout.login_activity);
        
        loadIndicator = (LinearLayout)findViewById(R.id.loginLayout);
        
        new OAuthRequestTokenTask(this, consumer, provider).execute();
    }
    
    public void loadScoopItUrlForLogin(String url) {
        WebView wv = (WebView)findViewById(R.id.login_webview);
        
        //FIX FOR BUG IN ORDER TO SHOW THE KEYBOARD
        wv.requestFocus(View.FOCUS_DOWN);
        wv.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
       
        wv.loadUrl(url);
        wv.setVisibility(View.GONE);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith("x-oauthflow")) {
                    executeStoringForConnectedUser(Uri.parse(url));
                    return true;
                }
                view.loadUrl(url);
                loadIndicator.setVisibility(View.VISIBLE);
                return false; // then it is not handled by default action
           }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadIndicator.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("/login")) {
                    view.setVisibility(View.VISIBLE);
                }
                loadIndicator.setVisibility(View.GONE);
            }
        });
    }
    
    public void executeStoringForConnectedUser(Uri uri) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
            Log.i(getClass().getName(), "Callback received : " + uri);
            Log.i(getClass().getName(), "Retrieving Access Token");
            
            new RetrieveAccessTokenTask(consumer, provider, prefs){
                protected void onPostExecute(Void result) {
                    LoginActivity.this.startActivity(
                            new Intent(LoginActivity.this, CuratedTopicListActivity.class));
                };
            }.execute(uri);
            //finish();
        }
    }

    /**
     * Called when the OAuthRequestTokenTask finishes (user has authorized the
     * request token). The callback URL will be intercepted here.
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        executeStoringForConnectedUser(uri);
       
    }
}
