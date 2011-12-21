package com.odeval.scoopit;


public class Constants {

	public static final String CONSUMER_KEY 	= "seesmic_pl|9g0jVdFUrxjl23yEAFdGEv1iuxWmddW1bNzxE_mvrmg";
	public static final String CONSUMER_SECRET 	= "U1Cd3BwOT3RXiOlKDTyVfaE3reIapzxdQ3Qofaj@3Y6NA32UbA";

	public static final String REQUEST_URL 		= "http://www.scoop.it/oauth/request";
	public static final String ACCESS_URL 		= "http://www.scoop.it/oauth/access";  
	public static final String AUTHORIZE_URL 	= "https://www.scoop.it/oauth/authorize";
	
	public static final String PROFILE_REQUEST 		= "http://www.scoop.it/api/1/profile?getFollowedTopics=false";
	public static final String CURATED_POST_REQUEST = "http://www.scoop.it/api/1/topic";
	public static final String CURABLE_POST_REQUEST = "http://www.scoop.it/api/1/topic?curated=0&curable=30";
	
	public static final String ENCODING 		= "UTF-8";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}
