package rwt.kevin.memories;

import android.content.Intent;
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

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryListActivity extends MapsActivity {

    public JSONArray jsonArray;
    ArrayList<Marker> markerList;
    List<List<String>> memList= new ArrayList<>();
    JSONArray listArray;
    List<String> slist;
    JSONArray array;
    Switch memlistPrivacySwitch;
    ListView listView;
    ArrayAdapter adapter;
    JSONArray resultJSON;



    public interface DownloadList {
        JSONArray downloadList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.memorylist_toolbar);
        if(toolbar != null){
            toolbar.setTitle("View Moments List");
        }
        Button backButton = (Button) findViewById(R.id.backButton);
        if(backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        final TextView memlistPrivacyTextView = (TextView) findViewById(R.id.newprivacy_textview);
        memlistPrivacySwitch = (Switch) findViewById(R.id.new_switch);

        if(memlistPrivacySwitch != null && memlistPrivacyTextView != null) {
            memlistPrivacySwitch.setChecked(true);
            memlistPrivacyTextView.setText("Public");
            memlistPrivacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (memlistPrivacySwitch.isChecked()) {
                        scope = "public";
                        memlistPrivacyTextView.setText("Public");
                    } else {
                        scope = "private";
                        memlistPrivacyTextView.setText("Private");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DownloadList loadList = new DownloadList() {
                        @Override
                        public JSONArray downloadList() {
                            String result = "0";
                            DownloadMemoryList dml = new DownloadMemoryList();
                            dml.setAdapter(adapter);
                            dml.setListView(listView);
                            dml.execute(scope);

//                            array = dml.getListArray();
                            Log.d(null, "array");
//                            setMemoryList(array);
                            return null;
                        }
                    };
                    loadList.downloadList();
//                    Log.d(null, markersList.toString());
                }
            });
        }
        listView = (ListView) findViewById(R.id.momentList);
        adapter = new ArrayAdapter<>(this, R.layout.activity_memory_list, R.id.momentList, slist);

        DownloadList loadList = new DownloadList() {
            @Override
            public JSONArray downloadList() {
                String result = "0";
                DownloadMemoryList dml = new DownloadMemoryList();
                dml.setAdapter(adapter);
                dml.setListView(listView);
                dml.execute(scope);

//                            array = dml.getListArray();
                Log.d(null, "array");
//                            setMemoryList(array);
                return null;
            }
        };
        loadList.downloadList();
        Log.d(null, markersList.toString());

//        JSONArray resultJSON = null;
//        Intent i = getIntent();


    }
    JSONArray getList(){
        Log.d(null, "getlist");
        return this.resultJSON;
    }


    public void setResultJSON(JSONArray array) {
        Log.d(null, array.toString());
        this.resultJSON = array;
    }
    public void setupList(JSONArray listArray,ListView listView,ArrayAdapter adapter) {
        try {
//            resultJSON = new JSONArray(i.getStringExtra("json"));
//            Log.d(null, resultJSON.toString());
            slist = new ArrayList<>();
            for (int n = 0; n < listArray.length(); n++) {
                String title, location;
                JSONObject object = listArray.getJSONObject(n);
                title = object.getString("title");
                location = object.getString("location");
                slist.add(title);
                slist.add(location);
                Log.d(null, title + " " + location);

                /***************************************************
                 * getting info through this point. how to apply to list?
                 *
                 * 
                 */


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(array != null) {
            if (listView != null && adapter != null) {
                Log.d(null, "listview is not null");
                listView.setAdapter(adapter);
                Log.d(null, "adapter click listener");

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                        Log.d(null, "yay");
//                            String selectedMemory = listView.get(position);
//                            Toast.makeText(getApplicationContext(), "Opening Moment", Toast.LENGTH_LONG).show();
                        //                    ViewMemoryActivity view = new ViewMemoryActivity();
                        //                    view.setId(listView.getSelectedItem().toString());
                        Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
                        startActivity(i);
                    }
                });
            } else {
                Log.d(null, "null json");
            }
        }

    }
}