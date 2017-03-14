package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Report {
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class AddReport extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject;

    protected void onPreExecute() {
        Log.d(null, "submitting report");
    }
    @Override
    protected Void doInBackground(String... params) {
        String memoryId = params[0];
        String atlasIdString = params[1];
        String timeStamp = params[2];
        String key = params[3];
        String urlString = params[4];
        String report = params[5];
        String inputString;

        try {
            JSONObject pageValuesObject = new JSONObject();
            JSONObject titleObject = new JSONObject().put("pageTypeStringAttributesId", "32").put("value", report);
            JSONObject timeObject = new JSONObject().put("pageTypeStringAttributesId", "35").put("value", timeStamp);
            JSONObject memoryIdObject = new JSONObject().put("pageTypeStringAttributesId", "29").put("value", memoryId);
            JSONObject usernameObject = new JSONObject().put("pageTypeStringAttributesId", "26").put("value", atlasIdString);
            pageValuesObject.put("0", titleObject).put("1", timeObject).put("2", memoryIdObject).put("3", usernameObject);

            String dataString = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode("REPORT", "UTF-8")
                    + "&" +URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
                    + "&" + URLEncoder.encode("atlasId", "UTF-8") + "=" + URLEncoder.encode(atlasIdString, "UTF-8")
                    + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("23", "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
                    + "&" + URLEncoder.encode("pageValues", "UTF-8") + "=" + URLEncoder.encode(pageValuesObject.toString(),"UTF-8");
            Log.d(null, dataString);
            URL url = new URL(urlString + "/page.php?command=addPage&" + dataString);
            Log.d(null, url.toString());

            final URLConnection conn= url.openConnection();
            Thread.sleep(500);
            final BufferedReader[] in = new BufferedReader[1];
            DownloadMemoryList.InputReader reader = new DownloadMemoryList.InputReader() {
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
                jsonObject = new JSONObject(inputString);
            }
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
    protected void onPostExecute(Void v) {
        if (jsonObject != null) {
            Log.d(null, jsonObject.toString());
            try {
                int idObject = jsonObject.getInt("id");
                String successObject = jsonObject.getString("success");
                if (successObject.equals("true")) {
                    Log.d(null, "success true...id: " + String.valueOf(idObject));
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }
        } else {
            Log.d(null, "null jsonobject");
        }
    }
}