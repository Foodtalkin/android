package in.foodtalk.android.module;

/**
 * Created by RetailAdmin on 29-04-2016.
 */
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem, firstCompleteItem, lastCompleteItem;

    private int current_page = 0;
    private LinearLayoutManager mLinearLayoutManager;
    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

        firstCompleteItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        lastCompleteItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();




        onScrolled1(dx, dy, firstVisibleItem, lastVisibleItem);
        if(dx == 1 || dx == -1){
          //  Log.d("onScrolled", "firstVItem: "+ firstVisibleItem+" lastVItem : "+ lastVisibleItem);
          //  Log.d("onScrolled--1", "firstCItem: "+ firstCompleteItem+" lastCItem : "+ lastCompleteItem);

        }

       // Log.d("onScrolled -- 2", "dx: "+ dx+" dy: "+dy);

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;
            onLoadMore(current_page);
            loading = true;
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        Log.d("onScrollStateChanged", newState+"");
    }



    public abstract void onLoadMore(int current_page);

    public abstract void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem);
}