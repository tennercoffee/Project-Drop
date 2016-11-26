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
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://atlas.webapps.centennialarts.com/authorize.html"));
//        startActivity(browserIntent);

        //once user has logged in
        //add app
        URLConnection conn;
//        try {
//            String appId = "201";
//            String appToken = "6mq8e4banlvlsvdpn63bta33h4";
//            String authGoto = "http://www.google.com";
//
//            url = new URL("http://atlas.webapps.centennialarts.com/authorize.html?appId=" + appId + "&appToken=" + appToken + "&authGoto=" + authGoto);
//
//            Log.d(null, url.toString());
////            conn = url.openConnection();
//            Thread.sleep(3000);
//            //http://atlas.webapps.centennialarts.com/authorize.html?
//            //    appId=228&appToken=0fe12ace-af84-11e6-9298-e0cb4ea6daff&authorizationGoto=http://www.centennialarts.com/
//
//
//            String data = URLEncoder.encode("appId", "UTF-8") + "=" + URLEncoder.encode(appId, "UTF-8")
//                            + "&" + URLEncoder.encode("appToken", "UTF-8") + "=" + URLEncoder.encode(appToken, "UTF-8")
//                            + "&" + URLEncoder.encode("authorizationGoto", "UTF-8") + "=" + URLEncoder.encode(authGoto, "UTF-8");
//            URL newUrl = new URL(url + data);
//            conn = newUrl.openConnection();
//            Log.d(null, conn.getURL().toString());



//            URL newUrl = conn.getURL();
//            Thread.sleep(1000);


//            Log.d(null, newUrl.toString());




//
//
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        /***********************************************************
         *
         * create async task
         * login screen
         *  -login user,
         *      -add app, get id from json return
         *          -call list pages, find jsonobject with this.id
         *              -get(from jsonobject) accesskey
         *                  -finally authorize with app id and accesskey
         *
         */
//        String email = params[0];
//        String password = params[1];
//        Log.d(null, "doInBackground for:" + email + password);
//        String url = null;


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