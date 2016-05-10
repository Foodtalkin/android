package in.foodtalk.android.module;

/**
 * Created by RetailAdmin on 29-04-2016.
 */
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem, firstCompleteItem, lastCompleteItem;

    private int current_page = 0;
    private LinearLayoutManager mLinearLayoutManager;

    public StaggeredGridLayoutManager staggeredGridLayoutManager;
    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, StaggeredGridLayoutManager staggeredGridLayoutManager) {
        if (linearLayoutManager != null){
            this.mLinearLayoutManager = linearLayoutManager;

        } else if (staggeredGridLayoutManager != null){
            this.staggeredGridLayoutManager = staggeredGridLayoutManager;
        }

    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();

        if (mLinearLayoutManager != null){
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

            firstCompleteItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            lastCompleteItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            //Log.d("scrolled", "totalItemCount:"+totalItemCount+" visibleItemCount:"+ visibleItemCount+" firstVisibleItem:"+ firstVisibleItem+" visibleThreshold:"+visibleThreshold);
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached
                // Do something
                current_page++;
                onLoadMore(current_page);
                loading = true;
            }
        }else {
            totalItemCount = staggeredGridLayoutManager.getItemCount();
            int mSpanCount = 3;
            int[] into = new int[mSpanCount];
            firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(into)[1];
            lastVisibleItem = staggeredGridLayoutManager.findLastVisibleItemPositions(into)[1];
            firstCompleteItem = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(into)[1];
            lastCompleteItem = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(into)[1];

            //Log.d("on scroll","totalItemCount: "+totalItemCount+" previousTotal: "+previousTotal);
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            //Log.d("on scroll",!loading ? "false" : "true");
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached
                // Do something
                current_page++;
                onLoadMore(current_page);
                loading = true;
                //Log.d("scroll last","call");
            }
            //Log.d("scrolled", "totalItemCount:"+totalItemCount+" visibleItemCount:"+ visibleItemCount+" firstVisibleItem:"+ firstVisibleItem+" visibleThreshold:"+visibleThreshold);
        }
        onScrolled1(dx, dy, firstVisibleItem, lastVisibleItem);
        if(dx == 1 || dx == -1){
            //  Log.d("onScrolled", "firstVItem: "+ firstVisibleItem+" lastVItem : "+ lastVisibleItem);
            //  Log.d("onScrolled--1", "firstCItem: "+ firstCompleteItem+" lastCItem : "+ lastCompleteItem);
        }
        // Log.d("onScrolled -- 2", "dx: "+ dx+" dy: "+dy);
        //Log.d("scrolled", "totalItemCount:"+totalItemCount+" visibleItemCount:"+ visibleItemCount+" firstVisibleItem:"+ firstVisibleItem+" visibleThreshold:"+visibleThreshold);
        //Log.d("scrolled",(totalItemCount - visibleItemCount)+" <= "+(firstVisibleItem + visibleThreshold));
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //Log.d("onScrollStateChanged", newState+"");
    }
    public abstract void onLoadMore(int current_page);

    public abstract void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem);
}