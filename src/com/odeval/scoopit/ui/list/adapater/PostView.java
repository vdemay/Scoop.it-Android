package com.odeval.scoopit.ui.list.adapater;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.model.Post;

public class PostView implements OnClickListener {
	
	TextView title;
	TextView content;
	TextView sourceTitle;
	ImageView sourceIcon;
	TextView postDate;
	ImageView image;    	    	
	Post post;
	int position;
	
	OnButtonClickedListener listener;
	
	public PostView() {}
	
	public PostView(View view, OnButtonClickedListener listener) {
		this.listener = listener;
		
		title = (TextView) view.findViewById(R.id.list_adapter_post_title);
        content = (TextView) view.findViewById(R.id.list_adapter_post_content);
        sourceTitle = (TextView) view.findViewById(R.id.list_adapter_source_title);
        sourceIcon = (ImageView) view.findViewById(R.id.list_adapter_source_icon);
        postDate = (TextView) view.findViewById(R.id.list_adapter_post_date);
        image = (ImageView) view.findViewById(R.id.list_adapter_post_image);
	}
	
	public void onClick(View v) {
		if (post != null && listener != null) {
			switch (v.getId()) {
				case R.id.btn_discard:
					listener.onDiscard(post, position);
					break;
				case R.id.btn_edit:
					listener.onEdit(post, position);
					break;
			}
		}
	}
}
