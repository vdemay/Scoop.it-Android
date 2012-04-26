package com.scoopit.android.curation.ui.list.adapater;

import com.scoopit.android.curation.model.Post;

public interface OnButtonClickedListener {
	public void onDiscard(Post p, int index);
	public void onEdit(Post p, int index);    	
}