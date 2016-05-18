package in.foodtalk.android.communicator;

import java.util.List;

import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.UserPostObj;

/**
 * Created by RetailAdmin on 11-05-2016.
 */
public interface ProfileRPostOpenCallback {
    public void rPostOpen(List<RestaurantPostObj> postObj, String postId, String urestaurantId);
}
