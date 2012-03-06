package com.odeval.scoopit.ui.list.adapater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.model.Post;

public class PostListAdapter extends ArrayAdapter<Post>{
	
    private ArrayList<Post> posts;
    
    private LayoutInflater li;
    
    private OnButtonClickedListener listener;
    
    private int postLayoutId;
    
    private int actionButtonId;
    
    public PostListAdapter(int postLayoutId, int actionButtonId, Context context, List<Post> list, OnButtonClickedListener listener) {
        super(context, 0, list);
        this.postLayoutId = postLayoutId;
        this.actionButtonId = actionButtonId;
        this.listener = listener;
        if (list != null) {
            this.posts = new ArrayList<Post>(list);
        } else {
            this.posts = new ArrayList<Post>();
        }
        li = LayoutInflater.from(context);
    }
    
    public void updatePostList(List<Post> list) {
        if (list != null) {
            this.posts = new ArrayList<Post>(list);
        } else {
            this.posts = new ArrayList<Post>();
        }
        this.notifyDataSetChanged();
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
            v = li.inflate(postLayoutId, null);
            postView = new PostView(v, listener);
            v.setTag(postView);
            v.findViewById(actionButtonId).setOnClickListener(postView);
        }
        postView = (PostView)v.getTag();

        //populate view
        postView.post = posts.get(position);
        postView.position = position;
        
        if (postView.post != null) {
            postView.title.setText(postView.post.getTitle());
            String postContent = postView.post.getContent();
            if (postContent != null && postContent.length() > 0) {
            	postView.content.setText(postContent);
            } else {
            	postView.content.setVisibility(View.GONE);
            }
            postView.sourceTitle.setText(postView.post.getSource().getName());
            ScoopItApp.INSTANCE.imageLoader.displayImage(postView.post.getSource().getIconUrl(), postView.sourceIcon);
            Date postDate = new Date(postView.post.getPublicationDate());
            postView.postDate.setText(ScoopItApp.INSTANCE.dateTimeFormatShortShort.format(postDate));
            
            //and image
            if (postView.post.getImageUrl() != null) {
                ScoopItApp.INSTANCE.imageLoader.displayImage(postView.post.getImageUrl(), postView.image);
            } else if (postView.post.getImageUrls() != null && postView.post.getImageUrls().size() > 0) {
                ScoopItApp.INSTANCE.imageLoader.displayImage(postView.post.getImageUrls().get(0), postView.image);
            } else {
                postView.image.getLayoutParams().height = 0;
                postView.image.getLayoutParams().width = 0;
            }
        }
        
        return v;
    }
    
    @Override
    public void remove(Post object) {
        super.remove(object);
        posts.remove(object);
    }
    
    @Override
    public void add(Post object) {
        super.add(object);
        posts.add(0, object);
    }
    
    public void replace(Post toRemove, Post toPut) {
        int pos = posts.indexOf(toRemove);
        posts.remove(pos);
        posts.add(pos, toPut);
    }
    
    
}
