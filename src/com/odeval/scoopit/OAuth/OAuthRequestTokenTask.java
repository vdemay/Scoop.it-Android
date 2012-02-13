package com.odeval.scoopit.OAuth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.odeval.scoopit.Constants;

/**
 * An asynchronous task that communicates with Google to 
 * retrieve a request token.
 * (OAuthGetRequestToken)
 * 
 * After receiving the request token from Google, 
 * show a browser to the user to authorize the Request Token.
 * (OAuthAuthorizeToken)
 * 
 */
public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

	final String TAG = getClass().getName();
	private LoginActivity	context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	
	private String url;

	/**
	 * 
	 * We pass the OAuth consumer and provider.
	 * 
	 * @param 	context
	 * 			Required to be able to start the intent to launch the browser.
	 * @param 	provider
	 * 			The OAuthProvider object
	 * @param 	consumer
	 * 			The OAuthConsumer object
	 */
	public OAuthRequestTokenTask(LoginActivity context,OAuthConsumer consumer,OAuthProvider provider) {
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
	}

	/**
	 * 
	 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
	 * 
	 */
	@Override
	protected Void doInBackground(Void... params) {
		
		try {
			url = provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
			//call an inner Browser : not the default browser!
		} catch (Exception e) {
			Log.e(TAG, "Error during OAUth retrieve request token", e);
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
	    super.onPostExecute(result);

        context.loadScoopItUrlForLogin(url);
	}

}