package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Kevin on 11/28/2016.
 */

public class DownloadProfile extends AsyncTask<String, String, Void> {
    String username;
    String userid;
    String accessKey;
    protected void onPreExecute() {
        Log.d(null, "downloading profile");
    }
    @Override
    protected Void doInBackground(String... params) {
        Log.d(null, "postExecute: " + username + " " + userid);
//        try {
//            URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=getPage&");
//            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
//                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(caccessKey, "UTF-8");
//            Log.d(null, url.toString() + data);
//            nUrl = new URL(url + data);
//            conn = nUrl.openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            Thread.sleep(100);
//            String s;
//            while ((s = in.readLine()) != null) {
//                Log.d(null, "jArray" + s);
//                jsonArray = new JSONArray(s);
//            }
//        } catch (UnsupportedEncodingException e1) {
//            Log.d(null, e1.toString());
//            e1.printStackTrace();
//        } catch (IllegalStateException e3) {
//            Log.d("IllegalStateException", e3.toString());
//            e3.printStackTrace();
//        } catch (IOException e4) {
//            Log.d("IOException", e4.toString());
//            e4.printStackTrace();
//        } catch (Exception e) {
//            Log.d(null, "StringBuilding & BufferedReader\", \"Error converting result ");
//        }

        return null;
    }
    protected void onPostExecute(Void v) {
        Log.d(null, "postExecute");
//        Log.d(null, jsonArray.toString());
        ProfileActivity p = new ProfileActivity();
        if(username != null && userid != null && accessKey != null){
            Log.d(null, "setuser");
            p.setUser(username, userid, accessKey);
        }
    }
    public void setUser(String username, String userid, String accessKey) {
        this.username = username;
        this.userid = userid;
        this.accessKey = accessKey;
    }
}