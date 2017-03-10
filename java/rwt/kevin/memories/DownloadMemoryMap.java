package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
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

class DownloadMemoryMap extends AsyncTask<String, String, Void> {
    private ArrayList<JSONArray> jsonPageArrayList = new ArrayList<>();
    private ArrayList<JSONArray> markerList = new ArrayList<>();
    private JSONArray jsonArray;
    private GoogleMap mMap;
    private LatLng myLocation;
    private ClusterManager cluster;

    interface InputReader{
        String getInput();
    }
    protected void onPreExecute() {
        Log.d(null, "downloading moments");
    }
    @Override
    protected Void doInBackground(String... params) {
        String scope,swLat,neLat,swLng,neLng,key,urlString,s;
        scope = params[0];
        neLat = params[1];
        neLng = params[2];
        swLat = params[3];
        swLng = params[4];
        key = params[5];
        urlString = params[6];
        Log.d(null, "corners: swlat-" + swLat + " nelat-" + neLat + " swlng-" + swLng + " nelng-" + neLng);

        try {
            int offset = 0;
            do{
                JSONObject filterObject = new JSONObject();
                JSONObject idfilterObject = new JSONObject().put("combine", "AND").put("field", "pageTypesId").put("option","EQUALS").put("value","20");
                JSONObject scopeFilterObject = new JSONObject().put("combine", "AND").put("field", "scope").put("option","EQUALS").put("value",scope);
//                JSONObject swLatitudeFilterObject, neLatitudeFilterObject, neLongitudeFilterObject, swLongitudeFilterObject;
//                if (Integer.parseInt(neLng) > Integer.parseInt(swLng)) {
//                    swLongitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Longitude").put("option", "GREATERTHEN").put("value", swLng);
//                    neLongitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Longitude").put("option", "LESSTHEN").put("value", neLng);
//                } else {
//                    swLongitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Longitude").put("option", "LESSTHEN").put("value", swLng);
//                    neLongitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Longitude").put("option", "GREATERTHEN").put("value", neLng);
//                }
//                if (Integer.parseInt(neLat) > Integer.parseInt(swLat)) {
//                    swLatitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Latitude").put("option", "GREATERTHEN").put("value", swLat);
//                    neLatitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Latitude").put("option", "LESSTHEN").put("value", neLat);
//                } else {
//                    swLatitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Latitude").put("option", "LESSTHEN").put("value", swLat);
//                    neLatitudeFilterObject = new JSONObject().put("combine", "AND").put("field", "Latitude").put("option", "GREATERTHEN").put("value", neLat);
//                }
                filterObject.put("0",idfilterObject)
                            .put("1",scopeFilterObject);
//                            .put("2",swLatitudeFilterObject)
//                            .put("3",swLongitudeFilterObject)
//                            .put("4",neLatitudeFilterObject)
//                            .put("5",neLongitudeFilterObject);
                String data = "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
                        + "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")
                        + "&" + URLEncoder.encode("offset", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(offset), "UTF-8")
                        + "&" + URLEncoder.encode("filters", "UTF-8") + "=" + URLEncoder.encode(filterObject.toString(),"UTF-8");
                URL url = new URL(urlString + "/page.php?command=listPages" + data);
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
                    jsonPageArrayList.add(jsonArray);
                }
                offset += 100;
                Thread.sleep(150);
            } while (jsonArray.length() > 1);
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
            for (int a = 0; a < jsonPageArrayList.size(); a++) {
                JSONArray array = jsonPageArrayList.get(a);
                mapArray = new JSONArray();
                for (int n = 0; n < array.length(); n++) {
                    JSONObject j = array.getJSONObject(n);
                    String latitudeValue = null;
                    String longitudeValue = null;
                    String colorValue = null;
                    int idObject = j.getInt("id");
                    JSONArray attrArray = j.getJSONArray("pageTypeValues");
                    for (int i = 0; i < attrArray.length(); i++) {
                        JSONObject attrObject = (JSONObject) attrArray.get(i);
                        String title = attrObject.getString("title");
                        if (title.equals("latitude")) {
                            latitudeValue = (String) attrObject.get("value");
                        }
                        if (title.equals("longitude")) {
                            longitudeValue = (String) attrObject.get("value");
                        }
                        if (title.equals("color")) {
                            colorValue = (String) attrObject.get("value");
                        }
                        if (colorValue == null) {
                            colorValue = "red";
                        }
                    }
                    if (latitudeValue != null && longitudeValue != null) {
                        JSONObject markerObject = new JSONObject();
                        markerObject.put("id", idObject).put("latitude", latitudeValue).put("longitude", longitudeValue).put("color", colorValue);
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
        MapsActivity map = new MapsActivity();
        ArrayList<ArrayList> list = map.addMarkersToMapArray(markerList, mMap);
        map.showMarkersArrayOnMap(mMap, cluster, list);
    }
    void setMap(GoogleMap map, ClusterManager clusterManager, ArrayList<Marker> list, LatLng myLocation){
        this.mMap = map;
        this.cluster = clusterManager;
        this.myLocation = myLocation;
    }
}