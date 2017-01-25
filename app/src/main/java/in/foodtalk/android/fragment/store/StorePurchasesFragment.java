package in.foodtalk.android.fragment.store;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import in.foodtalk.android.object.PurchasesObj;

/**
 * Created by RetailAdmin on 30-12-2016.
 */

public class StorePurchasesFragment extends Fragment implements ApiCallback {
    View layout;


    PurchasesPagerAdapter mPagerAdapter;

    FragmentManager fm;
    private FragmentActivity myContext;

    ProgressBar progressBar;
    LinearLayout tapToRetry;
    DatabaseHandler db;
    ApiCallback apiCallback;
    ApiCall apiCall;
    int pagerCurrentPosition = 0;
    public String storeItemId;
    LinearLayout placeholder;
    LinearLayout btnShopNow;

    OpenFragmentCallback openFragmentCallback;

    List<PurchasesObj> purchaseList = new ArrayList<>();


    TextView txtPlaceholder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_purchases_fragment, container, false);
       // viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        db = new DatabaseHandler(getActivity());
        apiCallback = this;
        apiCall = new ApiCall();

        openFragmentCallback = (OpenFragmentCallback) getActivity();

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        txtPlaceholder = (TextView) layout.findViewById(R.id.txt_placeholder);
        placeholder = (LinearLayout) layout.findViewById(R.id.placeholder);
        btnShopNow = (LinearLayout) layout.findViewById(R.id.btn_shop_now);
        btnShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentCallback.openFragment("storeFragment","");
            }
        });
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getPurchasesData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            getPurchasesData();
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
        Log.e("storePurchasesFramgnet","fm"+ fm);
        mPagerAdapter = new PurchasesPagerAdapter(fm, purchaseList, getActivity());
        ViewPager pager = (ViewPager) layout.findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
       // pager.setPageTransformer(true, new DepthPageTransformer());



        TabLayout tabLayout = (TabLayout) layout.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);

        Log.d("initialisePagin","size: "+purchaseList.size()+" currentPosition: "+pagerCurrentPosition+" : ");
        pager.setCurrentItem(pagerCurrentPosition);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
                pagerCurrentPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getPurchasesData() throws JSONException {
        tapToRetry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        apiCall = new ApiCall();
        apiCall.apiRequestPost(getActivity(),obj, Config.URL_STORE_PURCHASES,"purchases",apiCallback);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        progressBar.setVisibility(View.GONE);
        Log.d("new api response", response+"");
        if (response != null){
            if (tag.equals("purchases")){
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

    private void sendDataIntoAdapter(JSONObject response) throws JSONException{
        JSONArray purchasesList = response.getJSONArray("storePurchase");
        if (purchasesList.length() == 0){
            placeholder.setVisibility(View.VISIBLE);
        }else {
            placeholder.setVisibility(View.GONE);
        }

        purchaseList.clear();

        for (int i=0; i<purchasesList.length(); i++){
            PurchasesObj purchasesObj = new PurchasesObj();
            purchasesObj.storeItemId = purchasesList.getJSONObject(i).getString("storeItemId");
            purchasesObj.title = purchasesList.getJSONObject(i).getString("title");
            purchasesObj.coverImage = purchasesList.getJSONObject(i).getString("coverImage");
            purchasesObj.cardImage = purchasesList.getJSONObject(i).getString("cardImage");
            purchasesObj.actionButtonText = purchasesList.getJSONObject(i).getString("actionButtonText");
            purchasesObj.description = purchasesList.getJSONObject(i).getString("description");
            purchasesObj.cardActionButtonText = purchasesList.getJSONObject(i).getString("cardActionButtonText");
            purchasesObj.shortDescription = purchasesList.getJSONObject(i).getString("shortDescription");
            purchasesObj.cityText = purchasesList.getJSONObject(i).getString("cityText");
            purchasesObj.costType = purchasesList.getJSONObject(i).getString("costType");
            purchasesObj.costOnline = purchasesList.getJSONObject(i).getString("costOnline");
            purchasesObj.costPoints = purchasesList.getJSONObject(i).getString("costPoints");
            purchasesObj.termConditionsLink = purchasesList.getJSONObject(i).getString("termConditionsLink");
            purchasesObj.type = purchasesList.getJSONObject(i).getString("type");

            purchasesObj.thankYouText = purchasesList.getJSONObject(i).getString("thankYouText");
            purchasesObj.postPurchaseInstructions = purchasesList.getJSONObject(i).getString("postPurchaseInstructions");
            purchasesObj.startDate = purchasesList.getJSONObject(i).getString("startDate");
            purchasesObj.endDate = purchasesList.getJSONObject(i).getString("endDate");
            purchasesObj.userId = purchasesList.getJSONObject(i).getString("userId");
            purchasesObj.quantity = purchasesList.getJSONObject(i).getString("quantity");
            purchasesObj.paidCostOnline = purchasesList.getJSONObject(i).getString("paidCostOnline");
            purchasesObj.paidCostPoints = purchasesList.getJSONObject(i).getString("paidCostPoints");
            purchasesObj.createDate = purchasesList.getJSONObject(i).getString("createDate");
            purchasesObj.metaData = purchasesList.getJSONObject(i).getString("metaData");
            purchaseList.add(purchasesObj);
            if (storeItemId != null){
                if (storeItemId.equals(purchasesObj.storeItemId)){
                    pagerCurrentPosition = i;
                    Log.d("pagerCurrentPosition", storeItemId+" : "+ i);
                }
            }
        }
        initialisePaging();
    }
}
