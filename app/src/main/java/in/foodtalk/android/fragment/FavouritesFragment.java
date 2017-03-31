package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import in.foodtalk.android.FbLogin;
import in.foodtalk.android.Home;
import in.foodtalk.android.R;
import in.foodtalk.android.adapter.FavouritesAdapter;
import in.foodtalk.android.adapter.HomeFeedAdapter;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.FavoritesObj;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 13-05-2016.
 */
public class FavouritesFragment extends Fragment implements ApiCallback {
    View layout;
    RecyclerView recyclerView;
    DatabaseHandler db;
    int pageNo = 1;
    Config config;
    List<FavoritesObj> favList = new ArrayList<>();
    FavouritesAdapter favouritesAdapter;

    Boolean loading = false;
    LinearLayoutManager linearLayoutManager;

    ProgressBar progressBar;
    LinearLayout tapToRetry;
    TextView placeholderFavourites;

    ApiCall apiCall;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.favourites_fragment, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_fav);

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);

        apiCall = new ApiCall();

        placeholderFavourites = (TextView) layout.findViewById(R.id.placeholder_favourites);

        db = new DatabaseHandler(getActivity());
        config = new Config();

        pageNo = 1;

        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                tapToRetry.setVisibility(View.GONE);
                try {
                    pageNo = 1;
                    getFavList("load");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            getFavList("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;
    }
    public void getFavList(final String tag) throws JSONException {

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        obj.put("page",Integer.toString(pageNo));
        //obj.put("recordCount","10");

        if (getActivity()!=null)
            apiCall.apiRequestPost(getActivity(), obj, Config.URL_FAVOURITES, tag, this);
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {
        JSONArray favArray = response.getJSONArray("dish");


        if(favList.size()>0 && !tag.equals("loadMore")){
         favList.clear();
        }

        if (favArray.length()>0 && tag.equals("load")){
            Log.d("favArray","more the 0");
            placeholderFavourites.setVisibility(View.GONE);
        }else if (tag.equals("load")){
            Log.d("favArray","0");
            placeholderFavourites.setVisibility(View.VISIBLE);
        }
        for (int i=0; favArray.length()> i; i++){
            FavoritesObj current = new FavoritesObj();
            current.dishName = favArray.getJSONObject(i).getString("dishName");
            current.restaurantName = favArray.getJSONObject(i).getString("restaurantName");
            current.id = favArray.getJSONObject(i).getString("id");
            current.cityName = favArray.getJSONObject(i).getString("cityName");
            favList.add(current);
        }
        if (tag.equals("load")){
            if (getActivity() !=null){
                favouritesAdapter = new FavouritesAdapter(getActivity(), favList);
                recyclerView.setAdapter(favouritesAdapter);
            }
            callScrollClass();
        }else if (tag.equals("loadMore")){
            //favouritesAdapter.notifyDataSetChanged();
            remove(null);
            loading = false;

        }
    }
    public void remove(ContactsContract.Contacts.Data data) {
        int position = favList.indexOf(data);
        favList.remove(position);
        favouritesAdapter.notifyItemRemoved(position);
    }

    private void callScrollClass(){
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager, null) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("scroll listener", "current_page: "+ current_page);
                if(!loading){
                    pageNo++;
                    favList.add(null);
                    //recyclerView.addD
                    favouritesAdapter.notifyItemInserted(favList.size()-1);
                    loading = true;
                    Log.d("loadMore", "call getPostFeed('loadMore')");
                    try {
                        getFavList("loadMore");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem) {

            }
        });
    }

    private void logOut(){
        db.resetTables();
        Intent i = new Intent(getActivity(), FbLogin.class);
        startActivity(i);
        getActivity().finish();
    }
    public void showToast(String msg){
        Toast toast= Toast.makeText(getActivity(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        if (response != null){
            try {
                String status = response.getString("status");
                if (!status.equals("error")){
                    //-- getAndSave(response);
                    loadDataIntoView(response , tag);
                }else {
                    String errorCode = response.getString("errorCode");
                    if(errorCode.equals("6")){
                        Log.d("Response error", "Session has expired");
                        logOut();
                    }else {
                        Log.e("Response status", "some error");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Json Error", e+"");
            }
        } else {
            showToast("Please check your internet connection");

            if (tag.equals("load")){
                progressBar.setVisibility(View.GONE);
                tapToRetry.setVisibility(View.VISIBLE);
            }

            if(tag.equals("refresh")){
                //swipeRefreshHome.setRefreshing(false);
            }
            if(tag.equals("loadMore")){
                //remove(null);
                //callScrollClass();
                pageNo--;
            }
        }
    }
}