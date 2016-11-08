package in.foodtalk.android.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.CommentAdapter;
import in.foodtalk.android.adapter.FollowedListAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.MentionCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.CommentObj;
import in.foodtalk.android.object.FollowedUsersObj;
import in.foodtalk.android.object.PostObj;
import in.foodtalk.android.object.UserMention;

/**
 * Created by RetailAdmin on 20-06-2016.
 */
public class CommentFragment extends Fragment implements MentionCallback  {
    View layout;
    Config config;
    DatabaseHandler db;

    String postId;

    PostObj postObj;

    EditText edit_comment;
    List<CommentObj> postDataList  = new ArrayList<>();
    List<FollowedUsersObj> fUserList = new ArrayList<>();
    List<FollowedUsersObj> mentionUser = new ArrayList<>();

    JSONArray mentionUArray = new JSONArray();







    CommentAdapter commentAdapter;
    FollowedListAdapter followedListAdapter;

    RecyclerView recyclerView , recyclerViewMention;
    LinearLayoutManager linearLayoutManager;

    JSONObject response;

    MentionCallback mentionCallback;


    TextView txtUserName;

    TextView btnCommentSend;

    String userId;

    Context context;


    /*public CommentFragment (String postId){
        this.postId = postId;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.comment_fragment, container, false);

        postId =  getArguments().getString("postId");

        recyclerViewMention = (RecyclerView) layout.findViewById(R.id.recycler_view_mention);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_comment);
        recyclerView.setHasFixedSize(true);
        txtUserName = (TextView) layout.findViewById(R.id.txt_name_comment);
        edit_comment = (EditText) layout.findViewById(R.id.edit_comment);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerViewMention.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setLayoutManager(linearLayoutManager);

        mentionCallback = this;

        if (getActivity() != null){
            context = getActivity();
        }


        btnCommentSend = (TextView) layout.findViewById(R.id.txt_send_comment);

        btnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!edit_comment.getText().toString().equals("")){
                    try {
                        sendComment("postComment", edit_comment.getText().toString());
                        edit_comment.setText("");
                        hideSoftKeyboard();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.d("btn send","blank comment");
                }
            }
        });

        postObj = new PostObj();
        config = new Config();
        db = new DatabaseHandler(getActivity());

        userId = db.getUserDetails().get("userId");

        try {
            getPostFeed("load");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void getPostFeed(final String tag) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);
        //Log.d("getPostFeed","pageNo: "+pageNo);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_GET_POST, obj,
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
                                getListFollowed("load");
                                loadDataIntoView(response , tag);
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

                if(tag.equals("refresh")){
                    //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                   // pageNo--;
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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        JSONObject post = response.getJSONObject("post");
        postObj.id = post.getString("id");
        postObj.userId = post.getString("userId");
        postObj.checkedInRestaurantId = post.getString("checkedInRestaurantId");
        postObj.postImage = post.getString("postImage");
        postObj.dishName = post.getString("dishName");
        postObj.rating = post.getString("rating");
        postObj.tip = post.getString("tip");
        postObj.userName = post.getString("userName");
        postObj.next = post.getString("next");
        postObj.previous = post.getString("previous");
        postObj.iBookark = post.getString("iBookark");
        postObj.bookmarkCount = post.getString("bookmarkCount");
        postObj.iLikedIt = post.getString("iLikedIt");
        postObj.createDate = post.getString("createDate");
        postObj.currentDate = post.getString("currentDate");
        postObj.userImage = post.getString("userImage");
        postObj.userThumb = post.getString("userThumb");
        postObj.restaurantName = post.getString("restaurantName");
        postObj.restaurantIsActive = post.getString("restaurantIsActive");
        postObj.comment_count = post.getString("comment_count");
        postObj.like_count = post.getString("like_count");
        postObj.timeElapsed = post.getString("timeElapsed");
        txtUserName.setText(postObj.dishName);
        CommentObj commentObj = new CommentObj();
        commentObj.viewType = "post";
        postDataList.add(commentObj);

        JSONArray comment = response.getJSONArray("comments");

        for (int i = 0; comment.length() > i; i++){
            CommentObj current = new CommentObj();
            current.viewType = "comment";
            current.id = comment.getJSONObject(i).getString("id");
            current.comment = comment.getJSONObject(i).getString("comment");
            current.createDate = comment.getJSONObject(i).getString("createDate");
            current.currentDate = comment.getJSONObject(i).getString("currentDate");
            current.userId = comment.getJSONObject(i).getString("userId");
            current.userName = comment.getJSONObject(i).getString("userName");
            current.fullName = comment.getJSONObject(i).getString("fullName");
            current.userImage= comment.getJSONObject(i).getString("userImage");
            current.userThumb = comment.getJSONObject(i).getString("userThumb");
            current.timeElapsed = comment.getJSONObject(i).getString("timeElapsed");

            //----------add mention to list if have--------------------------
            JSONArray mentionList = comment.getJSONObject(i).getJSONArray("userMentioned");
            //Log.d("mentionList length", mentionList.length()+"");
            for (int ii = 0; ii< mentionList.length(); ii++){
                UserMention currentMention = new UserMention();
                Log.d("userN mentioned","from: "+i+" "+mentionList.getJSONObject(ii).getString("userName")+"");
                Log.d("userid mentioned","from: "+i+" "+mentionList.getJSONObject(ii).getString("userId")+"");
                currentMention.userName = mentionList.getJSONObject(ii).getString("userName");
                currentMention.userId = mentionList.getJSONObject(ii).getString("userId");
                current.userMentionsList.add(currentMention);
            }
            //-------------------

            postDataList.add(current);
           // current.id = comment.
            //Log.d("comment", comment.getJSONObject(i).getString("id"));
        }
        if (getActivity() != null ){
            commentAdapter = new CommentAdapter(getActivity(), postDataList, postObj);
            recyclerView.setAdapter(commentAdapter);
            initSwipe();
        }

        Log.d("post", post+"");
    }

    public void getListFollowed(final String tag) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("selectedUserId",userId);
        //Log.d("getPostFeed","pageNo: "+pageNo);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_LIST_FOLLOWED, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                setListFollowed(response);

                                //-- getAndSave(response);
                                //loadDataIntoView(response , tag);
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

                if(tag.equals("refresh")){
                    //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    // pageNo--;
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
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void setListFollowed(JSONObject response) throws JSONException {

        this.response = response;

        JSONArray followedList = response.getJSONArray("followedUsers");
        //Log.d("followerList", followedList.length()+"");
        for (int i = 0; i<followedList.length(); i++){
            FollowedUsersObj current = new FollowedUsersObj();
            current.id = followedList.getJSONObject(i).getString("id");
            current.userName = followedList.getJSONObject(i).getString("userName");
            fUserList.add(current);
        }
        if (getActivity() != null){
            followedListAdapter  = new FollowedListAdapter(getActivity(), fUserList, mentionCallback);
            recyclerViewMention.setAdapter(followedListAdapter);
            txtListener();
        }

    }

    public void sendComment(final String tag, String commentTxt) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);
        if (mentionUArray.length() > 0){
            obj.put("userMentioned", mentionUArray);
            Log.d("mentionUser added", mentionUArray.length()+"");
        }

        byte[] data = new byte[0];
        try {
            data = commentTxt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        obj.put("comment", base64);


        Log.d("json to send",obj+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_COMMENT_ADD, obj,
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
                               // loadDataIntoView(response , tag);
                                addNewComment (response);
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

                if(tag.equals("refresh")){
                    //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    // pageNo--;
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

    private void  addNewComment (JSONObject response) throws JSONException {
        CommentObj commentObj = new CommentObj();

        //JSONObject comment = response.getJSONObject("")
        JSONObject comment = response.getJSONObject("comment");


        commentObj.viewType = "comment";
        commentObj.id = comment.getString("id");
        commentObj.comment = comment.getString("comment");
        commentObj.userId = comment.getString("userId");
        commentObj.userName = comment.getString("userName");
        commentObj.userImage = comment.getString("userImage");
        commentObj.createDate = comment.getString("createDate");
        commentObj.currentDate = comment.getString("currentDate");
        commentObj.fullName = comment.getString("fullName");


        postDataList.add(commentObj);

        linearLayoutManager.scrollToPosition(postDataList.size());
        commentAdapter.notifyDataSetChanged();

        Log.d("addnew comment response", comment.getString("comment")+"");
    }

    private Paint p = new Paint();

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CommentAdapter.PostHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Log.d("viewHolder p", viewHolder.getAdapterPosition()+"");

                if (direction == ItemTouchHelper.LEFT){
                    if (postDataList.get(viewHolder.getAdapterPosition()).userId.equals(userId)){
                       // Log.d("show popup","delete comment");
                        dialogCommentAlert("delete", postDataList.get(viewHolder.getAdapterPosition()).id,viewHolder.getAdapterPosition());
                        //adapter.removeItem(position);

                    }else {
                        dialogCommentAlert("report", postDataList.get(viewHolder.getAdapterPosition()).id,viewHolder.getAdapterPosition());
                       // Log.d("show popup","report comment");
                    }
                   // adapter.removeItem(position);
                } else {
                   // removeView();
                   // edit_position = position;
                  //  alertDialog.setTitle("Edit Country");
                   // et_country.setText(countries.get(position));
                   // alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                RecyclerView.LayoutParams lparams = new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(lparams);
                tv.setText("Delete");
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if(dX > 0){
                        /*p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);*/
                    } else {
                        Log.d("check id","login uId: "+userId+" : "+ "comm uId: "+ postDataList.get(viewHolder.getAdapterPosition()).userId);
                        if (postDataList.get(viewHolder.getAdapterPosition()).userId.equals(userId)){
                            p.setColor(Color.parseColor("#D32F2F"));
                            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                            c.drawRect(background,p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                            RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                            c.drawBitmap(icon,null,icon_dest,p);
                        }else {
                            p.setColor(Color.parseColor("#D32F2F"));
                            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                            c.drawRect(background,p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_white_48dp);
                            RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                            c.drawBitmap(icon,null,icon_dest,p);
                        }
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
   /* private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }*/

    public void removeItem(int position) {
        postDataList.remove(position);
        commentAdapter.notifyItemRemoved(position);
        commentAdapter.notifyItemRangeChanged(position, postDataList.size());
    }
    public void notifiy(int position) {
        //postDataList.remove(position);
        commentAdapter.notifyDataSetChanged();
        commentAdapter.notifyItemRangeChanged(position, postDataList.size());
    }

    private void dialogCommentAlert(final String flagType, final String commentId, final int position){
        final Dialog dialogComm = new Dialog(context);
        dialogComm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogComm.setContentView(R.layout.dialog_comment);
        TextView txtComm = (TextView) dialogComm.findViewById(R.id.txt_comment_alert);
        TextView btnCancel = (TextView) dialogComm.findViewById(R.id.btn_cancel);
        TextView btnYes = (TextView) dialogComm.findViewById(R.id.btn_yes);
        if (flagType.equals("report")){
            txtComm.setText("Report Comment ?");
        }else if (flagType.equals("delete")){
            txtComm.setText("Delete Comment ?");
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick", "cancel");
                dialogComm.dismiss();
                //commentAdapter.notifyDataSetChanged();
                notifiy(position);
                //commentFlag(final String tag, String flagType, String commentId)
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    commentFlag(flagType+"Request", flagType, commentId, position);
                    dialogComm.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("btnClick", "yes");
            }
        });



        dialogComm.show();
        dialogComm.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("dialog comm", "on cancel");
                notifiy(position);
            }
        });
    }

    public void commentFlag(final String tag, String flagType, String commentId, int position) throws JSONException {
       // Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("commentId",commentId);

        if (flagType.equals("delete")){
            removeItem(position);
        }

        notifiy(position);




        String apiURL;
        if (flagType.equals("report")){
            apiURL = config.URL_REPORT_COMMENT;
        }else {
            apiURL = config.URL_DELETE_COMMENT;
        }

        //obj.put("userMentioned", ListArray of mentioned user);
        /*byte[] data = new byte[0];
        try {
            data = commentTxt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        obj.put("comment", base64);*/
        //Log.d("getPostFeed","pageNo: "+pageNo);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                apiURL, obj,
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
                                // loadDataIntoView(response , tag);
                                //addNewComment (response);
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

                if(tag.equals("refresh")){
                    //-- swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    // pageNo--;
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

    private void txtListener(){

        int afterSpace;
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               /* if (tabSeleted == 0){
                    searchCallback1.searchKey(s.toString(),"dish");
                }
                if (tabSeleted == 1){
                    searchCallback2.searchKey(s.toString(),"user");
                }
                if (tabSeleted == 2){
                    searchCallback3.searchKey(s.toString(),"restaurant");

                }*/
                String getString = s.toString();
               /* if (count != 0){
                    //Log.d("key comm", s.toString() + " : "+ getString.substring(count-1));
                    mentionWord(s.toString());
                }*/





            }
            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("afterTextChanged", s.toString());
                mentionWord(String.valueOf(s));
            }
        });
    }

    Boolean readChar = false;
    int charPosition;
    private void mentionWord(String str){

        String lastWord = str.substring(str.lastIndexOf(" ")+1);
      //  mTextView.setText(str);
        Log.d("last word ", lastWord+"");
        if (lastWord.length() > 0){
            String lastFirstChar = String.valueOf(lastWord.charAt(0));
            if (lastFirstChar.equals("@")){
                Log.d("Mention word", lastWord.substring(1));
                recyclerViewMention.setVisibility(View.VISIBLE);
                onTexChange(lastWord.substring(1));
               // txtV.setText(lastWord);
            }
        }else {
           // txtV.setText("0");
            recyclerViewMention.setVisibility(View.GONE);
            Log.d("last word length","0");
        }
    }

    private void onTexChange(String newText){
        try {
            JSONArray rListArray = response.getJSONArray("followedUsers");
            fUserList.clear();
            for (int i=0;i<rListArray.length();i++){
                FollowedUsersObj current = new FollowedUsersObj();
                current.id = rListArray.getJSONObject(i).getString("id");
                current.userName = rListArray.getJSONObject(i).getString("userName");
                //current.postCount = rListArray.getJSONObject(i).getString("postCount");
                fUserList.add(current);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Log.d("rListArray", "total: "+ rListArray.length());
        // tempList = new ArrayList<RestaurantListObj>(restaurantList);
        final List<FollowedUsersObj> filteredModelList = filter(fUserList, newText);
        followedListAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
    }
    private List<FollowedUsersObj> filter(List<FollowedUsersObj> models, String query) {
        query = query.toLowerCase();
        //this.postData =  new ArrayList<RestaurantPostObj>(postList);
        final List<FollowedUsersObj> filteredModelList = new ArrayList<>();
        for (FollowedUsersObj model : models) {
            final String text = model.userName.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    @Override
    public void mentionUser(String userName, String userId) {
        String str = edit_comment.getText().toString();

        if (str.indexOf(" ") != -1){
            str = str.substring(0, str.lastIndexOf(" ")) + " @"+userName+" ";

        }else {
            str = "@"+userName+" ";
        }
        edit_comment.setText(str);
        edit_comment.setSelection(edit_comment.getText().length());





        //FollowedUsersObj current = new FollowedUsersObj();
        //current.userName = userName;
        //current.id = userId;
       // mentionUser.add(current);


        /*HashMap<String,String> mentionMap = new HashMap<String,String>();
        mentionMap.put("userName", userName);
        mentionMap.put("userId", userId);*/
        JSONObject mentionObject = new JSONObject();
        try {
            mentionObject.put("userName", userName);
            mentionObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mentionUArray.put(mentionObject);
        //String lastWord = str.substring(str.lastIndexOf(" ")+1);
        Log.d("mentionUser", "user: "+userName+" userId: "+userId);
    }
}