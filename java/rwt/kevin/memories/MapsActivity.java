package rwt.kevin.memories;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.OnClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMyLocationButtonClickListener, com.google.android.gms.location.LocationListener{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1, MY_LOCATION_REQUEST_CODE = 1;
    public GoogleMap mMap;
    public GoogleApiClient client;
    public ArrayList<Marker> markersList = new ArrayList<>();
    public ArrayList<ArrayList> arrayList = new ArrayList<>();
    public SupportMapFragment mapFragment = null;
    Toolbar toolbar;
    ClusterManager<MarkerItems> mClusterManager;
    JSONArray resultJSON;
    String scope;

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
        }
        client = buildClient();
    }
    public void loadMap(Bundle savedInstanceState) {
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
//        ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        int i = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.INTERNET);
//        Log.d(null, String.valueOf(i));
        if (client == null) {
            buildClient();
        }
        if (map != null) {
            mMap = map;
            enableMyLocation();
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMinZoomPreference(16);
            mMap.setMaxZoomPreference(20);
//            mClusterManager = new ClusterManager<>(this, map);
//            mMap.setOnMarkerClickListener(mClusterManager);

            LatLng currentLocation = getLocation();
            if (currentLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
            }
//            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerItems>() {
//                @Override
//                public boolean onClusterClick(Cluster<MarkerItems> cluster) {
//                    Log.d(null, "clusterclick");
////                    MarkerManager.Collection collection = mClusterManager.getClusterMarkerCollection();
////                    Log.d(null, collection.toString());
//                    Intent i = new Intent(getApplicationContext(), MemoryListActivity.class);
//                    startActivity(i);
//                    return true;
//                }
//            });
            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(null, "marker click");
                    Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                    if (marker.getTitle() != null) {
                        String id = marker.getTitle();
                        i.putExtra("id",id);
                    } else {
                        Log.d(null, "null marker");
                    }
                    startActivity(i);
                    return false;
                }
            });
        }
        FloatingActionButton addMemoryButton = (FloatingActionButton) findViewById(R.id.addMemoryButton);
        if (addMemoryButton != null) {
            addMemoryButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LatLng myLocation = getLocation();
                    if (myLocation != null) {
//                        Calendar cal = Calendar.getInstance();
//                        TimeZone tz = cal.getTimeZone();
//                        Log.d("Time zone","="+tz.getID());
//                        Log.d(null, tz.toString());
//
//                        getRegionCode(tz.getID());

                        Intent i = new Intent(getApplicationContext(), AddMemoryActivity.class);
                        i.putExtra("location", myLocation);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        ImageButton profileButton = (ImageButton) findViewById(R.id.profile_icon);
        if(profileButton != null){
            profileButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), MyProfileActivity.class);
                    startActivity(i);
                }
            });
        }
        ImageButton listButton = (ImageButton) findViewById(R.id.list_icon);
        if(listButton != null){
            listButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), MemoryListActivity.class);
