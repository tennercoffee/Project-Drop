package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

class DownloadMemoryList extends AsyncTask<String, String, Void> {
    private ArrayList<JSONArray> arrayList = new ArrayList<>();
    private ArrayList<JSONArray> markerList = new ArrayList<>();
    private JSONArray jsonArray;
    private LatLng myLocation;
    private ArrayAdapter adapter;
    private ListView listView;

    interface InputReader{
        String getInput();
    }
    protected void onPreExecute() {
        Log.d(null, "downloading moments");
    }
    @Override
    protected Void doInBackground(String... params) {
        String scope = params[0];
        String key = params[1];
        String urlString = params[2];
        String s;
        int offset = 0;

        try {
            do{
                JSONObject filterObject = new JSONObject();
                JSONObject idfilterObject = new JSONObject().put("combine", "AND").put("field", "pageTypesId").put("option","EQUALS").put("value","20");
                JSONObject scopeFilterObject = new JSONObject().put("combine", "AND").put("field", "scopeString").put("option","EQUALS").put("value",scope);
                filterObject.put("0",idfilterObject);
                filterObject.put("1",scopeFilterObject);
                String data = "&" + URLEncoder.encode("accessKeyString", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
                    + "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")
                    + "&" + URLEncoder.encode("offset", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(offset), "UTF-8")
                    + "&" + URLEncoder.encode("filters", "UTF-8") + "=" + URLEncoder.encode(filterObject.toString(),"UTF-8");
                URL url = new URL(urlString + "/page.php?command=listPages" + data /*+ "&" */);
                Log.d(null, url.toString());
                Thread.sleep(100);

                final URLConnection conn = url.openConnection();
                final BufferedReader[] in = new BufferedReader[1];
                InputReader reader = new InputReader() {
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
                    jsonArray = new JSONArray(s);
                    arrayList.add(jsonArray);
                }
                offset += 100;
                Thread.sleep(150);
            } while (jsonArray.length() >= 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(null, "malformed url");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(null, "io exception");
        } catch (JSONException e) {
            Log.d(null, "json exception");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "postExecute");
        try {
            JSONArray mapArray;
            for (int a = 0; a < arrayList.size(); a++) {
                JSONArray array = arrayList.get(a);
                mapArray = new JSONArray();
                for (int n = 0; n < array.length(); n++) {
                    JSONObject j = array.getJSONObject(n);
                    String latitudeValue = null;
                    String longitudeValue = null;
                    int idObject = j.getInt("idString");
                    JSONArray attrArray = j.getJSONArray("pageTypeValues");
                    for (int i = 0; i < attrArray.length(); i++) {
                        JSONObject attrObject = (JSONObject) attrArray.get(i);
                        String title = attrObject.getString("titleString");
                        if (title.equals("Latitude")) {
                            latitudeValue = (String) attrObject.get("value");
                        }
                        if (title.equals("Longitude")) {
                            longitudeValue = (String) attrObject.get("value");
                        }
                    }
                    if (latitudeValue != null && longitudeValue != null) {
                        JSONObject markerObject = new JSONObject();
                        markerObject.put("idString", idObject).put("latitude", latitudeValue).put("longitude", longitudeValue);
                        mapArray.put(markerObject);
                    } else {
                        Log.d(null, "latitude and logitude-bad");
                    }
                }
                markerList.add(mapArray);
                Log.d(null, "markerlist" + markerList.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO:setup list here
        MemoryListActivity m = new MemoryListActivity();
    }
    void setAdapter(ArrayAdapter adapter){
        if(adapter !=  null) {
            this.adapter = adapter;
        } else {Log.d(null, "null this.adapter");}
    }
    void setMyLocation(LatLng location) {
        this.myLocation = location;
    }
    void setListView(ListView listView){
        this.listView = listView;
    }
    void setMapCorners(LatLng location) {
        this.myLocation = location;
    }
}