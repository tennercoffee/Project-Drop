package rwt.kevin.memories;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class AddMemoryActivity extends MapsActivity {
    Toolbar toolbar;
    public TextView charCountTextView;
    public String location;
    public String resultId;

    interface GetResultId {
        String getResultId();
    }

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
        Button cancelButton = (Button) findViewById(R.id.cancelMemoryButton);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        Button submitButton = (Button) findViewById(R.id.submitMemoryButton);
        if(submitButton != null){
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){
                    if(memoryInput != null && location != null && scopeSpinner != null /*&& user.loggedIn()*/) {
                        Log.d(null, location);
                        final String scope = scopeSpinner.getSelectedItem().toString();
                        final String memoryString = memoryInput.getText().toString();

                        GetResultId getId = new GetResultId() {
                            @Override
                            public String getResultId() {
                                AddMemory post = new AddMemory();
                                post.execute(location, scope, memoryString);
                                String id = post.getResultId("id");
                                Log.d(null, id);
                                return id;
                            }
                        };
                        Log.d(null, getId.getResultId());

                        Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                        startActivity(i);
//                        finish();
                    }else {
                        Log.d(null, "error");
                        Toast.makeText(getApplicationContext(), "error! oh no!", Toast.LENGTH_LONG).show();                    }
                }
            });
        }
        //setupSnapshot();
    }
//    void setResultId(String resultId){
//        this.resultId = resultId;
//        Log.d(null, "id set");
//    }
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
    JSONObject jsonObject;
    String timestampString;
    int idObject;

    protected void onPreExecute() {
        Log.d(null, "adding memory");

        //add timestamp here
        Calendar timestamp = Calendar.getInstance();
        String y = String.valueOf(timestamp.get(Calendar.YEAR));
        String m = String.valueOf(timestamp.get(Calendar.MONTH));
        String d = String.valueOf(timestamp.get(Calendar.DAY_OF_MONTH));
        String h = String.valueOf(timestamp.get(Calendar.HOUR_OF_DAY));
        String m1 = String.valueOf(timestamp.get(Calendar.MINUTE));
        String s1 = String.valueOf(timestamp.get(Calendar.SECOND));
        String m2 = String.valueOf(timestamp.get(Calendar.MILLISECOND));

        timestampString =  y + ":" + m + ":" + d + ":" + h + ":" + m1 + ":" + s1 + ":" + m2;
        // 0001 is western hemisphere
//        String desc = timestampString + " 0001 " + coordinates;
    }
    @Override
    protected Void doInBackground(String... params) {
        String scope = params[1];
        String location = params[0];
        String memoryString = params[2];
        String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";
        //old key--a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17
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

        String regionCode = "0001";

        ///////////////////////////////////////////////////////////////////////////////////////////
        try {
            JSONObject pageValuesObject = new JSONObject();
            JSONObject regionObject = new JSONObject().put("pageTypeStringAttributesId", "54").put("value", regionCode);
            JSONObject titleObject = new JSONObject().put("pageTypeStringAttributesId", "48").put("value", memoryString);
            JSONObject coorObject = new JSONObject().put("pageTypeStringAttributesId", "51").put("value", coordinates);
            JSONObject timestampObject = new JSONObject().put("pageTypeStringAttributesId", "57").put("value", timestampString);
            pageValuesObject.put("0", regionObject).put("1", titleObject).put("2", coorObject).put("3", timestampObject);
            Log.d(null, pageValuesObject.toString());

            String dataString = URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
                    + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(memoryString, "UTF-8")
                    + "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode(scope, "UTF-8")
                    + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8")
                    + "&" + URLEncoder.encode("pageValues", "UTF-8") + "=" + URLEncoder.encode(pageValuesObject.toString(),"UTF-8");

            URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=addPage&" + dataString);

            Log.d(null, url.toString());
            final URLConnection conn= url.openConnection();
//            final BufferedReader[] in = new BufferedReader[1];
//            DownloadMemoryList.InputReader reader = new DownloadMemoryList.InputReader() {
//                @Override
//                public String getInput() {
//                    try {
//                        in[0] = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return in[0].toString();
//                }
//            };
//            String input = reader.getInput();
//            BufferedReader 	in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((s = in.readLine()) != null) {
//                Log.d(null, s);
//                jsonObject = new JSONObject(s);
//                Log.d(null, "jArray" + s);
//            }
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
                    return null;
                }
            };
            reader.getInput();
            while ((s = in[0].readLine()) != null) {
                jsonObject = new JSONObject(s);
                Log.d(null, jsonObject.toString());
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
        }
        return null;
    }
    protected void onPostExecute(Void v) {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(jsonObject != null){
            try {
                Log.d(null, jsonObject.toString());
                idObject = jsonObject.getInt("id");
                Log.d(null, "id: " + String.valueOf(idObject));

                ViewMemoryActivity view = new ViewMemoryActivity();
                view.setId(String.valueOf(idObject));

                successObject = jsonObject.getString("success");
                Log.d(null, "successObject: " + successObject);
                if (successObject.equals("true")) {
                    Log.d(null, "success true");
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }
        } else { Log.d(null, "null jsonobject");}
    }

    String getResultId(String a) {
        if(a.equals("id")){
            return String.valueOf(idObject);
        } else {
            return null;
        }
    }
}