//                    MemoryListActivity mem = new MemoryListActivity();
//                    mem.setResultJSON(resultJSON);
                    startActivity(i);
                }
            });
        }
        final TextView privacyTextView = (TextView) findViewById(R.id.privacy_textview);
        final Switch simpleSwitch = (Switch) findViewById(R.id.privacy_switch);
        if(simpleSwitch != null && privacyTextView != null) {
            simpleSwitch.setChecked(true);
            privacyTextView.setText("Public");
            simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (simpleSwitch.isChecked()) {
                        scope = "public";
                        Log.d(null, "public");
                        privacyTextView.setText("Public");
                    } else {
                        scope = "private";
                        Log.d(null, "private");
                        privacyTextView.setText("Private");
                    }
                    mMap.clear();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DownloadMap loadMap = new DownloadMap() {
                        @Override
                        public List<List<String>> downloadMap() {
                            DownloadMemoryList dml = new DownloadMemoryList();
                            dml.execute(scope);
                            dml.setMap(mMap, mClusterManager, markersList);
//                            Log.d(null,"getmaparray:" + dml.getMapArray().toString());
                            return null;
                        }
                    };
                    loadMap.downloadMap();
                }
            });
        }
        DownloadMap loadMap = new DownloadMap() {
            @Override
            public List<List<String>> downloadMap() {
                DownloadMemoryList dml = new DownloadMemoryList();
                dml.execute(scope);
                dml.setMap(mMap, mClusterManager, markersList);
                return null;
            }
        };
        loadMap.downloadMap();
    }
    private int getRegionCode(String id) {
        int regionCode;
        if(id.equals("America/Chicago")){
            return 0001;
        } else if(id.equals("")){
            return 0000;
        }
        return 0000;
    }
    public boolean isMarkerClose(LatLng markerLocation){
        if(markerLocation.latitude <= getLocation().latitude && markerLocation.longitude <= getLocation().longitude) {
            //do this calculation
            if((markerLocation.latitude - getLocation().latitude) <= .00002 && (markerLocation.longitude - getLocation().longitude) <= .00002 ){
                //return true;
                return true;
            } else { return false;}
        } else if(markerLocation.latitude >= getLocation().latitude && markerLocation.longitude >= getLocation().longitude){
            //do this calculation
            if((markerLocation.latitude - getLocation().latitude) <= .00002 && (markerLocation.longitude - getLocation().longitude) <= .00002 ){
                //return true;
                return true;
            } else { return false;}
        } else {
            return false;
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
    public ArrayList<ArrayList> addMarkersToMap(ArrayList<JSONArray> downloadList, GoogleMap mMap) {
        Log.d(null, "addmarkerstomap");
        for(int n = 0; n < downloadList.size(); n++) {
            JSONArray downloadArray = downloadList.get(n);
            this.resultJSON = downloadArray;
            arrayList = new ArrayList<ArrayList>();
            markersList = new ArrayList<>();
            if (downloadArray != null && mMap != null) {
                for (int i = 0; i < downloadArray.length(); i++) {
                    try {
                        JSONObject downloadObject = (JSONObject) downloadArray.get(i);
                        String coordinates = downloadObject.get("location").toString();
                        String id = downloadObject.get("title").toString();
                        String[] latlng = coordinates.split(",");
                        double latitude = Double.parseDouble(latlng[0]);
                        double longitude = Double.parseDouble(latlng[1]);
                        LatLng latlngFinal = new LatLng(latitude, longitude);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlngFinal).title(id));
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
    public void revealMarkers(GoogleMap map, ClusterManager<MarkerItems> clusterManager, ArrayList<ArrayList> markersList) {
        this.mClusterManager = clusterManager;
        this.arrayList = markersList;
//        this.markersList = markersList;
        this.mMap = map;
//        mMap.clear();//this removes the markers that should be in cluster, removes duplicates
//        mClusterManager.clearItems();
        for (int n = 0; n < arrayList.size(); n++) {

            for(int i = 0; i < markersList.size(); i++) {
//                Marker currentMarker = markersList.get(i);
//            mClusterManager.addItem(new MarkerItems(currentMarker.getPosition()));
//            mClusterManager.cluster();
//                mMap.addMarker(new MarkerOptions().position(currentMarker.getPosition()).title(currentMarker.getTitle()));
            }
        }
        Log.d(null, "full map");
    }
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            //should this be commented
            mMap.setMyLocationEnabled(true);
        }
    }
    public LatLng getLocation() {
        Location location;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }else {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                return new LatLng(location.getLatitude(),location.getLongitude());
            }
        }
        return null;
    }
    public GoogleApiClient buildClient() {
        return new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */,
                    this /* OnConnectionFailedListener */)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                mMap.clear();
                LatLng currentLocation = getLocation();
                if (currentLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DownloadMap loadMap = new DownloadMap() {
                    @Override
                    public List<List<String>> downloadMap() {
                        DownloadMemoryList dml = new DownloadMemoryList();
                        dml.execute(scope);
                        dml.setMap(mMap, mClusterManager, markersList);

                        return null;
                    }
                };
                loadMap.downloadMap();
                return true;
            case R.id.action_about:
                //open about activity
                Intent about = new Intent(getApplicationContext(), AboutActivity.class);
                Log.d(null, "about selected");
                startActivity(about);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        getLocation();
    }
    public void onLocationChanged(Location location) {
        Toast.makeText(null, "location :"+location.getLatitude()+" , "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        LatLng currentLocation = getLocation();
        if (currentLocation != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("State", "Connection Failed");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //check permissions to see if fine location is enabled
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Log.d(null, "no permission");
        }
        // handles the result of the permission request by implementing the ActivityCompat.OnRequestPermissionsResultCallback
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                Log.d(null, "no permission");
            }
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    @Override
    public void onStart() {
        client.connect();
        super.onStart();
    }
    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        client.disconnect();
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
        // Release anything we don't need when paused
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        client.connect();
        // resetactivity
    }
}