package com.odeval.scoopit.OAuth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.User;

/**
 * Entry point in the application. 
 * Launches the OAuth flow by starting the PrepareRequestTokenActivity
 * 
 */
public class OAuthFlowApp extends Activity {
    private SharedPreferences prefs;

    LinearLayout loadLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        loadLayout = (LinearLayout) findViewById(R.id.mainLayout);
        loadLayout.setVisibility(View.INVISIBLE);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
        Button clearCredentials = (Button) findViewById(R.id.btn_clear_credentials);

        launchOauth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadLayout.setVisibility(View.INVISIBLE);
                startActivity(new Intent().setClass(v.getContext(), LoginActivity.class));
            }
        });

        clearCredentials.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearCredentials();
                performApiCall();
            }
        });

        performApiCall();
    }

    private void performApiCall() {
        final TextView textView = (TextView) findViewById(R.id.response_code);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String jsonOutput = "";
                try {
                    jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.PROFILE_REQUEST, getConsumer(OAuthFlowApp.this.prefs));
                    System.out.println("jsonOutput : " + jsonOutput);
                    JSONObject jsonResponse = new JSONObject(jsonOutput);
                    User user = new User();
                    user.popupateFromJsonObject(jsonResponse.getJSONObject("user"));
                } catch (Exception e) {
                    Log.e(OAuthFlowApp.class.getName(), "Error executing request", e);
                }
                return jsonOutput;
            }
            
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                textView.setText(result);
                loadLayout.setVisibility(View.INVISIBLE);
            }

        }.execute();

    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
    }

    private void clearCredentials() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();
    }

    private OAuthConsumer getConsumer(SharedPreferences prefs) {
        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, secret);
        return consumer;
    }
}