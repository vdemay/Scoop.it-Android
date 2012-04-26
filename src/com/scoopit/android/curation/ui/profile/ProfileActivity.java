package com.scoopit.android.curation.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoopit.android.curation.R;
import com.scoopit.android.curation.ScoopItApp;
import com.scoopit.android.curation.OAuth.LoginActivity;
import com.scoopit.android.curation.OAuth.OAutHelper;
import com.scoopit.android.curation.model.User;

public class ProfileActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        
        User user = User.readFromFile(this);
        
        ScoopItApp.INSTANCE.imageLoader.displayImage(user.getMediumAvatarUrl(), (ImageView)findViewById(R.id.profile_image));
        ((TextView)findViewById(R.id.profile_name)).setText(user.getName());
        ((TextView)findViewById(R.id.profile_screen_name)).setText(user.getShortName());
        ((TextView)findViewById(R.id.profile_bio)).setText(user.getBio());
        
        ((Button)findViewById(R.id.profile_logout)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                OAutHelper.clearCredentials(ProfileActivity.this.getPreferences(MODE_WORLD_WRITEABLE));
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                
                //clear cookie
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setCookie("http://www.scoop.it", "auth");
                cookieManager.setCookie("https://www.scoop.it", "auth");
                
                startActivity(intent);
                finish();
            }
        });
    }
}
