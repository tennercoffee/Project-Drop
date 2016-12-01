package rwt.kevin.memories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.net.URL;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity {
    Toolbar toolbar;
    URL sendUrl = null;
    Button aboutButton;
    Button signinButton;
    Button noLoginButton;
//    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        aboutButton = (Button) findViewById(R.id.aboutButton);
        signinButton = (Button) findViewById(R.id.login_button);
        noLoginButton = (Button) findViewById(R.id.noLoginButton);

        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Main");
        }

        LoginActivity l = new LoginActivity();
        if(!l.isLoggedIn()){
            if (aboutButton != null) {
                aboutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(i);
                    }
                });
            }
            if (noLoginButton != null) {
                noLoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(i);
                    }
                });
            }
            if(signinButton != null){
                signinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearButtons();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                });
            }
            //TODO:check for permissions/ask for permissions
            MapsActivity m = new MapsActivity();

        } else {
            Log.d(null, "loggedin, proceed to maps");
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
        }
    }
    public void clearButtons() {
        if(aboutButton != null && noLoginButton != null) {
            TextView textView = (TextView) findViewById(R.id.textView);
            TextView textView2 = (TextView) findViewById(R.id.textView2);
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            aboutButton.setVisibility(View.INVISIBLE);
            signinButton.setVisibility(View.INVISIBLE);
            noLoginButton.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}