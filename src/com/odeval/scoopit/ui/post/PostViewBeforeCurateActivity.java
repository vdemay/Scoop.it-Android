package com.odeval.scoopit.ui.post;

import java.util.Date;

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
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.actions.PostAction;
import com.odeval.scoopit.model.Post;

public class PostViewBeforeCurateActivity extends Activity {
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_view_before_curate_activity);
        
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
        } else if (post.getImageUrls() != null && post.getImageUrls().size() > 0) {
            ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrls().get(0), (ImageView)findViewById(R.id.post_image));
        } else {
        	((ImageView)findViewById(R.id.post_image)).setVisibility(View.GONE);
        }
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getSource().getIconUrl(), (ImageView)findViewById(R.id.post_source_icon));
        ((TextView)findViewById(R.id.post_date)).setText(ScoopItApp.INSTANCE.dateTimeFormatMediumShort.format(new Date(post.getPublicationDate())));
        
        ((Button)findViewById(R.id.post_view_before_curate_scoopit)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent i = new Intent(PostViewBeforeCurateActivity.this, PostCurateActivity.class);
                i.putExtra("post", post);
                PostViewBeforeCurateActivity.this.startActivityForResult(i, 1);
        	}
        });
        
        ((Button)findViewById(R.id.post_view_before_curate_discard)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		PostAction.discardPost(post, PostViewBeforeCurateActivity.this, true, new PostAction.OnActionComplete() {
					@Override
					public void onActionComplete(Post in, Post out) {
					    Intent i = new Intent();
					    i.putExtra("post", in);
						PostViewBeforeCurateActivity.this.setResult(TabPostsListActivity.RESULT_DELETE_CURABLE, i);
						PostViewBeforeCurateActivity.this.finish();
					}
				});
        	}
        });
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Propagate activity result to the caller activity
    	setResult(resultCode, data);
		finish();
	}
}
