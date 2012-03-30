package com.odeval.scoopit.OAuth;

import org.apache.http.auth.InvalidCredentialsException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	private Exception e;
	
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
		} catch (Exception ex) {
			Log.e(TAG, "Error during OAUth retrieve request token", ex);
			e = ex;
			cancel(false);
		}

		return null;
	}
	
	@Override
	protected void onCancelled() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Sorry! An error occured. Please check your internet connexion and try later! ")
               .setCancelable(false)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       context.finish();
                       throw new RuntimeException("Can not get Request token", e);
                   }
               }).setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       context.startActivity(new Intent(context, LoginActivity.class));
                       context.finish();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
        super.onCancelled();
	}
	
	@Override
	protected void onPostExecute(Void result) {
	    super.onPostExecute(result);

        context.loadScoopItUrlForLogin(url);
	}

}