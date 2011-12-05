package com.odeval.scoopit.ui.post;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.OAuth.OAuthFlowApp;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.ui.list.adapater.CurablePostListAdapter;
import com.odeval.scoopit.ui.list.adapater.CurablePostListAdapter.OnButtonClickedListener;
import com.odeval.scoopit.ui.list.adapater.CuratedPostListAdapter;
import com.odeval.scoopit.ui.task.DownloadImageTask;

public class TabPostsListActivity extends TabActivity implements OnTabChangeListener, OnButtonClickedListener {

    private ProgressDialog progress;
    private String topicId;
    private boolean curatedTopicLoaded;
    private boolean curableTopicLoaded;

    private CurablePostListAdapter curablePostListAdapter;
    
    
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
        
        ((TextView)findViewById(R.id.topic_title)).setText(getIntent().getExtras().getString("topicName"));
        DownloadImageTask task1 = new DownloadImageTask();
        task1.setImageId(R.id.topic_icon);
        task1.setContext(this);
        task1.setRow(getWindow().getDecorView());
        task1.execute(getIntent().getExtras().getString("topicImage"));
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
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    curableTopicLoaded = true;
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURATED_POST_REQUEST + "?id="
                            + topicId,
                            OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
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
                // populate
                final PullToRefreshListView lv = (PullToRefreshListView) findViewById(R.id.tab_post_curated_list);
                
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
                lv.setOnRefreshListener(new OnRefreshListener() {					
					@Override
					public void onRefresh() {
						loadCurablePosts();
					}
				});
                lv.onRefreshComplete();
            }
        }.execute();
    }
    
    private void loadCuratedPosts() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    curatedTopicLoaded = true;
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURABLE_POST_REQUEST + "&id="
                            + topicId,
                            OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
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
                // populate
                final PullToRefreshListView lv = (PullToRefreshListView) findViewById(R.id.tab_post_curable_list);
                curablePostListAdapter = new CurablePostListAdapter(TabPostsListActivity.this,
                        result.getCurablePosts(), TabPostsListActivity.this);
                lv.setAdapter(curablePostListAdapter);
                lv.setOnItemClickListener(new OnItemClickListener() {
                	public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                		Post p = (Post) lv.getAdapter().getItem(position);
                        
                        Intent i = new Intent(TabPostsListActivity.this, PostCurateActivity.class);
                        i.putExtra("post", p);
                        TabPostsListActivity.this.startActivity(i);
                		
                	}
                });
                lv.setOnRefreshListener(new OnRefreshListener() {					
					@Override
					public void onRefresh() {
						loadCuratedPosts();
					}
				});
                lv.onRefreshComplete();
            }

        }.execute();
    }

	@Override
	public void onDelete(Post p, int index) {
		deletePost(p);
	}

	@Override
	public void onAccept(Post p, int index) {
		curatePost(p);
	}

	@Override
	public void onEdit(Post p, int index) {
		Intent i = new Intent(TabPostsListActivity.this, PostCurateActivity.class);
        i.putExtra("post", p);
        TabPostsListActivity.this.startActivity(i);
	}
	
    
    private void curatePost(final Post post) {
        new AsyncTask<Void, Void, Post>() {

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Curating post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
            	try {
            		return doCuratePost(post);
            	} catch (JSONException e) {
					e.printStackTrace();
				}
            	return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                progress.dismiss();
                curablePostListAdapter.remove(result);
                curablePostListAdapter.notifyDataSetChanged();
            }

        }.execute();

    }

    private void deletePost(final Post post) {
        new AsyncTask<Void, Void, Post>() {

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Deleting post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
            	doDeletePost(post);
            	return post;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                curablePostListAdapter.remove(result);
                curablePostListAdapter.notifyDataSetChanged();
                progress.dismiss();                
            }

        }.execute();

    	
    }

    private Post doCuratePost(Post post) throws JSONException {
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("id", post.getId().toString());
    	params.put("action", "accept");
    	params.put("imageUrl", post.getImageUrls().get(0));
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(this)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        JSONObject jsonPost = new JSONObject(jsonOutput);
        Post ret = new Post();
        ret.popupateFromJsonObject(jsonPost);
        return ret;
    }
    
    private void doDeletePost(Post post) {
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("id", post.getId().toString());
    	params.put("action", "delete");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAuthFlowApp.getConsumer(PreferenceManager.getDefaultSharedPreferences(this)), params);
        System.out.println("jsonOutput : " + jsonOutput);    	
    }

	
}
