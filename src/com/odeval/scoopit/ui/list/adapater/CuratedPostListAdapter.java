package com.odeval.scoopit.ui.list.adapater;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.model.Post;

public class CuratedPostListAdapter extends ArrayAdapter<Post>{
    
    private ArrayList<Post> posts;
    
    private LayoutInflater li;
    
    private DateFormat postDateFormat;
    
    private OnButtonClickedListener listener;

    public CuratedPostListAdapter(Context context, List<Post> list, OnButtonClickedListener listener) {
        super(context, 0, list);
        if (list != null) {
            this.posts = new ArrayList<Post>(list);
        } else {
            this.posts = new ArrayList<Post>();
        }
    	this.listener = listener;
    	postDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        li = LayoutInflater.from(context);
    }
    
    @Override
    public Post getItem(int position) {
        return posts.get(position);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        PostView postView = null;
        //create view if it does not exist
        if (v == null) {
            //assign view
            v = li.inflate(R.layout.curated_post_list_adapter, null);
            postView = new PostView(v, listener);
            v.setTag(postView);
            v.findViewById(R.id.btn_edit).setOnClickListener(postView);
        }
        postView = (PostView)v.getTag();
        
        //populate view
        postView.post = posts.get(position);
        postView.position = position;
        
        if (postView.post != null) {
            postView.title.setText(postView.post.getTitle());
            postView.content.setText(postView.post.getContent());
            postView.sourceTitle.setText(postView.post.getSource().getName());
            ScoopItApp.INSTANCE.imageLoader.displayImage(postView.post.getSource().getIconUrl(), postView.sourceIcon);
            Date postDate = new Date(postView.post.getPublicationDate());
            postView.postDate.setText(postDateFormat.format(postDate));
            
            //and image
            if (postView.post.getImageUrls() != null && postView.post.getImageUrls().size() > 0) {
                ScoopItApp.INSTANCE.imageLoader.displayImage(postView.post.getImageUrls().get(0), postView.image);
                postView.image.getLayoutParams().height = ScoopItApp.scaleValue(50);
                postView.image.getLayoutParams().width = ScoopItApp.scaleValue(50);
            } else {
                postView.image.getLayoutParams().height = 0;
                postView.image.getLayoutParams().width = 0;
            }
        }
        
        return v;
    }
}
