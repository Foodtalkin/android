package in.foodtalk.android.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import in.foodtalk.android.adapter.newpost.SearchAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.SearchCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.SearchResultObj;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class SearchResult extends Fragment implements SearchCallback {

    View layout;
    public static final String ARG_PAGE = "page";
    private static final int TAB_DISH_SEARCH = 0;
    private static final int TAB_USER_SEARCH = 1;
    private static final int TAB_RESTAURANT_SEARCH = 2;

    RecyclerView recyclerView;
    DatabaseHandler db;
    Config config;
    String keyword;
    ImageView imgHolder;
    TextView txtHolder;

    private int mPageNumber;



    List<SearchResultObj> searchResultList = new ArrayList<>();

    String sessionId;
    JSONObject response;

    SearchAdapter searchAdapter;
    Boolean searchResultLoaded = false;

    static final String TAG = "searchDish";

    LinearLayoutManager linearLayoutManager;

    public RelativeLayout progressBar;

    Activity activity;

    LinearLayout iconHolder;





    public int pageNumber;

    static final int DISH_SEARCH = 0;
    static final int USER_SEARCH = 1;
    static final int RESTAURANT_SEARCH = 2;

    public void create(int pageNumber) {
        //SearchResult fragment = new SearchResult();
        //Bundle args = new Bundle();
        //args.putInt(ARG_PAGE, pageNumber);
        //fragment.setArguments(args);
        //return fragment;

       // this.pageNumber = pageNumber;
    }

    /*public SearchResult(int pageNumber){
        this.pageNumber = pageNumber;
    }*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mPageNumber = getArguments().getInt(ARG_PAGE);
        //Log.d("onCreate tab", getArguments().getInt(ARG_PAGE)+"");
    }

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.search_tab1, container, false);

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.search_tab1, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        progressBar = (RelativeLayout) layout.findViewById(R.id.progress_bar_search);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_search_dish);

        recyclerView.setLayoutManager(linearLayoutManager);

        imgHolder = (ImageView) layout.findViewById(R.id.img_holder_search);
        txtHolder = (TextView) layout.findViewById(R.id.txt_search);

        iconHolder = (LinearLayout) layout.findViewById(R.id.icon_copy_holder);




        //Log.d("testString onCreateV", testString);

        db = new DatabaseHandler(getActivity());

        sessionId = db.getUserDetails().get("sessionId");

        AppController.getInstance().sessionId = sessionId;

       // Log.d("session id createView", sessionId);









        //Log.d("testString onCreate", testString + "pageNo: "+pageNumber);

        switch (pageNumber){
            case DISH_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_local_dining_black_48dp);
                txtHolder.setText("Find awesome dishes.");
                //userThumb.setVisibility(View.GONE);
                //testString = "Tab_dish_Search";
                break;
            case USER_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_supervisor_account_black_48dp);
                txtHolder.setText("Food is fun with friends.");
                //userThumb.setVisibility(View.VISIBLE);

                //testString = "Tab_user_Search";
                break;
            case RESTAURANT_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_store_mall_directory_black_48dp);
                txtHolder.setText("Find best restaurants.");
               // userThumb.setVisibility(View.GONE);
                //testString = "Tab_restaurant_Search";
                break;
        }



        //if (mPageNumber == TAB_DISH_SEARCH)




        //Log.d("page no", mPageNumber+"");



        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Log.d("onAttach","call");

        activity = getActivity();




        config = new Config();
    }

    @Override
    public void searchKey(String keyword, String searchType) {
        this.keyword = keyword;
        if (keyword.length()<2 && searchResultLoaded == true){
            searchAdapter.notifyDataSetChanged();

            searchResultLoaded = false;
            searchResultList.clear();
            iconHolder.setVisibility(View.VISIBLE);


        }

        //AppController.getInstance().cancelPendingRequests(TAG);
        if (keyword.length()>1 && searchResultLoaded == false){
            try {
                getDishList(TAG, searchType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (searchResultLoaded){
            onTexChange(keyword);
        }

        Log.d("status on searchKey","keyword: "+keyword+" searchResultLoaded: "+ searchResultLoaded);
        //Log.d("search key", "keyword: "+ keyword+" searchType "+searchType + "session id: "+ sessionId);
    }
    public void getDishList(final String tag, String searchType) throws JSONException {

       // testString = "get dish List";
        //Log.d("testString loaded", testString+ "testString2: "+ testString2 +" : "+progressBar);
        //Log.d("pageNo loaded", pageNumber+"");

        progressBar.setVisibility(View.VISIBLE);

        //Log.d("session Id global", AppController.getInstance().sessionId+"");
        //Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", AppController.getInstance().sessionId);
        obj.put(pageNumber == RESTAURANT_SEARCH ? "searchText" : "search", keyword);
        obj.put("region", "delhi");
        String url;
        if (searchType.equals("dish")){
            url = config.URL_DISH_LIST;
        }else if (searchType.equals("user")){
            url = config.URL_USER_LIST;
        }else if (searchType.equals("restaurant")){
            url = config.URL_RESTAURANT_LIST;
        }else {
            url = null;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, obj,
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

                                if (!searchResultLoaded){
                                    loadDataIntoView(response , tag);
                                    Log.d("respons","loadDataIntoView");
                                }


                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
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
                //showToast("Please check your internet connection");

                if(tag.equals("refresh")){
                    //swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    //pageNo--;
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

        progressBar.setVisibility(View.GONE);

        iconHolder.setVisibility(View.GONE);

        this.response = response;



        setListArray(response);

        searchAdapter = new SearchAdapter(getActivity(),searchResultList, pageNumber);
        recyclerView.setAdapter(searchAdapter);
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null ){

        }else {
            //Log.d("getActivity", "null");
        }
        if (keyword.length()>2 && !searchResultLoaded){
            onTexChange(keyword);
        }
        searchResultLoaded = true;
    }
    private void onTexChange(String newText){

        setListArray(response);
        final List<SearchResultObj> filteredModelList = filter(searchResultList, newText);
        searchAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
    }

    private void setListArray(JSONObject response){

        try {
            switch (pageNumber){
                case DISH_SEARCH:
                    JSONArray rListArray = response.getJSONArray("result");
                    searchResultList.clear();
                    for (int i=0;i<rListArray.length();i++){
                        SearchResultObj current = new SearchResultObj();
                        current.id = rListArray.getJSONObject(i).getString("id");
                        current.txt1 = rListArray.getJSONObject(i).getString("dishName");
                        current.txt2 = rListArray.getJSONObject(i).getString("postCount")+" Dishes";

                        searchResultList.add(current);
                    }
                    break;
                case USER_SEARCH:
                    JSONArray rListArray1 = response.getJSONArray("users");
                    searchResultList.clear();
                    for (int i=0;i<rListArray1.length();i++){
                        SearchResultObj current = new SearchResultObj();
                        current.id = rListArray1.getJSONObject(i).getString("id");
                        current.txt1 = rListArray1.getJSONObject(i).getString("userName");
                        current.txt2 = rListArray1.getJSONObject(i).getString("fullName");
                        current.image = rListArray1.getJSONObject(i).getString("image");
                        searchResultList.add(current);
                    }
                    break;
                case RESTAURANT_SEARCH:
                    JSONArray rListArray2 = response.getJSONArray("restaurants");
                    searchResultList.clear();
                    for (int i=0;i<rListArray2.length();i++){
                        SearchResultObj current = new SearchResultObj();
                        current.id = rListArray2.getJSONObject(i).getString("id");
                        current.txt1 = rListArray2.getJSONObject(i).getString("restaurantName");
                        current.txt2 = rListArray2.getJSONObject(i).getString("area");
                        searchResultList.add(current);
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private List<SearchResultObj> filter(List<SearchResultObj> models, String query) {
        query = query.toLowerCase();
        //this.postData =  new ArrayList<RestaurantPostObj>(postList);
        final List<SearchResultObj> filteredModelList = new ArrayList<>();
        for (SearchResultObj model : models) {
            final String text = model.txt1.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
