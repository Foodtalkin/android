package in.foodtalk.android.fragment.store;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import in.foodtalk.android.R;
import in.foodtalk.android.apicall.ApiCall;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.ApiCallback;
import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.communicator.WebpageCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.DateFunction;
import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 27-12-2016.
 */

public class StoreDetailsFragment extends Fragment implements ApiCallback, View.OnTouchListener {
    View layout;

    public StoreObj storeObj;
    public String storeId;
    TabLayout tabLayout;
    ImageView imgCard;
    TextView txtTitle, txtTitle1, txtDate, txtVanue, txtCost, txtEventInfo, txtBtnBuy;

    ApiCall apiCall = new ApiCall();
    ApiCallback apiCallback;

    LinearLayout tapToRetry;
    LinearLayout progressHolder;

    DatabaseHandler db;

    LinearLayout btnBuyNow;

    ImageView btnClose;

    //----alert popup--
    RelativeLayout viewSuccess;
    RelativeLayout viewError;
    TextView txtSuccess;
    TextView txtError;
    LinearLayout btnGotoPurchases;
    LinearLayout btnGotoStore;

    LinearLayout alertPopupView;

    FrameLayout progressBarHolder;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    TextView txtGotoPurchases;


    OpenFragmentCallback openFragmentCallback;

    RelativeLayout rootView;
    ImageView btnPurchases;

    LinearLayout btnTermsCondition;
    WebpageCallback webpageCallback;

    String iPurchasedIt;
    String storeItemId;

