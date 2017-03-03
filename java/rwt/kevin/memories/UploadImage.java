//package rwt.kevin.memories;
//
//import android.app.ProgressDialog;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.util.Base64;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Hashtable;
//import java.util.Map;
//
///**
// * Created by Kevin on 3/1/2017.
// */
//
//public class UploadImage {
//
//
//    public void uploadImage(final String imageString/*int id, final String path*/) throws IOException, InterruptedException {
////        final String name = "IMG_20170227_024616_241.jpg";
////        final String path = "/storage/emulated/0/Pictures/Instagram/";
//        final String caUrl = url + "/images.php";
////        Log.d(null, "image upload for " + path + " by " + atlasIdString + " " + key);
//
//        //get bitmap from file path
//
////        bitmapImage = Bitmap.createScaledBitmap(bitmapImage,parent.getWidth(),parent.getHeight(),true);
//
//        //volley image upload
//        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
//        final StringRequest postRequest = new StringRequest(Request.Method.POST, caUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    Log.d(null, response);
//                    loading.dismiss();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loading.dismiss();
//                error.printStackTrace();
//                Log.d(null, error.toString());
//            }
//        }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new Hashtable<>();
//                params.put("command", "addImage");
//                params.put("accessKey", key);
//                params.put("atlasId", atlasIdString);
//                params.put("refAlbumId", "68126");
//                params.put("ImageFile[]", /*"data:image/jpeg;base64,/" + */imageString);  //could be ImageFile[]
//                Log.d(null, params.toString());
//                return params;
//            }
//        };
//        Volley.newRequestQueue(AddMemoryActivity.this).add(postRequest);
//    }
//
//    //get encoded image string
//    public String getStringImage(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 90, baos);
//        byte[] imageBytes = baos.toByteArray();
//        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
//    }
//
//    //method to get the file path from uri
//    public String getPath(Uri uri) {
//        String path = null;
//        String document_id = null;
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            document_id = cursor.getString(0);
//            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//            cursor.close();
//        }
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            cursor.close();
//        }
//        Log.d(null, "getpath- " + path);
//        return path;
//    }
//}