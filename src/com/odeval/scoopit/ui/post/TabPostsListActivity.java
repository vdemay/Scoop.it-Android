package com.odeval.scoopit.ui.post;

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
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.OAuth.OAutHelper;
import com.odeval.scoopit.actions.PostAction;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Topic;
import com.odeval.scoopit.ui.list.adapater.OnButtonClickedListener;
import com.odeval.scoopit.ui.list.adapater.PostListAdapter;
import com.odeval.scoopit.ui.topic.CuratedTopicListActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * This Activity can start other activity for result ResultCode can be
 * <ul>
 * <li> {@link TabPostsListActivity#RESULT_REFRESH_CURATED} : to refresh curated list. no data needed</li>
 * <li> {@link TabPostsListActivity#RESULT_REFRESH_CURABLE} : to refresh curable list. no data needed</li>
 * <li> {@link TabPostsListActivity#RESULT_REFRESH_ALL} : to refresh curated and curable list. no data needed</li>
 * <li> {@link TabPostsListActivity#RESULT_DELETE_CURABLE} : to delete a curable list. data need to contain a post</li>
 * <li> {@link TabPostsListActivity#RESULT_DELETE_CURATED} : to delete a curated list. data need to contain a post</li>
 * <li> {@link TabPostsListActivity#RESULT_ADD_CURATED_AND_REMOVE_CURABLE} : to delete a post from curable list and add a post in curated list. data
 * need to contain a postToRemove and postToAdd</li>
 * <li> {@link TabPostsListActivity#RESULT_REPLACE_CURATED} : to change a post from curated at the same pos. data need to contain a postToRemove and
 * postToAdd</li>
 * </ul>
 * 
 * @author vincentdemay
 * 
 */
public class TabPostsListActivity extends Activity implements OnButtonClickedListener {

    private ProgressDialog progress;
    private boolean progresshasBeenShown;
    
    /**
     * This class is also in charge of managing bothe curated and curable list of topic Result of other activity are handled here
     */
    public static final int RESULT_REFRESH_CURABLE = 2;
    public static final int RESULT_REFRESH_CURATED = 3;
    public static final int RESULT_REFRESH_ALL = 4;
    public static final int RESULT_DELETE_CURABLE = 5;
    public static final int RESULT_DELETE_CURATED = 6;
    public static final int RESULT_ADD_CURATED_AND_REMOVE_CURABLE = 7;
    public static final int RESULT_REPLACE_CURATED = 8;

    public static final int CURABLE_LIST_INDEX = 0;
    public static final int CURATED_LIST_INDEX = 1;

    private String topicId;

    private PostListAdapter curablePostListAdapter;
    private PostListAdapter curatedPostListAdapter;
    
    private PullToRefreshListView[] views;
    private static final String[] titles = new String[] {"CURATE", "VIEW TOPIC"};

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

            ((ViewPager) collection).addView(views[position], 0);
            return views[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((PullToRefreshListView) object);
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
        views[CURABLE_LIST_INDEX] = new PullToRefreshListView(this);
        views[CURATED_LIST_INDEX] = new PullToRefreshListView(this);

        views[CURABLE_LIST_INDEX].setCacheColorHint(0);
        views[CURABLE_LIST_INDEX].setDivider(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curable_list_divider));
        views[CURABLE_LIST_INDEX].setDividerHeight(1);
        views[CURABLE_LIST_INDEX].setSelector(R.drawable.curable_list_selector);
        views[CURABLE_LIST_INDEX].setBackgroundDrawable(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curable_background));

        views[CURATED_LIST_INDEX].setCacheColorHint(0);
        views[CURATED_LIST_INDEX].setDivider(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curated_list_divider));
        views[CURATED_LIST_INDEX].setDividerHeight(1);
        views[CURATED_LIST_INDEX].setSelector(R.drawable.curated_list_selector);
        views[CURATED_LIST_INDEX].setBackgroundDrawable(ScoopItApp.INSTANCE.getResources().getDrawable(R.color.curated_background));

        loadPostsToCurate();
        loadScoopedPosts();

        ((TextView) findViewById(R.id.topic_title)).setText(getIntent().getExtras().getString("topicName"));
        ScoopItApp.INSTANCE.imageLoader.displayImage(getIntent().getExtras().getString("topicImage"), (ImageView) findViewById(R.id.topic_icon));

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
                String cache = ScoopItApp.INSTANCE.cache.getCachedForUrl(Constants.CURATED_POST_REQUEST + "?id=" + topicId);
                if (cache != null) {
                    try {
                        Topic result = Topic.getTopicFromJson(cache);
                        final PullToRefreshListView lv = views[CURATED_LIST_INDEX];
                        if (curatedPostListAdapter == null) {
                            curatedPostListAdapter = new PostListAdapter(R.layout.curated_post_list_adapter, R.id.btn_edit, TabPostsListActivity.this,
                                    result.getCuratedPosts(), TabPostsListActivity.this);
                            lv.setAdapter(curatedPostListAdapter);
                        } else {
                            curatedPostListAdapter.updatePostList(result.getCuratedPosts());
                        }
                        progresshasBeenShown = true;
                    } catch (JSONException e) {}
                }
                
                if (!progresshasBeenShown) {
                    progresshasBeenShown = true;
                    progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Loading your posts...",
                            true);
                }
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURATED_POST_REQUEST + "?id=" + topicId,
                            OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    
                    ScoopItApp.INSTANCE.cache.setCacheForUrl(jsonOutput, Constants.CURATED_POST_REQUEST + "?id=" + topicId);
                    
                    return Topic.getTopicFromJson(jsonOutput);
                    
                } catch (JSONException e) {
                    // TODO
                }
                return null;
            }
        
            @Override
            protected void onPostExecute(Topic result) {
                super.onPostExecute(result);
                // populate
                final PullToRefreshListView lv = views[CURATED_LIST_INDEX];
                
                if (curatedPostListAdapter == null) {
                    curatedPostListAdapter = new PostListAdapter(R.layout.curated_post_list_adapter, R.id.btn_edit, TabPostsListActivity.this,
                            result.getCuratedPosts(), TabPostsListActivity.this);
                    lv.setAdapter(curatedPostListAdapter);
                } else {
                    curatedPostListAdapter.updatePostList(result.getCuratedPosts());
                }
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
                
                if (progress != null) {
                    progress.hide();
                }
            }
        }.execute();
    }

    private void loadPostsToCurate() {
        new AsyncTask<Void, Void, Topic>() {

            @Override
            protected void onPreExecute() {
                //read from cache
                String cache = ScoopItApp.INSTANCE.cache.getCachedForUrl(Constants.CURABLE_POST_REQUEST + "&id=" + topicId);
                if (cache != null) {
                    try {
                        Topic result = Topic.getTopicFromJson(cache);
                        final PullToRefreshListView lv = views[CURABLE_LIST_INDEX];
                        if (curablePostListAdapter == null) {
                            curablePostListAdapter = new PostListAdapter(R.layout.curable_post_list_adapter, R.id.btn_discard, TabPostsListActivity.this,
                                    result.getCurablePosts(), TabPostsListActivity.this);
                            lv.setAdapter(curablePostListAdapter);
                        } else {
                            curablePostListAdapter.updatePostList(result.getCurablePosts());
                        }
                        progresshasBeenShown = true;
                    } catch (JSONException e) {}
                }
                
                if (!progresshasBeenShown) {
                    progresshasBeenShown = true;
                    progress = ProgressDialog.show(TabPostsListActivity.this, "Please Wait", "Loading your posts...",
                            true);
                }
                super.onPreExecute();
            }

            @Override
            protected Topic doInBackground(Void... params) {

                try {
                    String jsonOutput = NetworkingUtils.sendRestfullRequest(Constants.CURABLE_POST_REQUEST + "&id=" + topicId,
                            OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(TabPostsListActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);

                    ScoopItApp.INSTANCE.cache.setCacheForUrl(jsonOutput, Constants.CURABLE_POST_REQUEST + "&id=" + topicId);
                    
                    return Topic.getTopicFromJson(jsonOutput);
                } catch (JSONException e) {
                    System.out.println(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Topic result) {
                super.onPostExecute(result);
                // populate
                final PullToRefreshListView lv = views[CURABLE_LIST_INDEX];
                if (curablePostListAdapter == null) {
                    curablePostListAdapter = new PostListAdapter(R.layout.curable_post_list_adapter, R.id.btn_discard, TabPostsListActivity.this,
                            result.getCurablePosts(), TabPostsListActivity.this);
                    lv.setAdapter(curablePostListAdapter);
                } else {
                    curablePostListAdapter.updatePostList(result.getCurablePosts());
                }
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

                if (progress != null) {
                    progress.hide();
                }
            }

        }.execute();
    }

    public void refreshView(int index) {
        views[index].onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null) {
            Post postToRemove = (Post) data.getExtras().getParcelable("postToRemove");
            Post postToAdd = (Post) data.getExtras().getParcelable("postToAdd");
            Post post = (Post) data.getExtras().getParcelable("post");

            PostListAdapter curablePla = (PostListAdapter) ((HeaderViewListAdapter) views[CURABLE_LIST_INDEX].getAdapter()).getWrappedAdapter();
            PostListAdapter curatedPla = (PostListAdapter) ((HeaderViewListAdapter) views[CURATED_LIST_INDEX].getAdapter()).getWrappedAdapter();

            switch (resultCode) {
                case RESULT_REFRESH_CURABLE:
                    views[CURABLE_LIST_INDEX].onRefresh();
                    break;
                case RESULT_REFRESH_CURATED:
                    views[CURATED_LIST_INDEX].onRefresh();
                    break;
                case RESULT_REFRESH_ALL:
                    views[CURABLE_LIST_INDEX].onRefresh();
                    views[CURATED_LIST_INDEX].onRefresh();
                    break;
                case RESULT_DELETE_CURABLE:
                    if (post != null) {
                        curablePla.remove(post);
                        views[CURABLE_LIST_INDEX].invalidateViews();
                    }
                    break;
                case RESULT_DELETE_CURATED:
                    if (post != null) {
                        curatedPla.remove(post);
                        curatedPla.notifyDataSetChanged();
                    }
                    break;
                case RESULT_ADD_CURATED_AND_REMOVE_CURABLE:
                    if (postToAdd != null && postToRemove != null) {
                        curablePla.remove(postToRemove);
                        curablePla.notifyDataSetChanged();
                        curatedPla.add(postToAdd);
                        curatedPla.notifyDataSetChanged();
                    }
                    break;
                case RESULT_REPLACE_CURATED:
                    if (postToAdd != null && postToRemove != null) {
                        curatedPla.replace(postToRemove, postToAdd);
                        curatedPla.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
    
    //Buttons on the list

    public void onEdit(Post p, int index) {
        Intent i = new Intent(TabPostsListActivity.this, PostEditActivity.class);
        i.putExtra("post", p);
        TabPostsListActivity.this.startActivity(i);
    }

    public void onDiscard(final Post p, int index) {
        PostAction.discardPost(p, this, true, new PostAction.OnActionComplete() {
            public void onActionComplete(Post in, Post out) {
                PostListAdapter curablePla = (PostListAdapter) ((HeaderViewListAdapter) views[CURABLE_LIST_INDEX].getAdapter()).getWrappedAdapter();
                ;
                curablePla.remove(in);
                views[CURABLE_LIST_INDEX].invalidateViews();
            }
        });
    }

}
