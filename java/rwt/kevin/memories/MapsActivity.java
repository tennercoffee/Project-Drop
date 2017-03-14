package rwt.kevin.memories;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMyLocationButtonClickListener, com.google.android.gms.location.LocationListener{
    //declare variables
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    public GoogleMap googleMap;
    public GoogleApiClient googleApiClient;
    public ArrayList<Marker> markersList = new ArrayList<>();
    public ArrayList<ArrayList> arrayList = new ArrayList<>();
    public SupportMapFragment supportMapFragment = null;
    String usernameString, accessKeyString, scopeString, atlasIdNumberString;
    List<String> titleStringList;
    LatLng myLocationLatLng, neLatLng, swLatLng;
    Toolbar toolbar;
    ClusterManager<MarkerItems> mClusterManager;
    TextView privacyTextView;
    Switch simpleSwitch;
    FloatingActionButton addMemoryButton;
    ImageButton profileImageButton, listImageButton;

    public interface DownloadMap {
        List<List<String>> downloadMap();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMap(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Moments");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        //start map client and get my location
        googleApiClient = buildClient();
        enableMyLocation();
        myLocationLatLng = getLocation(getApplicationContext());

        //setup views && buttons
        privacyTextView = (TextView) findViewById(R.id.privacy_textview);
        simpleSwitch = (Switch) findViewById(R.id.privacy_switch);
        addMemoryButton = (FloatingActionButton) findViewById(R.id.add_memory_button);
        profileImageButton = (ImageButton) findViewById(R.id.profile_icon_button);
        listImageButton = (ImageButton) findViewById(R.id.list_icon_button);

        addMemoryButton.setOnClickListener(this);
        profileImageButton.setOnClickListener(this);
        listImageButton.setOnClickListener(this);
    }
    public void loadMap(Bundle savedInstanceState) {
        //loadmap fragment
        setContentView(R.layout.activity_maps);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        //get login information
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String atlasAccessKey = sharedPreferences.getString(getString(R.string.atlas_app_token), null);
        usernameString = sharedPreferences.getString("usernameString", null);
        String atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
        Log.d(null, "logged in as: " + usernameString + " " + atlasIdString + " " + accessKeyString);

        if (googleApiClient == null) {
            //sign into maps
            buildClient();
        }
        if (map != null) {
            //train maps onto mylocation && build settings
            googleMap = map;
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setMinZoomPreference(16);
            googleMap.setMaxZoomPreference(20);

            LatLng currentLocation = getLocation(getApplicationContext());
            if (currentLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
            }

            //setup marker listener to load marker information
            googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(null, "marker click");
                    Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                    if(isMarkerCloseOnMap(marker.getPosition(), 100, getLocation(getApplicationContext()), googleMap)) {
                        if (marker.getTitle() != null) {
                            String id = marker.getTitle();
                            String location = getLocation(getApplicationContext()).toString();
                            if(id.equals("test")){
                                i.putExtra("test","test").putExtra("location", location);
                            }
                            i.putExtra("idString", id);
                            i.putExtra("usernameString", usernameString);
                            startActivity(i);
                        } else {
                            Log.d(null, "null marker");
                        }
                    } else {
                        Log.d(null, "too far");
                    }
                    //changed from return false;
                    // (true should keep it from panning to selected marker)
                    return true;
                }
            });
        }
        //setup privacy switch
        if (simpleSwitch != null && privacyTextView != null) {
            simpleSwitch.setChecked(true);
            privacyTextView.setText(R.string.public_string);
            simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (simpleSwitch.isChecked()) {
                        scopeString = "public";
                        privacyTextView.setText(R.string.public_string);
                    } else if (!simpleSwitch.isChecked() && usernameString != null){
                        scopeString = "private";
                        privacyTextView.setText(R.string.private_string);
                    } else {
                        scopeString = "public";
                        privacyTextView.setText(R.string.public_string);
                        Toast.makeText(getApplicationContext(), "To See Private Moments, Login First", Toast.LENGTH_LONG).show();
                        simpleSwitch.setChecked(true);
                    }
                    googleMap.clear();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    callDownloadMap(neLatLng,swLatLng);
                }
            });
        }
        //get corners of screen for downloading just close by markers
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        neLatLng = bounds.northeast;
        swLatLng = bounds.southwest;
        callDownloadMap(neLatLng,swLatLng);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_memory_button:
                //load addmemactivity
                final LatLng myLocation = getLocation(getApplicationContext());
                if (myLocation != null) {
                    Intent i = new Intent(getApplicationContext(), AddMemoryActivity.class);
                    i.putExtra("location", myLocation);
                    if(atlasIdNumberString != null) {
                        Log.d(null, atlasIdNumberString + " adding mem");
                        i.putExtra("atlasIdNumberString", atlasIdNumberString);
                    } else {
                        Log.d(null, "null user");
                    }
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.profile_icon_button:
                //load the user profile
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String atlasAccessKey = sharedPreferences.getString(getString(R.string.atlas_app_token), null);
                usernameString = sharedPreferences.getString("usernameString", null);
                String atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    if (usernameString != null && atlasAccessKey != null) {
                        i.putExtra("usernameString", usernameString);
                        i.putExtra("accessKeyString", atlasAccessKey);
                        startActivity(i);
                    } else {
                        Log.d(null, "no user logged in");
                        Toast.makeText(getApplicationContext(), "Please Log In First", Toast.LENGTH_LONG).show();
                    }

                break;
            case R.id.list_icon_button:
                //load memlistactivity with markerList
                Intent l = new Intent(getApplicationContext(), MemoryListActivity.class);
                    //TODO:collect markers here...if markerslist is not null(good) send markerslist to memlistact
                    //todo: remove first toast
                Toast.makeText(getApplicationContext(), String.valueOf(markersList.size()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), getString(R.string.newfeature), Toast.LENGTH_LONG).show();

                break;
        }
    }
    private void callDownloadMap(final LatLng neLatLng, final LatLng swLatLng) { //download markers in json format from server
        DownloadMap loadMap = new DownloadMap() {
            @Override
            public List<List<String>> downloadMap() {
                DownloadMemoryMap dmm = new DownloadMemoryMap();
                String webUrl = getString(R.string.ca_access_url);
                dmm.execute(scopeString, String.valueOf(neLatLng.latitude), String.valueOf(neLatLng.longitude),
                        String.valueOf(swLatLng.latitude), String.valueOf(swLatLng.longitude),
                        getString(R.string.ca_access_key), webUrl);
                dmm.setMap(googleMap, mClusterManager, markersList, getLocation(getApplicationContext()));
                return null;
            }
        };
        loadMap.downloadMap();
    }
    public boolean isMarkerCloseOnMap(LatLng markerLocation, int rDistance, LatLng myLocation, GoogleMap mMap){ //check to see if marker is close enough to view information
        float[] distance = new float[2];
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(rDistance)     //The radius of the circle, specified in meters. It should be zero or greater.
                .strokeColor(Color.rgb(0, 136, 255))
                .fillColor(Color.argb(20, 0, 136, 255)));
        Location.distanceBetween(markerLocation.latitude, markerLocation.longitude,
                circle.getCenter().latitude, circle.getCenter().longitude, distance);
        if( distance[0] > circle.getRadius()) {
            Toast.makeText(getBaseContext(), "Too far away. Move closer.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
    public ArrayList<ArrayList> addMarkersToMapArray(ArrayList<JSONArray> downloadList, GoogleMap mMap) { //read markers from json list, and add to markerslist
        Log.d(null, "addmarkerstomap");
        for(int n = 0; n < downloadList.size(); n++) {
            JSONArray downloadArray = downloadList.get(n);
            arrayList = new ArrayList<>();
            markersList = new ArrayList<>();
            if (downloadArray != null && mMap != null) {
                Log.d(null, "downloadarray: " + downloadArray.toString());
                for (int i = 0; i < downloadArray.length(); i++) {
                    try {
                        Marker marker;
                        String latitude, longitude, id, color;
                        JSONObject downloadObject = (JSONObject) downloadArray.get(i);
                        latitude = downloadObject.get("latitude").toString();
                        longitude = downloadObject.get("longitude").toString();
                        id = downloadObject.get("id").toString();
                        color = downloadObject.get("color").toString();
                        Log.d(null, "id:" + id + " color: " + color);

                        float hue;
                        switch (color) {
                            case "Red":
                                hue = BitmapDescriptorFactory.HUE_RED;
                                break;
                            case "Blue":
                                hue = BitmapDescriptorFactory.HUE_BLUE;
                                break;
                            case "Green":
                                hue = BitmapDescriptorFactory.HUE_GREEN;
                                break;
                            default:
                                hue = BitmapDescriptorFactory.HUE_RED;
                                break;
                        }
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng((Double.parseDouble(latitude)), Double.parseDouble(longitude)))
                                .icon(BitmapDescriptorFactory.defaultMarker(hue))
                                .title(id));

                        markersList.add(marker);
                        arrayList.add(markersList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d(null, "resultlist null-addmarkerstomap");
            }
        }
        return arrayList;
    }
    public void showMarkersArrayOnMap(GoogleMap map, ClusterManager<MarkerItems> clusterManager, ArrayList<ArrayList> list) { //display markers on map
        this.mClusterManager = clusterManager;
        this.arrayList = list;
        this.googleMap = map;

        for (int n = 0; n < arrayList.size(); n++) {
            ArrayList<Marker> markersList = new ArrayList<>(arrayList.get(n));
            for(int i = 0; i < markersList.size(); i++) {
                Marker currentMarker = markersList.get(i);
//                mClusterManager.addItem(new MarkerItems(currentMarker.getPosition()));
//                mClusterManager.cluster();
                googleMap.addMarker(new MarkerOptions().position(currentMarker.getPosition()).title(currentMarker.getTitle()));
            }
        }
        Log.d(null, "full map");
    }
    public GoogleApiClient buildClient() { //load google map api
        return new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */,
                    this /* OnConnectionFailedListener */)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //add menu to toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                //refresh markers on map
                googleMap.clear();
                LatLng currentLocation = getLocation(getApplicationContext());
                if (currentLocation != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
                }
                callDownloadMap(neLatLng,swLatLng);
                return true;
            case R.id.action_about:
                //open about activity
                Intent about = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(about);
                return true;
            case R.id.action_add_test_marker:
                //add test marker
                googleMap.addMarker(new MarkerOptions().position(getLocation(getApplicationContext())).title("test"));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        getLocation(getApplicationContext());
    }
    @Override
    public void onLocationChanged(Location location) {
        //update location--might be redundant
        Toast.makeText(null, "location update:" + location.getLatitude()+ " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        LatLng currentLocation = getLocation(getApplicationContext());
        if (currentLocation != null && googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("State", "Connection Failed");
    }
    private void enableMyLocation() {
        //check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Enable Location Services", Toast.LENGTH_LONG).show();
            Log.d(null, "enable-no permission");
            // Permission to access the location is missing. Ask for it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else if (googleMap != null) {
            Log.d(null, "enable-map is not null");
            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);
        }
    }
    public LatLng getLocation(Context context) {
        //track location
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()) {
            return new LatLng(gps.getLatitude(),gps.getLongitude());
        } else {
            gps.showSettingsAlert(context);
        }
        return null;
    }
    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }
    @Override
    public void onStop() {
        googleApiClient.disconnect();
        GPSTracker gps = new GPSTracker(getApplicationContext());
        gps.stopUsingGPS();
        super.onStop();
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        GPSTracker gps = new GPSTracker(getApplicationContext());
        gps.stopUsingGPS();
        googleApiClient.disconnect();
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
        // Release anything we don't need when paused
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        googleApiClient.connect();
        if(googleMap != null && getLocation(getApplicationContext()) != null) {
            LatLng latLng = getLocation(getApplicationContext());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }
    public class MarkerItems implements ClusterItem {
        private final LatLng mPosition;
        MarkerItems(LatLng position) {
            mPosition = position;
        }
        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }
}