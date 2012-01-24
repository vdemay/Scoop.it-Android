package com.odeval.scoopit.ui.list.adapater;

import com.odeval.scoopit.model.Post;

public interface OnButtonClickedListener {
	public void onDiscard(Post p, int index);
	public void onAccept(Post p, int index);
	public void onEdit(Post p, int index);    	
}