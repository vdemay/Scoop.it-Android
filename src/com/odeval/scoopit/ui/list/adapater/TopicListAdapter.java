package com.odeval.scoopit.ui.list.adapater;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.model.Topic;


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
        return topics.get(position);
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
        Topic t = topics.get(position);
        if (t != null) {
            TextView userName = (TextView) v.findViewById(R.id.list_adapter_topic_name);
            TextView content = (TextView) v.findViewById(R.id.list_adapter_topic_description);
            
            userName.setText(t.getName());
            content.setText(t.getDescription());
            
            ScoopItApp.INSTANCE.imageLoader.displayImage(t.getMediumImageUrl(), (ImageView)v.findViewById(R.id.list_adapter_topic_image));
        }
        
        return v;
    }
}