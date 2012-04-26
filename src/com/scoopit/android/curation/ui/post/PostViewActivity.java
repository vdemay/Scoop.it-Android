package com.scoopit.android.curation.ui.post;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ScoopItApp;
import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.model.Post;
import com.scoopit.android.curation.model.Sharer;
import com.scoopit.android.curation.model.User;
import com.scoopit.android.curation.ui.animation.ExpandCollapseAnimation;
import com.scoopit.android.curation.ui.layout.InlineLayout;

public class PostViewActivity extends Activity {
    
    private Post post;
    private boolean tagsShown;
    private boolean tagsAnimationRunning;
    
    private InlineLayout tl;
    
    private void populateFields(final Post post) {
        
    	if (post.getUrl() == null) {
    		((Button)findViewById(R.id.post_original)).setVisibility(View.INVISIBLE);
    	} else {
	        ((Button)findViewById(R.id.post_original)).setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl()));
	                startActivity(browserIntent);
	            }
	        });
    	}
        
        //data
        ((TextView)findViewById(R.id.post_source_title)).setText(post.getSource().getName());
        ((TextView)findViewById(R.id.post_title)).setText(post.getTitle());
        ((TextView)findViewById(R.id.post_content)).setText(post.getContent());
        
        //image
        if (post.getImageUrl() != null) {
            ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrl(), (ImageView)findViewById(R.id.post_image));
        } else if (post.getImageUrls() != null && post.getImageUrls().size() > 0) {
            ScoopItApp.INSTANCE.imageLoader.displayImage(post.getImageUrls().get(0), (ImageView)findViewById(R.id.post_image));
        } else {
            ((ImageView)findViewById(R.id.post_image)).setVisibility(View.GONE);
        }
        ScoopItApp.INSTANCE.imageLoader.displayImage(post.getSource().getIconUrl(), (ImageView)findViewById(R.id.post_source_icon));
        ((TextView)findViewById(R.id.post_date)).setText(ScoopItApp.INSTANCE.dateTimeFormatMediumShort.format(new Date(post.getPublicationDate())));
        
        //tags
        tl.removeAllViews();
        for (int i = 0; i < post.getTags().size(); i++) {
            addTag(post.getTags().get(i));
        }       
    }
    
    private void addTag(String tag) {
        Button t = new Button(this);
        t.setText(tag);
        t.setBackgroundResource(R.drawable.tag);
        t.setTextColor(Color.WHITE);
        t.setSingleLine(true);
        t.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                tl.removeView(v);
                updateTagsOnServeurSide();
            }
        });
        tl.addView(t, new InlineLayout.LayoutParams(2, 0));
    }


    private void updateTagsOnServeurSide() {
        post.setTags(getTags());
        PostAction.setTag(post, getTags(), PostViewActivity.this, false);
        //also create a result for update on back
        //this can be overriden but if not it allow post to be up to date 
        Intent i = new Intent();
        i.putExtra("postToAdd", post);
        i.putExtra("postToRemove", post);
        PostViewActivity.this.setResult(TabPostsListActivity.RESULT_REPLACE_CURATED, i);
    }
    
    private ArrayList<String> getTags() {
        ArrayList<String> tags = new ArrayList<String>();
        for (int i=0; i< tl.getChildCount(); i++) {
            Button tv = (Button) tl.getChildAt(i);
            tags.add(tv.getText().toString());
        }
        return tags;
    }
    
    private void showSharerMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostViewActivity.this);
        builder.setTitle("Share");
        
        final User user = User.readFromFile(PostViewActivity.this);

        ArrayList<CharSequence> items = new ArrayList<CharSequence>();
        items.add("Email");
        
        for (Sharer sharer : user.getSharers()) {
            items.add(sharer.getSharerName());
        }
                
        builder.setItems(items.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item==0) {
                    //email
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Scoop.it | " + post.getTitle());
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I thought you may find this information interesting :\n"
                            + post.getContent() + "\n"
                            + "Reed more on Scoop.it: " + post.getUrl());
                    PostViewActivity.this.startActivity(Intent.createChooser(emailIntent, "Email"));
                    return;
                }
                //slice for sharers
                item--;
                Sharer sharer = user.getSharers().get(item);
                if (sharer.isMustSpecifyShareText()) {
                    showSharerInput(sharer);
                } else {
                    share(sharer, null);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void showSharerInput(final Sharer sharer) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add or Edit message");
        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        if ("Twitter".equals(sharer.getSharerName())) {
            input.setText(post.generateTweetText());
        }
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                share(sharer, value.toString());
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
              }
        });
        alert.show();
    }
    
    private void share(Sharer sharer, String text) {
        // TODO Auto-generated method stub
        PostAction.sharePost(post, sharer, text, this, true);
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.post_view_activity);
        post = getIntent().getExtras().getParcelable("post");
        
        tl = (InlineLayout)findViewById(R.id.post_tag_container);
        
        populateFields(post);
        
        ((Button)findViewById(R.id.post_share)).setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        	    showSharerMenu();
        	}
        });
        
        ((Button)findViewById(R.id.post_tag)).setOnClickListener(new OnClickListener() {
            View tagView = ((View)findViewById(R.id.post_tag_view));
        	public void onClick(View v) {
        	    if (!tagsAnimationRunning) {
            	    tagsAnimationRunning = true;
            	    
            	    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    float logicalDensity = metrics.density;
                    int px = (int) (150 * logicalDensity + 0.5);
            	    
            	    if (!tagsShown) {
            	        ExpandCollapseAnimation anim = new ExpandCollapseAnimation(tagView, px, true);
            	        anim.setAnimationListener(new Animation.AnimationListener() {
                            
                            @Override
                            public void onAnimationStart(Animation animation) {
                                tagsShown = false;
                            }
                            
                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                            
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                tagsShown = true;
                                tagsAnimationRunning = false;
                            }
                        });
            	        tagView.startAnimation(anim);
            	    } else {
            	        ExpandCollapseAnimation anim = new ExpandCollapseAnimation(tagView, px, false);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            
                            @Override
                            public void onAnimationStart(Animation animation) {
                                tagsShown = true;
                            }
                            
                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                            
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                tagsShown = false;
                                tagsAnimationRunning = false;
                            }
                        });
                        tagView.startAnimation(anim);
            	    }
        	    }
        	}
        });
        
        ((Button)findViewById(R.id.button_add_tag)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                EditText tagField = (EditText) PostViewActivity.this.findViewById(R.id.editText_add_tag);
                if (tagField != null && !tagField.getText().toString().equals("")) {
                    addTag(tagField.getText().toString());
                    updateTagsOnServeurSide();
                    tagField.setText("");
                }
            }
            
        });
        
        ((Button)findViewById(R.id.post_edit)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        	    Intent i = new Intent(PostViewActivity.this, PostEditActivity.class);
                i.putExtra("post", post);
                PostViewActivity.this.startActivityForResult(i, 1);
        	}
        });
        
        ((Button)findViewById(R.id.post_delete)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		new AlertDialog.Builder(PostViewActivity.this)
        		.setTitle(getResources().getString(R.string.post_list_activity_delete_confirm_title))
                .setMessage(getResources().getString(R.string.post_list_activity_delete_confirm_message, post.getTitle()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    PostAction.deletePost(post, PostViewActivity.this, true, new PostAction.OnActionComplete() {
                            @Override
                            public void onActionComplete(Post in, Post out) {
                                Intent i = new Intent();
                                i.putExtra("post", in);
                                PostViewActivity.this.setResult(TabPostsListActivity.RESULT_DELETE_CURATED, i);
                                PostViewActivity.this.finish();
                            }
                        });
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
        	}
        });
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null) {
            //update fields
            Post postToAdd = (Post)data.getExtras().get("postToAdd");
            if (postToAdd != null) {
                post = postToAdd;
                populateFields(postToAdd);
            }
        }
        
        //propagate
        setResult(resultCode, data);
    }
}
