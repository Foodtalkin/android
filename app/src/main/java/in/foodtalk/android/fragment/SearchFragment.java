package in.foodtalk.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.SearchCallback;
import in.foodtalk.android.search.Pager;

/**
 * Created by RetailAdmin on 13-06-2016.
 */
public class SearchFragment extends Fragment implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    View layout;

    EditText txtSearch;

    TabLayout searchTabLayout;
    ViewPager searchViewPager;
    FragmentManager fm;

    private FragmentActivity myContext;

    SearchCallback searchCallback;
    Pager adapter;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.search_fragment, container, false);





        searchTabLayout = (TabLayout) layout.findViewById(R.id.tab_layout_search);

        searchTabLayout.addTab(searchTabLayout.newTab().setText("Dishes"));
        searchTabLayout.addTab(searchTabLayout.newTab().setText("Users"));
        searchTabLayout.addTab(searchTabLayout.newTab().setText("Restaurant"));
        searchTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        searchViewPager = (ViewPager) layout.findViewById(R.id.search_pager);

        adapter = new Pager(fm, searchTabLayout.getTabCount());
        //Adding adapter to pager
        searchViewPager.setAdapter(adapter);

        searchCallback = (SearchCallback) adapter.tab1;



        //Adding onTabSelectedListener to swipe views
        searchTabLayout.setOnTabSelectedListener(this);
        searchViewPager.setOnPageChangeListener(this);






        txtSearch = (EditText) layout.findViewById(R.id.txt_search_home);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        txtSearch.requestFocus();

        txtListener();
        return layout;
    }

    private void setCallbackRef(int no){
        if (no == 0){
            searchCallback = (SearchCallback) adapter.tab1;
            Log.d("searchcall back set to", "tab 1");
        }
        if (no == 1){
            searchCallback = (SearchCallback) adapter.tab2;
            Log.d("searchcall back set to", "tab 2");
        }
        if (no == 2){
            searchCallback = (SearchCallback) adapter.tab3;
            Log.d("searchcall back set to", "tab 3");
        }

    }

    private void txtListener(){
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 searchCallback.searchKey(s.toString(),"dish");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged", s.toString());
            }
        });
    }



    @Override
    public void onAttach(Activity activity) {

        myContext=(FragmentActivity) activity;
        fm = myContext.getSupportFragmentManager();


        super.onAttach(activity);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        searchViewPager.setCurrentItem(tab.getPosition());
        setCallbackRef(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.d("onPageSeleted", position+"");
    }

    @Override
    public void onPageSelected(int position) {
        //searchTabLayout.setCurrentTab(position);
       // Log.d("onPageSeleted", position+"");
        searchTabLayout.setScrollPosition(position,0f,true);
        setCallbackRef(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //og.d("onPageSeleted", state+"");
    }
}
