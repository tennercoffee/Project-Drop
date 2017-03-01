package rwt.kevin.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class ViewMemoryActivity extends MapsActivity implements View.OnClickListener{
	public String timestampString, titleString, username, id, atlasAppToken, ownerId, locationString, time;
	public TextView usernameTextView, memoryTextView, locationTextView, timestampTextView;
	public LatLng location;
	Button backButton;
	ImageView memoryImageView;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_memory);
		Toolbar toolbar = (Toolbar) findViewById(R.id.view_memory_toolbar);
		if(toolbar != null){
			toolbar.setTitle("View Moment");
			setSupportActionBar(toolbar);
			android.support.v7.app.ActionBar actionBar = getSupportActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		//setup views && buttons
		locationTextView = (TextView)findViewById(R.id.location_textview);
		memoryTextView = (TextView) findViewById(R.id.memory_textview);
		timestampTextView = (TextView) findViewById(R.id.timestamp_view);
		usernameTextView = (TextView) findViewById(R.id.username_textview);
		memoryImageView = (ImageView) findViewById(R.id.memory_image_view);
		atlasAppToken = getString(R.string.atlas_app_token);

		//test marker code
		if (id == null) {
			id = getIntent().getStringExtra("idString");
			username = getIntent().getStringExtra("usernameString");
			if(!id.equals("test")) {
				loadMemory(id);
			} else if (id.equals("test")) {
				//example for test marker
				titleString = id;
				locationString = getIntent().getStringExtra("location");
				AddMemoryActivity a = new AddMemoryActivity();
				time = a.getTimeStamp();

				locationTextView.setText(location.toString());
				memoryTextView.setText(titleString);
				timestampTextView.setText(time);
				usernameTextView.setText(username);
				memoryImageView.setImageURI(null);
			}
		} else {
			Log.d(null, "null idString");
		}
    }
	void setId(String id){
		this.id = id;
	}
    public void loadMemory(String id) { //download marker information
		DownloadMemory dm = new DownloadMemory();
		dm.setViews(locationTextView, memoryTextView, timestampTextView, usernameTextView, memoryImageView);
//		dm.setUsername(ownerId, usernameTextView);
		dm.execute(id,getString(R.string.ca_access_key), getString(R.string.ca_access_url),getString(R.string.atlas_app_token),getString(R.string.atlas_access_url));
    }
    public void setMemory(String atlasToken, String atlasUrlString, String timestampObject, TextView timestampTextView, LatLng locationObject, TextView locationTextView
			, String titleObject, TextView memoryTextView , String usernameObject, TextView usernameTextView /*, ImageView memoryImageView, URI memoryImageURI*/) {
        //set information from asynctask(downloadmem)
		this.timestampString = timestampObject;
		this.location = locationObject;
		this.titleString = titleObject;
		this.username = usernameObject;
		this.locationTextView = locationTextView;
		this.memoryTextView = memoryTextView;
		this.timestampTextView = timestampTextView;
		this.usernameTextView = usernameTextView;

		if(memoryTextView != null && titleString != null) {
			memoryTextView.setText(titleString);
		} else {
			Log.d(null, "no text view");
		}
		if(locationTextView != null && location != null) {
			locationTextView.setText(location.toString());
		} else if (location == null && locationTextView != null) {
			locationTextView.setText(R.string.location_text_string);
		}
		if(timestampTextView != null) {
			timestampTextView.setText(timestampString);
		}
		if(usernameTextView != null) {
			Log.d(null, "usernametextview not null");
			DownloadUser u = new DownloadUser();
			u.setTextView(usernameTextView, null);
			u.execute(username,atlasToken,atlasUrlString);      //THIS leads to vma.class, setuser(), line 116
			usernameTextView.setText(username);
		}
//		loadImage(memoryImageView,memoryImageURI);
    }
	public void setUser(TextView usernameTextView, String username) { //set user who posted memory
		this.usernameTextView = usernameTextView;
		this.ownerId = username;
		if(username!= null && usernameTextView != null) {
			Log.d(null, "setuser-username&&usernametextview not null");
			usernameTextView.setText(username);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_memory_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.report_memory:
				//report moment
				Intent i = new Intent(getApplicationContext(), ReportMemoryActivity.class);
				i.putExtra("idString",id);
				i.putExtra("usernameString",usernameString);
				startActivity(i);
				return true;
			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);
		}
	}
}