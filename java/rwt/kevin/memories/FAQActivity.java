package rwt.kevin.memories;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kevin on 12/7/2016.
 */
public class FAQActivity extends AppCompatActivity {
    //declare variables

    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_moments_toolbar);
        if(toolbar != null){
            toolbar.setTitle("Known Issues");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
