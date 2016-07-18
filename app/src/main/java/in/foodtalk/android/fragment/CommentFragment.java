package in.foodtalk.android.fragment;

import android.app.Fragment;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.CommentObj;
import in.foodtalk.android.object.PostObj;
import in.foodtalk.android.object.UserMention;

/**
 * Created by RetailAdmin on 20-06-2016.
 */
public class CommentFragment extends Fragment {
    View layout;
    Config config;
    DatabaseHandler db;

    String postId;

    PostObj postObj;

    EditText edit_comment;
    List<CommentObj> postDataList  = new ArrayList<>();

    CommentAdapter commentAdapter;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    TextView txtUserName;

    TextView btnCommentSend;

    /*public CommentFragment (String postId){
        this.postId = postId;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.comment_fragment, container, false);

        postId =  getArguments().getString("postId");

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_comment);

        txtUserName = (TextView) layout.findViewById(R.id.txt_name_comment);
        edit_comment = (EditText) layout.findViewById(R.id.edit_comment);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

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
        commentAdapter = new CommentAdapter(getActivity(), postDataList, postObj);
        recyclerView.setAdapter(commentAdapter);
        initSwipe();
        Log.d("post", post+"");
    }

    public void sendComment(final String tag, String commentTxt) throws JSONException {
        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postId",postId);
        //obj.put("userMentioned", ListArray of mentioned user);
        byte[] data = new byte[0];
        try {
            data = commentTxt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        obj.put("comment", base64);
        //Log.d("getPostFeed","pageNo: "+pageNo);
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
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
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
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
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
}