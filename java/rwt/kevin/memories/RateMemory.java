package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;

/**
 * Created by Kevin on 12/5/2016.
 */

class RateMemory extends AsyncTask<String, Void, Void> {
    InputStream inputStream = null;
    String result = "";
    URLConnection conn;
    BufferedReader in;
    JSONObject successObject;

    protected void onPreExecute() {
        Log.d(null, "rating async");
    }
    @Override
    protected Void doInBackground(String... params) {
        String id = params[0];
        String caccessKey = "a76c33b2-4c76-11e6-8c59-e0cb4ea6dd17";
        String s;


        //here we will update page values/moment values
		/*try {
			URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=removePage&");

			String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
					+ "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
			URL nUrl = new URL(url + data);
			conn = nUrl.openConnection();
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
		}*/
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((s = in.readLine()) != null) {
                //parse my json here
                Log.d(null, s);

				/*JSONObject jResult = new JSONObject(s);
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
				}*/
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
        //update viewmemoryactivity with current rating
    }
}
