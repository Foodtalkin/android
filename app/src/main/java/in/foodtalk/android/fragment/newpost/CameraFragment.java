package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 20-05-2016.
 */
public class CameraFragment extends Fragment {

    View layout;

    ImageView picHolder;

    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;

    private static final int CAMERA_REQUEST = 1888;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.camera_fragment, container, false);

        picHolder = (ImageView) layout.findViewById(R.id.picture_holder);



        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


        super.onActivityCreated(savedInstanceState);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            picHolder.setImageBitmap(photo);
        }
    }


}
