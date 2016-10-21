package in.foodtalk.android.fragment.postdetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by RetailAdmin on 21-10-2016.
 */

public class PagerAdapterPd extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    public PagerAdapterPd(FragmentManager fm, List<Fragment>fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
