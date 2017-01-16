package in.foodtalk.android.fragment.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import in.foodtalk.android.communicator.OpenFragmentCallback;
import in.foodtalk.android.object.NewsObj;

/**
 * Created by RetailAdmin on 09-07-2016.
 */
public class NewsPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<NewsObj> newsList;


    int count;
    Context context;
    OpenFragmentCallback openFragmentCallback;

    NewsCardFragment newsCardFragment;
    NewsPlaceholder newsPlaceholder;
    public NewsPagerAdapter(FragmentManager fm, List<NewsObj> newsList, Context context, OpenFragmentCallback openFragmentCallback) {
        super(fm);
        this.newsList = newsList;
        //count = newsList.size();
        Log.d("NewsPagerAdapter", newsList.size()+"");
        this.context = context;
        this.openFragmentCallback = openFragmentCallback;
        //newsCardFragment = new NewsCardFragment();
    }

    @Override
    public Fragment getItem(int position) {
        //fragments.add(Fragment.instantiate(context ,NewsCardFragment.class.getName()));
        //return this.fragments.get(position);

        Fragment fragment = null;
        Log.d("getItem", newsList.get(position).screenType);
        switch (newsList.get(position).screenType){
            case "news":
                newsCardFragment = new NewsCardFragment();
                newsCardFragment.newsObj = newsList.get(position);
                newsCardFragment.openFragmentCallback = openFragmentCallback;
                fragment = newsCardFragment;
                break;
            case "first":
                newsPlaceholder = new NewsPlaceholder();
                newsPlaceholder.screenType = newsList.get(position).screenType;
                fragment = newsPlaceholder;
                break;
            case "last":
                newsPlaceholder = new NewsPlaceholder();
                newsPlaceholder.screenType = newsList.get(position).screenType;
                fragment = newsPlaceholder;
                break;
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
