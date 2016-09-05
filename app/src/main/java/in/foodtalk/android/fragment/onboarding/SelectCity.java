package in.foodtalk.android.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.OnBoardingCallback;

/**
 * Created by RetailAdmin on 02-09-2016.
 */
public class SelectCity extends Fragment {
    OnBoardingCallback onBoardingCallback;
    LinearLayout btnSend;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.onboarding_city, container, false);
        btnSend = (LinearLayout) layout.findViewById(R.id.btn_send);

        onBoardingCallback = (OnBoardingCallback) getActivity();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectEmail","button clicked send");
                onBoardingCallback.onboardingBtnClicked("previos", null);
            }
        });
        return layout;
    }
}
