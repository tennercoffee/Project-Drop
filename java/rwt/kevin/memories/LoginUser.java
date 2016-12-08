package rwt.kevin.memories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static rwt.kevin.memories.LoginActivity.getURLMap;

class LoginUser extends AsyncTask<String, String, Void> {
    private WebView webView;
    URL sendUrl;
    URL url = null;
    String username;
    String id;
    String accessKey;

    protected void onPreExecute() {
        Log.d(null, "logging in");
    }
    @Override
    protected Void doInBackground(String... params) {
        try {
//            webView = (WebView) findViewById(R.id.webview);
            String appId = "513";
            String appToken = "47b9267d-bc1b-11e6-9298-e0cb4ea6daff";
            String authGoto = "rwt.kevin.memories://returnApp?/";
            sendUrl = new URL("http://atlas.webapps.centennialarts.com/authorize.html?"
                    + "appId=" + appId + "&appToken=" + appToken + "&authorizationGoto=" + authGoto);
            Log.d(null, "call: " + sendUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "logged in");
        if(webView != null) {
            webView.loadUrl(sendUrl.toString());
            webView.setWebChromeClient(new WebChromeClient());
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
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //move to onpagefinished
                    Log.d(null, "url2: " + url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.d(null, url);
                    Map<String, String> urlMap = getURLMap(url);
                    username = urlMap.get("username");
                    accessKey = urlMap.get("tokenid");  //changed from accesskey..
                    id = urlMap.get("rwt.kevin.memories://returnApp?/?userId");
                    Log.d(null, id + " " + username + " " + accessKey);
                    if (id != null && accessKey != null) {

//                    Intent i = new Intent(null, MapsActivity.class);
//                    Toast.makeText(null, "signed in as: " + username, Toast.LENGTH_LONG).show();
//                    startActivity(i);
                        return true;
                    } else {
//                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                    Toast.makeText(getApplicationContext(), "error signing in", Toast.LENGTH_LONG).show();
//                    startActivity(i);
                        return false;
                    }
                    // return true if you want to block redirection, false otherwise
                }
            });

        }
    }
//    public static Map<String, String> getURLMap(String query) {
//        String[] params = query.split("&");
//        Map<String, String> map = new HashMap<>();
//        for (String param : params)
//        {
//            String name = param.split("=")[0];
//            String value = param.split("=")[1];
//            Log.d(null, "name: " + name + " value: " + value);
//            map.put(name, value);
//        }
//        return map;
//    }
    public void setWebView(WebView webView){
        this.webView = webView;
    }
}