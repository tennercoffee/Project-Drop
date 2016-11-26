package rwt.kevin.memories;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Scanner;

public class MyProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_imageview);
        toolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My Profile");
            setSupportActionBar(toolbar);
        }
        TextView usernameTextView = (TextView) findViewById(R.id.usernameText);
        Button backButton = (Button) findViewById(R.id.backbutton);
        if(backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        if(logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(i);
                    new AlertDialog.Builder(getApplicationContext())
							.setTitle("Confirm")
							.setMessage("Do you really want to logout?")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    //TODO: send api call to logout of user
								}
							})
							.setNegativeButton(android.R.string.no, null).show();
                }
            });
        }
//        Button settingsButton = (Button) findViewById(R.id.settings_button);
//        if(settingsButton != null){
//            settingsButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
//                    startActivity(i);
//                }
//            });
//        }
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class LoadUser extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject;
    private String timestampString;
    String id;

    protected void onPreExecute() {
    }
    @Override
    protected Void doInBackground(String... params) {
        try {
//            JSONObject pageValuesObject = new JSONObject();
//            JSONObject regionObject = new JSONObject().put("pageTypeStringAttributesId", "54").put("value", regionCode);
//            JSONObject titleObject = new JSONObject().put("pageTypeStringAttributesId", "48").put("value", memoryString);
//            JSONObject coorObject = new JSONObject().put("pageTypeStringAttributesId", "51").put("value", coordinates);
//            JSONObject timestampObject = new JSONObject().put("pageTypeStringAttributesId", "57").put("value", timestampString);
//            pageValuesObject.put("0", regionObject).put("1", titleObject).put("2", coorObject).put("3", timestampObject);
//            Log.d(null, pageValuesObject.toString());
//            String dataString = URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
//                    + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(memoryString, "UTF-8")
//                    + "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode(scope, "UTF-8")
//                    + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")
//                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8")
//                    + "&" + URLEncoder.encode("pageValues", "UTF-8") + "=" + URLEncoder.encode(pageValuesObject.toString(),"UTF-8");
            URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=addPage&");
            Log.d(null, url.toString());
            final URLConnection conn= url.openConnection();
            Thread.sleep(1000);
            final BufferedReader[] in = new BufferedReader[1];
            DownloadMemoryList.InputReader reader = new DownloadMemoryList.InputReader() {
                @Override
                public String getInput() {
                    try {
                        in[0] = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return String.valueOf(in[0]);
                }
            };
            String input = reader.getInput();
            jsonObject = new JSONObject(input);
            Log.d(null, jsonObject.toString());
        } catch (IOException e4) {
            e4.printStackTrace();
        } catch (Exception e) {
            Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        if(jsonObject != null){
            //load username, and photo


//            try {
                Log.d(null, jsonObject.toString());
//                int idObject = jsonObject.getInt("id");
//                ViewMemoryActivity view = new ViewMemoryActivity();
//                view.setId(String.valueOf(idObject));
//                String successObject = jsonObject.getString("success");
//                if (successObject.equals("true")) {
//                    Log.d(null, "success true...id: " + idObject);
//                }
//            } catch (JSONException j) {
//                j.printStackTrace();
//            }
        } else { Log.d(null, "null jsonobject");}
    }
}