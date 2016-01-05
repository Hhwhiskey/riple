package com.khfire22gmail.riple.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Kevin on 1/1/2016.
 */
public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerViewOnScrollListener.class.getSimpleName();

    private int mPreviousTotal = 0; // The total number of items in the dataset after the last load
    private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before mLoading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;


    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerViewOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void reset() {
        mLoading = false;
        mPreviousTotal = 0;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }
        if (!mLoading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            mLoading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}