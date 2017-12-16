package com.example.doubanmovietop250.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

// RecyclerView的滑动监听器
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    // 标记是否正在向上滑动
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            // 如果滑到了最后一项并且正在向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                onLoadMore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // dy > 0表示正在向上滑动
        isSlidingUpward = dy > 0;
    }

    public abstract void onLoadMore();
}
