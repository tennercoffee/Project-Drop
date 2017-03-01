//package rwt.kevin.memories;
//
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.TextView;
//import com.google.gson.JsonObject;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//
///**
// * Created by Kevin on 12/18/2016.
// */
//
//public class DownloadUserStats extends AsyncTask<String, Void, JsonObject> {
//    private ArrayList<JSONArray> arrayList = new ArrayList<>();
//    private JSONArray jsonArray;
//    private TextView postCountTextView;
//    private String username;
//    private String key;
//    private int postCount = 0;
//
//    protected void onPreExecute() {
//        Log.d(null, "loading user");
//    }
//    @Override
//    protected JsonObject doInBackground(String... params) {
//        username = params[0];
//        key = params[1];
//        int offset = 0;
//        try {
////            do {
//                JSONObject filterObject = new JSONObject();
//                JSONObject usernameFilterObject = new JSONObject().put("combine", "AND").put("field", "usernameString").put("option","EQUALS").put("value",username);
//                filterObject.put("0",usernameFilterObject);
//                String data = URLEncoder.encode("accessKeyString", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8")
//                        + "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")
//                                + "&" + URLEncoder.encode("filters", "UTF-8") + "=" + URLEncoder.encode(filterObject.toString(),"UTF-8");
//                URL url = new URL("http://web.webapps.centennialarts.com/page.php?command=listPages&" + data
//                        + URLEncoder.encode("offset", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(offset), "UTF-8"));
//                final URLConnection conn = url.openConnection();
//                Log.d(null, url.toString());
//                final BufferedReader[] in = new BufferedReader[1];
//                DownloadMemoryList.InputReader reader = new DownloadMemoryList.InputReader() {
//                    @Override
//                    public String getInput() {
//                        try {
//                            in[0] = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                };
//                reader.getInput();
//                String s;
//                while ((s = in[0].readLine()) != null) {
//                    jsonArray = new JSONArray(s);
//                    arrayList.add(jsonArray);
//                }
//                offset += 100;
//                Thread.sleep(150);
////            } while (jsonArray.length() >= 1);
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
//        return null;
//    }
//    protected void onPostExecute(JsonObject obj) {
//        Log.d(null, "postexecute");
//        try {
//            for (int a = 0; a < arrayList.size(); a++) {
//                Log.d(null, String.valueOf(arrayList.size()));
////                JSONArray array = arrayList.get(arrayList.size());
////                Log.d(null, String.valueOf(array.length()));
////                Log.d(null, "should be a total of " + (String.valueOf(array.length() + arrayList.size())));
//            }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        postCountTextView.setText(String.valueOf(postCount));
//    }
//    public void setTextView(TextView postCountTextView) {
//        this.postCountTextView = postCountTextView;
//    }
//}