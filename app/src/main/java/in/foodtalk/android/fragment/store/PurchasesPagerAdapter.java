package in.foodtalk.android.fragment.store;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import in.foodtalk.android.fragment.news.NewsCardFragment;
import in.foodtalk.android.object.NewsObj;
import in.foodtalk.android.object.PurchasesObj;

/**
 * Created by RetailAdmin on 30-12-2016.
 */

public class PurchasesPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<PurchasesObj> purchaseList;


    int count;
    Context context;

    StorePurchasesCard storePurchasesCard;
    public PurchasesPagerAdapter(FragmentManager fm, List<PurchasesObj> purchaseList, Context context) {
        super(fm);
        this.purchaseList = purchaseList;
        //count = newsList.size();
        Log.d("NewsPagerAdapter", purchaseList.size()+"");
        this.context = context;
        Log.d("PurchasesPagerAdapter", purchaseList.size()+"");

        //newsCardFragment = new NewsCardFragment();
    }

    @Override
    public Fragment getItem(int position) {
        //fragments.add(Fragment.instantiate(context ,NewsCardFragment.class.getName()));
        //return this.fragments.get(position);
        storePurchasesCard = new StorePurchasesCard();
        storePurchasesCard.purchasesObj = purchaseList.get(position);
        return storePurchasesCard;
    }

    @Override
    public int getCount() {
        return purchaseList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
