package rwt.kevin.memories;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.util.Calendar;

public class AddMemoryActivity extends AppCompatActivity implements View.OnClickListener {
    //declare variables
    TextView charCountTextView, locationTextView;
    Button submitButton, uploadButton, removeImageButton;
    ImageButton rotateLeftImageButton, rotateRightImageButton;
    ImageView previewView;
    Toolbar toolbar;
    Spinner scopeSpinner, colorSpinner;
    EditText memoryInput;
    CheckBox visibilityCheckbox;
    LatLng latlngString;
    double latitude = 0, longitude = 0;
    String id, atlasIdString, key, url, imagePath, albumId;
    Matrix matrix;
    Bitmap cameraImage;
    Uri uploadedImage;

    interface AddMemoryInterface {
        String addMemory();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup environment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);
        toolbar = (Toolbar) findViewById(R.id.add_memory_toolbar);
        if(toolbar != null) {
            toolbar.setTitle("Add New Moment");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        //setup views and buttons
        memoryInput = (EditText) findViewById(R.id.editInp);
        visibilityCheckbox = (CheckBox) findViewById(R.id.checkBox);
        charCountTextView = (TextView) findViewById(R.id.charCountTextView);
        locationTextView = (TextView) findViewById(R.id.location_textview);
        submitButton = (Button) findViewById(R.id.submit_button);
        uploadButton = (Button) findViewById(R.id.uploadimage_button);
        previewView = (ImageView) findViewById(R.id.image_preview);
        scopeSpinner = (Spinner) findViewById(R.id.scope_list);
        colorSpinner = (Spinner) findViewById(R.id.color_spinner);
        previewView = (ImageView) findViewById(R.id.image_preview);
        rotateRightImageButton = (ImageButton) findViewById(R.id.rotate_right_image_button);
        rotateLeftImageButton = (ImageButton) findViewById(R.id.rotate_left_image_button);
        removeImageButton = (Button) findViewById(R.id.remove_image_button);

        uploadButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        //collect information for uploading
        latlngString = getIntent().getParcelableExtra("location");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
        key = getString(R.string.ca_access_key);
        url = getString(R.string.ca_access_url);

        //populate views with data
        if(memoryInput != null){
            memoryInput.addTextChangedListener(mTextEditorWatcher);
        }
        if(locationTextView != null && latlngString != null) {
            locationTextView.setText(latlngString.toString());
            latitude = latlngString.latitude;
            longitude = latlngString.longitude;
        }
        if(scopeSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.scopeListArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            scopeSpinner.setAdapter(adapter);
        }
        if(colorSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.colorListArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(adapter);
        }

        //create image matrix for rotating images
        matrix = new Matrix();
        previewView.setScaleType(ImageView.ScaleType.MATRIX);
        previewView.setImageMatrix(matrix);
    }
    //button click functions
    @Override
    public void onClick (View view){
        Matrix matrix = new Matrix();
        previewView.setScaleType(ImageView.ScaleType.MATRIX);
        switch (view.getId()) {
            //if image is not null, first upload the memory, get back the page id, get the album id from that,
            //then upload image
            //if image is null, just add memory
            case R.id.submit_button:
                String visibility = null;
                if(memoryInput != null && scopeSpinner != null && atlasIdString != null && colorSpinner != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String atlasAccessKey = sharedPreferences.getString(getString(R.string.atlas_app_token), null);
                    final String usernameString = sharedPreferences.getString("usernameString", null);
                    final String atlasIdString = sharedPreferences.getString("atlasIdNumberString", null);
                    final String scope = scopeSpinner.getSelectedItem().toString();
                    final String color = colorSpinner.getSelectedItem().toString();
                    final String memoryString = memoryInput.getText().toString();
                    if (visibilityCheckbox != null && visibilityCheckbox.isChecked()) {
                        visibility = "v";
                    } else if (visibilityCheckbox != null && !visibilityCheckbox.isChecked()) {
                        visibility = "i";
                    }
                    final String finalVisibility = visibility;

                    if (usernameString != null && atlasAccessKey != null) {
                        AddMemoryInterface addMemoryInterface = new AddMemoryInterface() {
                            @Override
                            public String addMemory() {
                                Memory memory = new Memory();
                                String timeStamp = memory.getTimeStamp();
                                AddMemory addmem = new AddMemory();
                                addmem.execute(atlasIdString, String.valueOf(latitude), String.valueOf(longitude),
                                        color, scope, finalVisibility, timeStamp, memoryString,
                                        key, url);

                                //add image if available
//                                if (imagePath != null && id != null) {
//                                    Log.d(null, imagePath);
//                                    ListAlbums listAlbums = new ListAlbums();
//                                    listAlbums.setContext(getApplicationContext());
//                                    listAlbums.execute(url, id,"Public",key,imagePath, atlasIdString);

//                                    UploadFile upload = new UploadFile();
//                                    upload.setContext(getApplicationContext());
//                                    upload.setAlbumId("14543");
//                                    upload.execute(atlasIdString, url, key, imagePath);
//                                } else if(imagePath == null) {
//                                    Log.d(null, "null path");
//                                } else if (id == null) {
//                                    Log.d(null, "null id");
//                                }

                                Toast.makeText(getApplicationContext(), "Adding Moment...", Toast.LENGTH_LONG).show();
                                return null;
                            }
                        };
                        addMemoryInterface.addMemory();
                    }
                } else if(atlasIdString == null) {
                    Toast.makeText(getApplicationContext(), "Please Login to Post Moments.", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(null, "error");
                    Toast.makeText(getApplicationContext(), "error! oh no!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rotate_left_image_button:
                //rotate image left
                previewView.setRotation(previewView.getRotation() + -90);
                break;
            case R.id.rotate_right_image_button:
                //rotate image right
                previewView.setRotation(previewView.getRotation() + 90);
                break;
            //open dialogue to upload or capture image
            case R.id.uploadimage_button:
                //check if you have permission to use storage/select image from gallery
                int locationPermissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                //if not permitted, ask for permission
                if(locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "NOT GRANTED", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            0);
                } else {
                    //if permission is granted, go to upload/capture dialog
                    //switch buttons
                    removeImageButton.setVisibility(View.VISIBLE);
                    removeImageButton.setOnClickListener(this);
                    uploadButton.setVisibility(View.INVISIBLE);
                    uploadButton.setOnClickListener(null);

                    CharSequence options[] = new CharSequence[] {"Upload Image", "Take Photo", "Cancel"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddMemoryActivity.this);
                    builder.setTitle("Choose an Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                //uploadimage
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
                            } else if (which == 1){
                                //take photo
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                            } else {
                                builder.setCancelable(true);
                            }
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.remove_image_button:
                //switch buttons
                uploadButton.setVisibility(View.VISIBLE);
                uploadButton.setOnClickListener(this);
                rotateLeftImageButton.setVisibility(View.INVISIBLE);
                rotateRightImageButton.setVisibility(View.INVISIBLE);
                removeImageButton.setVisibility(View.INVISIBLE);

                //clear image variables and preview imageview
                previewView.setImageURI(null);
                uploadedImage = null;
                cameraImage = null;
                break;
        }
    }
    void setId(String id){
        this.id = id;
    }
    void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
    //editTextBox text counter
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           charCountTextView.setText(String.valueOf(140-s.length()));
        }
        public void afterTextChanged(Editable s) {
        }
    };
    public boolean onOptionsItemSelected(MenuItem item){
        //back/up button function
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            cameraImage = (Bitmap) extras.get("data");
            ExifInterface exif = null;
            try {
                if (cameraImage != null) {
                    //get image from camera(captured image)
                    previewView.setImageBitmap(cameraImage);
                    //get photo orientation and rotate
                    exif = new ExifInterface(cameraImage.toString());
                    Log.d(null, exif.toString());
                }
                uploadedImage = data.getData();
                if (uploadedImage != null) {
                    //get image from gallery(uploadedimage)
                    previewView.setImageURI(uploadedImage);
                    //get photo orientation and rotate
                    exif = new ExifInterface(uploadedImage.toString());
                    Image image = new Image();
                    imagePath = image.getPath(uploadedImage,getContentResolver());
                    Log.d(null, imagePath);
                }
                rotateLeftImageButton.setVisibility(View.VISIBLE);
                rotateRightImageButton.setVisibility(View.VISIBLE);
                rotateLeftImageButton.setOnClickListener(this);
                rotateRightImageButton.setOnClickListener(this);

                if(exif != null) {
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d(null, "orientation value: " + String.valueOf(orientation));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}