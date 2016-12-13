package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Kevin on 12/5/2016.
 */

////////////////////////////////////////////////////////////////////////////////////////////////////
class RemoveMemory extends AsyncTask<String, Void, Void> {
    //private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
    InputStream inputStream = null;
    String result = "";
    URLConnection conn;
    BufferedReader in;
    JSONObject successObject;

    protected void onPreExecute() {
        /*progressDialog.setMessage("Downloading Moments...");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                DownloadMarkers.this.cancel(true);
            }
        });*/
        Log.d(null, "downloading moments");
    }
    @Override
    protected Void doInBackground(String... params) {
        String id = params[0];
        String caccessKey = "a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17";
        String s;

        try {
            URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=removePage&");

            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
            Log.d(null, "data and url");
            URL nUrl = new URL(url + data);
            Log.d(null, "time to connect");
            conn = nUrl.openConnection();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((s = in.readLine()) != null) {
                //parse my json here
                Log.d(null, s);

                JSONObject jResult = new JSONObject(s);
                //get success message
                //if true, give toast message and finish()
                //if false, give toast, and bounce back to viewMemory
                successObject = jResult.getJSONObject("success");
                if (successObject.equals("true")) {
                    Log.d(null, "moment deleted");
                    return null;
                }else{
                    Log.d(null, "failed upload");
                    return null;
                }
            }
        } catch (Exception e) {
            Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
                // just going to ignore this one
            }
        }
        return null;
    }
    protected void onPostExecute(Void v) {

    }
}