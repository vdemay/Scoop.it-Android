package com.odeval.scoopit.ui.topic;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.OAuth.OAutHelper;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.model.User;
import com.odeval.scoopit.ui.list.adapater.TopicListAdapter;
import com.odeval.scoopit.ui.post.TabPostsListActivity;
import com.odeval.scoopit.ui.profile.ProfileActivity;

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
                progress = ProgressDialog.show(CuratedTopicListActivity.this, "Please Wait", "Loading your topics...",
                        true);
                String cache = ScoopItApp.INSTANCE.cache.getCachedForUrl(Constants.PROFILE_REQUEST);
                if (cache != null) {
                    try {
                        onPostExecute(User.getUserFromJson(cache));
                    } catch (JSONException e) {} //ok fail : it is just cache
                }

                super.onPreExecute();
            }

            @Override
            protected User doInBackground(Void... params) {

                try {
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(
                            Constants.PROFILE_REQUEST,
                            OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(CuratedTopicListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    ScoopItApp.INSTANCE.cache.setCacheForUrl(jsonOutput, Constants.PROFILE_REQUEST);
                    return User.getUserFromJson(jsonOutput);
                } catch (JSONException e) {
                    // TODO
                }
                return null;
            }
            

            @Override
            protected void onPostExecute(User result) {
                super.onPostExecute(result);
                progress.hide();
                // populate
                if (result != null) {
                    //store user
                    result.witeToFile(CuratedTopicListActivity.this);
                    //update list
                    if (CuratedTopicListActivity.this.getListAdapter() == null) {
                        CuratedTopicListActivity.this.setListAdapter(new TopicListAdapter(CuratedTopicListActivity.this,
                            result.getCuratedTopics()));
                    } else {
                        TopicListAdapter tla = (TopicListAdapter)CuratedTopicListActivity.this.getListAdapter();
                        tla.updateTopics(result.getCuratedTopics());
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(CuratedTopicListActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Sorry, can not get data from server");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           alertDialog.cancel();
                       }
                    });
                    alertDialog.show();
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
        


        ((Button)findViewById(com.odeval.scoopit.R.id.topic_list_profile)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CuratedTopicListActivity.this, ProfileActivity.class);
                CuratedTopicListActivity.this.startActivity(intent);
            }
        });
        
    }

}
