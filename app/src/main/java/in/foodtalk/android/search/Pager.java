package in.foodtalk.android.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    public SearchResult searchResult;


    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);

        searchResult = new SearchResult();

        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        /*switch (position) {
            case 0:

                return tab1;
            case 1:

                return tab2;
            case 2:

                return tab3;
            default:
                return null;
        }*/
        Log.d("Pager getItem", position+"");
        return searchResult.create(position);
    }
    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}