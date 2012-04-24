package com.odeval.scoopit;


public class Constants {

	// PROD
	public static final String REQUEST_URL 		= "http://www.scoop.it/oauth/request";
	public static final String ACCESS_URL 		= "http://www.scoop.it/oauth/access";  
	public static final String AUTHORIZE_URL 	= "https://www.scoop.it/oauth/authorize";
	
	public static final String PROFILE_REQUEST 		= "http://www.scoop.it/api/1/profile?getFollowedTopics=false";
	public static final String CURATED_POST_REQUEST = "http://www.scoop.it/api/1/topic";
	public static final String CURABLE_POST_REQUEST = "http://www.scoop.it/api/1/topic?curated=0&curable=30";
	public static final String POST_ACTION_REQUEST = "http://www.scoop.it/api/1/post";
	
	public static final String UPLOAD_IMAGE_REQUEST = "http://www.scoop.it/api/1/upload";
	
	// QA
//	public static final String REQUEST_URL 		= "http://qa.scoop.it/oauth/request";
//	public static final String ACCESS_URL 		= "http://qa.scoop.it/oauth/access";  
//	public static final String AUTHORIZE_URL 	= "https://qa.scoop.it/oauth/authorize";
//	
//	public static final String PROFILE_REQUEST 		= "http://qa.scoop.it/api/1/profile?getFollowedTopics=false";
//	public static final String CURATED_POST_REQUEST = "http://qa.scoop.it/api/1/topic";
//	public static final String CURABLE_POST_REQUEST = "http://qa.scoop.it/api/1/topic?curated=0&curable=30";
//	public static final String POST_ACTION_REQUEST = "http://qa.scoop.it/api/1/post";
//	
//	public static final String UPLOAD_IMAGE_REQUEST = "http://qa.scoop.it/api/1/upload";
	
	// Local
//	public static final String REQUEST_URL 		= "http://fbertin.integration.scoop.it/oauth/request";
//	public static final String ACCESS_URL 		= "http://fbertin.integration.scoop.it/oauth/access";  
//	public static final String AUTHORIZE_URL 	= "http://fbertin.integration.scoop.it/oauth/authorize";
//	
//	public static final String PROFILE_REQUEST 		= "http://fbertin.integration.scoop.it/api/1/profile?getFollowedTopics=false";
//	public static final String CURATED_POST_REQUEST = "http://fbertin.integration.scoop.it/api/1/topic";
//	public static final String CURABLE_POST_REQUEST = "http://fbertin.integration.scoop.it/api/1/topic?curated=0&curable=30";
//	public static final String POST_ACTION_REQUEST = "http://fbertin.integration.scoop.it/api/1/post";
//	
//	public static final String UPLOAD_IMAGE_REQUEST = "http://fbertin.integration.scoop.it/api/1/upload";
	
	
	public static final String ENCODING 		= "UTF-8";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

	
	//public static final String CURATE_ACTION = "com.odeval.scoopit.CURATE_ACTION";
	//public static final String DELETE_ACTION = "com.odeval.scoopit.DELETE_ACTION";
}
