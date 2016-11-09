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

import org.json.JSONArray;

import java.util.List;

public class MemoryListActivity extends MapsActivity {
    ListView listView;
    public JSONArray jsonArray;
    String scope;
    public interface DownloadList {
        List<List<String>> downloadList();
    }
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
        //pass through jsonarray of markers from maps
        //--then parse away
        //use a set method in this file, accessed from maps

//
//        DownloadList loadList = new DownloadList() {
//            @Override
//            public List<List<String>> downloadList() {
//                DownloadMemoryList dml = new DownloadMemoryList();
//                dml.execute("public");
////                dml.setMap(mMap, markersList);
//                Log.d(null, "downloadList");
//                return null;
//            }
//        };
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        List<List<String>> slist = loadList.downloadList();
//        Log.d(null, slist.toString());
//        //define adapter
//        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
//                "WebOS","Ubuntu","Windows7","Max OS X"};
//        ArrayAdapter adapter = new ArrayAdapter<>(this,
//                R.layout.activity_memory_list, R.id.momentList, mobileArray);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if(listView != null){
//            Log.d(null, "listview is good");
////            Log.d(null, "listview: " + jsonArray.toString());
//
//            //assign adapter to ListView
//            listView.setAdapter(adapter);
//            //set click Listener, will load viewMemoryActivity
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
////                    String selectedMemory = listView.get(position);
////                    String selectedMemory
//                    Toast.makeText(getApplicationContext(), "Opening Moment", Toast.LENGTH_LONG).show();
//
////                    ViewMemoryActivity view = new ViewMemoryActivity();
////                    view.setId(listView.getSelectedItem().toString());
//
//                    //open ViewMemoryActivity
//                    Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);
//                    //add memoryID to bundle here(selectedMemory)
//                    startActivity(i);
//
//                }
//            });
//        }else{
//            Toast.makeText(null, "There are no Moments!", Toast.LENGTH_LONG).show();
//        }
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
        Log.d(null, "memlist" + jsonArray.toString());
    }
    public JSONArray getJsonArray(){
        return jsonArray;
    }
}