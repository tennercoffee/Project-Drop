package rwt.kevin.memories;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //declare variables
    String usernameString, accessKeyString;
    TextView usernameTextView;
    ImageView profileImageView;
    Toolbar toolbar;
    String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        toolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Profile");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //get user information
        urlString = getString(R.string.atlas_access_url);
        usernameString = getIntent().getStringExtra("usernameString");
        accessKeyString = getString(R.string.atlas_app_token);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String atlasAccessKeyString = sharedPreferences.getString(getString(R.string.atlas_app_token), null);
        Log.d(null, "user: " + usernameString + " " + accessKeyString);

        //load views
        usernameTextView = (TextView) findViewById(R.id.username_textview);
        profileImageView = (ImageView) findViewById(R.id.profile_imageview); //get img from profile info

        //load buttons
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        Button editProfileButton = (Button) findViewById(R.id.edit_profile_button);
        if(logoutButton != null && editProfileButton != null) {
            logoutButton.setOnClickListener(this);
            editProfileButton.setOnClickListener(this);
        }
        //download user information
        DownloadUser user = new DownloadUser();
        user.setTextView(usernameTextView, profileImageView);
        user.execute(usernameString, atlasAccessKeyString, urlString);
    }
    //button click functions
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.logout_button:
                //logout of app
                CharSequence options[] = new CharSequence[] {"Logout", "Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Do you want to Logout?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if(which == 0){
                            //call sharedprefs here, and delete usernameString and logintoken
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            Log.d(null,"logout for:" + usernameString);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.clear().apply();

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            //TODO: send api call to logout of user
                        } else {
                            builder.setCancelable(true);
                        }
                    }
                });
                builder.show();
                break;
            case R.id.edit_profile_button:
                //update user information
                Toast.makeText(getApplicationContext(),"Feature Coming Soon", Toast.LENGTH_LONG).show();
                break;
        }
    }
    public void setUser(TextView usernameTextView, String username, ImageView profileImageView, String photoUrl) { //setup profile
        //setup profile views
        this.usernameTextView = usernameTextView;
        this.profileImageView = profileImageView;
        this.usernameString = username;

        //set user information
        usernameTextView.setText(username);
        DownloadImage di = new DownloadImage();
        di.setImageView(profileImageView);
        di.execute(photoUrl);
    }
}