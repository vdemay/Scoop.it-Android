package com.odeval.scoopit.ui.topic;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.OAuth.OAuthFlowApp;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.model.User;
import com.odeval.scoopit.ui.list.adapater.TopicListAdapter;
import com.odeval.scoopit.ui.post.TabPostsListActivity;

public class CuratedTopicListActivity extends ListActivity {
    

    private ProgressDialog progress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(com.odeval.scoopit.R.layout.topic_list_activity);
        
        //start a task
        new AsyncTask<Void, Void, User>() {
            
            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(
                        CuratedTopicListActivity.this, 
                        "Please Wait", 
                        "Loading your topics...", 
                        true);
                super.onPreExecute();
            }

            @Override
            protected User doInBackground(Void... params) {

                try {
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.PROFILE_REQUEST, OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(CuratedTopicListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    JSONObject jsonResponse;
                    jsonResponse = new JSONObject(jsonOutput);
                   
                    User user = new User();
                    user.popupateFromJsonObject(jsonResponse.getJSONObject("user"));
                    return user;
                } catch (JSONException e) {
                    //TODO
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(User result) {
                super.onPostExecute(result);
                progress.hide();
                //populate 
                if (result != null) {
                    CuratedTopicListActivity.this.setListAdapter(new TopicListAdapter(CuratedTopicListActivity.this, result.getCuratedTopics()));
                } else {
                   Dialog d = new Dialog(CuratedTopicListActivity.this);
                   d.setTitle("An Error Occured");
                   d.show();
                }
            }
            
        }.execute();
        
        
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                Topic t = (Topic)getListAdapter().getItem(position);
                
                Intent i = new Intent(CuratedTopicListActivity.this, TabPostsListActivity.class);
                i.putExtra("topicId", "" + t.getId());
                i.putExtra("topicName", t.getName());
                i.putExtra("topicImage", t.getMediumImageUrl());
                CuratedTopicListActivity.this.startActivity(i);
            }
        });
    
    }
    
}
