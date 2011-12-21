package com.odeval.scoopit.ui.post;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.image.ImageLoader;
import com.odeval.scoopit.model.Post;

public class PostViewActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_list_activity);
        
        Post post = getIntent().getExtras().getParcelable("post");
        
        ((TextView)findViewById(R.id.post_list_title)).setText(post.getTitle());
        ((TextView)findViewById(R.id.post_list_content)).setText(post.getContent());
        
        ImageLoader imageLoader = new ImageLoader(this);
        if (post.getImageUrl() != null) {
            imageLoader.displayImage(post.getImageUrl(), (ImageView)findViewById(R.id.post_list_image));
        }   
    }
}
