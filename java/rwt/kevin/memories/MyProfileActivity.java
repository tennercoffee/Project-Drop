package rwt.kevin.memories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //load myprofile features
        /***************************************************
         *
         *  which are?
         *      profile photo
         *          edit photo
         *      username
         *          editname?
         *              think this out, cannot change mind later
         *      stats?
         *          number of posts
         *      back button
         *      report an issue
         *
         * //gotta call api with marker id number and get userid
         *
         **************************************************/

        //ImageView profileImageView = (ImageView) findViewById(R.id.profile_photo);

        toolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My Profile");
            setSupportActionBar(toolbar);
        }

        TextView usernameTextView = (TextView) findViewById(R.id.usernameText);
        //get username here and settext

        Button cancelButton = (Button) findViewById(R.id.backbutton);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
