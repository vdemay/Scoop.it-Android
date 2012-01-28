package com.odeval.scoopit.OAuth;


import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

	private OAuthProvider provider;
	private OAuthConsumer consumer;
	private SharedPreferences prefs;
	
	public RetrieveAccessTokenTask(OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
		this.consumer = consumer;
		this.provider = provider;
		this.prefs=prefs;
	}


	/**
	 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
	 * for future API calls.
	 */
	@Override
	protected Void doInBackground(Uri...params) {
		final Uri uri = params[0];
		
		
		final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

		try {
            provider.setOAuth10a(true);
			provider.retrieveAccessToken(consumer, oauth_verifier);

			final Editor edit = prefs.edit();
			edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
			edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
			edit.commit();
			
			String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
			String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			
			consumer.setTokenWithSecret(token, secret);

			Log.i(getClass().getName(), "OAuth - Access Token Retrieved");
			
		} catch (Exception e) {
			Log.e(getClass().getName(), "OAuth - Access Token Retrieval Error", e);
		}

		return null;
	}
}