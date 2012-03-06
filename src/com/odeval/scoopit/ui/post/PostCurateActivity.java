package com.odeval.scoopit.ui.post;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.odeval.scoopit.R;
import com.odeval.scoopit.actions.PostAction;
import com.odeval.scoopit.model.Post;
import com.odeval.scoopit.model.Sharer;
import com.odeval.scoopit.model.User;
import com.odeval.scoopit.ui.list.adapater.GalleryPostImageAdapter;

/**
 * View to curate a post
 * 
 * @author vincentdemay
 * 
 */
public class PostCurateActivity extends Activity {
    protected Post post;
    protected User user;

    protected EditText titleEditText;
    protected EditText descriptionEditText;
    protected Gallery gallery;
    protected Button action;
    protected TextView pageTitle;

    protected LinearLayout shareContainer;
    protected LinearLayout shareEditContainer;

    protected void populateFormFromIntent(Intent intent) {
        post = intent.getExtras().getParcelable("post");
        setContentView(R.layout.post_curate_activity);

        titleEditText = ((EditText) findViewById(R.id.post_list_title));
        descriptionEditText = ((EditText) findViewById(R.id.post_list_content));
        action = ((Button) findViewById(R.id.curate));
        gallery = (Gallery) findViewById(R.id.gallery);
        pageTitle = ((TextView) findViewById(R.id.curate_title));

        GalleryPostImageAdapter adapter = new GalleryPostImageAdapter(post, this);
        gallery.setUnselectedAlpha(0.5f);
        gallery.setAdapter(adapter);
        titleEditText.setText(post.getTitle());
        descriptionEditText.setText(post.getContent());

        shareContainer = (LinearLayout) findViewById(R.id.shareContainer);
        shareEditContainer = (LinearLayout) findViewById(R.id.shareEditContainer);

        user = User.readFromFile(this);
        if (user.getSharers() != null && user.getSharers().size() > 0) {
            shareContainer.setVisibility(View.VISIBLE);
            for (Sharer sharer : user.getSharers()) {
                addSharer2UI(sharer);
            }
        } else {
            shareContainer.setVisibility(View.GONE);
        }
    }

    private void addSharer2UI(final Sharer sharer) {
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

    private String jsonForSelectedSharers() {
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
            toReturn = toReturn.substring(0, toReturn.length() - 1) + "]";
            return toReturn;
        } else {
            return null;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        populateFormFromIntent(intent);

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
    }

}
