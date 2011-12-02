package com.odeval.scoopit.ui.post;

import java.net.URLEncoder;

import android.app.Activity;
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
        
        setContentView(R.layout.post_curate_activity);
        
        post = getIntent().getExtras().getParcelable("post");
        
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
        		doCuratePost(post);
        	}
        });	
        ((Button)findViewById(R.id.discard)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		doDeletePost(post);
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

    private void doCuratePost(Post post) {
        String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.POST_ACTION_REQUEST + "?id="
                + post.getId() + "&action=accept&imageUrl=" + URLEncoder.encode(post.getImageUrls().get(currentImage)),
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(this)));
        System.out.println("jsonOutput : " + jsonOutput);

    }
    private void doDeletePost(Post post) {
        String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.POST_ACTION_REQUEST + "?id="
                + post.getId() + "&action=delete&imageUrl=" + URLEncoder.encode(post.getImageUrls().get(currentImage)),
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(this)));
        System.out.println("jsonOutput : " + jsonOutput);
    	
    }
    
}
