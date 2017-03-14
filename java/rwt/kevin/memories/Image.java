package rwt.kevin.memories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.android.volley.toolbox.Volley.newRequestQueue;

class Image {

    String getPath(Uri uri, ContentResolver context) {
        String path = null;
        String document_id = null;
        Cursor cursor = context.query(uri, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
        }
        cursor = context.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if(cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class UploadImage extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject;
    private String albumId,key,id;
    private Context context;

    protected void onPreExecute() {
        Log.d(null, "upload image");
    }
    @Override
    protected Void doInBackground(String... params) {
        id = params[0];
        String url = params[1];
        key = params[2];
        String imagePath = params[3];
        if (imagePath != null) {
            Log.d(null, imagePath);
            final String caUrl = url + "/images.php?";

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

                //upload image
                final StringRequest postRequest = new StringRequest(Request.Method.POST, caUrl, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(null, response);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
                            params.put("refAlbumId", albumId);
                            params.put("ImageFile[]",encodedImageString);
                            Log.d(null, params.toString());
                            return params;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("enctype","multipart/form-data");
//                        params.put("method", "POST");
                        return params;
                    }
                };
                RequestQueue r = Volley.newRequestQueue(context);
                r.add(postRequest).setTag("upload");
//                r.start();
                Log.d(null, "before");
                Log.d(null,r.toString());
                Log.d(null, "after");
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
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }
        } else {
            Log.d(null, "null jsonobject");
        }
    }
    public void setContext(Context context) {
        this.context = context;
    }
    void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class DownloadImage extends AsyncTask<String, Void, JsonObject>{
    private ImageView imageView;
    private Bitmap bmp;

    protected void onPreExecute() {
        Log.d(null, "loading image");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        String urlString = params[0];
        try {
            urlString = urlString.replaceAll("\\\\","");
            URL url = new URL("http:" + urlString);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (UnsupportedEncodingException e1) {
            Log.d(null, e1.toString());
            e1.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.d("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.d("IOException", e4.toString());
            e4.printStackTrace();
        } catch (Exception e) {
            Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
        }
        return null;
    }
    protected void onPostExecute(JsonObject obj) {
        imageView.setImageBitmap(bmp);
    }
    void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class ListAlbums extends AsyncTask<String, String, Void> {
    private ArrayList<JSONArray> albumArrayList = new ArrayList<>();
    private JSONObject jsonObject;
    private String key, albumId, caUrl, imagePath, atlasId;
    private Context context;

    protected void onPreExecute() {
        Log.d(null, "adding memory");
    }
    @Override
    protected Void doInBackground(String... params) {
        caUrl = params[0];
        String memoryId = params[1];
        String scope = params[2];
        key = params[3];
        imagePath = params[4];
        atlasId = params[5];

        String inputString;
        Log.d(null, caUrl + " " + memoryId + " " + key);

        try {
            //create filters
            JSONObject idfilterObject = new JSONObject().put("combine", "AND").put("field", "refPageId").put("option", "EQUALS").put("value", memoryId);
            JSONObject publicFilterObject = new JSONObject().put("combine", "OR").put("field", "scope").put("option", "EQUALS").put("value", "Public");
            JSONObject privateFilterObject = new JSONObject().put("combine", "AND").put("field", "scope").put("option", "EQUALS").put("value", "Private");

            //combine filters
            JSONObject filterObject = new JSONObject().put("0", publicFilterObject).put("1", privateFilterObject);
            JSONObject scopeFilterObject = new JSONObject().put("combine","AND").put("additional",filterObject);
            JSONObject jsonObject = new JSONObject().put("0",scopeFilterObject).put("1",idfilterObject);

            //form url
            String data = "&" + URLEncoder.encode("filters", "UTF-8") + "="  + URLEncoder.encode(jsonObject.toString(), "UTF-8")
                    + "&" + URLEncoder.encode("accessKey","UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
            URL url = new URL(caUrl + "/images.php?command=listAlbums" + data
                    + "&" + URLEncoder.encode("filters", "UTF-8") + "="  + URLEncoder.encode(jsonObject.toString(), "UTF-8")
                    + "&" + URLEncoder.encode("accessKey","UTF-8") + "=" + URLEncoder.encode(key, "UTF-8"));
            Log.d(null, url.toString());

            final URLConnection conn= url.openConnection();
            final BufferedReader[] in = new BufferedReader[1];
            DownloadMemoryMap.InputReader reader = new DownloadMemoryMap.InputReader() {
                @Override
                public String getInput() {
                    try {
                        in[0] = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return String.valueOf(in[0]);
                }
            };
            reader.getInput();
            while ((inputString = in[0].readLine()) != null) {
                JSONArray jsonArray = new JSONArray(inputString);
                albumArrayList.add(jsonArray);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(Void v) {
        if (albumArrayList != null) {
            for (int a = 0; a < albumArrayList.size(); a++) {
                JSONArray array = albumArrayList.get(a);
                if (array != null) {
                    try {
                        Log.d(null, "loaded");
                        String titleObject = jsonObject.get("title").toString();
                        String albumIdNumber = null;
                        if (titleObject.equals("Public")) {
                            albumIdNumber = String.valueOf(jsonObject.getInt("id"));
                            Log.d(null, albumIdNumber);
                        }
                        AddMemoryActivity addMemoryActivity = new AddMemoryActivity();
                        addMemoryActivity.setAlbumId(albumIdNumber);

                        UploadImage upload = new UploadImage();
                        upload.setAlbumId(albumId);
                        upload.setContext(context);
                        upload.execute(atlasId, caUrl, key, imagePath);
                        jsonObject.get("Public");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(null, "null jsonobject");
                }
            }
        }
    }
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}