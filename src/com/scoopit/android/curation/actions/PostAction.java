package com.scoopit.android.curation.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.scoopit.android.curation.Constants;
import com.scoopit.android.curation.OAuth.OAutHelper;
import com.scoopit.android.curation.helper.NetworkingUtils;
import com.scoopit.android.curation.model.Post;
import com.scoopit.android.curation.model.Sharer;

public class PostAction {

    public interface OnActionComplete {
        public void onActionComplete(Post in, Post out);
    };
    
    // ------------------------------------------ PREPARE
    
    public static void preparePost(final String url, final Context context, final boolean showDialog) {
        preparePost(url, context, showDialog, null);
    }

    public static void preparePost(
            final String url, final Context context, final boolean showDialog, final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    dialog = ProgressDialog.show(context, "Please Wait", "Preparing post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    Post p = doPreparePost(url, context);
                    return p;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(null, result);
                }
            }

        }.execute();
    }

    private static Post doPreparePost(String url, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("url", url);
        params.put("action", "prepare");
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
                try {
                    doDeletePost(post, context);
                } catch (JSONException e) {
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    dialog.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();
    }

    private static void doDeletePost(Post post, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "delete");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        if (jsonOutput == null) {
            throw new JSONException("json is null");
        }
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
                try {
                    doDiscardPost(post, context);
                } catch (JSONException e) {
                    cancel(true);
                }
                return null;
            }
            
            @Override
            protected void onCancelled() {
                if (showDialog)
                    dialog.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
                super.onPostExecute(result);
                if (showDialog)
                    dialog.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(post, result);
                }
            }

        }.execute();
    }

    private static void doDiscardPost(Post post, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", post.getId().toString());
        params.put("action", "refuse");
        String jsonOutput = NetworkingUtils.sendRestfullPostRequest(Constants.POST_ACTION_REQUEST,
                OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(context)), params);
        System.out.println("jsonOutput : " + jsonOutput);
        if (jsonOutput == null) {
            throw new JSONException("output is null");
        }
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
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    progress.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
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
    
    
    // ------------------------------------- CREATE
    public static void createPost(final Post post, final String topicId,  final String shareOn, final Context context, final boolean showDialog) {
        createPost(post, topicId, shareOn, context, showDialog, null);
    }

    public static void createPost(
            final Post post, final String topicId, final String shareOn, final Context context, final boolean showDialog,
            final OnActionComplete onActionComplete) {
        new AsyncTask<Void, Void, Post>() {
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                if (showDialog)
                    progress = ProgressDialog.show(context, "Please Wait", "Creating post...", true);
                super.onPreExecute();
            }

            @Override
            protected Post doInBackground(Void... params) {
                try {
                    return doCreatePost(post, topicId, shareOn, context);
                } catch (JSONException e) {
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    progress.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
                super.onPostExecute(result);
                if (showDialog)
                    progress.dismiss();
                if (onActionComplete != null) {
                    onActionComplete.onActionComplete(null, result);
                }
            }

        }.execute();

    }

    private static Post doCreatePost(Post post,String topicId, final String shareOn, Context context) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("action", "create");
        
        params.put("imageUrl", post.getImageUrl());
        params.put("title", post.getTitle());
        params.put("content", post.getContent());
        params.put("topicId", topicId);
        if (post.getUrl() != null) {
        	params.put("url", post.getUrl());
        }
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
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    progress.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
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
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    progress.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
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
                    cancel(true);
                }
                return null;
            }
            
            protected void onCancelled() {
                if (showDialog)
                    progress.dismiss();
                
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       alertDialog.cancel();
                   }
                });
                alertDialog.show();
            };

            @Override
            protected void onPostExecute(Post result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
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
        if (jsonOutput == null) {
            throw new JSONException("json is null");
        }

    }
}
