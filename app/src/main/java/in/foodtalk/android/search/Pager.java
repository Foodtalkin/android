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

    public SearchResult searchResult1;
    public SearchResult searchResult2;
    public SearchResult searchResult3;


    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);

        searchResult1 = new SearchResult();
        searchResult2 = new SearchResult();
        searchResult3 = new SearchResult();

        //searchResult = new SearchResult(0);

        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                //searchResult1 = new SearchResult(position);
                searchResult1.pageNumber = position;
                return searchResult1;
            case 1:
                searchResult2.pageNumber = position;
                return searchResult2;
            case 2:
                searchResult3.pageNumber = position;
                return searchResult3;
            default:
                return null;
        }
        //searchResult = new SearchResult(position);
        //searchResult.testString = "from pager class"+ position;
       // Log.d("Pager getItem", position+"");
       // return searchResult;
    }
    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}