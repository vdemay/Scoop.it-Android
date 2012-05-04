package com.scoopit.android.curation.ui.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.OAuth.OAutHelper;
import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.helper.NetworkingUtils;
import com.scoopit.android.curation.model.Post;
import com.scoopit.android.curation.model.Sharer;
import com.scoopit.android.curation.model.User;
import com.scoopit.android.curation.ui.list.adapater.GalleryPostImageAdapter;

/**
 * View to curate a post
 * 
 * @author vincentdemay
 * 
 */
public class PostCurateActivity extends Activity {
	
	protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	protected static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	
	private Uri capturedImageUri;
	
    protected Post post;
    protected User user;

    protected EditText titleEditText;
    protected EditText descriptionEditText;
    protected Gallery gallery;
    protected Button action;
    protected TextView pageTitle;

    protected LinearLayout shareContainer;
    protected LinearLayout shareEditContainer;
    
    protected void populateFormFromPost(Post p) {
        titleEditText = ((EditText) findViewById(R.id.post_list_title));
        descriptionEditText = ((EditText) findViewById(R.id.post_list_content));
        action = ((Button) findViewById(R.id.curate));
        gallery = (Gallery) findViewById(R.id.gallery);
        pageTitle = ((TextView) findViewById(R.id.curate_title));
        
        
        shareContainer = (LinearLayout) findViewById(R.id.shareContainer);
        shareEditContainer = (LinearLayout) findViewById(R.id.shareEditContainer);

        user = User.readFromFile(this);
        
        shareContainer.removeAllViews();
        shareEditContainer.removeAllViews();
        if (user.getSharers() != null && user.getSharers().size() > 0) {
            shareContainer.setVisibility(View.VISIBLE);
            for (Sharer sharer : user.getSharers()) {
                addSharer2UI(sharer);
            }
        } else {
            shareContainer.setVisibility(View.GONE);
        }
        
        post = p;
        if (post != null) {

            titleEditText.setText(post.getTitle());
            descriptionEditText.setText(post.getContent());

            GalleryPostImageAdapter adapter = new GalleryPostImageAdapter(post, this);
            gallery.setUnselectedAlpha(0.5f);
            gallery.setAdapter(adapter);
        }
    }

