package rwt.kevin.memories;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends MapsActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        if(toolbar != null){
            toolbar.setTitle("Settings");
        }

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        if(logoutButton != null){
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    new AlertDialog.Builder(getApplicationContext())
							.setTitle("Confirm")
							.setMessage("Do you really want to logout?")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									//Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    //startActivity(i);
                                    //TODO: send api call to logout of user
								}
							})
							.setNegativeButton(android.R.string.no, null).show();
                }
            });
        }
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        if(cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
