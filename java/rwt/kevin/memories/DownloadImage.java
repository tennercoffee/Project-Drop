package rwt.kevin.memories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

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