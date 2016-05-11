package in.foodtalk.android.communicator;

import java.util.List;

import in.foodtalk.android.object.UserPostObj;

/**
 * Created by RetailAdmin on 11-05-2016.
 */
public interface ProfilePostOpenCallback {
    public void postOpen(List<UserPostObj> postObj, String postId);
}
