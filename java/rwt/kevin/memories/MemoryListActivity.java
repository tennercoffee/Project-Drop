package rwt.kevin.memories;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/****************************************************
 *
 * this will remain unused until my server is ready
 * for the increased workload
 *
 */
public class MemoryListActivity extends MapsActivity {
    //declare variables
    List<String> slist;
    JSONArray array;
    Switch memlistPrivacySwitch;
    LatLng myLocation;

    public interface DownloadList {
        void downloadList();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.memorylist_toolbar);
        if(toolbar != null){
            toolbar.setTitle("View Moments List");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //setup list and listview/adapter
        final ListView listView = (ListView) findViewById(R.id.moment_list);
        final TextView memlistPrivacyTextView = (TextView) findViewById(R.id.newprivacy_textview);
        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(
                MemoryListActivity.this,
                android.R.layout.simple_list_item_1,
                arrayList);
        memlistPrivacySwitch = (Switch) findViewById(R.id.new_switch);
        if(myLocation != null) {
            Log.d(null, "string location: " + myLocation.toString());
        } else {
            Log.d(null, "null location");
        }
        //build privacy switch
        listView.setAdapter(arrayAdapter);
        if(memlistPrivacySwitch != null && memlistPrivacyTextView != null) {
            memlistPrivacySwitch.setChecked(true);
            memlistPrivacyTextView.setText(getString(R.string.public_string));
            memlistPrivacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //this changes the privacy(public/private) in the api calls
                    //changing it here will automatically download markers again
                    if (memlistPrivacySwitch.isChecked()) {
                        scopeString = "public";
                    } else {
                        scopeString = "private";
                    }
                    memlistPrivacyTextView.setText(scopeString);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //download data
                    downloadMemoryList(arrayAdapter,listView,myLocation,scopeString);
                }
            });
        }
        //set click listeners
        Button backButton = (Button) findViewById(R.id.back_button);
        if(backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String selectedMemory = String.valueOf(listView.getItemAtPosition(position));
                Toast.makeText(getApplicationContext(), "Opening Moment: " + selectedMemory, Toast.LENGTH_LONG).show();
                Log.d(null, "selectedmem: " + selectedMemory);
                ViewMemoryActivity view = new ViewMemoryActivity();
//                view.setId(listView.getSelectedItem().toString());
//                Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
//                i.putExtra(memoryId);
//                startActivity(i);
            }

        });
        //download data
        downloadMemoryList(arrayAdapter,listView,myLocation,scopeString);
    }
    private void downloadMemoryList(final ArrayAdapter arrayAdapter, final ListView listView, final LatLng myLocation, final String scopeString) {
        //set up to download memory list
        final String url = getString(R.string.ca_access_url);
        DownloadList loadList = new DownloadList() {
            @Override
            public void downloadList() {
                DownloadMemoryList dml = new DownloadMemoryList();
                dml.setAdapter(arrayAdapter);
                dml.setListView(listView);
                if(myLocation != null) {
                    dml.setMyLocation(myLocation);
                } else {
                    Log.d(null, "null location");
                }
                dml.execute(scopeString,getString(R.string.ca_access_key),url);
                Log.d(null, "loadinglist...");
            }
        };
        //send download call
        loadList.downloadList();
    }
    public void setList(List<String> titleList, LatLng myLocation) {
        this.titleStringList = titleList;
        Log.d(null, "this titlelist" + titleList);
        if(myLocation != null) {
            Log.d(null, "mylocation: " + myLocation.toString());
            this.myLocation = myLocation;
        }
    }
    public void buildList(List<String> list, ArrayAdapter<String> arrayAdapter, GoogleMap mMap){
        Log.d(null, "addtolist");
        List<String> memList = new ArrayList<>();
        if(arrayAdapter != null) {
            Log.d(null, "jsonarraytostring: " + list.toString());
            for (int i = 0; i < list.size(); i++) {
                String title = list.get(i);
                memList.add(title);
            }
//            for (int n = 0; n < jsonArray.size(); n++) {
//                JSONArray ja = jsonArray.get(n);
//                Log.d(null, "ja: " + ja.toString());
//                try {
//                    for (int i = 0; i < jsonArray.size(); i++) {
//                        JSONObject j = jsonArray.get(i);
//                        String titleString = j.getString("titleString");
//                        String markerLocation = j.getString("location");
//                        Log.d(null, titleString + " " + markerLocation);
//                        TextScanner t = new TextScanner();
//                        LatLng markerLatLng = t.locationStringToLatLng(markerLocation);
//
//                        //run ismarkerclose on all markers to see if you can open from memlistact
//                        MapsActivity m = new MapsActivity();
//                        if (m.isMarkerCloseOnMap(markerLatLng, 100, myLocationLatLng, googleMap)) {
//                            //add marker to arrayList
//                            arrayList.add(titleString);
//                            Log.d(null, titleString + " added");
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            arrayAdapter.addAll(memList);
            arrayAdapter.notifyDataSetChanged();
//        }
    }
//    public void setMyLocation(LatLng mylocation) {
//        this.myLocation = mylocation;
//    }
}