package in.foodtalk.android.fragment.postdetails;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 21-10-2016.
 */

public class BookmarkListFragment extends Fragment {
    View layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.post_bookmarks_fragment, container, false);

        return layout;
    }
}
