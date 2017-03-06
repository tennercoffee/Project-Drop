package rwt.kevin.memories;

import android.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends MainActivity {
    //declare variables
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    String usernameString, atlasIdString, accessKeyString;
    WebView webView;
    Toolbar toolbar;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Login");
        }
        //this checks to see if there is a usernameString and loginToken stored in sharedprefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String atlasAccessKey = sharedPreferences.getString(getString(R.string.atlas_app_token), null);
        usernameString = sharedPreferences.getString("usernameString", null);
        String atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
        if (usernameString != null && atlasAccessKey != null) {
            //if there is good data stored in sharedprefs, open MapsActivity
            Log.d(null, "valid accessToken and usernameString for:" + atlasAccessKey + " " + usernameString + "/" + atlasIdString);
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            Toast.makeText(getApplicationContext(), "signed in as: " + usernameString, Toast.LENGTH_LONG).show();
            i.putExtra("usernameString", usernameString);
            i.putExtra("accessKeyString", atlasAccessKey);
            i.putExtra("atlasIdNumberString", atlasIdString);
            startActivity(i);
        }
        setWebView();
    }
    private void setWebView() {
        try {
            //open in-app browser(webview) for logging in //TODO clean up the webview settings
            webView = (WebView) findViewById(R.id.webview);
            String appId = getString(R.string.atlas_app_id);
            String atlasAccessKey = getString(R.string.atlas_app_token);
            String urlString = getString(R.string.atlas_access_url);
            String authGoto = "http://app.moments.com/launch";
            atlasAuthUrl = new URL(urlString + "/authorize.html?"
                    + "appId=" + appId + "&appToken=" + atlasAccessKey + "&authorizationGoto=" + authGoto);
            Log.d(null, atlasAuthUrl.toString());
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(atlasAuthUrl.toString());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap icon) {
                    Log.d(null, "onPageStarted(url): " + url);
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                }
                @Override
                public void onPageFinished(WebView view, final String url) {
                    super.onPageFinished(view, url);
                    Log.d(null, "onPageFinished(url): " + url);
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    super.shouldOverrideUrlLoading(view, url);
                    Log.d(null, "shouldOverrideUrlLoading(): " + url);
                    if (url.contains("username")) {
                        Log.d(null, "user: " + usernameString + " " + atlasIdString);
                        Map<String, String> urlMap = getURLMap(url);
                        usernameString = urlMap.get("username");
                        accessKeyString = urlMap.get("accessKey");
                        atlasIdString = urlMap.get("http://app.moments.com/launch?userId");

                        if (usernameString != null && accessKeyString != null) {
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(getString(R.string.atlas_app_token), accessKeyString);
                            editor.putString("usernameString", usernameString);
                            editor.putString("atlasIdNumberString", atlasIdString);
                            editor.apply();

                            //check if you have permission to use location
                            int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                            //if not permitted, ask for permission
                            if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(getApplicationContext(), "NOT GRANTED", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_LOCATION_REQUEST_CODE);
                            } else {
                                //check if location is turned on, check if data connection is available,
                                //if not, call intent to go to settings
                                //once permission is granted, check if location is turned on
                                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                                if(!gpsTracker.canGetLocation()) {
                                    //if not turned on, go to settings
                                    Toast.makeText(getApplicationContext(),"Please Enable Location Services", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(),"Then tap the 'sync' button up top!", Toast.LENGTH_LONG).show();
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                } else {
                                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                    Toast.makeText(getApplicationContext(), "signed in as: " + usernameString, Toast.LENGTH_LONG).show();
                                    i.putExtra("usernameString", usernameString);
                                    i.putExtra("accessKeyString", accessKeyString);
                                    i.putExtra("atlasIdNumberString", atlasIdString);
                                    webView.destroy();
                                    startActivity(i);
                                    return false;
                                }
                            }
                        } else {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            Toast.makeText(getApplicationContext(), "error signing in", Toast.LENGTH_LONG).show();
                            startActivity(i);
                            return false;
                        }// return true if you want to block redirection, false otherwise
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Map<String, String> getURLMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            Log.d(null, "name: " + name + " value: " + value);
            map.put(name, value);
        }
        return map;
    }
    public boolean isLoggedIn(Context c) {
        //check if you have permission to use location
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        //if not permitted, ask for permission
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "NOT GRANTED", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        } else {
            //check if location is turned on, check if data connection is available,
            //if not, call intent to go to settings
            //once permission is granted, check if location is turned on
            GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
            if(!gpsTracker.canGetLocation()) {
                //if not turned on, go to settings
                Toast.makeText(getApplicationContext(),"Please Enable Location Services", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
                String usernameString = sharedPreferences.getString("usernameString", null);
                String atlasIdNumberString = sharedPreferences.getString("atlasIdNumberString", null);

                return usernameString != null && atlasIdNumberString != null;
            }
        }
        return false;
    }
}