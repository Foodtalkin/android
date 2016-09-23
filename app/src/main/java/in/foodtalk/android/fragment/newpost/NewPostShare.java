package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 22-09-2016.
 */
public class NewPostShare extends Fragment {

    View layout;
    public Bitmap photo;
    ImageView imgHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.new_post_share,container,false);
        imgHolder = (ImageView) layout.findViewById(R.id.img_holder);
        if (photo != null){
            imgHolder.setImageBitmap(photo);
        }
        return layout;
    }
}