    String thankYouText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.store_details_fragment, container, false);

        db = new DatabaseHandler(getActivity());

        openFragmentCallback = (OpenFragmentCallback) getActivity();

        progressBarHolder = (FrameLayout) layout.findViewById(R.id.progressBarHolder);

        webpageCallback = (WebpageCallback) getActivity();

        //--alert popup--
        viewSuccess =  (RelativeLayout) layout.findViewById(R.id.view_success);
        viewError = (RelativeLayout) layout.findViewById(R.id.view_error);
        txtSuccess = (TextView) layout.findViewById(R.id.txt_success);
        txtError = (TextView) layout.findViewById(R.id.txt_error);
        alertPopupView = (LinearLayout) layout.findViewById(R.id.alert_popup);
        btnGotoPurchases = (LinearLayout) layout.findViewById(R.id.btn_goto_purchases);
        btnGotoStore = (LinearLayout) layout.findViewById(R.id.btn_goto_store);
        btnPurchases = (ImageView) layout.findViewById(R.id.btn_purchases);
        btnClose = (ImageView) layout.findViewById(R.id.btn_close);
        txtGotoPurchases = (TextView) layout.findViewById(R.id.txt_goto_purchases);
        btnTermsCondition = (LinearLayout) layout.findViewById(R.id.btn_terms_condition);
        //----

        tabLayout = (TabLayout) layout.findViewById(R.id.tab_layout);
        imgCard = (ImageView) layout.findViewById(R.id.img_card);
        txtTitle = (TextView) layout.findViewById(R.id.txt_title);
        txtTitle1 = (TextView) layout.findViewById(R.id.txt_title1);

        rootView = (RelativeLayout) layout.findViewById(R.id.root_view);
        rootView.setVisibility(View.GONE);

        txtDate = (TextView) layout.findViewById(R.id.txt_date);
        txtVanue = (TextView) layout.findViewById(R.id.txt_vanue);
        txtCost = (TextView) layout.findViewById(R.id.txt_cost);
        txtEventInfo = (TextView) layout.findViewById(R.id.txt_event_info);

        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);
        progressHolder = (LinearLayout) layout.findViewById(R.id.progress_h);

        txtBtnBuy = (TextView) layout.findViewById(R.id.txt_btn_buy);

        btnBuyNow = (LinearLayout) layout.findViewById(R.id.btn_buy_now);
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StoreDetailsFragment","buynow clicked");
                if (iPurchasedIt.equals("0")){
                    try {
                        buyNow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    openFragmentCallback.openFragment("storePurchases", storeItemId);
                }

            }
        });
        btnTermsCondition.setOnTouchListener(this);

        btnGotoPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentCallback.openFragment("storePurchases", "");
            }
        });
        btnGotoStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        btnPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentCallback.openFragment("storePurchases", "");
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alertPopup();
                alertPopupView.setVisibility(View.GONE);
            }
        });


        //alertDialog("success", "Your Purchase was successful.");

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
        jsonObject.put("storeItemId", storeId);
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_STORE_DETAIL, "storeDetails", apiCallback);
    }
    private void buyNow() throws JSONException {
       // progressHolder.setVisibility(View.VISIBLE);
        //tapToRetry.setVisibility(View.GONE);
        //placeholder.setVisibility(View.GONE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId", db.getUserDetails().get("sessionId"));
        jsonObject.put("storeItemId", storeId);
        apiCall.apiRequestPost(getActivity(),jsonObject, Config.URL_STORE_BUY, "storeItemBuy", apiCallback);
        progressBarView(true);
    }

    @Override
    public void apiResponse(JSONObject response, String tag) {
        Log.d("apiResponse", tag+" : " +response);
        if (tag.equals("storeDetails")){
            if (response != null){
                rootView.setVisibility(View.VISIBLE);
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
        if (tag.equals("storeItemBuy")){
            progressBarView(false);
            if (response != null){
                Log.d("storeItemBuy","enter");
                try {
                    String status = response.getString("status");
                    String message = "";
                    if (status.equals("OK")){
                        Log.d("storeItemBuy","success");
                        alertPopup("success", thankYouText);
                    }else if (status.equals("error")){
                        if (response.has("apiMessage")){
                            message = response.getString("apiMessage");
                        }
                        Log.d("storeItemBuy","error");
                        alertPopup("error", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //alertPopup(String view, String value);
            }
        }
    }
    JSONObject storeOffer;
    private void loadDataIntoView(JSONObject response, String tag)throws JSONException{
        Log.d("loadDataInfotView","loaded");
        progressHolder.setVisibility(View.GONE);
        tapToRetry.setVisibility(View.GONE);

        storeOffer = response.getJSONObject("storeOffer");

        //----
        if (!storeOffer.getString("coverImage").equals("")){
            Picasso.with(getActivity())
                    .load(storeOffer.getString("coverImage"))
                    .fit().centerCrop()
                    //.fit()
                    .placeholder(R.drawable.placeholder)
                    .into(imgCard);
        }

        if (storeOffer.getString("iPurchasedIt").equals("1")){
            txtBtnBuy.setText("Claimed");
            final int sdk = Build.VERSION.SDK_INT;
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                btnBuyNow.setBackgroundDrawable( getResources().getDrawable(R.drawable.gradient_green_0) );
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnBuyNow.setBackground( getResources().getDrawable(R.drawable.gradient_green_0));
                }
            }
        }else {
            txtBtnBuy.setText(storeOffer.getString("actionButtonText"));
        }
        storeItemId = storeOffer.getString("storeItemId");
        iPurchasedIt = storeOffer.getString("iPurchasedIt");
        txtTitle.setText(storeOffer.getString("title"));
        txtTitle1.setText(storeOffer.getString("title"));

        thankYouText = storeOffer.getString("thankYouText");
        if (storeOffer.getString("costPoints").equals("") || storeOffer.getString("costPoints").equals("0")){
            txtCost.setText("Free");
        }else {
            txtCost.setText(storeOffer.getString("costPoints")+" Points");
        }

        txtVanue.setText(storeOffer.getString("cityText"));
        txtEventInfo.setText(storeOffer.getString("description"));
        txtDate.setText(DateFunction.convertFormat(storeOffer.getString("endDate"),"yyyy-MM-dd HH:mm:ss","MMM dd, yyyy h:mm a"));

        /*btnTermsCondition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        try {
                            Log.d("StoreDetailsFragment", "btnTermsCondition"+storeOffer.getString("termConditionsLink"));
                            webpageCallback.inAppBrowser(false,"",storeOffer.getString("termConditionsLink"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });*/
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btn_terms_condition:
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        try {
                            Log.d("StoreDetailsFragment", "btnTermsCondition"+storeOffer.getString("termConditionsLink"));
                            webpageCallback.inAppBrowser(false,"",storeOffer.getString("termConditionsLink"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
        return false;
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

    /*private void alertDialog(String view, String value){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.store_popup);
        RelativeLayout viewSuccess = (RelativeLayout) dialog.findViewById(R.id.view_success);
        RelativeLayout viewError = (RelativeLayout) dialog.findViewById(R.id.view_error);
        TextView txtSuccess = (TextView) dialog.findViewById(R.id.txt_success);
        TextView txtError = (TextView) dialog.findViewById(R.id.txt_error);


        if (view.equals("success")){
            viewSuccess.setVisibility(View.VISIBLE);
            viewError.setVisibility(View.GONE);
            txtSuccess.setText(value);
        }else if (view.equals("error")){
            viewSuccess.setVisibility(View.GONE);
            viewError.setVisibility(View.VISIBLE);
            txtError.setText(value);
        }
        dialog.show();
    }*/
    private void alertPopup(String view, String value){
        alertPopupView.setVisibility(View.VISIBLE);
        if (view.equals("success")){
            viewSuccess.setVisibility(View.VISIBLE);
            viewError.setVisibility(View.GONE);
            txtGotoPurchases.setTextColor(getResources().getColor(R.color.green_shamrock));
            txtSuccess.setText(value);
        }else if (view.equals("error")){
            viewSuccess.setVisibility(View.GONE);
            viewError.setVisibility(View.VISIBLE);
            txtError.setText(value);
            txtGotoPurchases.setTextColor(getResources().getColor(R.color.red_persian));
        }
    }

    private void progressBarView(boolean show){
        if (show){
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }else {
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }
    }
}
