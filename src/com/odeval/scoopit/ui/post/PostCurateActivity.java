package com.odeval.scoopit.ui.post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.actions.PostAction;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.ui.list.adapater.GalleryPostImageAdapter;

/**
 * View to curate a post
 * 
 * @author vincentdemay
 *
 */
public class PostCurateActivity extends Activity {
    protected Post post;
    
    protected EditText titleEditText;
    protected EditText descriptionEditText;
    protected Gallery gallery;
    protected Button action;
    protected TextView pageTitle;
    
    protected void populateFormFromIntent(Intent intent) {
        post = intent.getExtras().getParcelable("post");
        setContentView(R.layout.post_curate_activity);
        
        titleEditText =  ((EditText) findViewById(R.id.post_list_title));
        descriptionEditText = ((EditText) findViewById(R.id.post_list_content));
        action = ((Button) findViewById(R.id.curate));
        gallery = (Gallery) findViewById(R.id.gallery);
        pageTitle = ((TextView) findViewById(R.id.curate_title));
        
        GalleryPostImageAdapter adapter = new GalleryPostImageAdapter(post, this);
        gallery.setUnselectedAlpha(0.5f);
        gallery.setAdapter(adapter);
        titleEditText.setText(post.getTitle());
        descriptionEditText.setText(post.getContent()); 
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        populateFormFromIntent(intent);
        
        pageTitle.setText("Accept a Post");
        
        action.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //start to update the post
                post.setTitle(titleEditText.getText().toString());
                post.setContent(descriptionEditText.getText().toString());
                post.setImageUrl((String)gallery.getSelectedItem());
                
                PostAction.curatePost(post, PostCurateActivity.this, true, new PostAction.OnActionComplete() {
                    
                    @Override
                    public void onActionComplete(Post in, Post out) {
                        Intent i = new Intent();
                        i.putExtra("postToAdd", out);
                        i.putExtra("postToRemove", in);
                        PostCurateActivity.this.setResult(TabPostsListActivity.RESULT_ADD_CURATED_AND_REMOVE_CURABLE, i);
                        PostCurateActivity.this.finish();
                    }
                });
            }
        });
    }

}
