package rwt.kevin.memories;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 3/8/2017.
 */

public class UploadFile extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject;
    private String id, imagePath, url,key;
    Context context;

    protected void onPreExecute() {
        Log.d(null, "upload image");
    }
    @Override
    protected Void doInBackground(String... params) {
        id = params[0];
        url = params[1];
        key = params[2];
        imagePath = params[3];
        if (imagePath != null) {
            Log.d(null, imagePath);
            final String caUrl = url + "/images.php?";
//            final ProgressDialog loading = ProgressDialog.show(context,"Uploading...","Please wait...",true,true);

            try {
                //decode file to string format
                BitmapFactory.Options bmOptions;
                bmOptions = new BitmapFactory.Options();
                Bitmap bitmapImage = BitmapFactory.decodeFile(imagePath,bmOptions);
                Bitmap out = Bitmap.createScaledBitmap(bitmapImage, 150, 150, false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                out.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                byte[] imageBytes = baos.toByteArray();
                final String encodedImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Log.d(null, encodedImageString);

                //upload image
                final StringRequest postRequest = new StringRequest(Request.Method.POST, caUrl, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(null, response);
//                            loading.dismiss();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        loading.dismiss();
//                        Toast.makeText(getApplicationContext(), "error! try again!", Toast.LENGTH_LONG).show();
                    }
                }
                ) {
                    @Override
                    protected Map<String,String> getParams() {
                        try {
                            Map<String,String> params = new HashMap<>();

                            params.put("command", "addImage");
                            params.put("accessKey", key);
                            params.put("atlasId", id);
                            params.put("refAlbumId", "68123");
                            params.put("ImageFile[]",encodedImageString);
                            return params;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                Volley.newRequestQueue(context).add(postRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        if (jsonObject != null) {
            Log.d(null, jsonObject.toString());
            try {
                int idObject = jsonObject.getInt("id");
                String successObject = jsonObject.getString("success");
                if (successObject.equals("true")) {
                    AddMemoryActivity ama = new AddMemoryActivity();
                    ama.setId(String.valueOf(idObject));
//                    ama.uploadImage(idObject, imagePath);
                }
            } catch (JSONException j) {
                j.printStackTrace();
            } /*catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        } else {
            Log.d(null, "null jsonobject");
        }
    }
    public void setContext(Context context) {
        this.context = context;
    }
}