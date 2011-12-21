package com.odeval.scoopit.ui.list.adapater;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.image.ImageLoader;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.ui.task.DownloadImageTask;

public class CurablePostListAdapter extends ArrayAdapter<Post>{
    private ArrayList<Post> posts;
    private LayoutInflater li;
    public ImageLoader imageLoader; 

    public CurablePostListAdapter(Context context, List<Post> list, OnButtonClickedListener listener) {
    	this(context, list);
    	this.listener = listener;
    }
   	
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
    
    public class ViewHolder implements OnClickListener {
    	TextView title;
    	TextView content;
    	ImageView image;    	    	
    	Post post;
    	int position;
    	
		@Override
		public void onClick(View v) {
			if (post != null && listener != null) {
				switch (v.getId()) {
				case R.id.btn_accept:
					listener.onAccept(post, position);
					break;
				case R.id.btn_delete:
					listener.onDelete(post, position);
					break;
				case R.id.btn_edit:
					listener.onEdit(post, position);
					break;						
				}
			}
		}
    }
    
    public interface OnButtonClickedListener {
    	public void onDelete(Post p, int index);
    	public void onAccept(Post p, int index);
    	public void onEdit(Post p, int index);    	
    }
    
    OnButtonClickedListener listener;
    
    public void setOnButtonClickListener(OnButtonClickedListener listener) {
    	this.listener = listener;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder h = null;
        //create view if it does not exist
        if (v == null) {
            //assign view
            v = li.inflate(R.layout.curable_post_list_adapter, null);
            h = new ViewHolder();
            v.setTag(h);
            h.title = (TextView) v.findViewById(R.id.list_adapter_post_title);
            h.content = (TextView) v.findViewById(R.id.list_adapter_post_content);
            h.image = (ImageView) v.findViewById(R.id.list_adapter_post_image);
            v.findViewById(R.id.btn_accept).setOnClickListener(h);
            v.findViewById(R.id.btn_delete).setOnClickListener(h);
            v.findViewById(R.id.btn_edit).setOnClickListener(h);
        }
        h = (ViewHolder)v.getTag();

        //populate view
        h.post = posts.get(position);
        h.position = position;
        
        if (h.post != null) {
            h.title.setText(h.post.getTitle());
            h.content.setText(h.post.getContent());
            
            //and image
            h.image.setImageResource(R.drawable.default_icon);
            DownloadImageTask task1 = new DownloadImageTask();
            task1.setImageId(R.id.list_adapter_post_image);
            task1.setContext(v.getContext());
            task1.setRow(v);
            if (h.post.getImageUrls() != null && h.post.getImageUrls().size() > 0) {
                ScoopItApp.INSTANCE.imgageLoader.displayImage(h.post.getImageUrls().get(0), h.image);
                h.image.getLayoutParams().height = ScoopItApp.scaleValue(50);
                h.image.getLayoutParams().width = ScoopItApp.scaleValue(50);
            } else {
                h.image.getLayoutParams().height = 0;
                h.image.getLayoutParams().width = 0;
            }
        }
        
        return v;
    }
    
    
}
