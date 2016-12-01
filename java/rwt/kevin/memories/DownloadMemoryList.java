package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

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

class DownloadMemoryList extends AsyncTask<String, String, Void> {
    private ArrayList<JSONArray> arrayList = new ArrayList<>();
    private ArrayList<JSONArray> markerList = new ArrayList<>();
    private JSONArray jsonArray;
    private GoogleMap mMap;
    private LatLng myLocation;
    private JSONArray listArray = new JSONArray();
    private ArrayAdapter adapter;
    private ListView listView;

    private JSONArray mapArray;
    JSONObject listObject;
    private ClusterManager cluster;
    private String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";

    interface InputReader{
        String getInput();
    }
    protected void onPreExecute() {
        Log.d(null, "downloading moments");
    }
    @Override
    protected Void doInBackground(String... params) {
        String scope = params[0];
//		result = params[1];
        String s;
        int offset = 0;
        try {
            JSONObject filterObject = new JSONObject();
            JSONObject idfilterObject = new JSONObject().put("combine", "AND").put("field", "pageTypesId").put("option","EQUALS").put("value","30");
            JSONObject scopeFilterObject = new JSONObject().put("combine", "AND").put("field", "scope").put("option","EQUALS").put("value",scope);
            filterObject.put("0",idfilterObject);
            filterObject.put("1",scopeFilterObject);
            String data = "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8")
                    + "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")
                    + "&" + URLEncoder.encode("filters", "UTF-8") + "=" + URLEncoder.encode(filterObject.toString(),"UTF-8");
            do{
                URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=listPages" + data + "&" + URLEncoder.encode("offset", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(offset), "UTF-8"));
                Log.d(null, url.toString());
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
            Log.d(null, String.valueOf(arrayList.size()));
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
            for(int a = 0; a < arrayList.size(); a++){
                JSONArray array = arrayList.get(a);
                Log.d(null, array.toString());
                mapArray = new JSONArray();
                for (int n = 0; n < array.length(); n++) {
                    JSONObject j = array.getJSONObject(n);
                    String locationValue;
                    int idObject = j.getInt("id");
                    String titleObject = j.getString("title");
                    JSONArray attrArray = j.getJSONArray("pageTypeValues");
                    for (int i = 0; i < attrArray.length(); i++) {
                        JSONObject attrObject = (JSONObject) attrArray.get(i);
                        String title = attrObject.getString("title");
                        if (title.equals("Location")) {
                            locationValue = (String) attrObject.get("value");
                            JSONObject markerObject = new JSONObject();
                            markerObject.put("title", idObject).put("location", locationValue);
                            mapArray.put(markerObject);

                            //for memlist
                            listObject = new JSONObject();
                            listObject.put("title", titleObject).put("location", locationValue);
                            listArray.put(listObject);
                        }
                    }
                }
                markerList.add(mapArray);
            }
            MapsActivity map = new MapsActivity();
            ArrayList<ArrayList> list = map.addMarkersToMap(markerList, mMap, myLocation);
            map.revealMarkers(mMap, cluster, list);

            MemoryListActivity mem = new MemoryListActivity();
            if(listView != null && adapter != null) {
                mem.setupList(listArray, listView, adapter);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
    void setMap(GoogleMap map, ClusterManager clusterManager, ArrayList<Marker> list, LatLng myLocation){
        this.mMap = map;
        this.cluster = clusterManager;
        this.myLocation = myLocation;
    }
    void setAdapter(ArrayAdapter adapter){
        if(adapter !=  null) {
            this.adapter = adapter;
        } else {Log.d(null, "null this.adapter");}
    }
    void setListView(ListView listView){
        this.listView = listView;
    }
}