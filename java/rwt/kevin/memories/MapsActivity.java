package rwt.kevin.memories;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import static android.view.View.OnClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMyLocationButtonClickListener, com.google.android.gms.location.LocationListener{
    String caccessKey = "c3b128b6-9890-11e6-9298-e0cb4ea6daff";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1, MY_LOCATION_REQUEST_CODE = 1;
    public GoogleMap mMap;
    public GoogleApiClient client;
    public ArrayList<Marker> markersList = new ArrayList<>();
    public SupportMapFragment mapFragment = null;
    Toolbar toolbar;
    List<List<String>> resultList;

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        if (client == null) {
            buildClient();
        }
        //LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest()
        if (map != null) {
            mMap = map;
//        mMap.setOnCameraChangeListener(getCameraChangeListener());

            mMap.setOnMyLocationButtonClickListener(this);

            //needs check if location not available
            enableMyLocation();

            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            LatLng currentLocation = getLocation();
            if (currentLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
            }
            FloatingActionButton addMemoryButton = (FloatingActionButton) findViewById(R.id.addMemoryButton);
            if (addMemoryButton != null) {
                addMemoryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A4C639")));
                addMemoryButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LatLng myLocation = getLocation();

                        if (myLocation != null) {
                            Intent i = new Intent(getApplicationContext(), AddMemoryActivity.class);
                            i.putExtra("location", myLocation);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                    if (marker.getTitle() != null) {

                        //run download memory here
                        //get description and title
                        //pass through as intent extras
                        
                        DownloadMemory mem = new DownloadMemory();
                        mem.execute(marker.getTitle());
                    } else {
                        Log.d(null, "null marker");
                    }
                    startActivity(i);
                    return false;
                }
            });
            //mMap.setOnMyLocationChangeListener();
            mMap.setMinZoomPreference(18);
            mMap.setMaxZoomPreference(18);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////
    
    
    
    
    public void addMarkerToList(LatLng location, String s) {
        Log.d(null, "addmarkertolist" + s);
        resultList = new ArrayList<>();
        List<String> result = new ArrayList<>();
        result.add(location.toString());
        result.add(s);
        resultList.add(result);
    }
    public void addMarkerToMap() {
        try {
            //has to sleep here,
            // in order to get response before moving forward
            //bad practice
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(null, "addmarkertomap");
        String id = null;
        String locationString = null;
        if(resultList != null) {
            id = String.valueOf(resultList.get(0));
            Log.d(null, "id:" + id);
            locationString = String.valueOf(resultList.get(1));
            Log.d(null, "location:" + locationString);
        }

        if (markersList == null){
            markersList = new ArrayList<>();
            Log.d(null, "null list");
        }
        if(mMap != null) {
            /*TextScanner t = new TextScanner();
            LatLng latlngFinal;
            if(desc != null) {
                String location = t.descSplitter(desc, 1, 0);
                String latlngSplitString = t.locationSplitter(location);

                String[] latlng = latlngSplitString.split(",");

                double latitude = Double.parseDouble(latlng[0]);
                double longitude = Double.parseDouble(latlng[1]);
                latlngFinal= new LatLng(latitude, longitude);
            */
                //this might be the wrong map to be calling
                TextScanner t = new TextScanner();
                LatLng latlng = t.locationStringtoLatLng(locationString);

                Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(id));
                Log.d(null, "adding marker");
                markersList.add(marker);

        }else{Log.d(null, "mMap null");}
    }
    public void revealMarkers(GoogleMap mMap) {
        if(markersList != null) {
            for (int n = 0; n < markersList.size(); n++) {
                Log.d(null, "marker#" + String.valueOf(n));
                Marker currentMarker = markersList.get(n);

                String markerId = currentMarker.getTitle();
                LatLng position = currentMarker.getPosition();

                mMap.addMarker(new MarkerOptions().position(position).title(markerId));
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////
    
    
    
    
    
    
    
    
    
    
    
    
    
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
            case R.id.action_settings:
                //open settings activity
                Intent iSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(iSettings);
                return true;
            case R.id.action_sync:
                //download memories, then reveal on map
                
                
                
             /***************************************************************************************
			 * 
			 * 
			 * Design callback funtion here
			 *  idk what that means
			 * 
			 * 
			 ****************************************************************************************/
                
                DownloadMemoryList dml = new DownloadMemoryList();
                dml.execute("public");

                //addMarkerToMap();
                //revealMarkers(mMap);
                return true;
            case R.id.action_list:
                //open list activity
                Intent iList = new Intent(getApplicationContext(), MemoryListActivity.class);
                startActivity(iList);
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
        //TODO, update map latlng position
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
            //handle permission request
            //ContextCompat.
            Log.d(null, "no permission");
        }
        // handles the result of the permission request by implementing the ActivityCompat.OnRequestPermissionsResultCallback
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                //handle permission request
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
    }


}