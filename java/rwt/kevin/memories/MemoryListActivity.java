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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemoryListActivity extends MapsActivity {
    ListView listView;
    public JSONArray jsonArray;

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
        Intent i = getIntent();
        String jsonString = i.getStringExtra("json");
        if(jsonString != null){
            try {
                List<String> slist = new ArrayList<>();
                JSONArray array = new JSONArray(jsonString);
                String title = null;
                String location = null;
                for (int n = 0; n < array.length(); n++) {
                    try {
                        JSONObject object = array.getJSONObject(n);
                        title = object.getString("title");
                        location = object.getString("location");
    //                    slist.add("title",title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                slist.add(title);
                slist.add(location);
                Log.d(null, slist.toString());

                //define adapter
                //        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
                //                "WebOS","Ubuntu","Windows7","Max OS X"};
    //            ArrayAdapter adapter = new ArrayAdapter<>(this,
    //                    R.layout.activity_memory_list, R.id.momentList, slist);
    //            if(listView != null){
    //                Log.d(null, "listview is good");
    //                listView.setAdapter(adapter);
    //                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    //                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
    //    //                    String selectedMemory = listView.get(position);
    //    //                    String selectedMemory
    //                        Toast.makeText(getApplicationContext(), "Opening Moment", Toast.LENGTH_LONG).show();
    //    //                    ViewMemoryActivity view = new ViewMemoryActivity();
    //    //                    view.setId(listView.getSelectedItem().toString());
    //                        Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
    //                        startActivity(i);
    //                    }
    //                });
    //            }else{
    //                Toast.makeText(null, "There are no Moments!", Toast.LENGTH_LONG).show();
    //            }
            } catch (JSONException e) {
            e.printStackTrace();
            }
        } else {
            Log.d(null, "null json");
//            MapsActivity m = new MapsActivity();
//            try {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                    JSONArray jsonArray = new JSONArray(m.getJsonArray());
//                    Log.d(null, jsonArray.toString());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
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
    public void setJsonArray(JSONArray array){
        this.jsonArray = array;
    }

}