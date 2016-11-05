package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

import static com.google.android.gms.analytics.internal.zzy.a;
import static com.google.android.gms.analytics.internal.zzy.e;

/**
 * Created by Kevin on 8/22/2016.
 */

class DownloadMemory extends AsyncTask<String, Void, JsonObject> {
    private String titleObject;
	private String descObject;
	private JSONArray jsonArray;

	protected void onPreExecute() {
        Log.d(null, "loading moment");
    }

    @Override
    protected JsonObject doInBackground(String... params) {

        URLConnection conn;
        URL nUrl;
        String id = params[0];
		//List<List<String>> resultList = params[1];
        String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";
        String s;

		try {
			URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=getPage&");
			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
				+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");

			Log.d(null, url.toString() + data);
			nUrl = new URL(url + data);
			conn = nUrl.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((s = in.readLine()) != null) {
				jsonArray = new JSONArray(s);
				Log.d(null, "jArray" + s);
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
			//Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
			Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
		}
        return null;
    }
    protected void onPostExecute(JsonObject obj) {
		String timestampString = null;
		LatLng latlngFinal = null;

		for(int n = 0; n < jsonArray.length(); n++) {
			try {
				Log.d(null, String.valueOf(n));
				JSONObject j = jsonArray.getJSONObject(n);

				titleObject = String.valueOf("title");
				Log.d(null, "title: " + titleObject);

				descObject = j.getString("description");
				Log.d(null, "desc: " + descObject);
				
				
    			 /****************************************************
    			 * 
    			 * add rest of json parsing here, (timestamp, regioncode, 
    			 * 
    			 *      get code from downloadmemlist
    			 * 
    			 *****************************************************/
				
				
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(null, "downloaded");

			TextScanner t = new TextScanner();
			Log.d(null, "scanner");
			if(descObject != null) {
				String latlngString = t.descSplitter(descObject, 1, 0);
				String latlngSplitString = t.locationSplitter(latlngString);

				String[] latlng = latlngSplitString.split(",");
				Log.d(null, "split string");

				double latitude = Double.parseDouble(latlng[0]);
				double longitude = Double.parseDouble(latlng[1]);
				latlngFinal = new LatLng(latitude, longitude);
				Log.d(null, latlngFinal.toString());

				 timestampString = t.descSplitter(descObject, 1, 1);
			}
			//create method in viewmemory to set obj
			ViewMemoryActivity v = new ViewMemoryActivity();
			v.setMemory(timestampString, latlngFinal, titleObject);
		}
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class DownloadMemoryList extends AsyncTask<String, String, Void> {
//	public List<List<String>> resultList = new ArrayList<>();
	String descObject;
	int idObject;
	String titleObject;
	String locationObject;
	private JSONArray jsonArray;
	URL url;
	String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";

	public interface InputReader{
		String getInput();
	}

	protected void onPreExecute() {
		Log.d(null, "downloading moments");
	}
	@Override
	protected Void doInBackground(String... params) {
		String s;
		final URLConnection conn;

		//http://web.webapps.centennialarts.com/page.php?command=listPages&limit=100&filters={%220%22:{%22combine%22:%22AND%22,%22field%22:%22pageTypesId%22,%22option%22:%22EQUALS%22,%22value%22:%2230%22}}
		try {
			JSONObject filterObject = new JSONObject();
			JSONObject idfilterObject = new JSONObject().put("combine", "AND").put("field", "pageTypesId").put("option","EQUALS").put("value","30");
			filterObject.put("0",idfilterObject);

			String data = "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8")
					+ "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")
					+ "&" + URLEncoder.encode("filters", "UTF-8") + "=" + URLEncoder.encode(filterObject.toString(),"UTF-8");

			url = new URL("http://web.webapps.centennialarts.com/page.php?command=listPages" + data);
			Log.d(null, url.toString());
			conn = url.openConnection();

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
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.d(null, "malformed url");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(null, "io exception");
		} catch (JSONException e) {
			Log.d(null, "json exception");
			e.printStackTrace();
		}
		return null;
	}
	protected void onPostExecute(Void v) {
		Log.d(null, "postExecute");
		//Log.d(null, jsonArray.toString());

		try{
			for(int n = 0; n < jsonArray.length(); n++) {
				JSONObject j = jsonArray.getJSONObject(n);
				String locationValue = null;
				idObject = j.getInt("id");

				JSONArray attrArray = j.getJSONArray("pageTypeValues");
				for(int i = 0; i < attrArray.length(); i++) {
					JSONObject attrObject = (JSONObject) attrArray.get(i);
					String title = attrObject.getString("title");
					if (title.equals("Location")){
						locationValue = (String) attrObject.get("value");
					} else{

					}
//					JSONObject o = attrArray.getJSONObject(i);
//					o.getJSONObject()
					//locationObject = o.getString("Location");
				}
				Log.d(null, String.valueOf(idObject) + " " + locationValue);

				MapsActivity m = new MapsActivity();
				m.addMarkerToList(locationValue, String.valueOf(idObject));

			}
	} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
}