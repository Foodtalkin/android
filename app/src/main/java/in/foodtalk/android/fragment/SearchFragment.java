package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import in.foodtalk.android.R;
import in.foodtalk.android.search.Pager;

/**
 * Created by RetailAdmin on 13-06-2016.
 */
public class SearchFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    View layout;

    EditText txtSearch;

    TabLayout searchTabLayout;
    ViewPager searchViewPager;
    FragmentManager fm;



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

        Pager adapter = new Pager(fm, searchTabLayout.getTabCount());



        txtSearch = (EditText) layout.findViewById(R.id.txt_search_home);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        txtSearch.requestFocus();
        return layout;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
