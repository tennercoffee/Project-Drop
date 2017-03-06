//package rwt.kevin.memories;
//
//import android.*;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.widget.Toast;
//
///**
// * Created by Kevin on 3/5/2017.
// */
//
//public class PermissionUtilities {
//    private static final int MY_LOCATION_REQUEST_CODE = 0;
//
//
//
//
//    public boolean hasLocationPermission(Context c) {
//        //check if you have permission to use location
//        int locationPermissionCheck = ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        //if not permitted, ask for permission
//        if(locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(c, "NOT GRANTED", Toast.LENGTH_LONG).show();
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_LOCATION_REQUEST_CODE);
//        } else {
//            return true;
//        }
//        return false;
//    }
//}
//
//
