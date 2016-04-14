package in.foodtalk.android;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.module.GetLocation;

public class FbLogin extends AppCompatActivity implements OnClickListener {


    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);
        FacebookSdk.sdkInitialize(getApplicationContext());


        GetLocation getLocation = new GetLocation(this);
        String latitude = getLocation.getUserLocation().latitude;
        String longitude = getLocation.getUserLocation().longitude;
        String altitude = getLocation.getUserLocation().altitude;
        String speed = getLocation.getUserLocation().speed;

        Log.d("location", "latitude: "+ latitude);
        Log.d("location", "longitude: "+ longitude);
        Log.d("location", "altitude: "+ altitude);
        Log.d("location", "speed: "+ speed);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               // System.out.print("Logged in");
                String userId = loginResult.getAccessToken().getUserId();
                Log.d("facebook","Loged in: "+ loginResult);
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
                                    Log.d("fb user info", "id: "+id+ "name: "+ name + " email: "+ email+" gender: "+ gender+" birthday: "+ birthday);
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
                Log.d("facebook","oncancel");
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
                String errormsg = getString(R.string.connection_error);
                Log.i("Error" , "Error");
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
                Log.d("faceboo: ","click login button");
                break;
        }
    }
}
