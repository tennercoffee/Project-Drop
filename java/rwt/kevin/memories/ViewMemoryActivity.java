package rwt.kevin.memories;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ViewMemoryActivity extends MapsActivity {
	public String title;
	public String timestamp;
	public LatLng location;
	public String username;
	String id;
	public TextView locationTextView;
	public TextView timestampTextView;
	public TextView memoryTextView;
	public TextView usernameTextView;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		setContentView(R.layout.activity_view_memory);
		Toolbar toolbar = (Toolbar) findViewById(R.id.view_memory_toolbar);
		if(toolbar != null){
			toolbar.setTitle("View Moment");
		}
		locationTextView = (TextView)findViewById(R.id.locationTextView);
		memoryTextView = (TextView) findViewById(R.id.memoryTextView);
		timestampTextView = (TextView) findViewById(R.id.timestamp_view);
		usernameTextView = (TextView) findViewById(R.id.usernameText);
		if (id == null) {
			Intent intent = getIntent();
			id = intent.getStringExtra("id");
			Log.d(null, id);
			loadMemory(id);
		} else {
			Log.d(null, "null id");
		}
//		/*final Button removeButton = (Button) findViewById(R.id.removeButton);
//		if(removeButton != null){
//			removeButton.setOnClickListener( new View.OnClickListener(){
//				@Override
//				public void onClick(View view){
//
//					new AlertDialog.Builder(getApplicationContext())
//							.setTitle("Confirm")
//							.setMessage("Do you really want delete this Moment?")
//							.setIcon(android.R.drawable.ic_dialog_alert)
//							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int whichButton) {
//									Toast.makeText(ViewMemoryActivity.this, "Gone!", Toast.LENGTH_SHORT).show();
//
//									RemoveMemory rm = new RemoveMemory();
//									rm.execute(id);
//									finish();
//								}
//							})
//							.setNegativeButton(android.R.string.no, null).show();
//				}
//			});
//		}*/
//
////		Button dislikeButton = (Button) findViewById(R.id.dislikeButton);
////		if(dislikeButton != null) {
////			dislikeButton.setOnClickListener(new View.OnClickListener() {
////				@Override
////				public void onClick(View view) {
////					//TODO: enter code for rating system
////					//thumbs up/down?
////					//or variation on principle
////					//i like reddit's upvote system
////					//add xml
////					//build async program to handle ratings system
////				}
////			});
////		}
////		Button likeButton = (Button) findViewById(R.id.likeButton);
////		if(likeButton != null) {
////			likeButton.setOnClickListener(new View.OnClickListener() {
////				@Override
////				public void onClick(View view) {
////					//TODO: enter code for rating system
////					//thumbs up/down?
////					//or variation on principle
////					//i like reddit's upvote system
////					//add xml
////				}
////			});
////		}
		Button backButton = (Button) findViewById(R.id.backButton);
		if(backButton != null){
			backButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
				}
			});
		}
    }
    public void loadMemory(String id) {
		DownloadMemory dm = new DownloadMemory();
		dm.setTextViews(locationTextView, memoryTextView, timestampTextView, usernameTextView);
		dm.execute(id);
    }
	void setId(String id){
		this.id = id;
	}
    public void setMemory(String timestampObject, TextView timestampTextView, LatLng locationObject, TextView locationTextView, String titleObject, TextView memoryTextView, String usernameObject, TextView usernameTextView) {
		this.timestamp = timestampObject;
		this.location = locationObject;
		this.title = titleObject;
		this.username = usernameObject;

		Log.d(null, timestamp + " " + location + " " + title + " " + username + "--- setmem");

		this.locationTextView = locationTextView;
		this.memoryTextView = memoryTextView;
		this.timestampTextView = timestampTextView;
		this.usernameTextView = usernameTextView;

		if(memoryTextView != null) {
			memoryTextView.setText(title);
			Log.d(null, title + " set");
		} else if (title == null) {
			Log.d(null, "null title");
		} else { Log.d(null, "no text view");}
		if(locationTextView != null && location != null) {
			locationTextView.setText(location.toString());
			Log.d(null, location + " set");
		} else if (location == null) {
			locationTextView.setText("error getting location");
		} else if (locationTextView == null) {
			Log.d(null, "textview null");
		}
		if(timestampTextView != null) {
			timestampTextView.setText(timestamp);
			Log.d(null, timestamp + " set");
		}
		if (usernameTextView != null && username != null) {
			usernameTextView.setText(username);
		}
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class RemoveMemory extends AsyncTask<String, Void, Void> {
	//private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
	InputStream inputStream = null;
	String result = "";
	URLConnection conn;
	BufferedReader in;
	JSONObject successObject;

	protected void onPreExecute() {
        /*progressDialog.setMessage("Downloading Moments...");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                DownloadMarkers.this.cancel(true);
            }
        });*/
		Log.d(null, "downloading moments");
	}
	@Override
	protected Void doInBackground(String... params) {
		String id = params[0];
		String caccessKey = "a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17";
		String s;

		try {
			URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=removePage&");

			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
					+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
			Log.d(null, "data and url");
			URL nUrl = new URL(url + data);
			Log.d(null, "time to connect");
			conn = nUrl.openConnection();
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
		}
		try {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((s = in.readLine()) != null) {
				//parse my json here
				Log.d(null, s);

				JSONObject jResult = new JSONObject(s);
				//get success message
				//if true, give toast message and finish()
				//if false, give toast, and bounce back to viewMemory
				successObject = jResult.getJSONObject("success");
				if (successObject.equals("true")) {
					Log.d(null, "moment deleted");
					return null;
				}else{
					Log.d(null, "failed upload");
					return null;
				}
			}
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

	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class RateMemory extends AsyncTask<String, Void, Void> {
	InputStream inputStream = null;
	String result = "";
	URLConnection conn;
	BufferedReader in;
	JSONObject successObject;

	protected void onPreExecute() {
		Log.d(null, "rating async");
	}
	@Override
	protected Void doInBackground(String... params) {
		String id = params[0];
		String caccessKey = "a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17";
		String s;


		//here we will update page values/moment values
		/*try {
			URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=removePage&");

			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
					+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
			URL nUrl = new URL(url + data);
			conn = nUrl.openConnection();
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
		}*/
		try {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((s = in.readLine()) != null) {
				//parse my json here
				Log.d(null, s);

				/*JSONObject jResult = new JSONObject(s);
				//get success message
				//if true, give toast message and finish()
				//if false, give toast, and bounce back to viewMemory
				successObject = jResult.getJSONObject("success");
				if (successObject.equals("true")) {
					Log.d(null, "moment deleted");
					return null;
				}else{
					Log.d(null, "failed upload");
					return null;
				}*/
			}
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
		//update viewmemoryactivity with current rating
	}
}