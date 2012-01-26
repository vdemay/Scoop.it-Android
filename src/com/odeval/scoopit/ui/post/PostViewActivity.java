package com.odeval.scoopit.ui.post;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.OAuth.OAuthFlowApp;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.ui.post.PostCurateActivity.OnActionComplete;

public class PostViewActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_view_activity);
        
        final Post post = getIntent().getExtras().getParcelable("post");
        
        ((Button)findViewById(R.id.post_original)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl()));
        		startActivity(browserIntent);
        	}
        });
        
        ((TextView)findViewById(R.id.post_source_title)).setText(post.getSource().getName());
        ((TextView)findViewById(R.id.post_title)).setText(post.getTitle());
        ((TextView)findViewById(R.id.post_content)).setText(post.getContent());
        
        if (post.getImageUrl() != null) {
        	ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrl(), (ImageView)findViewById(R.id.post_image));
        }
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getSource().getIconUrl(), (ImageView)findViewById(R.id.post_source_icon));
        ((TextView)findViewById(R.id.post_date)).setText(ScoopItApp.INSTANCE.dateTimeFormatMediumShort.format(new Date(post.getPublicationDate())));
        
        ((Button)findViewById(R.id.post_share)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
        
        ((Button)findViewById(R.id.post_tag)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
        
        ((Button)findViewById(R.id.post_edit)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
        
        ((Button)findViewById(R.id.post_delete)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		new AlertDialog.Builder(PostViewActivity.this)
        		.setTitle(getResources().getString(R.string.post_list_activity_delete_confirm_title))
                .setMessage(getResources().getString(R.string.post_list_activity_delete_confirm_message, post.getTitle()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deletePost(post, true, PostViewActivity.this, true);
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
        	}
        });
    }
    
    public static void deletePost(final Post post, final boolean finish, final Activity activity, final boolean showDialog) {
   		deletePost(post, finish, activity, showDialog, null);
   	}
    
    public static void deletePost(final Post post, final boolean finish, final Activity activity, final boolean showDialog, final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
        	ProgressDialog dialog;
        	
            @Override
            protected void onPreExecute() {
                if (showDialog)
                	dialog = ProgressDialog.show(activity, "Please Wait", "Deleting post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
            	doDeletePost(post, activity);
            	return post;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                	dialog.dismiss();
                if (onActionComplete != null) {
                	onActionComplete.onActionComplete();
                }
                activity.setResult(TabPostsListActivity.RESULT_REFRESH_TOPIC_LIST);
                activity.finish();
            }

        }.execute();
    }
    
    private static void doDeletePost(Post post, Activity activity) {
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("id", post.getId().toString());
    	params.put("action", "delete");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(activity)), params);
        System.out.println("jsonOutput : " + jsonOutput);    	
    }
}
