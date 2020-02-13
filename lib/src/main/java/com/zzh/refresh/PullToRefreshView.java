package com.zzh.refresh;

import android.view.View;

import com.zzh.refresh.loadingview.LoadingView;

/**
 * @Date: 2020-02-13 18:07
 * @Email: zzh_hz@126.com
 * @QQ: 1299234582
 * @Author: zzh
 * @Description: PullToRefreshView.java 刷新接口
 */
public interface PullToRefreshView {
    /**
     * 默认的拖动距离消耗比例
     */
    float DEFAULT_COMSUME_SCROLL_PERCENT = 0.5f;
    /**
     * 默认的显示刷新结果的时长（毫秒）
     */
    int DEFAULT_DURATION_SHOW_REFRESH_RESULT = 600;

    /**
     * 设置刷新模式
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * 设置刷新回调
     *
     * @param onRefreshCallback
     */
    void setOnRefreshCallback(OnRefreshCallback onRefreshCallback);

    /**
     * 设置状态变化回调
     *
     * @param onStateChangeCallback
     */
    void setOnStateChangeCallback(OnStateChangeCallback onStateChangeCallback);

    /**
     * 设置view位置变化回调
     *
     * @param onViewPositionChangeCallback
     */
    void setOnViewPositionChangeCallback(OnViewPositionChangeCallback onViewPositionChangeCallback);

    /**
     * 设置可以触发拖动的条件，设置后当view内部满足拖动，并且此对象也满足条件后才可以触发拖动
     *
     * @param pullCondition
     */
    void setPullCondition(PullCondition pullCondition);

    /**
     * 设置HeaderView和FooterView是否是覆盖的模式（默认false）
     *
     * @param overLayMode
     */
    void setOverLayMode(boolean overLayMode);

    /**
     * 是否是覆盖的模式
     *
     * @return
     */
    boolean isOverLayMode();

    /**
     * 设置拖动的时候要消耗的拖动距离比例，默认{@link #DEFAULT_COMSUME_SCROLL_PERCENT}
     *
     * @param comsumeScrollPercent [0-1]
     */
    void setComsumeScrollPercent(float comsumeScrollPercent);

    /**
     * 设置显示刷新结果的时长，默认{@link #DEFAULT_DURATION_SHOW_REFRESH_RESULT}
     *
     * @param durationShowRefreshResult
     */
    void setDurationShowRefreshResult(int durationShowRefreshResult);

    /**
     * 设置HeaderView处处于刷新状态
     */
    void startRefreshingFromHeader();

    /**
     * 设置Foot而View处处于刷新状态
     */
    void startRefreshingFromFooter();

    /**
     * 停止刷新
     */
    void stopRefreshing();

    /**
     * 停止刷新并展示刷新结果，当状态处于刷新中的时候此方法调用才有效
     *
     * @param success true-刷新成功，false-刷新失败
     */
    void stopRefreshingWithResult(boolean success);

    /**
     * 是否处于刷新中
     *
     * @return
     */
    boolean isRefreshing();

    /**
     * 返回当前的状态
     *
     * @return
     */
    State getState();

    /**
     * 返回当前的刷新模式
     *
     * @return
     */
    Mode getMode();

    /**
     * 返回HeaderView
     *
     * @return
     */
    LoadingView getHeaderView();

    /**
     * 设置HeaderView
     *
     * @param headerView
     */
    void setHeaderView(LoadingView headerView);

    /**
     * 返回FooterView
     *
     * @return
     */
    LoadingView getFooterView();

    /**
     * 设置FooterView
     *
     * @param footerView
     */
    void setFooterView(LoadingView footerView);

    /**
     * 返回要支持刷新的view
     *
     * @return
     */
    View getRefreshView();

    /**
     * 返回当前拖动方向
     *
     * @return
     */
    Direction getDirection();

    /**
     * 返回滚动的距离
     *
     * @return
     */
    int getScrollDistance();

    enum State {
        /**
         * 重置
         */
        RESET,
        /**
         * 下拉刷新
         */
        PULL_TO_REFRESH,
        /**
         * 松开刷新
         */
        RELEASE_TO_REFRESH,
        /**
         * 刷新中
         */
        REFRESHING,
        /**
         * 刷新结果，成功
         */
        REFRESHING_SUCCESS,
        /**
         * 刷新结果，失败
         */
        REFRESHING_FAILURE,
        /**
         * 刷新完成
         */
        FINISH,
    }

    enum Direction {
        NONE,
        FROM_HEADER,
        FROM_FOOTER,
    }

    enum Mode {
        /**
         * 支持上下拉
         */
        PULL_BOTH,
        /**
         * 只支持下拉
         */
        PULL_FROM_HEADER,
        /**
         * 只支持上拉
         */
        PULL_FROM_FOOTER,
        /**
         * 不支持上下拉
         */
        PULL_DISABLE,
    }

    interface OnStateChangeCallback {
        /**
         * 状态变化回调
         *
         * @param oldState
         * @param newState
         * @param view
         */
        void onStateChanged(State oldState, State newState, PullToRefreshView view);
    }

    interface OnRefreshCallback {
        /**
         * 下拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromHeader(PullToRefreshView view);

        /**
         * 上拉触发刷新回调
         *
         * @param view
         */
        void onRefreshingFromFooter(PullToRefreshView view);
    }

    interface OnViewPositionChangeCallback {
        /**
         * view位置变化回调
         *
         * @param view
         */
        void onViewPositionChanged(PullToRefreshView view);
    }

    interface PullCondition {
        /**
         * 是否可以从Header处触发拖动
         *
         * @param view
         * @return
         */
        boolean canPullFromHeader(PullToRefreshView view);

        /**
         * 是否可以从Footer处触发拖动
         *
         * @param view
         * @return
         */
        boolean canPullFromFooter(PullToRefreshView view);
    }
}
