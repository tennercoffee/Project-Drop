package rwt.kevin.memories;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;

import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1, MY_LOCATION_REQUEST_CODE = 1;

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
                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(getApplicationContext(), "Enable Location and Data Services", Toast.LENGTH_LONG).show();
                            Log.d(null, "no permission for location/data");
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 0);
//                            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
//                                Toast.makeText(getApplicationContext(), "Enable Internet", Toast.LENGTH_LONG).show();
//                                Log.d(null, "no permission for internet");
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 0);
//                            } else {
//                                Log.d(null, "data enabled");
//                                Toast.makeText(getApplicationContext(), "Please enable an internet connection", Toast.LENGTH_LONG).show();
//                            }
//                            startActivity(i);
                        } else {
                            Log.d(null, "permission for location granted");
                            startActivity(i);
                        }
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

//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.INTERNET}, 1);
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