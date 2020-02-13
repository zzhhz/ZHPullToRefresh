package com.zzh.refresh.loadingview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zzh.refresh.PullToRefreshView;


public abstract class BaseLoadingView extends FrameLayout implements LoadingView {
    public BaseLoadingView(Context context) {
        super(context);
    }

    public BaseLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public final PullToRefreshView getPullToRefreshView() {
        return (PullToRefreshView) getParent();
    }

    @Override
    public void onViewPositionChanged(PullToRefreshView view) {
    }

    @Override
    public boolean canRefresh(int scrollDistance) {
        return scrollDistance >= getMeasuredHeight();
    }

    @Override
    public int getRefreshingHeight() {
        return getMeasuredHeight();
    }
}
