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

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Main");
        }
        LoginActivity l = new LoginActivity();
        if(!l.isLoggedIn()){
            Button aboutButton = (Button) findViewById(R.id.aboutButton);
            if (aboutButton != null) {
                aboutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(i);
                    }
                });
            }
            Button noLoginButton = (Button) findViewById(R.id.noLoginButton);
            if (noLoginButton != null) {
                noLoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(i);
                    }
                });
            }
            Button signinButton = (Button) findViewById(R.id.login_button);
            if(signinButton != null){
                signinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                });
            }
            Button signupButton = (Button) findViewById(R.id.signupButton);
            if(signupButton != null){
                signupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                        startActivity(i);
                    }
                });
            }
        } else {
            Log.d(null, "loggedin, proceed to maps");
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
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