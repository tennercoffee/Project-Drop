package rwt.kevin.memories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReportMemoryActivity extends ViewMemoryActivity implements View.OnClickListener{
    //declare variables
    TextView charCountTextView, memoryIdTextView;
    Button cancelButton, submitButton;
    Toolbar toolbar;
    EditText reportIntput;
    String atlasUserIdNumber,id,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_memory);
        toolbar = (Toolbar) findViewById(R.id.report_memory_toolbar);
        if(toolbar != null) {
            toolbar.setTitle("Report Moment");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //get information from viewmem
        Intent i = getIntent();
        id = i.getStringExtra("idString");

        //this checks to see if there is a userIdNumber/username stored in shared prefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        atlasUserIdNumber = sharedPreferences.getString("atlasIdNumberString", null);
        usernameString = sharedPreferences.getString("usernameString", null);

        //setup buttons and views
        submitButton = (Button) findViewById(R.id.submit_memory_report_button);
        reportIntput = (EditText) findViewById(R.id.editInp);
        charCountTextView = (TextView) findViewById(R.id.charCountTextView);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        memoryIdTextView = (TextView) findViewById(R.id.memory_id_text_view);

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        if(reportIntput != null){
            //add text counter to edittext box
            reportIntput.addTextChangedListener(mTextEditorWatcher);
        }
        if(id != null && username != null) {
            String s = "Moment ID: " + id;
            Log.d(null, s + " by " + username);
            memoryIdTextView.setText(s);
        } else {
            memoryIdTextView.setText("Please Log In First");
        }
    }
    @Override
    public void onClick (View view){
        switch (view.getId()) {
            case R.id.submit_memory_report_button:
                //submit report
                AddMemoryActivity a = new AddMemoryActivity(); //TODO: move elsewhere
                String timestamp = a.getTimeStamp();
                if (atlasUserIdNumber != null) {
                    AddReport addReport = new AddReport();
                    addReport.execute(id, atlasUserIdNumber, timestamp, getString(R.string.ca_access_key), getString(R.string.ca_access_url), reportIntput.getText().toString());
                    Toast.makeText(getApplicationContext(), "Report added", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case R.id.cancel_button:
                //return to viewmem
                finish();
                break;
        }
    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        //textbox counter
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            charCountTextView.setText(String.valueOf(140-s.length()));
        }
        public void afterTextChanged(Editable s) {
        }
    };
}