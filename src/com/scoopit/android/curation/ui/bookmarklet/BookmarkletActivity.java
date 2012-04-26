package com.scoopit.android.curation.ui.bookmarklet;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.actions.PostAction;
import com.scoopit.android.curation.model.Post;
import com.scoopit.android.curation.ui.post.PostCurateActivity;

public class BookmarkletActivity extends PostCurateActivity {
	private EditText urlEditText;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show url area but it can not be editable
		findViewById(R.id.curate_url_label).setVisibility(View.VISIBLE);

		urlEditText = (EditText) findViewById(R.id.curate_url);
		urlEditText.setEnabled(false);
		
		//get the url in intent to prepar a post
		Uri uri = getIntent().getData();
		PostAction.preparePost(uri.toString(), BookmarkletActivity.this, true, new PostAction.OnActionComplete() {
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

		pageTitle.setText("Bookmark this page");

		/*action.setOnClickListener(new OnClickListener() {
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
		});*/
	}
}
