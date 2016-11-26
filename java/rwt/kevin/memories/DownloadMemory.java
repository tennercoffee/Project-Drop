package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.JsonObject;
import com.google.maps.android.clustering.ClusterManager;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 8/22/2016.
 */

class DownloadMemory extends AsyncTask<String, Void, JsonObject> {
    private String titleObject;
	private JSONArray jsonArray;
	private TextView locationTextView;
	private TextView memoryTextView;
	private TextView timestampTextView;
	private TextView usernameTextView;
	String id;

	protected void onPreExecute() {
        Log.d(null, "loading moment");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        final URLConnection conn;
        URL nUrl;
		id = params[0];
        String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";
		try {
			URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=getPage&");
			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
				+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
			Log.d(null, url.toString() + data);
			nUrl = new URL(url + data);
			conn = nUrl.openConnection();
			BufferedReader 	in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Thread.sleep(100);
			String s;
			while ((s = in.readLine()) != null) {
				Log.d(null, "jArray" + s);
				jsonArray = new JSONArray(s);
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
    protected void onPostExecute(JsonObject obj) {
		Log.d(null, "postexecute");
		String timestamp= null;
		String location;
		LatLng latlngFinal = null;

		try {
			for(int n = 0; n < jsonArray.length(); n++) {
				JSONObject jsonObject = jsonArray.getJSONObject(n);
				if (jsonObject != null) {
					titleObject = jsonObject.getString("title");
					Log.d(null, "title: " + titleObject);

					JSONArray attrArray = jsonObject.getJSONArray("pageTypeValues");
					for (int i = 0; i < attrArray.length(); i++) {
						JSONObject attrObject = (JSONObject) attrArray.get(i);
						String title = attrObject.getString("title");
						if (title.equals("Location")) {
							location = (String) attrObject.get("value");
							String[] latlngArray = location.split(",");
							double latitude = Double.parseDouble(latlngArray[0]);
							double longitude = Double.parseDouble(latlngArray[1]);
							latlngFinal = new LatLng(latitude, longitude);
						}
						if (i == 3) {
							timestamp = (String) attrObject.get("value");
							Log.d(null, timestamp);
						}
					}
				} else {
					Log.d(null, "error");
				}
				String username = "username";
				ViewMemoryActivity view = new ViewMemoryActivity();
//				view.setId(id);
				view.setMemory(timestamp, timestampTextView, latlngFinal, locationTextView, titleObject, memoryTextView, username, usernameTextView);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(null, "downloaded");
	}
	void setTextViews(TextView locationTextView, TextView memoryTextView, TextView timestampTextView, TextView usernameTextView){
		this.locationTextView = locationTextView;
		this.memoryTextView = memoryTextView;
		this.timestampTextView = timestampTextView;
		this.usernameTextView = usernameTextView;
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class DownloadMemoryList extends AsyncTask<String, String, Void> {
	private ArrayList<JSONArray> arrayList = new ArrayList<>();
	private ArrayList<JSONArray> markerList = new ArrayList<>();
	private JSONArray jsonArray;
	private GoogleMap mMap;
	private JSONArray listArray = new JSONArray();
	private ArrayAdapter adapter;
	private ListView listView;

	private JSONArray mapArray;
	JSONObject listObject;
	private ClusterManager cluster;
	private String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";
	private String result = null;



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
			ArrayList<ArrayList> list = map.addMarkersToMap(markerList, mMap);
			map.revealMarkers(mMap, cluster, list);

			MemoryListActivity mem = new MemoryListActivity();
			if(listView != null && adapter != null) {
				mem.setupList(listArray, listView, adapter);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
	void setMap(GoogleMap map, ClusterManager clusterManager, ArrayList<Marker> list){
		this.mMap = map;
		this.cluster = clusterManager;
	}
	JSONArray getListArray(){
		Log.d(null, "return array: " + this.listArray);
		return this.listArray;
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