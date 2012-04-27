package com.scoopit.android.curation.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.model.Post;

public class PostCreateActivity extends PostCurateActivity {

	private EditText urlEditText;

	private String topicId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		topicId = (String) i.getExtras().get("topicId");

		// show url area
		findViewById(R.id.curate_url_label).setVisibility(View.VISIBLE);
		urlEditText = (EditText) findViewById(R.id.curate_url);
		urlEditText.setVisibility(View.VISIBLE);
		urlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String url = urlEditText.getText().toString();
					if (url != null && url.length() > 0) {
						if (!url.startsWith("http://")
								&& !url.startsWith("https://")) {
							url = "http://" + url;
							urlEditText.setText(url);
						}
						PostAction.preparePost(url, PostCreateActivity.this, true, new PostAction.OnActionComplete() {
							@Override
							public void onActionComplete(Post in, Post out) {
								populateFormFromPost(out);
							}
						});
					}
				}
			}
		});

		if (post == null) {
			// empty fields
			urlEditText.setText("");
			urlEditText.setHint("Url");
			titleEditText.setText("");
			descriptionEditText.setText("");
		}

		pageTitle.setText("Write a Post");

		action.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// start to update the post
				post.setTitle(titleEditText.getText().toString());
				post.setContent(descriptionEditText.getText().toString());
				post.setImageUrl((String) gallery.getSelectedItem());

				PostAction.createPost(post, topicId, jsonForSelectedSharers(),
						PostCreateActivity.this, true,
						new PostAction.OnActionComplete() {

							@Override
							public void onActionComplete(Post in, Post out) {
								Intent i = new Intent();
								i.putExtra("postToAdd", out);
								PostCreateActivity.this.setResult(TabPostsListActivity.RESULT_ADD_CURATED_AND_REMOVE_CURABLE, i);
								PostCreateActivity.this.finish();
							}
						});
			}
		});
	}
}
