package com.zzh.refresh;

import android.content.Context;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.zzh.refresh.gesture.HGestureManager;
import com.zzh.refresh.gesture.HTouchHelper;
import com.zzh.refresh.loadingview.LoadingView;

/**
 *
 * @Date: 2020-02-13 18:06
 * @Email: zzh_hz@126.com
 * @QQ: 1299234582
 * @Author: zzh
 * @Description: FPullToRefreshView.java 刷新控件
 */
public class HPullToRefreshView extends BasePullToRefreshView implements NestedScrollingParent, NestedScrollingChild {
    private HGestureManager mGestureManager;
    private final int mTouchSlop;

    public HPullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        getGestureManager().setDebug(debug);
    }

    private HGestureManager getGestureManager() {
        if (mGestureManager == null) {
            mGestureManager = new HGestureManager(this, new HGestureManager.Callback() {
                @Override
                public boolean shouldInterceptEvent(MotionEvent event) {
                    return canPull();
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event) {
                    return mGestureManager.getTagHolder().isTagIntercept() || canPull();
                }

                @Override
                public void onEventConsume(MotionEvent event) {
                    final int dy = (int) getGestureManager().getTouchHelper().getDeltaY();
                    moveViews(dy, true);
                }

                @Override
                public void onEventFinish(VelocityTracker velocityTracker, MotionEvent event) {
                    if (mGestureManager.getLifecycleInfo().hasConsumeEvent()) {
                        if (mIsDebug)
                            Log.e(getDebugTag(), "onConsumeEventFinish:" + event.getAction() + " " + getState());

                        processDragFinish();
                    }
                }

                @Override
                public void onStateChanged(HGestureManager.State oldState, HGestureManager.State newState) {
                    switch (newState) {
                        case Consume:
                            break;
                        case Fling:
                            ViewCompat.postInvalidateOnAnimation(HPullToRefreshView.this);
                            break;
                        case Idle:
                            dealViewIdle();
                            break;
                    }
                }

                @Override
                public void onScrollerCompute(int lastX, int lastY, int currX, int currY) {
                    final int dy = currY - lastY;
                    moveViews(dy, false);

                    if (mIsDebug) {
                        final LoadingView loadingView = getLoadingViewByDirection();
                        final int top = ((View) loadingView).getTop();
                        Log.i(getDebugTag(), "onScroll:" + top + " " + getState());
                    }
                }
            });
            mGestureManager.getTagHolder().setCallback(new HGestureManager.TagHolder.Callback() {
                @Override
                public void onTagInterceptChanged(boolean tag) {
                    HTouchHelper.requestDisallowInterceptTouchEvent(HPullToRefreshView.this, tag);
                }

                @Override
                public void onTagConsumeChanged(boolean tag) {
                    HTouchHelper.requestDisallowInterceptTouchEvent(HPullToRefreshView.this, tag);
                }
            });
        }
        return mGestureManager;
    }

    private void processDragFinish() {
        if (getState() == State.RELEASE_TO_REFRESH)
            setState(State.REFRESHING);

        updateViewByState();
    }

    /**
     * 根据移动距离设置方向
     *
     * @param delta 大于0-下拉，小于0-上拉
     */
    private void setDirectionByDelta(int delta) {
        if (delta == 0)
            throw new IllegalArgumentException();

        if (delta > 0) {
            setDirection(Direction.FROM_HEADER);
            getGestureManager().getScroller().setMaxScrollDistance(((View) getHeaderView()).getHeight());
        } else if (delta < 0) {
            setDirection(Direction.FROM_FOOTER);
            getGestureManager().getScroller().setMaxScrollDistance(((View) getFooterView()).getHeight());
        }
    }

    private boolean canPull() {
        final boolean checkDegree = getGestureManager().getTouchHelper().getDegreeYFromDown() < 30;
        if (!checkDegree)
            return false;

        final int deltaY = (int) getGestureManager().getTouchHelper().getDeltaYFromDown();
        final boolean checkPullDelta = Math.abs(deltaY) > mTouchSlop;
        if (!checkPullDelta)
            return false;

        final boolean checkPull = (canPullFromHeader() && deltaY > 0) || (canPullFromFooter() && deltaY < 0);
        if (!checkPull)
            return false;

        final boolean checkState = getState() == State.RESET;
        if (!checkState)
            return false;

        final boolean checkNotNestedScroll = !mIsNestedScrollStarted;
        if (!checkNotNestedScroll)
            return false;

        setDirectionByDelta(deltaY);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return getGestureManager().onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return getGestureManager().onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (getGestureManager().getScroller().computeScrollOffset())
            ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected boolean isViewIdle() {
        final boolean checkStateIdle = getGestureManager().getState() == HGestureManager.State.Idle;
        if (!checkStateIdle)
            return false;

        final boolean checkNotNestedScroll = !mIsNestedScrollStarted;
        if (!checkNotNestedScroll)
            return false;

        return true;
    }

    @Override
    protected boolean smoothScroll(int startY, int endY) {
        return getGestureManager().getScroller().scrollToY(startY, endY, -1);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getGestureManager().getScroller().abortAnimation();
    }

    private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    private final NestedScrollingChildHelper mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    private boolean mIsNestedScrollStarted;
    private boolean mNeedConsumeNestedScroll;

    //---------- NestedScrollingParent Start ----------

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        final boolean checkState = getState() == State.RESET;
        if (!checkState)
            return false;

        final boolean checkMode = getMode() != Mode.PULL_DISABLE;
        if (!checkMode)
            return false;

        final boolean checkIsScrollToBound = HTouchHelper.isScrollToTop(getRefreshView()) || HTouchHelper.isScrollToBottom(getRefreshView());
        if (!checkIsScrollToBound)
            return false;

        final boolean checkDirection = getDirection() == Direction.NONE;
        if (!checkDirection)
            return false;

        final boolean checkTarget = target == getRefreshView();
        if (!checkTarget)
            return false;

        final boolean checkNestedScrollVertical = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (!checkNestedScrollVertical)
            return false;

        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        if (mIsDebug)
            Log.i(getDebugTag(), "onNestedScrollAccepted----------");

        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mIsNestedScrollStarted = true;
    }

    @Override
    public void onStopNestedScroll(View child) {
        if (mIsDebug)
            Log.e(getDebugTag(), "onStopNestedScroll:" + getDirection());

        mNestedScrollingParentHelper.onStopNestedScroll(child);
        mIsNestedScrollStarted = false;

        if (mNeedConsumeNestedScroll) {
            mNeedConsumeNestedScroll = false;
            processDragFinish();
        }

        stopNestedScroll();
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (mNeedConsumeNestedScroll) {
            consumed[1] = dy;
            moveViews(-dy, true);
        }

        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], mParentScrollConsumed, null)) {
            consumed[0] += mParentScrollConsumed[0];
            consumed[1] += mParentScrollConsumed[1];
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy == 0)
            return;

        if (getDirection() == Direction.NONE) {
            if (dy < 0) {
                // header
                if (canPullFromHeader()) {
                    setDirection(Direction.FROM_HEADER);
                    getGestureManager().getScroller().setMaxScrollDistance(((View) getHeaderView()).getHeight());
                }
            } else if (dy > 0) {
                // footer
                if (canPullFromFooter()) {
                    setDirection(Direction.FROM_FOOTER);
                    getGestureManager().getScroller().setMaxScrollDistance(((View) getFooterView()).getHeight());
                }
            }

            if (getDirection() != Direction.NONE) {
                mNeedConsumeNestedScroll = true;
                if (mIsDebug)
                    Log.i(getDebugTag(), "onNestedScroll need consume");
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    //---------- NestedScrollingParent End ----------

    //---------- NestedScrollingChild Start ----------

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    //---------- NestedScrollingChild End ----------
}
