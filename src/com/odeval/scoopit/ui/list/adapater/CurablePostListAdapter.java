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
import com.odeval.scoopit.image.ImageLoader;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.ui.task.DownloadImageTask;

public class CurablePostListAdapter extends ArrayAdapter<Post>{
    
    
    private ArrayList<Post> posts;
    private LayoutInflater li;
    public ImageLoader imageLoader; 

    public CurablePostListAdapter(Context context, List<Post> list) {
        super(context, 0, list);
        if (list != null) {
            this.posts = new ArrayList<Post>(list);
        } else {
            this.posts = new ArrayList<Post>();
        }
        li = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
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
            v = li.inflate(R.layout.curable_post_list_adapter, null);
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
            DownloadImageTask task1 = new DownloadImageTask();
            task1.setImageId(R.id.list_adapter_post_image);
            task1.setContext(v.getContext());
            task1.setRow(v);
            if (p.getImageUrls() != null && p.getImageUrls().size() > 0) {
                task1.execute(p.getImageUrls().get(0));
                imageView.getLayoutParams().height = 50;
                imageView.getLayoutParams().width = 50;
            } else {
                imageView.getLayoutParams().height = 0;
                imageView.getLayoutParams().width = 0;
            }
        }
        
        return v;
    }
}
