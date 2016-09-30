package in.foodtalk.android.module;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.constant.ConstantVar;
import in.foodtalk.android.object.LocationGps;

/**
 * Created by RetailAdmin on 14-04-2016.
 */
public class GetLocation  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    TextView txtOutputLat, txtOutputLon;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private Activity context;

    LatLonCallback latLonCallback;

    String requestFrom;



    public GetLocation(Context context, LatLonCallback latLonCallback, String requestFrom){
        this.context = (Activity) context;
        this.latLonCallback = latLonCallback;
        buildGoogleApiClient();
        this.requestFrom = requestFrom;

        Log.d("GetLocation","requestFrom: "+ requestFrom);

    }
    public LocationGps getUserLocation(){
        LocationGps location = new LocationGps();
        location.latitude = "52545";
        location.longitude = "6548";
        location.altitude = "545";
        location.speed = "20";
        return location;

    }

    public void onStart(){
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // buildAlertMessageNoGps();
            mGoogleApiClient.connect();
            Log.d("locaton manager", "gps on");
        }else {
            if (!requestFrom.equals("newPostShare")){
                 //buildAlertMessageNoGps();
                locationChecker(mGoogleApiClient, context);
            }else {
                latLonCallback.location(ConstantVar.LOCATION_DISABLED,"","");
            }
            Log.d("locaton manager", "please on gps");
        }
    }
    public void onStop(){
        mGoogleApiClient.disconnect();
    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        init();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
            updateUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended",i+"");
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
        Log.d("onConnectionFailed", connectionResult+"");
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void init() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        //Activity activity = android.app.;
        Log.d("check version", "v" + currentapiVersion);
        if (currentapiVersion >= 23) {
            Log.d("check version", "Marshmallow");
           //ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            // Do something for lollipop and above versions
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("check version", " less then Marshmallow");
            // do something for phones running an SDK before lollipop
        }
    }
    void updateUI() {
       // Log.d("GPS location","Latitude"+lat);
        Log.d("GPS location","Longitude"+lon);


        latLonCallback.location(ConstantVar.LOCATION_GOT,lat,lon);

        //txtOutputLat.setText(lat);
        //txtOutputLon.setText(lon);
    }
    private void buildAlertMessageNoGps() {
        Log.d("GetLocation", "buildAlertMessageNoGps");
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        latLonCallback.location(ConstantVar.LOCATION_DISABLED,"","");
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //---------------------------

    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                Log.d("status code", status.getStatusCode()+"");
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        Log.d("onResult","SUCCESS");
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        Log.d("onResult","RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.d("onResult","SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                    case LocationSettingsStatusCodes.SIGN_IN_REQUIRED:
                        Log.d("onResult","SIGN_IN_REQUIRED");
                        break;
                    case LocationSettingsStatusCodes.SUCCESS_CACHE:
                        Log.d("onResult","SUCCESS_CACHE");
                        break;
                }
            }
        });
    }
}



