package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import in.foodtalk.android.R;
import in.foodtalk.android.communicator.CamBitmapCallback;

/**
 * Created by RetailAdmin on 20-05-2016.
 */
public class CameraFragment extends Fragment {

    View layout;
    ImageView imVCature_pic;
    Button btnCapture;

    File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");

    String mCurrentPhotoPath;

    CamBitmapCallback camBitmapCallback;

    CropImageView cropedImg;

    private static final int CAMERA_REQUEST = 1888;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.camera_fragment, container, false);

        imVCature_pic = (ImageView) layout.findViewById(R.id.picture_holder);

        cropedImg = (CropImageView) layout.findViewById(R.id.cropImageView);

        camBitmapCallback = (CamBitmapCallback) getActivity();
        initializeControls();
        return layout;
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void initializeControls() {
        imVCature_pic=(ImageView)layout.findViewById(R.id.picture_holder);
        btnCapture=(Button) layout.findViewById(R.id.btn_camera);
        btnCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				/* create an instance of intent
				 * pass action android.media.action.IMAGE_CAPTURE
				 * as argument to launch camera
				 */
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //MediaStore.ACTION_IMAGE_CAPTURE
				/*create instance of File with name img.jpg*/
                //File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
				/*put uri as extra in intent object*/
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				/*start activity for result pass intent as argument and request code */
                startActivityForResult(intent, 1);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onAcrivitiresult", requestCode+"");
        //if request code is same we pass as argument in startActivityForResult
        if(requestCode==1 && resultCode == getActivity().RESULT_OK){
            //create instance of File with same name we created before to get image from storage
            //File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
            //Crop the captured image using an other intent

           // Bundle extras = data.getExtras();
           // Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("data", data+" ");

            try {
				/*the user's device may not support cropping*/
                //--cropCapturedImage(Uri.fromFile(file));
                Bitmap photo = decodeFile(file);
                //Bitmap photo = setPic(file);
                //Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(file)));

//                Bitmap croppedBmp = Bitmap.createBitmap(photo, 0, 0,
                   //     photo.getWidth() / 2, photo.getHeight());
                //imVCature_pic.setImageBitmap(photo);

                //-------
                startCropImageActivity(Uri.fromFile(file));
                Log.d("onActivityResult","call cropimage activity");

                //--------
            }
            catch(ActivityNotFoundException aNFE){
                //display an error message if user device doesn't support
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
           // Log.d("result code",CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE+" : "
           // + CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE+ ": "
           // + CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            Log.d("crop img","cropd");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("Log","crop image activity request"+result.getUri());

                Bitmap photo = decodeFile(new File(result.getUri().getPath()));

                imVCature_pic.setImageBitmap(photo);

                camBitmapCallback.capturedBitmap(photo , new File(result.getUri().getPath()));

               // cropedImg.setImageUriAsync(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if(requestCode==2){
            //Create an instance of bundle and get the returned data
            //Bundle extras = data.getExtras();
            //get the cropped bitmap from extras
            //--Bitmap thePic = extras.getParcelable("data");
            //set image bitmap to image view
            //--imVCature_pic.setImageBitmap(thePic);
            //Log.d("get extras", extras.getParcelable("data")+"");
            try {
                if(file.exists()){

                    Log.d("onActivityResult", "try to load image");
                    Bitmap photo = decodeFile(file);
                    //Bitmap photo = setPic(file);
                    //Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(file)));
                    Bitmap croppedBmp = Bitmap.createBitmap(photo, 0, 0,
                            photo.getWidth() / 2, photo.getHeight());
                   // imVCature_pic.setImageBitmap(croppedBmp);
                }
                else {
                    Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void startCropImageActivity(Uri imageUri) {
     // CropImage.activity(imageUri)
             //  .setGuidelines(CropImageView.Guidelines.ON)
             //  .setFixAspectRatio(true)
          //     .start(getActivity());
//
        Intent intent = CropImage.activity(imageUri).setFixAspectRatio(true).getIntent(getActivity());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
