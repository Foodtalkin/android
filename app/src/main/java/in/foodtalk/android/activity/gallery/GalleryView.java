package in.foodtalk.android.activity.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 28-03-2017.
 */

public class GalleryView extends Activity {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    GalleryAdapter galleryAdapter;

    ImageView btnCamera;

    private static final String TAG = "GalleryView";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_gallery);

        btnCamera = (ImageView) findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GalleryView","camera clicked");
                Intent data = new Intent().putExtra("all_path", "gotoCam");
                setResult(RESULT_OK, data);
                finish();
            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        galleryAdapter = new GalleryAdapter(this, getFilePaths1());
        recyclerView.setAdapter(galleryAdapter);
        Log.e(TAG,"launch activity");
    }

    public List<GalleryObj> getFilePaths()
    {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //Uri u = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA,  MediaStore.Images.Media._ID};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        List<GalleryObj> resultIAV = new ArrayList<>();
        String[] directories = null;

        //String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.DATE_TAKEN};
        String orderBy = MediaStore.Images.Media._ID;
        if (u != null)
        {
            //c = managedQuery(u, projection, null, null, null);
            c = getContentResolver().query(u, projection, null, null, orderBy);

        }
        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if(imagePath.isDirectory())
                    {
                        imageList = imagePath.listFiles();
                    }
                    if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            )
                    {
                        String path= imagePath.getAbsolutePath();

                        Uri imgUri = Uri.fromFile(imagePath);
                        GalleryObj galleryObj = new GalleryObj();
                        galleryObj.imgPath = imagePath.getAbsolutePath();

                        resultIAV.add(galleryObj);
                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultIAV;
    }

    public List<GalleryObj> getFilePaths1()
    {
        List<GalleryObj> galleryList = new ArrayList<GalleryObj>();
        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()) {

                    GalleryObj galleryObj = new GalleryObj();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    galleryObj.imgPath = imagecursor.getString(dataColumnIndex);

                    galleryList.add(galleryObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }
}
