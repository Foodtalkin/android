package in.foodtalk.android.module;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import in.foodtalk.android.object.LocationGps;

/**
 * Created by RetailAdmin on 14-04-2016.
 */
public class GetLocation {

    TextView txtOutputLat, txtOutputLon;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private Context context;
    public GetLocation(Context context){
        this.context = context;
    }
    public LocationGps getUserLocation(){
        LocationGps location = new LocationGps();
        location.latitude = "52545";
        location.longitude = "6548";
        location.altitude = "545";
        location.speed = "20";
        return location;

    }


}



