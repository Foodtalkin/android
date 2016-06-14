package in.foodtalk.android.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.SearchCallback;

/**
 * Created by Belal on 2/3/2016.
 */

public class Tab3 extends Fragment implements SearchCallback {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_tab3, container, false);
    }

    @Override
    public void searchKey(String keyword, String searchType) {
        Log.d("search restaurant", keyword);
    }
}
