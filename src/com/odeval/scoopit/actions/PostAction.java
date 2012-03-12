package com.odeval.scoopit.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.odeval.scoopit.Constants;
import com.odeval.scoopit.OAuth.OAutHelper;
import com.odeval.scoopit.helper.NetworkingUtils;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Sharer;

public class PostAction {

    public interface OnActionComplete {
        public void onActionComplete(Post in, Post out);
    };

    // ------------------------------------------ DELETE

    public static void deletePost(final Post post, final Context context, final boolean showDialog) {
        deletePost(post, context, showDialog, null);
    }

    public static void deletePost(
            final Post post, final Context context, final boolean showDialog, final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    dialog = ProgressDialog.show(context, "Please Wait", "Deleting post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                doDeletePost(post, context);
                return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();
    }

    private static void doDeletePost(Post post, Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "delete");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
    }

    
    // -------------------------------------------------- DISCARD
    public static void discardPost(final Post post, final Context context, final boolean showDialog) {
        discardPost(post, context, showDialog, null);
    }

    public static void discardPost(
            final Post post, final Context context, final boolean showDialog, final OnActionComplete onActionComplete) {
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
                return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();
    }

    private static void doDiscardPost(Post post, Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "refuse");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
    }

    // ------------------------------------- ACCEPT
    public static void curatePost(final Post post, final String shareOn, final Context context, final boolean showDialog) {
        curatePost(post, shareOn, context, showDialog, null);
    }

    public static void curatePost(
            final Post post, final String shareOn, final Context context, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(context, "Please Wait", "Scooping post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    return doCuratePost(post, shareOn, context);
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
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();

    }

    private static Post doCuratePost(Post post, final String shareOn, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("action", "accept");
        
        params.put("id", post.getId().toString());
        params.put("imageUrl", post.getImageUrl());
        params.put("title", post.getTitle());
        params.put("content", post.getContent());
        if (shareOn != null) {
            params.put("shareOn", shareOn);
        }
        
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        JSONObject jsonPost = new JSONObject(jsonOutput);

        Post ret = null;
        if (jsonPost.has("post")) {
            ret = new Post();
            ret.popupateFromJsonObject(jsonPost.getJSONObject("post"));
        }
        return ret;
    }
    
    
    // ------------------------------------- EDIT
    public static void editPost(final Post post, final Context context, final boolean showDialog) {
        editPost(post, context, showDialog, null);
    }

    public static void editPost(
            final Post post, final Context context, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(context, "Please Wait", "Saving post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    return doEditPost(post, context);
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
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();

    }

    private static Post doEditPost(Post post, Context context) throws JSONException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        params.add(new BasicNameValuePair("action", "edit"));
        
        params.add(new BasicNameValuePair("id", post.getId().toString()));
        params.add(new BasicNameValuePair("imageUrl", post.getImageUrl()));
        params.add(new BasicNameValuePair("title", post.getTitle()));
        params.add(new BasicNameValuePair("content", post.getContent()));
        
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        JSONObject jsonPost = new JSONObject(jsonOutput);

        Post ret = null;
        if (jsonPost.has("post")) {
            ret = new Post();
            ret.popupateFromJsonObject(jsonPost.getJSONObject("post"));
        }
        return ret;
    }
    
    // ------------------------------------- SET TAGS 
    public static void setTag(final Post post, ArrayList<String> tags, final Context context, final boolean showDialog) {
        editPost(post, tags, context, showDialog, null);
    }

    public static void editPost(
            final Post post, final ArrayList<String> tags, final Context context, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(context, "Please Wait", "Saving tags...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    return doSetTags(post, tags, context);
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
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();

    }

    private static Post doSetTags(Post post, ArrayList<String> tags, Context context) throws JSONException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        params.add(new BasicNameValuePair("action", "edit"));
        params.add(new BasicNameValuePair("imageUrl", post.getImageUrl()));

        params.add(new BasicNameValuePair("id", post.getId().toString()));
        
        if (tags == null || tags.isEmpty()) {
            params.add(new BasicNameValuePair("tag", ""));
        } else {
            for (String tag : tags) {
                params.add(new BasicNameValuePair("tag", tag));
            }
        }
        
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        JSONObject jsonPost = new JSONObject(jsonOutput);

        Post ret = null;
        if (jsonPost.has("post")) {
            ret = new Post();
            ret.popupateFromJsonObject(jsonPost.getJSONObject("post"));
        }
        return ret;
    }
    
    // ------------------------------------- SHARE
    public static void sharePost(final Post post, Sharer sharer, String text, final Context context, final boolean showDialog) {
        editPost(post, sharer, text, context, showDialog, null);
    }

    public static void editPost(
            final Post post, final Sharer sharer, final String text, final Context context, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(context, "Please Wait", "Sharing post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    doSharePost(post, sharer, text, context);
                    return null;
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
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();

    }

    private static void doSharePost(Post post, Sharer sharer, String text, Context context) throws JSONException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        params.add(new BasicNameValuePair("action", "share"));
        params.add(new BasicNameValuePair("id", post.getId().toString()));
        
        params.add(new BasicNameValuePair("shareOn", "[" + sharer.generateJsonFragment(text) + "]"));
        
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        //JSONObject jsonPost = new JSONObject(jsonOutput);

    }
}
