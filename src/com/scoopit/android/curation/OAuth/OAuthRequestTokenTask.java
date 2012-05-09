package com.scoopit.android.curation.OAuth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.scoopit.android.curation.Constants;

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
		} catch (OAuthCommunicationException ex) {
			Log.e(TAG, "OAuthCommunicationException - Error during OAUth retrieve request token", ex);
			e = ex;
			cancel(false);
		} catch (OAuthMessageSignerException ex) {
			Log.e(TAG, "OAuthMessageSignerException - Error during OAUth retrieve request token", ex);
			e = ex;
			cancel(false);
		} catch (OAuthNotAuthorizedException ex) {
			Log.e(TAG, "OAuthNotAuthorizedException - Error during OAUth retrieve request token", ex);
			e = ex;
			cancel(false);
		} catch (OAuthExpectationFailedException ex) {
			Log.e(TAG, "OAuthExpectationFailedException - Error during OAUth retrieve request token", ex);
			e = ex;
			cancel(false);
		}

		return null;
	}
	
	@Override
	protected void onCancelled() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    String mess = "Sorry! Cannot connect to the server. Please check your internet connexion and try later!";
	    String respBody = "";
	    if (e instanceof OAuthCommunicationException) {
	    	if (e.getCause() != null && e.getCause() instanceof OAuthCommunicationException) {
	    		OAuthCommunicationException oace = (OAuthCommunicationException) e.getCause();
	    		if (oace.getResponseBody() != null) {
	    			if (oace.getResponseBody().contains("timestamp_refused")) {
	    				mess = "Sorry! it seems your phone time is not well configured. It is needed for security reason. Please check your date and time configuration";
	    			}
	    			respBody = oace.getResponseBody();
	    		}
	    	}
	    } else if (e instanceof OAuthMessageSignerException) {
	    	mess = "Sorry! Cannot sign your request, Try later!";
	    } else if (e instanceof OAuthNotAuthorizedException) {
	    	mess = "Sorry! Cannot authorize your request, Try later!";
	    } else if (e instanceof OAuthExpectationFailedException) {
	    	mess = "Sorry! Authentification fail, Try later!";
	    }
	    
	    final String errorMess = mess + "[" + respBody + "]";
	    
        builder.setMessage(mess)
               .setCancelable(false)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       context.finish();
                       throw new RuntimeException(errorMess, e);
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