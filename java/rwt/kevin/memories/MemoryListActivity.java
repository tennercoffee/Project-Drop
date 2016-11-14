package rwt.kevin.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryListActivity extends MapsActivity {
    ListView listView;
    public JSONArray jsonArray;
    ArrayList<Marker> markerList;
    List<List<String>> memList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.memorylist_toolbar);
        if(toolbar != null){
            toolbar.setTitle("View Moments List");
//            Log.d(null, "title set");
        }
        listView = (ListView) findViewById(R.id.momentList);
        setMemoryList();
    }
    private void setMemoryList(){
        DownloadList loadList = new DownloadList() {
            @Override
            public List<List<String>> downloadList() {
                DownloadMemoryList dml = new DownloadMemoryList();
                dml.execute("public", "0");
                dml.setList(memList);
//                dml.setMap(mMap, mClusterManager, markersList);
                return null;
            }
        };
        loadList.downloadList();

        if(jsonArray != null){
            Log.d(null, "memlist json: " + jsonArray.toString());

            try {
                List<String> slist = new ArrayList<>();

                String title = null;
                String location = null;
                for (int n = 0; n < jsonArray.length(); n++) {
                    JSONObject object = jsonArray.getJSONObject(n);
                    title = object.getString("title");
                    location = object.getString("location");
                    slist.add(title);
                    slist.add(location);

                    Log.d(null, slist.toString());
//
//                //define adapter
//                //        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
//                //                "WebOS","Ubuntu","Windows7","Max OS X"};
//    //            ArrayAdapter adapter = new ArrayAdapter<>(this,
//    //                    R.layout.activity_memory_list, R.id.momentList, slist);
//    //            if(listView != null){
//    //                Log.d(null, "listview is good");
//    //                listView.setAdapter(adapter);
//    //                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//    //                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
//    //    //                    String selectedMemory = listView.get(position);
//    //    //                    String selectedMemory
//    //                        Toast.makeText(getApplicationContext(), "Opening Moment", Toast.LENGTH_LONG).show();
//    //    //                    ViewMemoryActivity view = new ViewMemoryActivity();
//    //    //                    view.setId(listView.getSelectedItem().toString());
//    //                        Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
//    //                        startActivity(i);
//    //                    }
//    //                });
//    //            }else{
//    //                Toast.makeText(null, "There are no Moments!", Toast.LENGTH_LONG).show();
//    //            }
                }
            } catch (JSONException e) {
            e.printStackTrace();
            }
        } else {
            Log.d(null, "null json");
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
    }
    public void setJsonArray(JSONArray markerList){
        this.jsonArray = markerList;
        Log.d(null, "setJsonArray: " + jsonArray.toString());
    }
}