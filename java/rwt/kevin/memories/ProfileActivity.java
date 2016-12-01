package rwt.kevin.memories;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    TextView usernameTextView;
    Toolbar toolbar;
    String username;
    String userid;
    String accessKey;

    interface LoadUser {
        void loadUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_imageview);
        toolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Profile");
            setSupportActionBar(toolbar);
        }

        Intent i = getIntent();
        username = i.getStringExtra("username");
        userid = i.getStringExtra("userid");

        Button backButton = (Button) findViewById(R.id.backbutton);
        if(backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        if(logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(i);
                    new AlertDialog.Builder(getApplicationContext())
							.setTitle("Confirm")
							.setMessage("Do you really want to logout?")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    //TODO: send api call to logout of user
								}
							})
							.setNegativeButton(android.R.string.no, null).show();
                }
            });
        }
//        Button settingsButton = (Button) findViewById(R.id.settings_button);
//        if(settingsButton != null){
//            settingsButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
//                    startActivity(i);
//                }
//            });
//        }
        LoadUser loadUser = new LoadUser() {
            @Override
            public void loadUser(){
                DownloadProfile dp = new DownloadProfile();
                if(username != null && userid != null && accessKey != null) {
                    dp.setUser(username, userid, accessKey);
                    dp.execute();
                }
            }
        };
        loadUser.loadUser();

    }
    public void setUser(String username, String userid, String accessKey) {
        //set all user data here
//        this.username = username;
        usernameTextView.setText(username);
    }
}