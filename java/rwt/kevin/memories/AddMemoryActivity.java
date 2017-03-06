package rwt.kevin.memories;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddMemoryActivity extends AppCompatActivity implements View.OnClickListener {
    //declare variables
    TextView charCountTextView, locationTextView;
    Button submitButton, uploadButton, rotateRightButton, removeImageButton, rotateLeftButton;
    ImageView previewView;
    Toolbar toolbar;
    Spinner scopeSpinner, colorSpinner;
    EditText memoryInput;
    CheckBox visibilityCheckbox;
    LatLng latlngString;
    double latitude = 0, longitude = 0;
    String id, atlasIdString, key, url, imagePath, imageName;
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
        rotateRightButton = (Button) findViewById(R.id.rotate_right_button);
        rotateLeftButton = (Button) findViewById(R.id.rotate_left_button);
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
//                                String timeStamp = getTimeStamp();
//                                AddMemory addmem = new AddMemory();
//                                    addmem.execute(atlasIdString, String.valueOf(latitude), String.valueOf(longitude),
//                                            color, scope, finalVisibility, timeStamp, memoryString,
//                                            key, url);
//                                Toast.makeText(getApplicationContext(), "Adding Moment...", Toast.LENGTH_LONG).show();
                                return null;
                            }
                        };
                        addMemoryInterface.addMemory();

                        //volley image upload
                        if (uploadedImage != null || cameraImage != null) {
                            final String caUrl = url + "/images.php";
                            final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",true,true);

                            BitmapFactory.Options bmOptions;
                            try {
                                //decode file to string format
                                bmOptions = new BitmapFactory.Options();
                                Bitmap bitmapImage = BitmapFactory.decodeFile(imagePath,bmOptions);
                                Bitmap out = Bitmap.createScaledBitmap(bitmapImage, 150, 150, true);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                out.compress(Bitmap.CompressFormat.PNG, 25, baos);
                                byte[] imageBytes = baos.toByteArray();
                                final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                Log.d(null, encodedImage);
                                Log.d(null, encodedImage);
                                Log.d(null, encodedImage);Log.d(null, encodedImage);Log.d(null, encodedImage);

                                //upload image
                                final StringRequest postRequest = new StringRequest(Request.Method.POST, caUrl, new Response.Listener<String>(){
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.d(null, response);
                                            loading.dismiss();
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        loading.dismiss();
                                    }
                                }
                                ) {
                                    @Override
                                    protected Map<String,String> getParams() {
                                        try {
                                            Map<String,String> params = new HashMap<>();
                                            params.put("command", "addImage");
                                            params.put("accessKey", key);
                                            params.put("atlasId", atlasIdString);
                                            params.put("refAlbumId", "68123");
                                            params.put("ImageFile[]", encodedImage);
                                            return params;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                };
                                Volley.newRequestQueue(AddMemoryActivity.this).add(postRequest);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //finish addmemactivity
//                        finish();
                    }
                } else if(atlasIdString == null) {
                    Toast.makeText(getApplicationContext(), "Please Login to Post Moments.", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(null, "error");
                    Toast.makeText(getApplicationContext(), "error! oh no!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rotate_left_button:
                //rotate image left
                matrix.postRotate(-90f,previewView.getDrawable().getBounds().width()/2,previewView.getDrawable().getBounds().height()/2);
                previewView.setScaleType(ImageView.ScaleType.MATRIX);
                previewView.setImageMatrix(matrix);
                break;
            case R.id.rotate_right_button:
                //rotate image right
                matrix.postRotate(90f,previewView.getDrawable().getBounds().width()/2,previewView.getDrawable().getBounds().height()/2);
                previewView.setScaleType(ImageView.ScaleType.MATRIX);
                previewView.setImageMatrix(matrix);
                break;
            //open dialogue to upload or capture image
            case R.id.uploadimage_button:
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
//                                Intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                        } else {
                            builder.setCancelable(true);
                        }
                    }
                });
                builder.show();
                removeImageButton.setVisibility(View.VISIBLE);
                removeImageButton.setOnClickListener(this);
                uploadButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.remove_image_button:
                //clear image variables and preview imageview
                previewView.setImageURI(null);
                uploadButton.setVisibility(View.VISIBLE);
                uploadButton.setOnClickListener(this);
                rotateLeftButton.setVisibility(View.INVISIBLE);
                rotateRightButton.setVisibility(View.INVISIBLE);
                removeImageButton.setVisibility(View.INVISIBLE);

                uploadedImage = null;
                cameraImage = null;
                break;
        }
    }
    void setId(String id){
        this.id = id;
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
    //create custom timestamp
    String getTimeStamp(){  //TODO: move this elsewhere
        String timestampString;
        Calendar timestamp = Calendar.getInstance();
        String y = String.valueOf(timestamp.get(Calendar.YEAR));
        String m = String.valueOf(timestamp.get(Calendar.MONTH));
        String d = String.valueOf(timestamp.get(Calendar.DAY_OF_MONTH));
        String h = String.valueOf(timestamp.get(Calendar.HOUR_OF_DAY));
        String m1 = String.valueOf(timestamp.get(Calendar.MINUTE));
        String s1 = String.valueOf(timestamp.get(Calendar.SECOND));
        String m2 = String.valueOf(timestamp.get(Calendar.MILLISECOND));
        timestampString =  y + ":" + m + ":" + d + ":" + h + ":" + m1 + ":" + s1 + ":" + m2;
        return timestampString;
    }
    //method to get the file path from uri
    public String getPath(Uri uri) {
        String path = null;
        String document_id = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
        }
        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if(cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        Log.d(null, "getpath- " + path);
        return path;
    }
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
                }
                uploadedImage = data.getData();
                if (uploadedImage != null) {
                    //get image from gallery(uploadedimage)
                    previewView.setImageURI(uploadedImage);
                    //get photo orientation and rotate
                    exif = new ExifInterface(uploadedImage.toString());
                    imagePath = getPath(uploadedImage);
                    Log.d(null, imagePath);
                }
                rotateLeftButton.setOnClickListener(this);
                rotateRightButton.setOnClickListener(this);

                rotateLeftButton.setVisibility(View.VISIBLE);
                rotateRightButton.setVisibility(View.VISIBLE);
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