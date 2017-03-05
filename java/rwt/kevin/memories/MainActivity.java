package rwt.kevin.memories;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import java.net.URL;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    //declare variables
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private static final int REQUEST_PERMISSIONS = 20;
    Toolbar toolbar;
    URL atlasAuthUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        LoginActivity l = new LoginActivity();
        if (!l.isLoggedIn(getApplicationContext())) {
            //if not logged in, setup mainactivity, else open maps activity
            toolbar = (Toolbar) findViewById(R.id.login_toolbar);
            if (toolbar != null) {
                toolbar.setTitle("Welcome to Moments!");
            }

            //setup buttons and listeners
            Button aboutButton = (Button) findViewById(R.id.about_button);
            Button signinButton = (Button) findViewById(R.id.login_button);
            Button noLoginButton = (Button) findViewById(R.id.no_login_button);

            aboutButton.setOnClickListener(this);
            signinButton.setOnClickListener(this);
            noLoginButton.setOnClickListener(this);
        } else {
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
        }
    }
    //button functions
    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()) {
            case R.id.login_button:
                i = new Intent(getApplicationContext(), LoginActivity.class);
                break;
            case R.id.about_button:
                i = new Intent(getApplicationContext(), AboutActivity.class);
                break;
            case R.id.no_login_button:
                i = new Intent(getApplicationContext(), MapsActivity.class);
                break;
        }
        startActivity(i);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //check permissions to see if fine location is enabled
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(null, "permission is granted");
        } else {
            Log.d(null, "no permission");
        }
        // handles the result of the permission request by implementing the ActivityCompat.OnRequestPermissionsResultCallback
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(null, "permission is granted");
            } else {
                Log.d(null, "no permission");
            }
        }
    }
}