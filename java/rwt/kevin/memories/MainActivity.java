package rwt.kevin.memories;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends FragmentActivity {
    Toolbar toolbar;
    URL sendUrl = null;
    Button aboutButton;
    Button signinButton;
    Button noLoginButton;
    WebView webView;

    public interface GetUrl {
        List<List<String>> getUrl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        aboutButton = (Button) findViewById(R.id.aboutButton);
        signinButton = (Button) findViewById(R.id.login_button);
        noLoginButton = (Button) findViewById(R.id.noLoginButton);

        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Main");
        }

        final GetUrl getUrl = new GetUrl() {
            @Override
            public List<List<String>> getUrl() {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(null, "pagefinished2: " + webView.getUrl());
                        // pagefinished: https://www.google.com/?userId=2661&username=kbilleaud&accessKey=ca5a9047-b346-11e6-9298-e0cb4ea6daff&gws_rd=ssl
                    }
                });
                return null;
            }
        };
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
                        signIn();
                    }
                });
            }
        } else {
            Log.d(null, "loggedin, proceed to maps");
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
        }
    }
    public void signIn() {
        try {
            String appId = "450";
            String appToken = "7028c8f4-b385-11e6-9298-e0cb4ea6daff";
            String authGoto = "rwt.kevin.memories://returnApp?/";
            sendUrl = new URL("http://atlas.webapps.centennialarts.com/authorize.html?"
                    + "appId=" + appId + "&appToken=" + appToken + "&authorizationGoto=" + authGoto);
            Log.d(null, "call: " + sendUrl.toString());

            if(aboutButton != null && noLoginButton != null) {
                TextView textView = (TextView) findViewById(R.id.textView);
                TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                aboutButton.setVisibility(View.INVISIBLE);
                signinButton.setVisibility(View.INVISIBLE);
                noLoginButton.setVisibility(View.INVISIBLE);
            }
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(sendUrl.toString());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Map<String,String> urlMap = getURLMap(url);
                    String username = urlMap.get("username");
                    String accessKey = urlMap.get("accessKey");
                    String id = urlMap.get("rwt.kevin.memories://returnApp?/?userId");
                    Log.d(null, id + " " + username + " " + accessKey);

                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(i);
                    // return true if you want to block redirection, false otherwise
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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