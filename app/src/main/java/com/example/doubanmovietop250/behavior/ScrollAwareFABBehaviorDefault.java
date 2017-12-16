package com.example.doubanmovietop250.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ScrollAwareFABBehaviorDefault extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehaviorDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                       final FloatingActionButton child,
                                       final View directTargetChild,
                                       final View target, final int nestedScrollAxes) {
        // 指定Behavior关注的滑动方向
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
                               final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0) {
            // 向上滚动时，隐藏fab
            animateOut(child);
        } else if (dyConsumed < 0) {
            // 向下滚动时，显示fab
            animateIn(child);
        }
    }

    // 隐藏fab
    private void animateOut(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;
        fab.animate().translationY(fab.getHeight() + bottomMargin).
                setInterpolator(new LinearInterpolator()).start();
    }

    // 显示fab
    private void animateIn(FloatingActionButton fab) {
        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
}
