package com.odeval.scoopit.ui.list.adapater;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.model.Post;

public class CuratedPostListAdapter extends ArrayAdapter<Post>{
    
    
    private ArrayList<Post> posts;
    private LayoutInflater li;

    public CuratedPostListAdapter(Context context, List<Post> list) {
        super(context, 0, list);
        if (list != null) {
            this.posts = new ArrayList<Post>(list);
        } else {
            this.posts = new ArrayList<Post>();
        }
        li = LayoutInflater.from(context);
    }
    
    @Override
    public Post getItem(int position) {
        return posts.get(position);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        //create view if it does not exist
        if (v == null) {
            //assign view
            v = li.inflate(R.layout.curated_post_list_adapter, null);
        }
        
        //populate view
        Post p = posts.get(position);
        if (p != null) {
            TextView userName = (TextView) v.findViewById(R.id.list_adapter_post_title);
            TextView content = (TextView) v.findViewById(R.id.list_adapter_post_content);
            
            userName.setText(p.getTitle());
            content.setText(p.getContent());
            
            //and image
            ImageView imageView = (ImageView) v.findViewById(R.id.list_adapter_post_image);
            imageView.setImageResource(R.drawable.default_icon);
            if (p.getImageUrl() != null) {
                ScoopItApp.INSTANCE.imgageLoader.displayImage(p.getImageUrl(), imageView);
            }
        }
        
        return v;
    }
}
