package in.foodtalk.android.activity.gallery;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 28-03-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "GalleryAdapter";

    List<GalleryObj> pathList;
    Context context;

    int imgWidth;
    private int width;
    private int height;
    private LayoutInflater layoutInflater;

    public GalleryAdapter(Context context, List<GalleryObj> pathList){
        layoutInflater  = LayoutInflater.from(context);
        this.context = context;
        this.pathList = pathList;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        imgWidth = width/3;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.card_custom_gallery, parent, false);
        ImageHolder imageHolder = new ImageHolder(view);
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageHolder imageHolder = (ImageHolder) holder;
        GalleryObj galleryObj = pathList.get(position);

        imageHolder.imgView.getLayoutParams().width = imgWidth;
        imageHolder.imgView.getLayoutParams().height = imgWidth;

        Picasso.with(context)
                .load("file://" + galleryObj.imgPath)
                .fit().centerCrop()
                //.fit()
                .placeholder(R.drawable.placeholder)
                .into(imageHolder.imgView);
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        public ImageView imgView;

        public ImageHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.img_view);
            imgView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.img_view:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            Intent data = new Intent().putExtra("all_path", pathList.get(getAdapterPosition()).imgPath);
                            Activity activity = ((Activity)context);
                            activity.setResult(activity.RESULT_OK, data);
                            activity.finish();
                            Log.d(TAG, "image clicked");
                        break;
                    }
                    break;
            }
            return false;
        }
    }
}
