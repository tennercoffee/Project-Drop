package rwt.kevin.memories;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends MainActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Sign Up");
        }

        EditText emailText = (EditText) findViewById(R.id.emailText);
        EditText passText1 = (EditText) findViewById(R.id.passText1);
        EditText passText2 = (EditText) findViewById(R.id.passText2);

        String email = null;
        String password = null;

        if(emailText != null && passText1 != null && passText2 != null){
            email = emailText.getText().toString();
            if(passText1 == passText2){
                password = passText1.getText().toString();
                
                //call to server
            }else{
                //failMessage("Passwords must match");
            }
        }else{
            //error, null values
            //failMessage("ERROR!");
        }

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        Button submitButton = (Button) findViewById(R.id.submitButton);
        if(submitButton != null){
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        //get email and password from sign up form
        //send to SignUp async

        SignUp n = new SignUp();
        n.execute(email,password);
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class SignUp extends AsyncTask<String, String, Void> {

    protected void onPreExecute() {
        Log.d(null, "adding user");
    }
    @Override
    protected Void doInBackground(String... params) {

        String email = params[0];
        String password = params[1];
        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "user added");
    }
}