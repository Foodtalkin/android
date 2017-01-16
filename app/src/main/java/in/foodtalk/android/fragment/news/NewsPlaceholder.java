package in.foodtalk.android.fragment.news;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 16-01-2017.
 */

public class NewsPlaceholder extends Fragment {
    View layout;

    public String screenType;
    RelativeLayout firstScreen, lastScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.news_placeholder, container, false);
        firstScreen = (RelativeLayout) layout.findViewById(R.id.first_screen);
        lastScreen = (RelativeLayout) layout.findViewById(R.id.last_screen);
        if (screenType.equals("first")){
            firstScreen.setVisibility(View.VISIBLE);
            lastScreen.setVisibility(View.GONE);
        }else {
            firstScreen.setVisibility(View.GONE);
            lastScreen.setVisibility(View.VISIBLE);
        }
        return layout;
    }
}
