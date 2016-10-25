package in.foodtalk.android.fragment.postdetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by RetailAdmin on 21-10-2016.
 */

public class PagerAdapterPd extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    HashMap<String, String>hashMap;


    public PagerAdapterPd(FragmentManager fm, List<Fragment>fragments, HashMap<String, String> hashMap) {
        super(fm);
        this.fragments = fragments;
        this.hashMap = hashMap;
        Log.d("PagerAdapter","hashMap "+hashMap.get("postId"));
    }

    @Override
    public Fragment getItem(int position) {
        Fragment newFragment = this.fragments.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("postId",hashMap.get("postId"));
        bundle.putString("sessionId", hashMap.get("sessionId"));
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
