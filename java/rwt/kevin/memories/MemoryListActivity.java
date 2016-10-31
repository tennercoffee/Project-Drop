package rwt.kevin.memories;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class MemoryListActivity extends MapsActivity {

    String scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrivateMemoryListActivity();
    }

    private void PrivateMemoryListActivity(){
        setContentView(R.layout.activity_private_memorylist);
        Toolbar toolbar = (Toolbar)findViewById(R.id.private_memorylist_toolbar);
        if(toolbar != null){
            toolbar.setTitle("View Moments List");
        }
        ListView listView = (ListView) findViewById(R.id.momentList);
        //download memoryList from server listPages call
        //http://web.webapps.centennialarts.com/page.php?command=listPages&limit=100&filters=
          //{%220%22:{%22combine%22:%22AND%22,%22field%22:%22pageTypesId%22,%22option%22:%22EQUALS%22,%22value%22:%2260%22},
          //%221%22:{%22combine%22:%22AND%22,%22field%22:%22id%22,%22option%22:%22GREATERTHENEQUALTO%22,%22value%22:%2234246%22}}



        //DownloadMemoryList dml = new DownloadMemoryList();
        //add code, 1 for private, 0 for public, default is 0
        //dml.execute(getScope());
        
        
        /*if(markersList != null) {
            //set loop to add all private markers that are in the markersList

            //define adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, markersList);

            if (listView != null) {
                //assign adapter to ListView
                listView.setAdapter(adapter);
                //set click Listener, will load viewMemoryActivity
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                        String selectedMemory = listView.get(position);
                        Toast.makeText(getApplicationContext(), "Opening Moment", Toast.LENGTH_LONG).show();

                        //open ViewMemoryActivity
                        Intent i = new Intent(getApplicationContext(), ViewMemoryActivity.class);

                        //add memoryID to bundle here(selectedMemory)

                        startActivity(i);
                    }
                });
            }
        }else{
            Toast.makeText(null, "There are no Moments!", Toast.LENGTH_LONG).show();
        }*/

        String cs = "coming soon!";
        TextView comingText = (TextView) findViewById(R.id.coming_soon_text);
        if(comingText != null) {
            comingText.setText(cs);
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
    private String getScope() {

        Switch mySwitch = (Switch) findViewById(R.id.mySwitch);
        //set the switch to ON
        if(mySwitch != null) {
            mySwitch.setChecked(false);

            //check the current state before we display the screen
            if (mySwitch.isChecked()) {
                //switchStatus.setText("Switch is currently ON");
                scope = "private";
            } else {
                //switchStatus.setText("Switch is currently OFF");
                scope = "public";
            }

            //attach a listener to check for changes in state
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //switchStatus.setText("Switch is currently ON");
                        //add scope parameter here
                        scope = "private";
                    } else {
                        //switchStatus.setText("Switch is currently OFF");
                        scope = "public";
                    }
                }
            });
        }
        return scope;
    }
    //http://www.mysamplecode.com/2013/04/android-switch-button-example.html
}