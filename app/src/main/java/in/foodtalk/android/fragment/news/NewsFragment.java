package in.foodtalk.android.fragment.news;


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

import in.foodtalk.android.R;
import in.foodtalk.android.module.DepthPageTransformer;
import in.foodtalk.android.module.VerticalViewPager;

/**
 * Created by RetailAdmin on 22-12-2016.
 */

public class NewsFragment extends Fragment {
    View layout;
    private NewsPagerAdapter mPagerAdapter;
    FragmentManager fm;
    private FragmentActivity myContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.news_fragment, container, false);
        initialisePaging();
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
        fm = myContext.getSupportFragmentManager();
    }

    private void initialisePaging(){
        mPagerAdapter = new NewsPagerAdapter(fm, 6, getActivity());
        VerticalViewPager pager = (VerticalViewPager) layout.findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
        pager.setPageTransformer(true, new DepthPageTransformer());

        pager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
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
