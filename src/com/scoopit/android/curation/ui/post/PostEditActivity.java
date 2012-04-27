package com.scoopit.android.curation.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.model.Post;

/**
 * Activity to edit a post
 */
public class PostEditActivity extends PostCurateActivity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        pageTitle.setText("Edit a Post");
        
        action.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //start to update the post
                post.setTitle(titleEditText.getText().toString());
                post.setContent(descriptionEditText.getText().toString());
                post.setImageUrl((String)gallery.getSelectedItem());
                
                PostAction.editPost(post, PostEditActivity.this, true, new PostAction.OnActionComplete() {
                    
                    @Override
                    public void onActionComplete(Post in, Post out) {
                        Intent i = new Intent();
                        i.putExtra("postToAdd", out);
                        i.putExtra("postToRemove", in);
                        PostEditActivity.this.setResult(TabPostsListActivity.RESULT_REPLACE_CURATED, i);
                        PostEditActivity.this.finish();
                    }
                });
            }
        });
    }

}
