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

class AddMemory extends AsyncTask<String, String, Void> {
    private JSONObject jsonObject;
    private String id, imagePath, imageName;

    protected void onPreExecute() {
        Log.d(null, "adding memory");
    }
    @Override
    protected Void doInBackground(String... params) {
        String atlasIdString = params[0];
        String latitude = params[1];
        String longitude = params[2];
        String color = params[3];
        String scope = params[4];
        String visibility = params[5];
        String timeStamp = params[6];
        String memoryString = params[7];
        String key = params[8];
        String urlString = params[9];
//        imagePath = params[10];
//        imageName = params[11];
        String inputString;

        try {
            JSONObject pageValuesObject = new JSONObject();
            JSONObject titleObject = new JSONObject().put("pageTypeStringAttributesId", "38").put("value", URLEncoder.encode(memoryString, "UTF-8"));
            JSONObject latObject = new JSONObject().put("pageTypeStringAttributesId", "41").put("value", latitude);
            JSONObject lngObject = new JSONObject().put("pageTypeStringAttributesId", "44").put("value", longitude);
            JSONObject timeObject = new JSONObject().put("pageTypeStringAttributesId", "47").put("value", timeStamp);
            JSONObject colorObject = new JSONObject().put("pageTypeStringAttributesId", "50").put("value", color);
            pageValuesObject.put("0", titleObject).put("1", timeObject).put("2", latObject).put("3", lngObject).put("4",colorObject);
            String dataString = URLEncoder.encode("titleString", "UTF-8") + "=" + URLEncoder.encode(memoryString, "UTF-8")
                    + "&" +URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(visibility, "UTF-8")
                    + "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode(scope, "UTF-8")
                    + "&" + URLEncoder.encode("atlasId", "UTF-8") + "=" + URLEncoder.encode(atlasIdString, "UTF-8")
                    + "&" + URLEncoder.encode("pageTypeId", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
                    + "&" + URLEncoder.encode("pageValues", "UTF-8") + "=" + URLEncoder.encode(pageValuesObject.toString(),"UTF-8");
            Log.d(null, pageValuesObject.toString());
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
}