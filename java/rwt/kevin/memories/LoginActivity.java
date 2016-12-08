package rwt.kevin.memories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

public class LoginActivity extends MainActivity{
    WebView webView;
    Toolbar toolbar;
    String username;
    String id;
    String accessKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if(toolbar != null){
            toolbar.setTitle("Login");
        }
        try {
            webView = (WebView) findViewById(R.id.webview);
            String appId = "525";
            String appToken = "6512ac00-bcf1-11e6-9298-e0cb4ea6daff";
            String authGoto = "rwt.kevin.memories://returnApp?/";
            sendUrl = new URL("http://atlas.webapps.centennialarts.com/authorize.html?"
                    + "appId=" + appId + "&appToken=" + appToken + "&authorizationGoto=" + authGoto);
            Log.d(null, "call: " + sendUrl.toString());
            webView.setWebViewClient(new WebViewClient() {
                 @Override
                 public void onPageStarted(WebView view, String url, Bitmap icon) {
                     Log.d(null, "url1: " + url);
                     //this returns the middle url redirect, need what this returns
                     Log.d(null, "view.geturl: " + view.getUrl());
                     //not this. because it doesn't pull up a new page after clicking authorize
                 }
                 @Override
                 public void onPageFinished(WebView view, final String url) {
                     super.onPageFinished(view, url);
                     Log.d(null, "url2: " + url);
                     webView.setWebChromeClient(new WebChromeClient());
                     webView.getSettings().setJavaScriptEnabled(true);
                     webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //move to onpagefinished
                 }
                 @Override
                 public boolean shouldOverrideUrlLoading(WebView view, String url) {
                     Log.d(null, url);
                     if(url.contains("username")) {
                         Map<String, String> urlMap = getURLMap(url);
                         username = urlMap.get("username");
                         accessKey = urlMap.get("accessKey");
                         id = urlMap.get("rwt.kevin.memories://returnApp?/?userId");
                         Log.d(null, id + " " + username + " " + accessKey);
                         if (username != null && accessKey != null) {
                             Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                             Toast.makeText(getApplicationContext(), "signed in as: " + username, Toast.LENGTH_LONG).show();
                             startActivity(i);
                             return false;
                         } else {
                             Intent i = new Intent(getApplicationContext(), MainActivity.class);
                             Toast.makeText(getApplicationContext(), "error signing in", Toast.LENGTH_LONG).show();
                             startActivity(i);
                             return false;
                         }
                         // return true if you want to block redirection, false otherwise
                     }
                     return false;
                 }
             });
//            webView.setWebViewClient(client);
            webView.loadUrl(sendUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LoginUser user = new LoginUser();
//        user.setWebView(webView);
//        user.execute();
    }
    public boolean isLoggedIn(){
        //return true in order to simplify for now
        return false;
    }
    public static Map<String, String> getURLMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            Log.d(null, "name: " + name + " value: " + value);
            map.put(name, value);
        }
        return map;
    }
}