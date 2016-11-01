package rwt.kevin.memories;

import android.content.Intent;
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
                    //what to do here
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
        Button signupButton = (Button) findViewById(R.id.signup_button);
        if(signupButton != null){
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(i);
                }
            });
        }
        Button quicktourButton = (Button) findViewById(R.id.quicktour_button);
        if(quicktourButton != null){
            quicktourButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), TourActivity.class);
                    startActivity(i);
                }
            });
        }
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class LoginUser extends AsyncTask<String, String, Void> {
    protected void onPreExecute() {
        Log.d(null, "logging in");
    }
    @Override
    protected Void doInBackground(String... params) {

        String email = params[0];
        String password = params[1];
        Log.d(null, "doInBackground for:" + email + password);
        String url = null;

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