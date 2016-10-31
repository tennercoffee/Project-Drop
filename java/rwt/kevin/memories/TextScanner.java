package rwt.kevin.memories;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Scanner;
import java.util.regex.Pattern;

class TextScanner {
    private String timestamp;
    private String regionCode;

    String descSplitter(String desc, int formatCode, int resultCode) {
        //if formatcode is 1, run timestamp scanner, then location scanner
        //if formatcode is 0, run just location scanner
        //if resultCode is 0, return location
        //if 1, return timestamp
        Scanner scanner = new Scanner(desc);
        if(formatCode == 1) {
            timestamp = scanner.next();
            if(scanner.hasNextInt()) {
                regionCode = scanner.next();
            } else {
                Log.d(null, "regioncode error");
            }
        }
        //required for AddMemory, can possible split later
        if (scanner.hasNext("lat/lng:")) {
            scanner.skip("lat/lng:");
        } else if (scanner.hasNext(" lat/lng")) {
            scanner.skip(" lat/lng: ");
        }

        String latlngString = scanner.next();

        if(resultCode == 1) {
            return timestamp;
        } else if(resultCode == 0){
            return latlngString;
        } else {
            return null;
        }
    }
    String locationSplitter(String location){
        Scanner scanner = new Scanner(location);
        if (scanner.hasNext("lat/lng:")) {
            scanner.skip(Pattern.compile("lat/lng:"));
        } else if (scanner.hasNext(" lat/lng")) {
            scanner.skip(Pattern.compile(" lat/lng: "));
        }

        location = location.startsWith("(") ? location.substring(1) : location;
        location = location.endsWith(")") ? location.substring(0, location.length() - 1) : location;

        //double latitude;
        //double longitude;
        //String[] latlng = location.split(",");

        //latitude = Double.parseDouble(latlng[0]);
        //longitude = Double.parseDouble(latlng[1]);

        return location;
    }
    LatLng locationStringtoLatLng(String location){
        Scanner scanner = new Scanner(location);
        if (scanner.hasNext("lat/lng:")) {
            scanner.skip(Pattern.compile("lat/lng:"));
        } else if (scanner.hasNext(" lat/lng")) {
            scanner.skip(Pattern.compile(" lat/lng: "));
        }

        location = location.startsWith("(") ? location.substring(1) : location;
        location = location.endsWith(")") ? location.substring(0, location.length() - 1) : location;

        double latitude;
        double longitude;
        String[] latlng = location.split(",");

        latitude = Double.parseDouble(latlng[0]);
        longitude = Double.parseDouble(latlng[1]);

        return new LatLng(latitude,longitude);
    }
}