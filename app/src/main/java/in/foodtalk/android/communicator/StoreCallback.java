package in.foodtalk.android.communicator;

import in.foodtalk.android.object.StoreObj;

/**
 * Created by RetailAdmin on 16-08-2016.
 */
public interface StoreCallback {
    public void storeHistory(String type, String value);
    public void openDetailsStore(String type, String storeId);
}
