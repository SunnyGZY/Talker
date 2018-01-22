package net.sunny.talker.view.okrecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yukuo on 2016/4/29.
 * 这是一个带下拉刷新和上拉加载的revycleview
 */
public class OkRecycleView extends RecyclerView {
    /**
     * 是否可以刷新的常量
     */
    private boolean pullRefreshEnabled = true;
    /**
     * 是否可以加载更多的常量
     */
    private boolean loadMoreEnabled = true;
    /**
     * 头布局集合
     */
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    /**
     * 脚布局集合
     */
    private ArrayList<View> mFootViews = new ArrayList<>();
    //当前的头布局
    private OkRecycleviewRefreshHeadView mHeadView;
    //监听器
    private OnRefreshAndLoadMoreListener refreshAndLoadMoreListener;
    //加载数据中的状态
    private boolean isLoadingData = false;
    //adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    //没有数据的状态
    private boolean isNoMore = false;
    //阻率
    private static final float DRAG_RATE = 2;
    /**
     * 数据观察者625
     */
    private final AdapterDataObserver mDataObserver = new DataObserver();
    /**
     * 最后的y坐标
     */
    private float mLastY = -1;
    private WrapAdapter mWrapAdapter;
    private static final int TYPE_REFRESH_HEADER = -5;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = -3;
    private static List<Integer> sHeaderTypes = new ArrayList<>();
    private static final int HEADER_INIT_INDEX = 10000;

    public OkRecycleView(Context context) {
        this(context, null);
    }

    public OkRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OkRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 这是一个初始化的方法
     */
    private void init() {
        //可以进行刷新
        if (pullRefreshEnabled) {
            //获取头布局
            OkRecycleviewRefreshHeadView refreshHeader = new OkRecycleviewRefreshHeadView(getContext());
            //添加进去
            mHeaderViews.add(0, refreshHeader);
            mHeadView = refreshHeader;
        }
        OkRecycleviewRefreshFootView footView = new OkRecycleviewRefreshFootView(getContext());
        addFootView(footView);
        mFootViews.get(0).setVisibility(GONE);
    }

