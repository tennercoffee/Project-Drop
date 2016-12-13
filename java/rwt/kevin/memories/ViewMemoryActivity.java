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
		} else if (location == null && locationTextView != null) {
			locationTextView.setText("error getting location");
		} else if (locationTextView == null) {
			Log.d(null, "textview null");
		}
		if(timestampTextView != null) {
			timestampTextView.setText(timestamp);
			Log.d(null, timestamp + " set");
		}
		if (usernameTextView != null && username != null) {
			Log.d(null, username);
			String usernameText = "Posted By: " + username;
			usernameTextView.setText(usernameText);
		}
    }
}