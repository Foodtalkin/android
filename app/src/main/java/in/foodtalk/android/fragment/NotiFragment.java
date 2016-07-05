package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.NotificationAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.NotificationObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class NotiFragment extends Fragment {
    View layout;
    Config config;
    DatabaseHandler db;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    NotificationAdapter notificationAdapter;

    List<NotificationObj> notiList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.noti_fragment, container, false);
        config = new Config();
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_notifications);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (getActivity() != null){
            db = new DatabaseHandler(getActivity());
        }

        Log.d("notification","open");
        try {
            getNotiList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }

    public void getNotiList(final String tag) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("notificationGroup", "1");


        //Log.d("post page number param", pageNo+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_NOTIFICATION, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);
                                loadDataIntoView(response , "load");
                               // loadDataIntoView(response , tag);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                   // logOut();
                                }else {
                                    Log.e("Response status", "some error");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Json Error", e+"");
                        }
                        //----------------------
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
               // showToast("Please check your internet connection");

                if (tag.equals("load")){
                   // tapToRetry.setVisibility(View.VISIBLE);
                  //  progressBar.setVisibility(View.GONE);
                }


                // hideProgressDialog();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }
    private void loadDataIntoView(JSONObject response , String tag) throws JSONException {
        JSONArray notiArray = response.getJSONArray("notifications");
        if (notiList.size()>0){
            notiList.clear();
        }
        for (int i=0;i<notiArray.length();i++){
            NotificationObj current = new NotificationObj();
            current.elementId = notiArray.getJSONObject(i).getString("elementId");
            current.raiserId = notiArray.getJSONObject(i).getString("raiserId");
            current.raiserName = notiArray.getJSONObject(i).getString("raiserName");
            current.raiserImage = notiArray.getJSONObject(i).getString("raiserImage");
            current.message = notiArray.getJSONObject(i).getString("message");
            current.eventDate = notiArray.getJSONObject(i).getString("eventDate");
            current.eventType = notiArray.getJSONObject(i).getString("eventType");
            notiList.add(current);
        }
        notificationAdapter = new NotificationAdapter(getActivity(), notiList);
        recyclerView.setAdapter(notificationAdapter);
    }
}
