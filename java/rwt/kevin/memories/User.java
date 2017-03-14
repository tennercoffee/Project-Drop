package rwt.kevin.memories;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class User {
}
////////////////////////////////////////////////////////////////////////////////////////////////////
class DownloadUser extends AsyncTask<String, Void, JsonObject> {
    private JSONObject jsonObject;
    private TextView usernameTextView;
    private ImageView profileImageView;

    protected void onPreExecute() {
        Log.d(null, "loading user");
    }
    @Override
    protected JsonObject doInBackground(String... params) {
        String username = params[0];
        String atlasKey = params[1];
        String atlasUrl = params[2];
        try {
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                    + "&" + URLEncoder.encode("accessKey", "UTF-8") + "=" + URLEncoder.encode(atlasKey, "UTF-8");
            URL url = new URL(atlasUrl + "/account.php?command=details&" + data);
            Thread.sleep(100);
            Log.d(null, url.toString());
            final URLConnection conn = url.openConnection();
            Thread.sleep(100);
            final BufferedReader[] in = new BufferedReader[1];
            DownloadMemoryList.InputReader reader = new DownloadMemoryList.InputReader() {
                @Override
                public String getInput() {
                    try {
                        in[0] = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            reader.getInput();
            String s;
            while ((s = in[0].readLine()) != null) {
                jsonObject = new JSONObject(s);
                Log.d(null, s);
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
    protected void onPostExecute(JsonObject obj) {
        Log.d(null, "postexecute");
        try {
            String username = jsonObject.getString("username");
            String photoUrl = jsonObject.getString("photo");

            //set usernameString and profile photo url
            ProfileActivity profileActivity = new ProfileActivity();
            profileActivity.setUser(usernameTextView, username, profileImageView, photoUrl);

            ViewMemoryActivity view = new ViewMemoryActivity();
            view.setUser(usernameTextView,username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void setTextView(TextView usernameTextView, ImageView profileImageView) {
        this.profileImageView = profileImageView;
        this.usernameTextView = usernameTextView;
    }
}