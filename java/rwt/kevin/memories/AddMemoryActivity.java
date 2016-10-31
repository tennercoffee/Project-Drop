package rwt.kevin.memories;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Scanner;

public class AddMemoryActivity extends MapsActivity {
    Toolbar toolbar;
    public TextView charCountTextView;
    public String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        toolbar = (Toolbar) findViewById(R.id.add_memory_toolbar);
        if(toolbar != null) {
            toolbar.setTitle("Add New Moment");
        }
        try {
            addMemoryActivity();
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void addMemoryActivity() throws MalformedURLException, UnsupportedEncodingException {
        final EditText memoryInput = (EditText) findViewById(R.id.editInp);
        if(memoryInput != null){
            memoryInput.addTextChangedListener(mTextEditorWatcher);
        }

        Intent intent = getIntent();
        location = intent.getParcelableExtra("location").toString();

        Scanner scanner = new Scanner(location);
        if (scanner.hasNext("lat/lng:")) {
            scanner.skip("lat/lng:");
            Log.d(null, "skip 1");
        } else if (scanner.hasNext(" lat/lng:")) {
            Log.d(null, "skip 2");
            scanner.skip(" lat/lng: ");
        }
        Log.d(null, location);

        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        if(locationTextView != null) {
            locationTextView.setText(location);
        }
        charCountTextView = (TextView) findViewById(R.id.charCountTextView);

        //add scope
        final Spinner scopeSpinner = (Spinner) findViewById(R.id.scopeList);
        if(scopeSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.scopeListArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scopeSpinner.setAdapter(adapter);
        }

        Button submitButton = (Button) findViewById(R.id.submitMemoryButton);
        //final LoginActivity user = new LoginActivity();
        if(submitButton != null){
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){
                    if(memoryInput != null && location != null && scopeSpinner != null /*&& user.loggedIn()*/) {
                        Log.d(null, location);
                        final String scope = scopeSpinner.getSelectedItem().toString();
                        String memoryString = memoryInput.getText().toString();

                        AddMemory post = new AddMemory();
                        String id = post.execute(location, scope, memoryString).toString();

                        Log.d(null, id);
                        //TODO: finish this, add regionCode
                        finish();
                        //Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                        //i.putExtra("id", id);
                        //startActivity(i);
                    }else {
                        Log.d(null, "error");
                        //change color of toolbar to red
                        /*if(toolbar != null) {
                            toolbar.findViewById(R.id.toolbar_error);
                            toolbar.setTitle("Error, not logged in");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            toolbar.findViewById(R.id.add_memory_toolbar);
                            toolbar.setTitle("Add New Moment");
                            //error: user not signed in
                        }*/
                    }
                }
            });
        }
        Button cancelButton = (Button) findViewById(R.id.cancelMemoryButton);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        //setupSnapshot();
    }

    private void setupSnapshot() throws MalformedURLException, UnsupportedEncodingException {
        Log.d(null, "scan location");
        TextScanner scan = new TextScanner();
        String locationString = scan.descSplitter(location,0,0);

        Log.d(null, "build snapshot url");
        URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?");
        String data = URLEncoder.encode("center", "UTF-8") + "=" + URLEncoder.encode(locationString, "UTF-8")
                + "&" + URLEncoder.encode("zoom", "UTF-8") + "=" + URLEncoder.encode("18", "UTF-8")
                + "&" + URLEncoder.encode("size", "UTF-8") + "=" + URLEncoder.encode("50x50", "UTF-8");

        Log.d(null, "snapshot url" + url.toString() + data);
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
           //This sets a textview to the current length
           charCountTextView.setText(String.valueOf(140-s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class AddMemory extends AsyncTask<String, String, Void> {
    private BufferedReader in;
    private String successObject;
    String id;
    JSONArray jsonArray;

    protected void onPreExecute() {
        Log.d(null, "adding memory");
    }
    @Override
    protected Void doInBackground(String... params) {
        String location = params[0];
        String scope = params[1];
        String memoryString = params[2];
        String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";//a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17
        String s;

        Scanner scanner = new Scanner(location);
        if (scanner.hasNext("lat/lng:")) {
            scanner.skip("lat/lng:");
        } else if (scanner.hasNext(" lat/lng: ")) {
            scanner.skip(" lat/lng: ");
        }
        String coordinates = scanner.next();
        coordinates = coordinates.startsWith("(") ? coordinates.substring(1) : coordinates;
        coordinates = coordinates.endsWith(")") ? coordinates.substring(0, coordinates.length() - 1) : coordinates;

        //add timestamp here
        Calendar timestamp = Calendar.getInstance();
        String y = String.valueOf(timestamp.get(Calendar.YEAR));
        String m = String.valueOf(timestamp.get(Calendar.MONTH));
        String d = String.valueOf(timestamp.get(Calendar.DAY_OF_MONTH));
        String h = String.valueOf(timestamp.get(Calendar.HOUR_OF_DAY));
        String m1 = String.valueOf(timestamp.get(Calendar.MINUTE));
        String s1 = String.valueOf(timestamp.get(Calendar.SECOND));
        String m2 = String.valueOf(timestamp.get(Calendar.MILLISECOND));

        String timestampString =  y + ":" + m + ":" + d + ":" + h + ":" + m1 + ":" + s1 + ":" + m2;
        // 0001 is western hemisphere
        String desc = timestampString + " 0001 " + coordinates;
        Log.d(null, desc);

        try {
            URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=addPage&");
            String data = URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(desc, "UTF-8")
                    + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(memoryString, "UTF-8")
                    + "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode(scope, "UTF-8")
                    + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("75", "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");

            URL nUrl = new URL(url + data);
            Log.d(null, nUrl.toString());
            URLConnection conn = nUrl.openConnection();
            try{
                Thread.sleep(2000);
            } catch(Exception e){
                Log.d(null, "no sleep1");
            }
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((s = in.readLine()) != null) {
                Log.d(null, s);

                jsonArray = new JSONArray(s);
                Log.d(null, "jArray");

            }
        } catch (UnsupportedEncodingException e1) {
            Log.d(null, e1.toString());
            e1.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.d("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.d("IOException", e4.toString());
            e4.printStackTrace();
        } catch (Exception e) {
            Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
                // just going to ignore this one
            }
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        //TODO: parse success

        try {
            for (int n = 0; n < jsonArray.length(); n++) {
                Log.d(null, String.valueOf(n));
                JSONObject j = jsonArray.getJSONObject(n);

                int idObject = j.getInt("id");
                Log.d(null, "id: " + String.valueOf(idObject));

                successObject = j.getString("success");
                Log.d(null, "successObject: " + successObject);
                //TODO: set up success message after adding mem
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(successObject == "true"){
            Log.d(null, "success true");
            Toast.makeText(null, "success!", Toast.LENGTH_LONG);
        }
    }
}