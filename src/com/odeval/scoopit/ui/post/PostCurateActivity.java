package com.odeval.scoopit.ui.post;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.R;
import com.odeval.scoopit.ScoopItApp;
import com.odeval.scoopit.OAuth.OAutHelper;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;

public class PostCurateActivity extends Activity {
    private Post post;

    public class PostImageAdapter implements SpinnerAdapter {
        Post post;
        Context context;

        public PostImageAdapter(Post post, Context context) {
            this.context = context;
            this.post = post;
        }

        public int getCount() {
            return post.getImageUrls().size();
        }

        public Object getItem(int position) {
            return post.getImageUrls().get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = new ImageView(context);
            }
            ScoopItApp.INSTANCE.imageLoader.displayImage((String) getItem(position), (ImageView) convertView);
            return convertView;
        }

        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            // TODO Auto-generated method stub

        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            // TODO Auto-generated method stub

        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        post = getIntent().getExtras().getParcelable("post");

        if (intent != null && post != null) {

            if (Constants.CURATE_ACTION.equals(intent.getAction())) {
                curatePost(post, true, this, 0, false);
                return;
            } else if (Constants.DELETE_ACTION.equals(intent.getAction())) {
                discardPost(post, true, this, false);
                return;
            }
        }

        setContentView(R.layout.post_curate_activity);

        ((TextView) findViewById(R.id.post_list_title)).setText(post.getTitle());
        ((TextView) findViewById(R.id.post_list_content)).setText(post.getContent());

        // if (post.getImageUrls().get(0) != null) {
        // imageLoader.DisplayImage(post.getImageUrls().get(0), (ImageView)findViewById(R.id.post_list_image));
        // }
        ((Button) findViewById(R.id.next_image)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                nextImage();
            }
        });
        ((Button) findViewById(R.id.prev_image)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                prevImage();
            }
        });
        ((Button) findViewById(R.id.curate)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                curatePost(post, true, PostCurateActivity.this, currentImage, true);
            }
        });
        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        PostImageAdapter adapter = new PostImageAdapter(post, this);
        gallery.setAdapter(adapter);
    }

    int currentImage;

    private void nextImage() {
        currentImage = (currentImage + 1) % post.getImageUrls().size();
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrls().get(currentImage), (ImageView) findViewById(R.id.post_image));
    }

    private void prevImage() {
        currentImage--;
        if (currentImage < 0) {
            currentImage = post.getImageUrls().size() - 1;
        }
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrls().get(currentImage), (ImageView) findViewById(R.id.post_image));
    }

    ProgressDialog progress;

    public static void curatePost(final Post post, final boolean finish, final Activity activity, final int imageIndex, final boolean showDialog) {
        curatePost(post, finish, activity, imageIndex, showDialog, null);
    }

    public static void curatePost(
            final Post post, final boolean finish, final Activity activity, final int imageIndex, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(activity, "Please Wait", "Scooping post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    return doCuratePost(post, imageIndex, activity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                    progress.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete();
                }
                activity.setResult(TabPostsListActivity.RESULT_REFRESH_ALL);
                activity.finish();
            }

        }.execute();

    }

    public interface OnActionComplete {
        public void onActionComplete();
    };

    public static void discardPost(final Post post, final boolean finish, final Context context, final boolean showDialog) {
        discardPost(post, finish, context, showDialog, null);
    }

    public static void discardPost(
            final Post post, final boolean finish, final Context context, final boolean showDialog, final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    dialog = ProgressDialog.show(context, "Please Wait", "Discarding post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                doDiscardPost(post, context);
                return post;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete();
                }
            }

        }.execute();
    }

    private static Post doCuratePost(Post post, int imageIndex, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "accept");
        params.put("imageUrl", post.getImageUrls().isEmpty() ? null : post.getImageUrls().get(imageIndex));
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        JSONObject jsonPost = new JSONObject(jsonOutput);
        Post ret = new Post();
        ret.popupateFromJsonObject(jsonPost);
        return ret;
    }

    private static void doDiscardPost(Post post, Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "refuse");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
    }

}
