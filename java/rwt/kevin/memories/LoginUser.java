//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//class LoginUser extends AsyncTask<String, String, Void> {
//    URL url = null;
//    protected void onPreExecute() {
//        Log.d(null, "logging in");
//    }
//    @Override
//    protected Void doInBackground(String... params) {
//        String url = params[0];
//        Map<String,String> urlMap = getURLMap(url);
//        String username = urlMap.get("username");
//        String accessKey = urlMap.get("accessKey");
//        String userid = urlMap.get("rwt.kevin.memories://returnApp?/?userId");
//        Log.d(null, userid + " " + username + " " + accessKey);
//
//        if(userid != null && accessKey != null) {
////            Intent i = new Intent(null, MapsActivity.class);
////            Toast.makeText(getApplicationContext(), "signed in as: " + username, Toast.LENGTH_LONG).show();
////            i.putExtra("username", username);
////            i.putExtra("userid", userid);
////            i.putExtra("accessKey", accessKey);
////            startActivity(i);
//        } else {
////            Intent i = new Intent(getApplicationContext(), MainActivity.class);
////            Toast.makeText(getApplicationContext(), "error signing in", Toast.LENGTH_LONG).show();
////            startActivity(i);
//        }
//        return null;
//    }
//    protected void onPostExecute(Void v) {
//        Log.d(null, "logged in");
//    }
//
//}