package com.odeval.scoopit.ui.post;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitleProvider;

public class TabPostsListActivity extends Activity implements OnButtonClickedListener {

    private ProgressDialog progress;
    private String topicId;
    private boolean curatedTopicLoaded;
    private boolean curableTopicLoaded;

    private CurablePostListAdapter curablePostListAdapter;
    private PullToRefreshListView[] views;
    private static final String[] titles = new String[] { "CURATE", "VIEW TOPIC"};
    
    public class MyAdapter extends PagerAdapter implements TitleProvider {

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			
			((ViewPager) collection).addView(views[position],0);
			return views[position];
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			 return view==((PullToRefreshListView)object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public String getTitle(int position) {
			// TODO Auto-generated method stub
			return titles[position];
		}

    }
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicId = getIntent().getExtras().getString("topicId");

        setContentView(R.layout.tab_posts_list_activity);

//        TabHost mTabHost = getTabHost();
//        mTabHost.setOnTabChangedListener(this);
//        mTabHost.addTab(mTabHost.newTabSpec("curatedPost").setIndicator("Curate").setContent(R.id.tab_post_curable_list));
//        mTabHost.addTab(mTabHost.newTabSpec("curablePost").setIndicator("View Topic").setContent(
//                R.id.tab_post_curated_list));
//
//        mTabHost.setCurrentTab(0);
        views = new PullToRefreshListView[2];
        views[0] = new PullToRefreshListView(this);
        views[1] = new PullToRefreshListView(this);
        
        loadCurablePosts();
        loadCuratedPosts();
        ((TextView)findViewById(R.id.topic_title)).setText(getIntent().getExtras().getString("topicName"));
        DownloadImageTask task1 = new DownloadImageTask();
        task1.setImageId(R.id.topic_icon);
        task1.setContext(this);
        task1.setRow(getWindow().getDecorView());
        task1.execute(getIntent().getExtras().getString("topicImage"));

        MyAdapter awesomeAdapter = new MyAdapter();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(awesomeAdapter);
        
        PageIndicator pageindicator = (PageIndicator) findViewById(R.id.pageindicator);
        pageindicator.setViewPager(pager);
    }
    
//    @Override
//    public void onTabChanged(String tabName) {
//        if(tabName.equals("curatedPost") && !curatedTopicLoaded) {
//            loadCuratedPosts();
//        } else if(tabName.equals("curablePost") && !curableTopicLoaded) {
//            loadCurablePosts();
//        }
//    }

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
                final PullToRefreshListView lv = views[1];
                
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
                final PullToRefreshListView lv = views[0];
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

    final static int DELETE_POST = 0;
    final static int CURATE_POST = 1;
    
	@Override
	public void onDelete(Post p, int index) {
		PostCurateActivity.deletePost(p, false, this, false);
	}

	@Override
	public void onAccept(Post p, int index) {
		PostCurateActivity.curatePost(p, false, this, 0, false);
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	public void onEdit(Post p, int index) {
		Intent i = new Intent(TabPostsListActivity.this, PostCurateActivity.class);
        i.putExtra("post", p);
        TabPostsListActivity.this.startActivity(i);
	}
		
}
