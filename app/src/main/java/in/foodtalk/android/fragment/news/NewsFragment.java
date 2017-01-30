package in.foodtalk.android.fragment.news;


import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.DepthPageTransformer;
import in.foodtalk.android.module.VerticalViewPager;
import in.foodtalk.android.object.NewsObj;

/**
 * Created by RetailAdmin on 22-12-2016.
 */

public class NewsFragment extends Fragment implements ApiCallback, OpenFragmentCallback {
    View layout;
    private NewsPagerAdapter mPagerAdapter;
    FragmentManager fm;
    private FragmentActivity myContext;
    ApiCall apiCall;
    DatabaseHandler db;
    ApiCallback apiCallback;
    List<NewsObj> newsList = new ArrayList<>();

    ProgressBar progressBar;
    LinearLayout tapToRetry;
    public int pagerCurrentPosition = 0;
    public String newsId;
    Boolean loadMore = false;
    Boolean haveMore = true;
    int pageNo = 1;

    public WebView webView;
    public Boolean webPage = false;

    OpenFragmentCallback openFragmentCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.news_fragment, container, false);

        db = new DatabaseHandler(getActivity());
        apiCallback = this;
        openFragmentCallback = this;

        Log.d("NewsFragment onC","newsId: "+ newsId);

        webView = (WebView) layout.findViewById(R.id.webview);

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getNewsData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        try {

            getNewsData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layout;
    }

    @Override
    public void onAttach(Activity activity) {

        myContext=(FragmentActivity) activity;
        fm = myContext.getSupportFragmentManager();


        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        initialisePaging();
    }

    private void initialisePaging(){
        Log.d("NewsFragment","fm : "+fm);
        mPagerAdapter = new NewsPagerAdapter(fm, newsList, getActivity(), openFragmentCallback);
        VerticalViewPager pager = (VerticalViewPager) layout.findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
        pager.setPageTransformer(true, new DepthPageTransformer());


        pager.setCurrentItem(pagerCurrentPosition);

       // newsId = null;
       // pagerCurrentPosition = 0;
        pager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
                pagerCurrentPosition = position;
                if (newsList.size()-2 == position){
                    pageNo++;
                    try {
                        if (haveMore){
                            getNewsData();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getNewsData() throws JSONException {
        tapToRetry.setVisibility(View.GONE);
        if (loadMore == false){
            progressBar.setVisibility(View.VISIBLE);
        }

        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("page",Integer.toString(pageNo));
        apiCall = new ApiCall();
        apiCall.apiRequestPost(getActivity(),obj, Config.URL_NEWS,"news",apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        progressBar.setVisibility(View.GONE);
        Log.d("new api response", response+"");
        if (response != null){
            if (tag.equals("news")){
                try {
                    sendDataIntoAdapter(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            tapToRetry.setVisibility(View.VISIBLE);
        }
    }
    private void sendDataIntoAdapter(JSONObject response) throws JSONException {
        if (loadMore == false){
            newsList.clear();
        }
        JSONArray newsListArray = response.getJSONArray("news");
        Log.d("sendDataIntoAdapter", newsListArray.length()+"");

        if (!loadMore){
            NewsObj current1 = new NewsObj();
            current1.screenType = "first";
            newsList.add(current1);
        }


        for (int i=0; i<newsListArray.length();i++){
            NewsObj current = new NewsObj();
            current.id = newsListArray.getJSONObject(i).getString("id");
            current.title = newsListArray.getJSONObject(i).getString("title");
            current.coverImage = newsListArray.getJSONObject(i).getString("coverImage");
            current.source = newsListArray.getJSONObject(i).getString("source");
            current.sourceUrl = newsListArray.getJSONObject(i).getString("sourceUrl");
            current.description = newsListArray.getJSONObject(i).getString("description");
            current.startDate = newsListArray.getJSONObject(i).getString("startDate");
            current.isDisabled = newsListArray.getJSONObject(i).getString("isDisabled");
            current.screenType = "news";
            newsList.add(current);
            Log.d("NewsFragment","newsId:"+ newsId);
            if (newsId != null){
                if (newsId.equals(current.id)){
                    pagerCurrentPosition = i+1;
                    Log.d("newFragment","newId "+i);
                }
            }else {

            }
        }

        if (newsListArray.length() == 0 || newsListArray.length() < 5){
            NewsObj current2 = new NewsObj();
            current2.screenType = "last";
            newsList.add(current2);
            haveMore = false;
        }



        if (loadMore == false){
            initialisePaging();
            loadMore = true;
        }else {
            // newsId = null;
            // pagerCurrentPosition = 0;
            mPagerAdapter.notifyDataSetChanged();
        }

      //  NewsObj newsObj = new NewsObj();

       // int index = newsList.indexOf("7");
        //Log.d("NewsFragment", "index: "+ index);
    }

    private void setWevView(String url){

        //next line explained below
        //---
        webView.clearView();
        webView.setWebViewClient(new CustomWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void openFragment(String fragmentName, String value) {
        Log.d("openFragment web", value);

        setWevView(value);
        webView.setVisibility(View.VISIBLE);
        webPage = true;
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
