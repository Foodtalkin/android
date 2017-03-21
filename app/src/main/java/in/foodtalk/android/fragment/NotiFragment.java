package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.NotificationObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class NotiFragment extends Fragment implements ApiCallback {
    View layout;
    Config config;
    DatabaseHandler db;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    NotificationAdapter notificationAdapter;

    List<NotificationObj> notiList = new ArrayList<>();

    LinearLayout progressBar;

    TextView txtMsg;

    Context context;

    ApiCall apiCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.noti_fragment, container, false);
        config = new Config();
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_notifications);

        txtMsg = (TextView) layout.findViewById(R.id.txt_msg);
        progressBar = (LinearLayout) layout.findViewById(R.id.progress_bar);

        apiCall = new ApiCall();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (getActivity() != null){
            db = new DatabaseHandler(getActivity());
            context = getActivity();
        }

        Log.d("notification","open");
        try {
            getNotiList("getNotificationList");
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

        apiCall.apiRequestPost(getActivity(), obj, Config.URL_NOTIFICATION, tag, this);

    }
    private void loadDataIntoView(JSONObject response , String tag) throws JSONException {
        JSONArray notiArray = response.getJSONArray("notifications");
        progressBar.setVisibility(View.GONE);
        if (notiList.size()>0){
            notiList.clear();
            txtMsg.setVisibility(View.GONE);
        }else {
            txtMsg.setVisibility(View.VISIBLE);
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
        notificationAdapter = new NotificationAdapter(context, notiList);
        recyclerView.setAdapter(notificationAdapter);
    }
    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (tag.equals("getNotificationList")){
            if (response != null){
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
            }
        }
    }
}
