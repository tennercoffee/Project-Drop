package rwt.kevin.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AboutActivity extends MapsActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_moments);
        Log.d(null, "setview about");

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_moments_toolbar);
        if(toolbar != null){
            toolbar.setTitle("About Moments");
        }
        Button backButton = (Button) findViewById(R.id.backButton);
        if(backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        Button tutorialButton = (Button) findViewById(R.id.tutorial_button);
        if(tutorialButton != null) {
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Feature Coming Soon!", Toast.LENGTH_LONG).show();
//                    Intent i = new Intent(getApplicationContext(),TourActivity.class);
//                    startActivity(i);
                }
            });
        }
    }
}
