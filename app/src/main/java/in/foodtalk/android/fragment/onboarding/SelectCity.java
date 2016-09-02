package in.foodtalk.android.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class SelectCity extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.onboarding_city, container, false);

        return layout;
    }
}
