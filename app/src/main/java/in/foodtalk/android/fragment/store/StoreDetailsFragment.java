package in.foodtalk.android.fragment.store;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.SetDateFormat;
import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 27-12-2016.
 */

public class StoreDetailsFragment extends Fragment implements ApiCallback {
    View layout;

    public StoreObj storeObj;
    TabLayout tabLayout;
    ImageView imgCard;
    TextView txtTitle, txtDate, txtVanue, txtCost, txtEventInfo;

    ApiCall apiCall = new ApiCall();
    ApiCallback apiCallback;

    LinearLayout tapToRetry;
    LinearLayout progressHolder;

    DatabaseHandler db;

    LinearLayout btnBuyNow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_details_fragment, container, false);

        db = new DatabaseHandler(getActivity());

        tabLayout = (TabLayout) layout.findViewById(R.id.tab_layout);
        imgCard = (ImageView) layout.findViewById(R.id.img_card);
        txtTitle = (TextView) layout.findViewById(R.id.txt_title);

        txtDate = (TextView) layout.findViewById(R.id.txt_date);
        txtVanue = (TextView) layout.findViewById(R.id.txt_vanue);
        txtCost = (TextView) layout.findViewById(R.id.txt_cost);
        txtEventInfo = (TextView) layout.findViewById(R.id.txt_event_info);

        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        progressHolder = (LinearLayout) layout.findViewById(R.id.progress_h);

        btnBuyNow = (LinearLayout) layout.findViewById(R.id.btn_buy_now);
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StoreDetailsFragment","buynow clicked");
                try {
                    buyNow();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        apiCallback = this;
        try {
            getStoreList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStoreList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        StorePagerAdapter adapter = new StorePagerAdapter();
        final ViewPager pager = (ViewPager) layout.findViewById(R.id.view_pager);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                tabLayout.setScrollPosition(position,0f,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return layout;
    }

    private void getStoreList() throws JSONException {
        progressHolder.setVisibility(View.VISIBLE);
        tapToRetry.setVisibility(View.GONE);
        //placeholder.setVisibility(View.GONE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        jsonObject.put("storeItemId", storeObj.storeItemId);
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_STORE_DETAIL, "storeDetails", apiCallback);
    }
    private void buyNow() throws JSONException {
       // progressHolder.setVisibility(View.VISIBLE);
        //tapToRetry.setVisibility(View.GONE);
        //placeholder.setVisibility(View.GONE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        jsonObject.put("storeItemId", storeObj.storeItemId);
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_STORE_BUY, "storeItemBuy", apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("apiResponse", tag+" : " +response);
        if (tag.equals("storeDetails")){
            if (response != null){
                try {
                    loadDataIntoView(response, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                tapToRetry.setVisibility(View.VISIBLE);
                progressHolder.setVisibility(View.GONE);
            }
        }



    }

    private void loadDataIntoView(JSONObject response, String tag)throws JSONException{
        Log.d("loadDataInfotView","loaded");
        progressHolder.setVisibility(View.GONE);
        tapToRetry.setVisibility(View.GONE);

        JSONObject storeOffer = response.getJSONObject("storeOffer");

        //----
        if (!storeOffer.getString("coverImage").equals("")){
            Picasso.with(getActivity())
                    .load(storeOffer.getString("coverImage"))
                    .fit().centerCrop()
                    //.fit()
                    .placeholder(R.drawable.placeholder)
                    .into(imgCard);
        }


        txtTitle.setText(storeOffer.getString("title"));
        txtCost.setText(storeOffer.getString("costPoints")+" Points");
        txtVanue.setText(storeOffer.getString("cityText"));
        txtEventInfo.setText(storeOffer.getString("description"));
        txtDate.setText(SetDateFormat.convertFormat(storeOffer.getString("endDate"),"yyyy/MM/dd HH:mm:ss","MMM dd, yyyy h:mm a"));
    }

    public class StorePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.quick_view;
                    break;
                case 1:
                    resId = R.id.details;
                    break;
            }
            return layout.findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }
    }
}
