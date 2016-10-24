package in.foodtalk.android.fragment.postdetails;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Vector;

import in.foodtalk.android.R;


/**
 * Created by RetailAdmin on 21-10-2016.
 */

public class PostDetailsFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    View layout;
    FragmentManager fm;
    private FragmentActivity myContext;

    PagerAdapterPd adapter;

    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.post_details_fragment, container, false);
        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        initialisePaging();
    }

    @Override
    public void onAttach(Context context) {
        myContext = (FragmentActivity) context;
        fm = myContext.getSupportFragmentManager();
        super.onAttach(context);
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
    List<android.support.v4.app.Fragment> fragments;
    private void initialisePaging(){
       //final List<Fragment> fragments = new Vector<Fragment>();
        fragments = new Vector<android.support.v4.app.Fragment>();
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),LikeListFragment.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),CommentsPostFragment.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(),BookmarkListFragment.class.getName()));
        //fragments.add(Fragment.instantiate())


        adapter = new PagerAdapterPd(fm, fragments);
        ViewPager pager = (ViewPager) layout.findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
