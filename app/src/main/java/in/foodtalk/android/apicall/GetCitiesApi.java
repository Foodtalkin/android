package in.foodtalk.android.apicall;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.communicator.ApiCallback;

/**
 * Created by RetailAdmin on 06-09-2016.
 */
public class GetCitiesApi {


    public static void getRequest(Context context, String searchKey, final ApiCallback apiCallback){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        //String url ="http://www.google.com";

        // Request a string response from the provided URL.
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+searchKey+"&types=(cities)&key=AIzaSyCkhfzw_JLdFtJkwkHEUNBtsHm_GRNF59Y";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getRequest", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("GoogleApi json", jsonObject+"");
                            apiCallback.apiResponse(jsonObject, "googleApiCities");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //onResponse(response);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                //Log.d("error response-", +response.statusCode+" : "+ new String(response.data));
                //mTextView.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }
}
