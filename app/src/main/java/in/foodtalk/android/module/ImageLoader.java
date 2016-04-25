package in.foodtalk.android.module;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import in.foodtalk.android.R;
import in.foodtalk.android.app.AppController;

/**
 * Created by RetailAdmin on 25-04-2016.
 */
public class ImageLoader {
    public void imgLoader(String url){
        ImageView mImageView;

        //mImageView = (ImageView) findViewById(R.id.myImage);


        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                       // mImageView.setImageBitmap(bitmap);
                       // return bitmap;
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                       // mImageView.setImageResource(R.drawable.image_load_error);
                    }
                });
// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(request);
    }
}
