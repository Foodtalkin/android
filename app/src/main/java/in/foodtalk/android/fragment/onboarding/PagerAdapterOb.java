package in.foodtalk.android.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class PagerAdapterOb extends FragmentPagerAdapter {
    private List<Fragment>fragments;
    public PagerAdapterOb(FragmentManager fm, List<Fragment>fragments) {
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
