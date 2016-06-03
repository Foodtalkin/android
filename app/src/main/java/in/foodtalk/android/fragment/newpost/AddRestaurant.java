package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 03-06-2016.
 */
public class AddRestaurant extends Fragment {

    View layout;

    EditText address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.add_restaurant,container, false);
        address = (EditText) layout.findViewById(R.id.input_address);
        address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                return false;
            }
        });
        return layout;
    }
}
