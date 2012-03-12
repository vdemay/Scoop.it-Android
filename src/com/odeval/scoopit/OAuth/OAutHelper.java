package com.odeval.scoopit.OAuth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.odeval.scoopit.PrivateConstants;

public class OAutHelper {

    public static void clearCredentials(SharedPreferences prefs) {
        final Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();
    }

    public static OAuthConsumer getConsumer(SharedPreferences prefs) {
        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        if (token.length() == 0) {
        	return null;
        }
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(PrivateConstants.CONSUMER_KEY, PrivateConstants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, secret);
        return consumer;
    }
}