package in.foodtalk.android.communicator;

import org.json.JSONObject;

/**
 * Created by RetailAdmin on 12-08-2016.
 */
public interface ApiCallback {
    public void apiResponse(JSONObject response, String tag);
}
