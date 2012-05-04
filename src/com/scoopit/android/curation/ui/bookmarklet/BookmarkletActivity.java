package com.scoopit.android.curation.ui.bookmarklet;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.scoopit.android.curation.Constants;
import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ScoopItApp;
import com.scoopit.android.curation.OAuth.LoginActivity;
import com.scoopit.android.curation.OAuth.OAutHelper;
import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.helper.NetworkingUtils;
import com.scoopit.android.curation.model.Post;
import com.scoopit.android.curation.model.Topic;
import com.scoopit.android.curation.model.User;
import com.scoopit.android.curation.ui.list.adapater.SpinnerTopicListAdapter;
import com.scoopit.android.curation.ui.post.PostCurateActivity;

public class BookmarkletActivity extends PostCurateActivity {
	private EditText urlEditText;

	ProgressDialog dialog;
	Spinner selectTopic;
	
	String url;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//com.scoopit.android.curation.R.id.curate_select_topic_label)
		findViewById(com.scoopit.android.curation.R.id.curate_select_topic_label).setVisibility(View.VISIBLE);
		selectTopic = (Spinner)findViewById(com.scoopit.android.curation.R.id.curate_select_topic);
		urlEditText = (EditText) findViewById(R.id.curate_url);
		
		selectTopic.setVisibility(View.VISIBLE);
		

		pageTitle.setText("Bookmark this page");
				
		//get the url in intent to prepar a post
		if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
			url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
			run();
		} 
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String urlBack = data.getStringExtra("url");
		if (urlBack != null) {
			url = urlBack;
			run();
		}
	}
	
	public void run() {
		if (url != null) {
			if (OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(BookmarkletActivity.this)) != null) {
				//lauch get Process
				launchProcess();
				//set Click Listener
				action.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// start to update the post
						post.setTitle(titleEditText.getText().toString());
						post.setContent(descriptionEditText.getText().toString());
						post.setImageUrl((String) gallery.getSelectedItem());
						
						//get topicLid
						Long id = ((Topic)selectTopic.getSelectedItem()).getId();
		
						PostAction.createPost(post, id.toString(), jsonForSelectedSharers(),
							BookmarkletActivity.this, true,
							new PostAction.OnActionComplete() {
	
								@Override
								public void onActionComplete(Post in, Post out) {
									final AlertDialog alertDialog = new AlertDialog.Builder(BookmarkletActivity.this).create();
					                alertDialog.setTitle("Congrats");
					                alertDialog.setMessage("Post has been scooped");
					                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					                   public void onClick(DialogInterface dialog, int which) {
					                	   //quit
					                	   BookmarkletActivity.this.finish();
					                   }
					                });
					                alertDialog.show();
								}
							});
					}
				});
			} else {
				//User is not yet logged in
				final AlertDialog alertDialog = new AlertDialog.Builder(BookmarkletActivity.this).create();
	            alertDialog.setTitle("Sorry");
	            alertDialog.setMessage("Your are not yet connected to Scoop.it");
	            alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   //quit
	            	   BookmarkletActivity.this.finish();
	               }
	            });
	            alertDialog.setButton("Connect", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   //quit
	            	   Intent i = new Intent(BookmarkletActivity.this, LoginActivity.class);
	            	   i.putExtra("url", url);
	            	   startActivityForResult(i, 1);
	               }
		         });
	            alertDialog.show();
			}
		} else {
			//Should never appear
			final AlertDialog alertDialog = new AlertDialog.Builder(BookmarkletActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Sorry, can not get data from this page");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
            	   //quit
            	   BookmarkletActivity.this.finish();
               }
            });
            alertDialog.show();
		}
	}
	
	
	private void launchProcess() {
		new AsyncTask<Void, Void, ArrayList<Topic>>() {
			
	        @Override
	        protected void onPreExecute() {
	            dialog = ProgressDialog.show(BookmarkletActivity.this, "Please Wait", "Grabbing data...", true);
	            String cache = ScoopItApp.INSTANCE.cache.getCachedForUrl(Constants.PROFILE_REQUEST);
                if (cache != null) {
                    try {
                        onPostExecute(User.getUserFromJson(cache).getCuratedTopics());
                        this.cancel(false);
                        return;
                    } catch (JSONException e) {} //ok fail : it is just cache
                } 
                super.onPreExecute();
	        }
			
			@Override
			protected ArrayList<Topic> doInBackground(Void... params) {
                try {
                	//get list of topic
					String jsonOutput = NetworkingUtils.sendRestfullRequest(
                            Constants.PROFILE_REQUEST,
                            OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(BookmarkletActivity.this)));
                    System.out.println("jsonOutput : " + jsonOutput);
                    ScoopItApp.INSTANCE.cache.setCacheForUrl(jsonOutput, Constants.PROFILE_REQUEST);
					return User.getUserFromJson(jsonOutput).getCuratedTopics();
					
				} catch (JSONException e) {}
                
                //handle errors
                final AlertDialog alertDialog = new AlertDialog.Builder(BookmarkletActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry, can not get data from server");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   //quit
                	   BookmarkletActivity.this.finish();
                   }
                });
                alertDialog.show();
                return null;
			}
			
			 @Override
            protected void onPostExecute(ArrayList<Topic> result) {
                if (isCancelled()) {
                    onCancelled();
                    return;
                }
                super.onPostExecute(result);
                dialog.dismiss();
                //populate selector
                selectTopic.setAdapter(new SpinnerTopicListAdapter(BookmarkletActivity.this, result));
                //fair enougth -> just to next step prepar the post
                BookmarkletActivity.this.preparePost();
            }
			
		}.execute();
	}
	
	private void preparePost() {
		PostAction.preparePost(url, BookmarkletActivity.this, true, new PostAction.OnActionComplete() {
			@Override
			public void onActionComplete(Post in, Post out) {
				populateFormFromPost(out);
			}
		});

		populateFormFromPost(post);
		if (post == null) {
			// empty fields
			urlEditText.setText("");
			urlEditText.setHint("Url");
			titleEditText.setText("");
			descriptionEditText.setText("");
		}
	}
	
}
