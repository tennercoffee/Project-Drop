package rwt.kevin.memories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

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
        TextView usernameTextView = (TextView) findViewById(R.id.usernameText);



    }
}
