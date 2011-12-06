package com.odeval.scoopit.ui.post;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.OAuth.OAuthFlowApp;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.image.ImageLoader;
import com.odeval.scoopit.model.Post;

public class PostCurateActivity extends Activity {
    private Post post;
    ImageLoader imageLoader;
    
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String test = intent.getAction();
        post = getIntent().getExtras().getParcelable("post");
        
        if (intent != null && post != null) {
        	
        	if (Constants.CURATE_ACTION.equals(intent.getAction())) {
        		curatePost(post, true, this, 0, false);
        		return;
        	} else if (Constants.DELETE_ACTION.equals(intent.getAction())) {
        		deletePost(post, true, this, false);
        		return;
        	}        	
        }
        
        setContentView(R.layout.post_curate_activity);
        
        
        ((TextView)findViewById(R.id.post_list_title)).setText(post.getTitle());
        ((TextView)findViewById(R.id.post_list_content)).setText(post.getContent());
        
        imageLoader = new ImageLoader(this);
        if (post.getImageUrls().get(0) != null) {
            imageLoader.DisplayImage(post.getImageUrls().get(0), (ImageView)findViewById(R.id.post_list_image));
        }   
        ((Button)findViewById(R.id.next_image)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		nextImage();
        	}
        });	
        ((Button)findViewById(R.id.prev_image)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		prevImage();
        	}
        });	
        ((Button)findViewById(R.id.curate)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		curatePost(post, true, PostCurateActivity.this, currentImage, true);
        	}
        });	
        ((Button)findViewById(R.id.discard)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		deletePost(post, true, PostCurateActivity.this, true);
        	}
        });	
    }

    int currentImage;
    
    private void nextImage() {
    	currentImage = (currentImage+1) % post.getImageUrls().size();
        imageLoader.DisplayImage(post.getImageUrls().get(currentImage), (ImageView)findViewById(R.id.post_list_image));
    }

    private void prevImage() {
    	currentImage--;
    	if (currentImage < 0) {
    		currentImage =  post.getImageUrls().size() - 1;
    	}
        imageLoader.DisplayImage(post.getImageUrls().get(currentImage), (ImageView)findViewById(R.id.post_list_image));    	
    }
    
    ProgressDialog progress;
    
    public static void curatePost(final Post post, final boolean finish, final Context context, final int imageIndex, final boolean showDialog) {
        new AsyncTask<Void, Void, Post>() {
        	ProgressDialog progress;
            @Override
            protected void onPreExecute() {
                if (showDialog)
                	progress = ProgressDialog.show(context, "Please Wait", "Curating post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
            	try {
            		return doCuratePost(post, imageIndex, context);
            	} catch (JSONException e) {
					e.printStackTrace();
				}
            	return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog) progress.dismiss();
            }

        }.execute();

    }

    public static void deletePost(final Post post, final boolean finish, final Context context, final boolean showDialog) {
        new AsyncTask<Void, Void, Post>() {
        	ProgressDialog dialog;
        	
            @Override
            protected void onPreExecute() {
                if (showDialog)
                	dialog = ProgressDialog.show(context, "Please Wait", "Deleting post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
            	doDeletePost(post, context);
            	return post;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                	dialog.dismiss();
            }

        }.execute();
    }

    private static Post doCuratePost(Post post, int imageIndex, Context context) throws JSONException {
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("id", post.getId().toString());
    	params.put("action", "accept");
    	params.put("imageUrl", post.getImageUrls().get(imageIndex));
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        JSONObject jsonPost = new JSONObject(jsonOutput);
        Post ret = new Post();
        ret.popupateFromJsonObject(jsonPost);
        return ret;
    }
    
    private static void doDeletePost(Post post, Context context) {
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("id", post.getId().toString());
    	params.put("action", "delete");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);    	
    }
    
}
