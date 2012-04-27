package com.scoopit.android.curation.ui.list.adapater;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ScoopItApp;
import com.scoopit.android.curation.model.Topic;

public class SpinnerTopicListAdapter extends ArrayAdapter<Topic> {

    private ArrayList<Topic> topics;

    private LayoutInflater li;
    
    public SpinnerTopicListAdapter(Context context, ArrayList<Topic> objects) {
        super(context, 0, objects);
        this.topics = objects;
        li = LayoutInflater.from(context);
    }

    public void updateTopics(ArrayList<Topic> topics) {
        this.topics = topics;
        this.notifyDataSetChanged();
    }
    
    @Override
    public Topic getItem(int position) {
        return topics.get(position);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        //create view if it does not exist
        if (v == null) {
            //assign view
            v = li.inflate(R.layout.spinner_topic_list_adapter, null);
        }
        //populate view
        Topic t = topics.get(position);
        if (t != null) {
            TextView name = (TextView) v.findViewById(R.id.list_adapter_topic_name);
            TextView content = (TextView) v.findViewById(R.id.list_adapter_topic_description);
            
            name.setTextColor(Color.DKGRAY);
            content.setTextColor(Color.DKGRAY);
            
            name.setText(t.getName());
            content.setText(t.getDescription());
            
            ScoopItApp.INSTANCE.imageLoader.displayImage(t.getMediumImageUrl(), (ImageView)v.findViewById(R.id.list_adapter_topic_image));
        }
        
        return v;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	return getView(position, convertView, parent);
    }
}
