package rwt.kevin.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{
    Button tutorialButton, knownIssuesButton, backButton, privacyPolicyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_moments);
        Log.d(null, "setview about");

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_moments_toolbar);
        if(toolbar != null){
            toolbar.setTitle("About Moments");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tutorialButton = (Button) findViewById(R.id.tutorial_button);
        knownIssuesButton = (Button) findViewById(R.id.issues_button);
        privacyPolicyButton = (Button) findViewById(R.id.privacy_policy_button);

        tutorialButton.setOnClickListener(this);
        knownIssuesButton.setOnClickListener(this);
        privacyPolicyButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.issues_button:
                Intent i = new Intent(getApplicationContext(),FAQActivity.class);
                startActivity(i);
                break;
            case R.id.tutorial_button:
                Intent t = new Intent(getApplicationContext(),TourActivity.class);
                startActivity(t);
                break;
            case R.id.privacy_policy_button:
                Intent p = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                startActivity(p);
        }
    }
}