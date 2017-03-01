package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

class DownloadMemory extends AsyncTask<String, Void, JsonObject> {
    private String titleObject;
	private JSONArray jsonArray;
	private TextView locationTextView;
	private TextView memoryTextView;
	private TextView timestampTextView;
	private TextView usernameTextView;
	private ImageView memoryImageView;
	String memoryId, caAccessKey, atlasAccessKey, atlasId, username, webUrlString, atlasUrlString;

	protected void onPreExecute() {
        Log.d(null, "loading moment");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        final URLConnection conn;
        URL nUrl;
		memoryId = params[0];
		caAccessKey = params[1];
		webUrlString = params[2];
		atlasAccessKey = params[3];
		atlasUrlString = params[4];

		try {
			URL url = new URL(webUrlString + "/page.php?command=getPage&");
			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(memoryId, "UTF-8")
				+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caAccessKey, "UTF-8");
			nUrl = new URL(url + data);
			Log.d(null, nUrl.toString());
			conn = nUrl.openConnection();
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
				Log.d(null, s);
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
		LatLng latlngFinal = null;
		try {
			for(int n = 0; n < jsonArray.length(); n++) {
				String visibility = null;
				String latitudeValue = null;
				String longitudeValue = null;
				JSONObject jsonObject = jsonArray.getJSONObject(n);
				if (jsonObject != null) {
					titleObject = jsonObject.getString("title");
					visibility = jsonObject.getString("description");
					atlasId = jsonObject.getString("atlasId");
					JSONArray attrArray = jsonObject.getJSONArray("pageTypeValues");
					Log.d(null, attrArray.toString());
					for (int i = 0; i < attrArray.length(); i++) {
						JSONObject attrObject = (JSONObject) attrArray.get(i);
						String title = attrObject.getString("title");
						if (title.equals("latitude")) {
							latitudeValue = (String) attrObject.get("value");
						}
						if (title.equals("longitude")) {
							longitudeValue = (String) attrObject.get("value");
						}
						if (title.equals("timeStamp")) {
							timestamp = (String) attrObject.get("value");
						}
						if (latitudeValue != null && longitudeValue != null) {
							latlngFinal = new LatLng(Double.parseDouble(latitudeValue), Double.parseDouble(longitudeValue));
						}
					}
				} else {
					Log.d(null, "error");
				}
				username = null;
				if (visibility != null && visibility.equals("v")){
					username = atlasId;
				} else if (visibility != null && visibility.equals("i")){
					username = "anonymous";
				}
				ViewMemoryActivity view = new ViewMemoryActivity();
				view.setMemory(atlasAccessKey, atlasUrlString, timestamp, timestampTextView, latlngFinal, locationTextView, titleObject, memoryTextView, username, usernameTextView  /*,memoryImageView, memoryImageURI*/ );
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(null, "downloaded");
	}
	void setViews(TextView locationTextView, TextView memoryTextView, TextView timestampTextView, TextView usernameTextView, ImageView memoryImageView){
		this.locationTextView = locationTextView;
		this.memoryTextView = memoryTextView;
		this.timestampTextView = timestampTextView;
		this.usernameTextView = usernameTextView;
		this.memoryImageView = memoryImageView;
	}
}