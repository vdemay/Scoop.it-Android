package com.odeval.scoopit.ui.post;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.OAuth.OAuthFlowApp;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.ui.list.adapater.OnButtonClickedListener;
import com.odeval.scoopit.ui.list.adapater.PostListAdapter;
import com.odeval.scoopit.ui.post.PostCurateActivity.OnActionComplete;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitleProvider;

public class TabPostsListActivity extends Activity implements OnButtonClickedListener {
	
	public static final int RESULT_REFRESH_CURATION_LIST = 2;
	public static final int RESULT_REFRESH_TOPIC_LIST = 3;
	public static final int RESULT_REFRESH_ALL = 4;

    private String topicId;

    private PostListAdapter curablePostListAdapter;
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

		public String getTitle(int position) {
			// TODO Auto-generated method stub
			return titles[position];
		}

    }
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicId = getIntent().getExtras().getString("topicId");

        setContentView(R.layout.tab_posts_list_activity);

        views = new PullToRefreshListView[2];
        views[0] = new PullToRefreshListView(this);
        views[1] = new PullToRefreshListView(this);

        views[0].setCacheColorHint(0);
        views[0].setDivider(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curable_list_divider));
        views[0].setDividerHeight(1);
        views[0].setSelector(R.drawable.curable_list_selector);
        views[0].setBackgroundDrawable(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curable_background));
        
        views[1].setCacheColorHint(0);
        views[1].setDivider(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curated_list_divider));
        views[1].setDividerHeight(1);
        views[1].setSelector(R.drawable.curated_list_selector);
        views[1].setBackgroundDrawable(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curated_background));
        
        loadPostsToCurate();
        loadScoopedPosts();
        
        ((TextView)findViewById(R.id.topic_title)).setText(getIntent().getExtras().getString("topicName"));
        ScoopItApp.INSTANCE.imageLoader.displayImage(getIntent().getExtras().getString("topicImage"), (ImageView)findViewById(R.id.topic_icon));

        MyAdapter awesomeAdapter = new MyAdapter();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(awesomeAdapter);
        PageIndicator pageindicator = (PageIndicator) findViewById(R.id.pageindicator);
        pageindicator.setViewPager(pager);
    }
    
    private void loadScoopedPosts() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
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
                
                lv.setAdapter(new PostListAdapter(R.layout.curated_post_list_adapter, R.id.btn_edit, TabPostsListActivity.this, result.getCuratedPosts(), TabPostsListActivity.this));
                
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                        Post p = (Post) lv.getAdapter().getItem(position);
                        
                        Intent i = new Intent(TabPostsListActivity.this, PostViewActivity.class);
                        i.putExtra("post", p);
                        TabPostsListActivity.this.startActivityForResult(i, 1);
                    }
                });
                lv.setOnRefreshListener(new OnRefreshListener() {					
					public void onRefresh() {
						loadScoopedPosts();
					}
				});
                lv.onRefreshComplete();
            }
        }.execute();
    }
    
    private void loadPostsToCurate() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
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
                curablePostListAdapter = new PostListAdapter(R.layout.curable_post_list_adapter, R.id.btn_discard, TabPostsListActivity.this,
                        result.getCurablePosts(), TabPostsListActivity.this);
                lv.setAdapter(curablePostListAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                	public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                		Post p = (Post) lv.getAdapter().getItem(position);
                        Intent i = new Intent(TabPostsListActivity.this, PostViewBeforeCurateActivity.class);
                        i.putExtra("post", p);
                        TabPostsListActivity.this.startActivityForResult(i, 1);
                	}
                });
                lv.setOnRefreshListener(new OnRefreshListener() {					
					public void onRefresh() {
						loadPostsToCurate();
					}
				});
                lv.onRefreshComplete();
            }

        }.execute();
    }
    
    public void refreshView(int index) {
    	views[index].onRefresh();
    }

    final static int DELETE_POST = 0;
    final static int CURATE_POST = 1;
    
	public void onDiscard(final Post p, int index) {
		PostCurateActivity.discardPost(p, false, this, true, new OnActionComplete() {
			public void onActionComplete() {
				views[0].onRefresh();
			}
		});
	}

	public void onAccept(final Post p, int index) {
		PostCurateActivity.curatePost(p, false, this, 0, true, new OnActionComplete() {
			public void onActionComplete() {
				views[0].onRefresh(); // refresh to remove scooped post from curation list
				views[1].onRefresh(); // refresh to add scooped post to published posts list
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
			case RESULT_REFRESH_CURATION_LIST: 
				views[0].onRefresh(); break;
			case RESULT_REFRESH_TOPIC_LIST: 
				views[1].onRefresh(); break;
			case RESULT_REFRESH_ALL: 
				views[0].onRefresh(); 
				views[1].onRefresh(); 
				break;
		}
	}

	public void onEdit(Post p, int index) {
		Intent i = new Intent(TabPostsListActivity.this, PostCurateActivity.class);
        i.putExtra("post", p);
        TabPostsListActivity.this.startActivity(i);
	}
		
}
