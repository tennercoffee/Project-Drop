package rwt.kevin.memories;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ViewMemoryActivity extends MapsActivity implements View.OnClickListener{
	//declare variables
	public String timestampString, titleString, username, id, atlasAppToken, ownerId, locationString, time, caUrl,key;
	public TextView usernameTextView, memoryTextView, locationTextView, timestampTextView, ownerTextView;
	public LatLng location;
	Button removeMemoryButton;
	ImageView memoryImageView;
	Context context;

	protected void onCreate(Bundle savedInstanceState) {
		//set environment
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
		ownerTextView = (TextView) findViewById(R.id.owner_textview);
		atlasAppToken = getString(R.string.atlas_app_token);
		removeMemoryButton = (Button) findViewById(R.id.remove_memory_button);

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
				Memory memory = new Memory();
				time = memory.getTimeStamp();

				locationTextView.setText(location.toString());
				memoryTextView.setText(titleString);
				timestampTextView.setText(time);
				usernameTextView.setText(username);
				memoryImageView.setImageURI(null);
			}
		} else {
			Log.d(null, "null idString");
		}
		//load information
		caUrl = getString(R.string.ca_access_url);
		key = getString(R.string.ca_access_key);
	}
	void setId(String id){
		this.id = id;
	}
    public void loadMemory(String id) {
		//download marker information
		removeMemoryButton.setOnClickListener(this);

        //downloadmem from server
		DownloadMemory dm = new DownloadMemory();
		dm.setViewMemoryViews(locationTextView, memoryTextView, timestampTextView, usernameTextView,removeMemoryButton,ownerTextView);
		dm.setContext(getApplicationContext());
		dm.execute(id,getString(R.string.ca_access_key), getString(R.string.ca_access_url),getString(R.string.atlas_app_token),getString(R.string.atlas_access_url));
    }
    public void setMemory(String atlasToken, String atlasUrlString, String timestampObject, TextView timestampTextView, LatLng locationObject, TextView locationTextView
			, String titleObject, TextView memoryTextView , String usernameObject, TextView usernameTextView, Button removeMemoryButton, String ownerId, TextView ownerTextView, Context context) {
		Log.d(null, "setmem");
        //set information from asynctask(downloadmem)
		this.timestampString = timestampObject;
		this.location = locationObject;
		this.titleString = titleObject;
		this.username = usernameObject;
		this.locationTextView = locationTextView;
		this.memoryTextView = memoryTextView;
		this.timestampTextView = timestampTextView;
		this.usernameTextView = usernameTextView;
		this.removeMemoryButton = removeMemoryButton;
		this.ownerId = ownerId;
		this.ownerTextView = ownerTextView;
		this.context = context;

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
			Log.d(null, "username" + username);
			Log.d(null, "ownerId" + ownerId);
			//download user
//			DownloadUser u = new DownloadUser();
//			u.setTextView(usernameTextView, null);
//			u.execute(username,atlasToken,atlasUrlString);      //THIS leads to vma.class, setuser(), line 116
			usernameTextView.setText(username);

			ownerTextView.setText("This Moment belongs to you, so you can edit/delete it.");

			//get userid & username from shared prefs
			//then compare to atlasId of moment
				//if this is your moment, show remove memory button/add buttonclicklistener
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
			Log.d(null, "atlasIdString" + atlasIdString);

			if(atlasIdString.equals(ownerId)) {
				Log.d(null, "this memory is mine");
				removeMemoryButton.setVisibility(View.VISIBLE);
			}
		}
//		loadImage(memoryImageView,memoryImageURI);
    }
	public void setUser(TextView usernameTextView, String username) {
		//set user who posted memory
		this.usernameTextView = usernameTextView;
		this.ownerId = username;
		if(username!= null && usernameTextView != null) {
			Log.d(null, "setuser-username&&usernametextview not null");
			usernameTextView.setText(username);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//setup menu bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_memory_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//menu onclick functions
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
	//button click functions
	@Override
	public void onClick (View view){
		switch (view.getId()) {
			//if image is not null, first upload the memory, get back the page id, get the album id from that,
			//then upload image
			//if image is null, just add memory
			case R.id.remove_memory_button:
				RemoveMemory removeMemory = new RemoveMemory();
				removeMemory.execute(key, id, caUrl);
				finish();
				break;
		}
	}
}