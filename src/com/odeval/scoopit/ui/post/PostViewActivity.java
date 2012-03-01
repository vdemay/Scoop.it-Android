package com.odeval.scoopit.ui.post;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class PostViewActivity extends Activity {
    
    private Post post;
    
    private void populateFields(final Post post) {
       
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
        } else {
            ((ImageView)findViewById(R.id.post_image)).setVisibility(View.GONE);
        }
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getSource().getIconUrl(), (ImageView)findViewById(R.id.post_source_icon));
        ((TextView)findViewById(R.id.post_date)).setText(ScoopItApp.INSTANCE.dateTimeFormatMediumShort.format(new Date(post.getPublicationDate())));
                
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_view_activity);
        post = getIntent().getExtras().getParcelable("post");
        
        populateFields(post);
       
        
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
        	    Intent i = new Intent(PostViewActivity.this, PostEditActivity.class);
                i.putExtra("post", post);
                PostViewActivity.this.startActivityForResult(i, 1);
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
					    PostAction.deletePost(post, PostViewActivity.this, true, new PostAction.OnActionComplete() {
                            @Override
                            public void onActionComplete(Post in, Post out) {
                                Intent i = new Intent();
                                i.putExtra("post", in);
                                PostViewActivity.this.setResult(TabPostsListActivity.RESULT_DELETE_CURATED, i);
                                PostViewActivity.this.finish();
                            }
                        });
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
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null) {
            //update fields
            Post postToAdd = (Post)data.getExtras().get("postToAdd");
            if (postToAdd != null) {
                post = postToAdd;
                populateFields(postToAdd);
            }
        }
        
        //propagate
        setResult(resultCode, data);
    }
}
