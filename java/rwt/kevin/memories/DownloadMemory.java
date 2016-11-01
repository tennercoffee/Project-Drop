package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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
	private JSONArray jsonArray;
	URL url;
	String caccessKey = "a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17";

	protected void onPreExecute() {
		Log.d(null, "downloading moments");
	}
	@Override
	protected Void doInBackground(String... params) {
		String s;
		URLConnection conn;

		try {
			url = new URL("http://web.webapps.centennialarts.com/page.php?command=listPages"
					+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8")
					+ "&filters={"
					+ "\"0\"" + ":{" + "\"combine\":" + "\"AND\"," + "\"field\":" + "\"pageTypesId\"," + "\"option\":" + "\"EQUALS\"," + "\"value\":" + "\"75\"" + "},"
					+ "\"1\"" + ":{" + "\"combine\":" + "\"AND\"," + "\"field\":" + "\"scope\"," + "\"option\":" + "\"EQUALS\"," + "\"value\":" + "\"" + "public" + "\"" + "}}");

			Log.d(null, url.toString());
			conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Log.d(null, "in");
			
			
			/***************************************************************************************
			 * 
			 * 
			 * Design callback funtion here
			 *  idk what that means
			 * 
			 * 
			 * ***************************************************************************************/
			
			
			while ((s = in.readLine()) != null) {
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
		Log.d(null, jsonArray.toString());

		/*try{
			for(int n = 0; n < jsonArray.length(); n++) {
				JSONObject j = jsonArray.getJSONObject(n);

				idObject = j.getInt("id");
				descObject = j.getString("description");
				
				JsonArray attrArray = j.getJsonArray("pageTypeAttributes");
				JsonObject locationObject = attrArray.getJsonObject("Location");
				
				MapsActivity m = new MapsActivity();
				m.addMarkerToList(descObject, String.valueOf(idObject));
				
				/****************************************************
    			 * 
    			 * just get location here, instead of all desc.
    			 * this may or may not work
    			 * 
    			 *****************************************************/

			Log.d(null, "list downloaded");
			}

/*
		} catch (JSONException e) {
			Log.d(null, "json exception");
			e.printStackTrace();
		}
	}
*/
}