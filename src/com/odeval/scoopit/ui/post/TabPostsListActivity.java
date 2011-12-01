package com.odeval.scoopit.ui.post;

import org.json.JSONException;
import org.json.JSONObject;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.ui.list.adapater.CurablePostListAdapter;
import com.odeval.scoopit.ui.list.adapater.CuratedPostListAdapter;
import com.odeval.scoopit.ui.topic.CuratedTopicListActivity;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class TabPostsListActivity extends TabActivity implements OnTabChangeListener {

    private ProgressDialog progress;
    private String topicId;
    private boolean curatedTopicLoaded;
    private boolean curableTopicLoaded;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicId = getIntent().getExtras().getString("topicId");

        setContentView(R.layout.tab_posts_list_activity);

        TabHost mTabHost = getTabHost();
        mTabHost.setOnTabChangedListener(this);
        mTabHost.addTab(mTabHost.newTabSpec("curatedPost").setIndicator("Curate").setContent(R.id.tab_post_curable_list));
        mTabHost.addTab(mTabHost.newTabSpec("curablePost").setIndicator("View Topic").setContent(
                R.id.tab_post_curated_list));

        mTabHost.setCurrentTab(0);
    }
    
    @Override
    public void onTabChanged(String tabName) {
        if(tabName.equals("curatedPost") && !curatedTopicLoaded) {
            loadCuratedPosts();
        } else if(tabName.equals("curablePost") && !curableTopicLoaded) {
            loadCurablePosts();
        }
    }

    private void loadCurablePosts() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Loading curated posts...",
                        true);
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    curableTopicLoaded = true;
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURATED_POST_REQUEST + "?id="
                            + topicId,
                            getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    JSONObject jsonResponse;
                    jsonResponse = new JSONObject(jsonOutput);

                    Topic topic = new Topic();
                    topic.popupateFromJsonObject(jsonResponse.getJSONObject("topic"));
                    return topic;
                } catch (JSONException e) {
                    // TODO
                }
                return null;
            }

            @Override
            protected void onPostExecute(Topic result) {
                super.onPostExecute(result);
                progress.hide();
                // populate
                final ListView lv = (ListView) findViewById(R.id.tab_post_curated_list);
                lv.setAdapter(new CuratedPostListAdapter(TabPostsListActivity.this,
                        result.getCuratedPosts()));
                
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                        Post p = (Post) lv.getAdapter().getItem(position);
                        
                        Intent i = new Intent(TabPostsListActivity.this, PostViewActivity.class);
                        i.putExtra("post", p);
                        TabPostsListActivity.this.startActivity(i);
                    }
                });
            }

        }.execute();
    }
    
    private void loadCuratedPosts() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Loading curable posts...",
                        true);
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    curatedTopicLoaded = true;
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURABLE_POST_REQUEST + "&id="
                            + topicId,
                            getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    JSONObject jsonResponse;
                    jsonResponse = new JSONObject(jsonOutput);

                    Topic topic = new Topic();
                    topic.popupateFromJsonObject(jsonResponse.getJSONObject("topic"));
                    return topic;
                } catch (JSONException e) {
                    System.out.println(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Topic result) {
                super.onPostExecute(result);
                progress.hide();
                // populate
                ListView lv = (ListView) findViewById(R.id.tab_post_curable_list);
                lv.setAdapter(new CurablePostListAdapter(TabPostsListActivity.this,
                        result.getCurablePosts()));
            }

        }.execute();
    }

    private OAuthConsumer getConsumer(SharedPreferences prefs) {
        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, secret);
        return consumer;
    }
}
