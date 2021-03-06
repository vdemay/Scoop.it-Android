package com.scoopit.android.curation.ui.list.adapater;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ScoopItApp;
import com.scoopit.android.curation.model.Topic;


public class TopicListAdapter extends ArrayAdapter<Topic>{

    private ArrayList<Topic> topics;

    private LayoutInflater li;
    
    public TopicListAdapter(Context context, ArrayList<Topic> objects) {
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
    	Topic t = null;
        if (topics.size() > position) {
        	t = topics.get(position);
        }
        return t;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        //create view if it does not exist
        if (v == null) {
            //assign view
            v = li.inflate(R.layout.topic_list_adapter, null);
        }
        
        //populate view
        Topic t = null;
        if (topics.size() > position) {
        	t = topics.get(position);
        }
        
        TextView userName = (TextView) v.findViewById(R.id.list_adapter_topic_name);
        TextView content = (TextView) v.findViewById(R.id.list_adapter_topic_description);
        ImageView imageView = (ImageView)v.findViewById(R.id.list_adapter_topic_image);
        if (t != null) {
            
            userName.setText(t.getName());
            content.setText(t.getDescription());

            imageView.setVisibility(View.VISIBLE);
            ScoopItApp.INSTANCE.imageLoader.displayImage(t.getMediumImageUrl(), imageView);
        } else {
        	userName.setText("");
        	content.setText("");
        	imageView.setVisibility(View.GONE);
        }
        
        return v;
    }
}