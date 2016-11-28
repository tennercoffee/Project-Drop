package rwt.kevin.memories;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends MainActivity{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Log In");
        }

        EditText emailText = (EditText) findViewById(R.id.emailText);
        EditText passText1 = (EditText) findViewById(R.id.passText1);

        String email = null;
        String password = null;

        if(emailText != null && passText1 != null){
            email = emailText.getText().toString();
            password = passText1.getText().toString();
        }else{
            //error, null values
            //failMessage("ERROR!");
            Log.d(null, "signin error");
        }

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        Button loginButton = (Button) findViewById(R.id.login_button);
        if(loginButton != null){
            final String finalEmail = email;
            final String finalPassword = password;
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUser user = new LoginUser();
                    user.execute(finalEmail, finalPassword);
                }
            });
        }
    }
    public boolean isLoggedIn(){
        //return true in order to simplify for now
        return false;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class LoginUser extends AsyncTask<String, String, Void> {
    URL url = null;
    protected void onPreExecute() {
        Log.d(null, "logging in");
    }
    @Override
    protected Void doInBackground(String... params) {
//        try {
//            URL sendUrl = null;
//
//            String appId = "450";
//            String appToken = "7028c8f4-b385-11e6-9298-e0cb4ea6daff";
//            String authGoto = "http://www.google.com";
//            sendUrl = new URL("http://atlas.webapps.centennialarts.com/authorize.html?"
//                    + "appId=" + appId + "&appToken=" + appToken + "&authorizationGoto=" + authGoto);
//            Log.d(null, "call: " + sendUrl.toString());
//
//            if(aboutButton != null && noLoginButton != null) {
//                TextView textView = (TextView) findViewById(R.id.textView);
//                TextView textView2 = (TextView) findViewById(R.id.textView2);
//                textView.setVisibility(View.INVISIBLE);
//                textView2.setVisibility(View.INVISIBLE);
//                aboutButton.setVisibility(View.INVISIBLE);
//                signinButton.setVisibility(View.INVISIBLE);
//                noLoginButton.setVisibility(View.INVISIBLE);
//            }
//            webView.setVisibility(View.VISIBLE);
//            webView.loadUrl(sendUrl.toString());
//
//            WebSettings settings = webView.getSettings();
//            settings.setJavaScriptEnabled(true);
//
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    Map<String,String> urlMap = getURLMap(url);
//                    String username = urlMap.get("username");
//                    String accessKey = urlMap.get("accessKey");
//                    String id = urlMap.get("rwt.kevin.memories://returnApp?/?userId");
//                    Log.d(null, id + " " + username + " " + accessKey);
//
//                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                    startActivity(i);
//                    // return true if you want to block redirection, false otherwise
//                    return false;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "logged in");
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class LogoutUser extends AsyncTask<String, String, Void> {

    protected void onPreExecute() {
        Log.d(null, "logging out");
    }
    @Override
    protected Void doInBackground(String... params) {

        String email = params[0];
        String password = params[1];
        String url = null;
        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "logged out");
    }
}