package in.foodtalk.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.module.GetLocation;

public class FbLogin extends AppCompatActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private CallbackManager callbackManager;


    //--location provider vars----------
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    //-----------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);

        /********** get Gps location service LocationManager object ***********/
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		/*
		  Parameters :
		     First(provider)    :  the name of the provider with which to register
		     Second(minTime)    :  the minimum time interval for notifications, in milliseconds. This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value.
		     Third(minDistance) :  the minimum distance interval for notifications, in meters
		     Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location) method will be called for each location update
        */




        /********* After registration onLocationChanged method called periodically after each 3 sec ***********/

        buildGoogleApiClient();


        FacebookSdk.sdkInitialize(getApplicationContext());

        GetLocation getLocation = new GetLocation(this);
        String latitude = getLocation.getUserLocation().latitude;
        String longitude = getLocation.getUserLocation().longitude;
        String altitude = getLocation.getUserLocation().altitude;
        String speed = getLocation.getUserLocation().speed;

       // Log.d("location", "latitude: " + latitude);
       // Log.d("location", "longitude: " + longitude);
       // Log.d("location", "altitude: " + altitude);
       // Log.d("location", "speed: " + speed);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // System.out.print("Logged in");
                String userId = loginResult.getAccessToken().getUserId();
                Log.d("facebook", "Loged in: " + loginResult);
                //-----graph api---------facebook-------
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code.
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                    String name = object.getString("name");
                                    String gender = object.getString("gender");
                                    Log.d("fb user info", "id: " + id + "name: " + name + " email: " + email + " gender: " + gender + " birthday: " + birthday);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
                //-------------

                //--Start new activity--
                Intent i = new Intent(FbLogin.this, WelcomeUsername.class);
                startActivity(i);
                finish();
                //----------------------
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("facebook", "oncancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                String errormsg = getString(R.string.connection_error);
                Log.i("Error", "Error");
                Toast.makeText(FbLogin.this,
                        errormsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                // OK button
                Log.d("faceboo: ", "click login button");
                break;
        }
    }

    //---------------location provider-------------------------------


    @Override
    public void onConnected(Bundle bundle) {


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        init();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

        }
        updateUI();
    }

    public void init(){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        Log.d("check version", "v" + currentapiVersion);
        if (currentapiVersion >= 23) {
            Log.d("check version", "Marshmallow");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            // Do something for lollipop and above versions
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("check version", " less then Marshmallow");
            // do something for phones running an SDK before lollipop
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission result", " permission granted");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d("checkSelfP","No");
                        return;
                    }else {
                        Log.d("checkSelfP","Yes");
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        //  3000,   // 3 sec
                        //  10, this);
                    }
                } else {

                    Log.d("permission result","denied permission");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        //updateUI();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
    void updateUI() {
        Log.d("GPS location","Latitude"+lat);
        Log.d("GPS location","Longitude"+lon);
        //txtOutputLat.setText(lat);
        //txtOutputLon.setText(lon);
    }
}
