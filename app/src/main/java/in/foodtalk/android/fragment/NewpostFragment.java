package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class NewpostFragment extends Fragment {


    View layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.newpost_fragment, container, false);
        return layout;
    }
}
