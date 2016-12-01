package in.foodtalk.android.module;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import in.foodtalk.android.communicator.CloudinaryCallback;

/**
 * Created by RetailAdmin on 30-05-2016.
 */
public class CloudinaryUpload extends AsyncTask<File, Void, Map> {

    Context context;
    Map config = new HashMap();
    CloudinaryCallback cloudinaryCallback;
    public CloudinaryUpload(Context context, CloudinaryCallback cloudinaryCallback){
        this.context = context;
        this.cloudinaryCallback = cloudinaryCallback;
      //  this.cloudinaryCallback = (CloudinaryCallback) context;

    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("onPreExecutee"," call");
    }

    @Override
    protected Map doInBackground(File... params) {

        Log.d("doInbackground","call");

        Log.d("file", Uri.fromFile(params[0])+"");

        Map config = new HashMap();
        config.put("cloud_name", "digital-food-talk-pvt-ltd");
        config.put("api_key", "849964931992422");
        config.put("api_secret", "_xG26XxqmqCVcpl0l9-5TJs77Qc");
        Cloudinary cloudinary = new Cloudinary(config);
        try {
            Map result = cloudinary.uploader().upload(params[0], ObjectUtils.asMap("resource_type", "auto"));
          //  Map result = cloudinary.uploader().upload(params[0], ObjectUtils.asMap("public_id", publicId, "signature", signature, "timestamp", timestamp, "api_key", api_key))
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }
    @Override
    protected void onPostExecute(Map s) {
        super.onPostExecute(s);
        if (s != null){
            cloudinaryCallback.uploaded(s);
        }
        Log.d("onPostExecute", s+"");
    }
}
