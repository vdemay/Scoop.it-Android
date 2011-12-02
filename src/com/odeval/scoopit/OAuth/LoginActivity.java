package com.odeval.scoopit.OAuth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.signature.HmacSha1MessageSigner;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.PrivateConstants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.ui.topic.CuratedTopicListActivity;

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
        
        new OAuthRequestTokenTask(this, consumer, provider).execute();
    }

    /**
     * Called when the OAuthRequestTokenTask finishes (user has authorized the
     * request token). The callback URL will be intercepted here.
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
            Log.i(getClass().getName(), "Callback received : " + uri);
            Log.i(getClass().getName(), "Retrieving Access Token");
            
            new RetrieveAccessTokenTask(consumer, provider, prefs){
                protected void onPostExecute(Void result) {
                    LoginActivity.this.startActivity(
                            new Intent(LoginActivity.this, CuratedTopicListActivity.class));
                };
            }.execute(uri);
            finish();
        }
    }
}
