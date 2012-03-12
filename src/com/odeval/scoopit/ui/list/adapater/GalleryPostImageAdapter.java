package com.odeval.scoopit.ui.list.adapater;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.model.Post;

public class GalleryPostImageAdapter extends BaseAdapter {
    Post post;
    Context context;
    int imageBackground;

    public GalleryPostImageAdapter(Post post, Context context) {
        this.context = context;
        this.post = post;
        
        TypedArray ta = context.obtainStyledAttributes(com.odeval.scoopit.R.styleable.GalleryPreview);
        imageBackground = ta.getResourceId(com.odeval.scoopit.R.styleable.GalleryPreview_android_galleryItemBackground, 0);
        ta.recycle();
    }

    public int getCount() {
        int count = post.getImageUrls().size();
        if (post.getImageUrl() != null) {
            count++;
        }
        return count;
    }

    public Object getItem(int position) {
        if (post.getImageUrl() != null) {
            if (position == 0) {
                return post.getImageUrl();
            }
            position --;
        }
        return post.getImageUrls().get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(context);
        }
        
        ImageView iv = (ImageView) convertView;
        
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setLayoutParams(new Gallery.LayoutParams(250, 250));
        iv.setBackgroundResource(imageBackground);
        
        ScoopItApp.INSTANCE.imageLoader.displayImage((String) getItem(position), (ImageView) convertView);
        return convertView;
    }

}
