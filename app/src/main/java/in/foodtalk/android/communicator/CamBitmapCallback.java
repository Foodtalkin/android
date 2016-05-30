package in.foodtalk.android.communicator;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by RetailAdmin on 25-05-2016.
 */
public interface CamBitmapCallback {

    public void capturedBitmap(Bitmap photo, File file);
}
