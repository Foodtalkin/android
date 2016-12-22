package in.foodtalk.android.fragment.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by RetailAdmin on 09-07-2016.
 */
public class NewsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;


    int count;
    Context context;

    NewsCardFragment newsCardFragment;
    public NewsPagerAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        count = tabCount;
        this.context = context;
        //newsCardFragment = new NewsCardFragment();
    }

    @Override
    public Fragment getItem(int position) {
        //fragments.add(Fragment.instantiate(context ,NewsCardFragment.class.getName()));
        //return this.fragments.get(position);
        newsCardFragment = new NewsCardFragment();
        return newsCardFragment;
    }

    @Override
    public int getCount() {
        return count;
    }
}
