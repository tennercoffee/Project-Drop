package rwt.kevin.memories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.JsonObject;
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
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;

class Memory {
    String getTimeStamp(){
        String timestampString;
        Calendar timestamp = Calendar.getInstance();
        String y = String.valueOf(timestamp.get(Calendar.YEAR));
        String m = String.valueOf(timestamp.get(Calendar.MONTH));
        String d = String.valueOf(timestamp.get(Calendar.DAY_OF_MONTH));
        String h = String.valueOf(timestamp.get(Calendar.HOUR_OF_DAY));
        String m1 = String.valueOf(timestamp.get(Calendar.MINUTE));
        String s1 = String.valueOf(timestamp.get(Calendar.SECOND));
        String m2 = String.valueOf(timestamp.get(Calendar.MILLISECOND));
        timestampString =  y + ":" + m + ":" + d + ":" + h + ":" + m1 + ":" + s1 + ":" + m2;
        return timestampString;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class AddMemory extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject = new JSONObject();
    private JSONObject successJSONObject = new JSONObject();

    protected void onPreExecute() {
        Log.d(null, "adding memory");
    }
    @Override
    protected Void doInBackground(String... params) {

        String atlasIdString = params[0];
        String latitude = params[1];
        String longitude = params[2];
        String color = params[3];
        String scope = params[4];
        String visibility = params[5];
        String timeStamp = params[6];
        String memoryString = params[7];
        String key = params[8];
        String urlString = params[9];

        //build addmemfunctions
        try {
            JSONObject addMemoryJSONObject = buildAddMemoryJSONObject(memoryString, latitude, longitude, timeStamp, color, visibility);
            String addMemoryDataString = buildAddMemoryDataString(addMemoryJSONObject,memoryString,scope,atlasIdString,key);
            URL addMemoryUrl = buildAddMemoryUrl(urlString, addMemoryDataString);
            successJSONObject = callURL(addMemoryUrl,jsonObject);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        getAddMemorySuccessJSONObject(successJSONObject);
    }
    private void getAddMemorySuccessJSONObject(JSONObject successJSONObject) {
        if (successJSONObject != null) {
            try {
                int idObject = successJSONObject.getInt("id");
                String successObject = successJSONObject.getString("success");
                if (successObject.equals("true")) {
                    AddMemoryActivity ama = new AddMemoryActivity();

                    //TODO: set id for image upload
                    ama.setId(String.valueOf(idObject));
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }
        } else {
            Log.d(null, "null jsonobject");
        }
    }
    private JSONObject callURL(URL addMemoryUrl, JSONObject jsonObject) throws IOException, InterruptedException, JSONException {
        final URLConnection conn= addMemoryUrl.openConnection();
        Thread.sleep(500);
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
        reader.getInput();
        String inputString;
        while ((inputString = in[0].readLine()) != null) {
            jsonObject = new JSONObject(inputString);
        }
        return jsonObject;
    }
    private URL buildAddMemoryUrl(String urlString, String addMemoryDataString) throws MalformedURLException {
        URL addMemoryUrl = new URL(urlString + "/page.php?command=addPage&" + addMemoryDataString);
        Log.d(null, addMemoryUrl.toString());
        return addMemoryUrl;
    }
    private String buildAddMemoryDataString(JSONObject addMemoryJSONObject, String memoryString, String scope, String atlasIdString, String key) throws UnsupportedEncodingException {
        return URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(memoryString, "UTF-8")
                + "&" +URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
                + "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode(scope, "UTF-8")
                + "&" + URLEncoder.encode("atlasId", "UTF-8") + "=" + URLEncoder.encode(atlasIdString, "UTF-8")
                + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8")
                + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
                + "&" + URLEncoder.encode("pageValues", "UTF-8") + "=" + URLEncoder.encode(addMemoryJSONObject.toString(),"UTF-8");
    }
    private JSONObject buildAddMemoryJSONObject(String memoryString, String latitude, String longitude, String timeStamp, String color, String visibility)
            throws UnsupportedEncodingException, JSONException {
        JSONObject addMemoryJSONObject = new JSONObject();
        JSONObject titleObject = new JSONObject().put("pageTypeStringAttributesId", "26").put("value", URLEncoder.encode(memoryString, "UTF-8"));
        JSONObject latObject = new JSONObject().put("pageTypeStringAttributesId", "29").put("value", latitude);
        JSONObject lngObject = new JSONObject().put("pageTypeStringAttributesId", "32").put("value", longitude);
        JSONObject timeObject = new JSONObject().put("pageTypeStringAttributesId", "35").put("value", timeStamp);
        JSONObject colorObject = new JSONObject().put("pageTypeStringAttributesId", "38").put("value", color);
        JSONObject visibilityObject = new JSONObject().put("pageTypeStringAttributesId", "41").put("value", visibility);
        addMemoryJSONObject.put("0", titleObject).put("1", timeObject).put("2", latObject).put("3", lngObject).put("4",colorObject).put("5",visibilityObject);
        return addMemoryJSONObject;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class DownloadMemory extends AsyncTask<String, Void, JsonObject> {
    private TextView locationTextView,memoryTextView,timestampTextView,usernameTextView,ownerTextView;
    private String titleObject,atlasAccessKey,ownerId,atlasUrlString;
    private JSONArray jsonArray;
    private Context context;
    private Button removeMemoryButton;

    protected void onPreExecute() {
        Log.d(null, "loading moment");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        String id = params[0];
        String caAccessKey = params[1];
        String urlString = params[2];
        atlasAccessKey = params[3];
        atlasUrlString = params[4];

        try {
            URL downloadMemoryUrl = buildDownloadMemoryUrl(urlString, id, caAccessKey);
            jsonArray = callDownloadMemoryUrl(downloadMemoryUrl);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(JsonObject obj) {
        getDownloadMemorySuccessJSONObject(jsonArray);
    }
    private void getDownloadMemorySuccessJSONObject(JSONArray jsonArray) {
        Log.d(null, "memory downloaded");
        String timestamp= null;
        LatLng latlngFinal = null;
        try {
            for(int n = 0; n < jsonArray.length(); n++) {
                String visibility = null;
                String latitudeValue = null;
                String longitudeValue = null;
                JSONObject jsonObject = jsonArray.getJSONObject(n);
                if (jsonObject != null) {
                    titleObject = jsonObject.getString("title");
                    ownerId = jsonObject.getString("atlasId");
                    JSONArray attrArray = jsonObject.getJSONArray("pageTypeValues");
                    for (int i = 0; i < attrArray.length(); i++) {
                        JSONObject attrObject = (JSONObject) attrArray.get(i);
                        String title = attrObject.getString("title");
                        if (title.equals("latitude")) {
                            latitudeValue = (String) attrObject.get("value");
                        }
                        if (title.equals("longitude")) {
                            longitudeValue = (String) attrObject.get("value");
                        }
                        if (title.equals("visibility")) {
                            visibility = (String) attrObject.get("value");
                        }
                        if (title.equals("timeStamp")) {
                            timestamp = (String) attrObject.get("value");
                        }
                        if (latitudeValue != null && longitudeValue != null) {
                            latlngFinal = new LatLng(Double.parseDouble(latitudeValue), Double.parseDouble(longitudeValue));
                        }
                    }
                } else {
                    Log.d(null, "error downloading memory");
                }
                //if user chose to display username
                    //username = ownerId
                //else
                    //username = anonymous
                String username = null;
                if (visibility != null && visibility.equals("v")){
                    username = ownerId;
                } else if (visibility != null && visibility.equals("i")){
                    username = "anonymous";
                }
                ViewMemoryActivity view = new ViewMemoryActivity();
                view.setMemory(atlasAccessKey, atlasUrlString, timestamp, timestampTextView, latlngFinal, locationTextView,
                        titleObject, memoryTextView, username, usernameTextView, removeMemoryButton, ownerId,ownerTextView, context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(null, "downloaded");
    }
    private JSONArray callDownloadMemoryUrl(URL removeMemoryUrl) throws IOException, JSONException {
        final URLConnection conn = removeMemoryUrl.openConnection();
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
        String s;
        while ((s = in[0].readLine()) != null) {
            jsonArray = new JSONArray(s);
        }
        return jsonArray;
    }
    private URL buildDownloadMemoryUrl(String urlString, String id, String caAccessKey) throws MalformedURLException, UnsupportedEncodingException {
        String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caAccessKey, "UTF-8");
        return new URL(urlString + "/page.php?command=getPage&" + data);
    }
    void setViewMemoryViews(TextView locationTextView, TextView memoryTextView, TextView timestampTextView,
                            TextView usernameTextView, Button removeMemoryButton, TextView ownerTextView){
        this.locationTextView = locationTextView;
        this.memoryTextView = memoryTextView;
        this.timestampTextView = timestampTextView;
        this.usernameTextView = usernameTextView;
        this.removeMemoryButton = removeMemoryButton;
        this.ownerTextView = ownerTextView;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class RemoveMemory extends AsyncTask<String, Void, JsonObject> {
    private JSONObject jsonObject;
    private JSONObject removeMemorySuccessJSONObject;
    URL url;

    protected void onPreExecute() {
        Log.d(null, "removing moment");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        //do in background removemem
        String caAccessKey = params[0];
        String id = params[1];
        String urlString = params[2];

        try {
            URL removeMemoryUrl = buildRemoveMemoryUrl(urlString, id, caAccessKey);
            removeMemorySuccessJSONObject = callRemoveMemoryURL(removeMemoryUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(JsonObject obj) {
        getRemoveMemorySuccessJSONObject(removeMemorySuccessJSONObject);
    }
    private void getRemoveMemorySuccessJSONObject(JSONObject removeMemorySuccessJSONObject) {
        Log.d(null, "memory removed");
        if (removeMemorySuccessJSONObject != null) {
            try {
                String successObject = removeMemorySuccessJSONObject.getString("success");
                if (successObject.equals("true")) {
                    Log.d(null, "success");
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }
        } else {
            Log.d(null, "null jsonobject");
        }
    }
    private JSONObject callRemoveMemoryURL(URL removeMemoryUrl) throws IOException, JSONException {
        final URLConnection conn = removeMemoryUrl.openConnection();
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
        String s;
        while ((s = in[0].readLine()) != null) {
            jsonObject = new JSONObject(s);
        }
        return jsonObject;
    }
    private URL buildRemoveMemoryUrl(String urlString, String id, String caAccessKey) throws UnsupportedEncodingException, MalformedURLException {
        String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caAccessKey, "UTF-8");
        URL removeMemoryUrl = new URL(urlString + "/page.php?command=removePage&" + data);
        Log.d(null, removeMemoryUrl.toString());
        return removeMemoryUrl;
    }
}