    protected void addSharer2UI(final Sharer sharer) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(sharer.getSharerName());
        if (sharer.isMustSpecifyShareText()) {
            checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        TextView tv = new TextView(PostCurateActivity.this);
                        tv.setTag(sharer.getCnxId());
                        tv.setText(sharer.getSharerName());
                        shareEditContainer.addView(tv);

                        EditText et = new EditText(PostCurateActivity.this);
                        et.setTag(sharer.getCnxId());
                        shareEditContainer.addView(et);
                        if ("Twitter".equals(sharer.getSharerName())) {
                            et.setText(post.generateTweetText());
                        }

                    } else {
                        ArrayList<View> view2Remove = new ArrayList<View>();
                        for (int i = 0; i < shareEditContainer.getChildCount(); i++) {
                            View v = shareEditContainer.getChildAt(i);
                            if (v.getTag().equals(sharer.getCnxId())) {
                                view2Remove.add(v);
                            }
                        }
                        for (View v : view2Remove) {
                            shareEditContainer.removeView(v);
                        }
                    }
                }
            });
        }

        shareContainer.addView(checkBox);
        findViewById(R.id.shareScroll).invalidate();
    }

    protected String jsonForSelectedSharers() {
        if (shareContainer.getChildCount() > 0) {
            String toReturn = "[";
            for (int i = 0; i < shareContainer.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) shareContainer.getChildAt(i);
                if (checkBox.isChecked()) {
                    // need to add this sharer
                    Sharer sharer = user.getSharers().get(i);
                    String text = null;
                    if (sharer.isMustSpecifyShareText()) {
                        // get the user text
                        for (int j = 0; j < shareEditContainer.getChildCount(); j++) {
                            View v = shareEditContainer.getChildAt(j);
                            if (v instanceof EditText && v.getTag().equals(sharer.getCnxId())) {
                                text = ((EditText) v).getText().toString();
                            }
                        }
                    }
                    String fragment = sharer.generateJsonFragment(text);
                    toReturn  = toReturn + fragment + ",";
                }
            }
            //remove last ","
            if (toReturn.length() > 1) { // at least one item
                toReturn = toReturn.substring(0, toReturn.length() - 1) + "]";
            } else {
                toReturn = null;
            }
            return toReturn;
        } else {
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putParcelable("post", post);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	 if (savedInstanceState!= null && savedInstanceState.containsKey("post")) {
    		 post = (Post) savedInstanceState.get("post");
    	 }
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        setContentView(R.layout.post_curate_activity);
                
        if (savedInstanceState!= null && savedInstanceState.containsKey("post")) {
        	post =  (Post) savedInstanceState.get("post");
        } else {
	        Intent intent = getIntent();
	        if (intent.getExtras() != null && post == null) {
	        	//get post from intent only if it is null
	        	post = intent.getExtras().getParcelable("post");
	        }
        }
	    populateFormFromPost(post);

        pageTitle.setText("Accept a Post");

        action.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // start to update the post
                post.setTitle(titleEditText.getText().toString());
                post.setContent(descriptionEditText.getText().toString());
                post.setImageUrl((String) gallery.getSelectedItem());

                PostAction.curatePost(post, jsonForSelectedSharers(), PostCurateActivity.this, true, new PostAction.OnActionComplete() {

                    @Override
                    public void onActionComplete(Post in, Post out) {
                        Intent i = new Intent();
                        i.putExtra("postToAdd", out);
                        i.putExtra("postToRemove", in);
                        PostCurateActivity.this.setResult(TabPostsListActivity.RESULT_ADD_CURATED_AND_REMOVE_CURABLE, i);
                        PostCurateActivity.this.finish();
                    }
                });
            }
        });
        
        findViewById(R.id.post_image_upload).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showImageSelectorMenu();
			}
		});
        
        capturedImageUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "scoopit_capture.jpg"));
    }

    protected void showImageSelectorMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostCurateActivity.this);
        builder.setTitle("Upload");
        builder.setCancelable(true);
        builder.setItems(new String[]{"Choose a picture", "Take a picture"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item==0) {
                	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            		photoPickerIntent.setType("image/*");
            		startActivityForResult(photoPickerIntent, PICK_IMAGE_ACTIVITY_REQUEST_CODE);
            		
                } else if (item==1) {
                	// create Intent to take a picture and return control to the calling application
    				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri); // set the image file name
    				// start the image capture Intent
    				startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.cancel();
	        }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap image = null; 
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved
				try {
					Uri imageUri = requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ? capturedImageUri : data.getData();
					image = decodeUri(imageUri, 500); // 500px size is enough for scoop.it's 2-column layout
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		final Bitmap finalImage = image;
		// if an image was uploaded or picked, upload it to our image server
		if (image != null) {
			new AsyncTask<Void, Void, Void>() {
	            ProgressDialog dialog;
	            boolean success = false;

	            @Override
	            protected void onPreExecute() {
                    dialog = ProgressDialog.show(PostCurateActivity.this, "Please Wait", "Uploading...", true);
	                super.onPreExecute();
	            }

	            @Override
	            protected Void doInBackground(Void... params) {
	                try {
	                	String response = NetworkingUtils.uploadImage(OAutHelper.getConsumer(PreferenceManager.getDefaultSharedPreferences(PostCurateActivity.this)), finalImage);
	                	if (response == null) {
	                		// error uploading image
	                		dialog.dismiss();
	                		cancel(false);
	                		return null;
	                	}
	    				JSONObject json = new JSONObject(response);
	    				if (json.has("image")) {
	    					success = true;
	    					String uploadedImage = json.getString("image");
	    					if (post != null) {
	    						if (post.getImageUrl() != null) {
	    							post.setImageUrl(uploadedImage);
	    							post.getImageUrls().add(uploadedImage);
	    						} else {
	    							post.getImageUrls().add(0, uploadedImage);
	    						}
	    					} else {
	    						post = new Post();
	    						ArrayList<String> imageList = new ArrayList<String>();
	    						imageList.add(uploadedImage);
	    						post.setImageUrls(imageList);
	    					}
	    				}
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	                return null;
	            }

	            @Override
	            protected void onPostExecute(Void result) {
	                if (isCancelled()) {
	                    onCancelled();
	                    return;
	                }
	                super.onPostExecute(result);
                    dialog.dismiss();
                    if (success) {
	                    // refresh gallery
	                    GalleryPostImageAdapter adapter = new GalleryPostImageAdapter(post, PostCurateActivity.this);
	                    gallery.setAdapter(adapter);
                    }
	            }
	            
	            @Override
	        	protected void onCancelled() {
	        	    AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
	                builder.setMessage("Sorry! An error occured. Please check your internet connexion and try later! ")
	                       .setCancelable(false)
	                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                           public void onClick(DialogInterface dialog, int id) {
	                               
	                           }
	                       });
	                AlertDialog alert = builder.create();
	                alert.show();
	                super.onCancelled();
	        	}

	        }.execute();
		}
	}
    
    /**
     * Downsample an image to avoid OutOfMemory errors.
     */
    private Bitmap decodeUri(Uri selectedImage, int size) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < size
               || height_tmp / 2 < size) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }
}
