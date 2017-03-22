package in.foodtalk.android.apicall;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.UserAgent;

/**
 * Created by RetailAdmin on 29-04-2016.
 */
public class PostReportApi implements ApiCallback {

    DatabaseHandler db;
    Config config;
    String apiUrl;
    Context context;
    PostDeleteCallback deleteCallback;

    ApiCall apiCall;

    String from;

    String tag1;

   // String tag;
    public PostReportApi (Context context){
        config = new Config();
        db = new DatabaseHandler(context);
        this.context = context;
        deleteCallback = (PostDeleteCallback) context;
    }
    public void postReport(String sessionId, String postId, final String tag, final String from) throws JSONException {

       // this.tag = tag;
        if (tag.equals("delete")){
            apiUrl = config.URL_POST_DELETE;
        }else if(tag.equals("report")) {
            apiUrl = config.URL_POST_REPORT;
        }

        this.from = from;

        JSONObject obj = new JSONObject();
        //obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("sessionId", sessionId);
        obj.put("postId", postId);

        apiCall = new ApiCall();

        tag1 = tag;

        apiCall.apiRequestPost(context, obj, apiUrl, "postReport", this);
    }

    public void showToast(String msg){
        Toast toast= Toast.makeText(context,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (tag.equals("postReport")){
            if (response != null){
                try {
                    String status = response.getString("status");
                    if (!status.equals("error")){
                        //-- getAndSave(response);
                        //loadDataIntoView(response);
                        if(tag1.equals("report")){
                            showToast(context.getString(R.string.postReportMsg));
                        }else if(tag1.equals("delete")){
                            Log.d("PostReportApi","delete successfully");
                            deleteCallback.postDelete(from);
                        }
                    }else {
                        String errorCode = response.getString("errorCode");
                        if(errorCode.equals("6")){
                            Log.d("Response error", "Session has expired");
                            //logOut();
                        }else if(errorCode.equals("7")) {
                            Log.e("Response error", "Already Report");
                            showToast(context.getString(R.string.postReportMsg));
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