    /**
     * 当滚动状态改变的时候
     *
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && refreshAndLoadMoreListener != null && !isLoadingData && loadMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && !isNoMore && mHeadView.getStatus() < OkRecycleviewRefreshHeadView.STATE_REFRESHING) {

                View footView = mFootViews.get(0);
                isLoadingData = true;
                if (footView instanceof OkRecycleviewRefreshFootView) {
                    ((OkRecycleviewRefreshFootView) footView).setState(OkRecycleviewRefreshFootView.STATE_LOADING);
                } else {
                    footView.setVisibility(View.VISIBLE);
                }
                refreshAndLoadMoreListener.onLoadMore();
            }
        }
    }

    private int findMax(int[] into) {
        int max = into[0];
        for (int value : into) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 控件的触摸事件的监听
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mLastY == -1) {
            mLastY = e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = e.getRawY() - mLastY; // 手机在屏幕上滑动的距离 为正向下滑 为负向上滑
                mLastY = e.getRawY();
                if (isOnTop() && pullRefreshEnabled) {
                    mHeadView.onMove(deltaY / DRAG_RATE);
                    refreshAndLoadMoreListener.onMove();
                    if (mHeadView.getVisibleHeight() > 0 && mHeadView.getStatus() < mHeadView.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                //复位
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mHeadView.releaseAction()) {
                        if (refreshAndLoadMoreListener != null) {
                            refreshAndLoadMoreListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 是否是顶部
     *
     * @return
     */
    public boolean isOnTop() {
        return !(mHeaderViews == null || mHeaderViews.isEmpty())
                && mHeaderViews.get(0).getParent() != null;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * 包装的适配器
     */
    private class WrapAdapter extends Adapter<ViewHolder> {
        private Adapter adapter;
        private int mCurrentPosition = 0;
        private int headerPosition = 1;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isContentHeader(int position) {
            return position >= 1 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }


        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                mCurrentPosition++;
                return new SimpleViewHolder(mHeaderViews.get(0));
            } else if (isContentHeader(mCurrentPosition)) {
                if (viewType == sHeaderTypes.get(mCurrentPosition - 1)) {
                    mCurrentPosition++;
                    return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
                }
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootViews.get(0));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 这是一个数据观察者
     */
    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                int emptyCount = 0;
                if (pullRefreshEnabled) {
                    emptyCount++;
                }
                if (loadMoreEnabled) {
                    emptyCount++;
                }
                if (adapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    OkRecycleView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    OkRecycleView.this.setVisibility(View.VISIBLE);
                }
            }
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }

    }

    /**
     * 设置没有更多数据了
     *
     * @param noMore
     */
    public void setNoMoreData(boolean noMore) {
        this.isNoMore = noMore;
        View footView = mFootViews.get(0);
        ((OkRecycleviewRefreshFootView) footView).setState(isNoMore ? OkRecycleviewRefreshFootView.STATE_NOMORE : OkRecycleviewRefreshFootView.STATE_COMPLETE);
    }

    /**
     * 获取一个空布局
     *
     * @return
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 设置空布局
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        //数据改变
        mDataObserver.onChanged();
    }

    /**
     * 还原所有的状态
     */
    public void reSetStatus() {
        setLoadMoreComplete();
        refreshComplete();
    }

    /**
     * 设置刷新完成
     */
    private void refreshComplete() {
        mHeadView.refreshComplete();
    }

    /**
     * 设置加载更多完成
     */
    public void setLoadMoreComplete() {
        //设置加载数据为false
        isLoadingData = false;
        View footView = mFootViews.get(0);
        if (footView instanceof OkRecycleviewRefreshFootView) {
            ((OkRecycleviewRefreshFootView) footView).setState(OkRecycleviewRefreshFootView.STATE_COMPLETE);
        } else {
            footView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置刷新和加载更多的监听器
     *
     * @param refreshAndLoadMoreListener 监听器
     */
    public void setRefreshAndLoadMoreListener(OnRefreshAndLoadMoreListener refreshAndLoadMoreListener) {
        this.refreshAndLoadMoreListener = refreshAndLoadMoreListener;
    }

    /**
     * 刷新和加载更多的监听器
     */
    public interface OnRefreshAndLoadMoreListener {
        void onRefresh();

        void onLoadMore();

        void onMove();
    }

    /**
     * 设置是否启用下拉刷新功能
     *
     * @param isEnabled
     */
    public void setReFreshEnabled(boolean isEnabled) {
        pullRefreshEnabled = isEnabled;
    }

    /**
     * 设置刷新完成
     */
    public void setReFreshComplete() {
        mHeadView.refreshComplete();
    }

    /**
     * 设置是否启用上拉加载功能
     *
     * @param isEnabled
     */
    public void setLoadMoreEnabled(boolean isEnabled) {
        loadMoreEnabled = isEnabled;
        //如果不启用加载更多功能,就隐藏脚布局
        if (!isEnabled && mFootViews.size() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * 设置是否刷新ing状态
     *
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && pullRefreshEnabled && refreshAndLoadMoreListener != null) {
            mHeadView.setState(OkRecycleviewRefreshHeadView.STATE_REFRESHING);
            mHeadView.onMove(mHeadView.getMeasuredHeight());
            refreshAndLoadMoreListener.onRefresh();
        }
    }

    /**
     * 添加头布局
     *
     * @param headView 刷新头布局
     */
    public void addHeadView(View headView) {
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof OkRecycleviewRefreshHeadView)) {
            OkRecycleviewRefreshHeadView refreshHeader = new OkRecycleviewRefreshHeadView(getContext());
            mHeaderViews.add(0, refreshHeader);
            mHeadView = refreshHeader;
        }
        mHeaderViews.add(headView);
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
    }

    /**
     * 添加底部布局
     *
     * @param footView
     */
    private void addFootView(OkRecycleviewRefreshFootView footView) {
        mFootViews.clear();
        mFootViews.add(footView);
    }

}
