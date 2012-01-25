package com.odeval.scoopit.ui.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.image.ImageLoader;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.ui.post.PostCurateActivity.OnActionComplete;

public class PostViewBeforeCurateActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_view_before_curate_activity);
        
        final Post post = getIntent().getExtras().getParcelable("post");
        
        ((Button)findViewById(R.id.post_list_original)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl()));
        		startActivity(browserIntent);
        	}
        });
        
        ((TextView)findViewById(R.id.post_list_title)).setText(post.getTitle());
        ((TextView)findViewById(R.id.post_list_content)).setText(post.getContent());
        
        ImageLoader imageLoader = new ImageLoader(this);
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            imageLoader.displayImage(post.getImageUrls().get(0), (ImageView)findViewById(R.id.post_list_image));
        }
        
        ((Button)findViewById(R.id.post_view_before_curate_scoopit)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
        
        ((Button)findViewById(R.id.post_view_before_curate_discard)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		PostCurateActivity.discardPost(post, true, PostViewBeforeCurateActivity.this, true, new OnActionComplete() {
					@Override
					public void onActionComplete() {
						PostViewBeforeCurateActivity.this.setResult(TabPostsListActivity.RESULT_REFRESH_CURATION_LIST);
						PostViewBeforeCurateActivity.this.finish();
					}
				});
        	}
        });
    }
}
