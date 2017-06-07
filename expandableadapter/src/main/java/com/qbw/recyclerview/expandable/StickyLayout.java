package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qbw.log.XLog;
import com.qbw.recyclerview.util.PositionUtil;

import java.lang.ref.WeakReference;

/**
 * @author qbw
 * 2016/04/22 14:19
 * 如果要显示悬浮的Group，需要将RecyclerView包裹到此布局中
 */


public class StickyLayout extends FrameLayout {

    private StickyGroupHelper mStickyGroupHelper = new StickyGroupHelper();

    private int mStickyGroupY = -1;

    /**
     * Recyclerview item个数改变触发onLayout，但是在onLayout里面不能及时获取RecyclerView的itemview坐标，需要延迟一下再去获取
     */
    private int mUpdateDelay = 80;//5帧

    private RecyclerView mRecyclerView;
    private ExpandableAdapter mExpandableAdapter;
    private StickyListener mStickyListener;

    /**
     * 是否悬浮group
     */
    private boolean mStickyGroup;

    private StickyScrollListener mStickyScrollListener = new StickyScrollListener();

    public StickyLayout(Context context) {
        super(context);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (XLog.isEnabled()) XLog.d("changed[%b],mStickyGroupY[%d]", changed, mStickyGroupY);
        if (getChildCount() > 1) {
            View childView = getChildAt(1);
            childView.layout(childView.getLeft(),
                    mStickyGroupY,
                    childView.getRight(),
                    childView.getMeasuredHeight() + mStickyGroupY);
            if (XLog.isEnabled()) {
                Rect rect = new Rect();
                childView.getHitRect(rect);
                XLog.v("sticky group rect %s", rect.toString());
            }
            removeCallbacks(mUpdateDelayRunn);
            postDelayed(mUpdateDelayRunn, getUpdateDelay());
        }

    }

    public int getUpdateDelay() {
        return mUpdateDelay;
    }

    public void setUpdateDelay(int updateDelay) {
        mUpdateDelay = updateDelay;
    }

    private static class UpdateDelayRunn implements Runnable {

        private WeakReference<StickyLayout> mWRStickyLayout;

        public UpdateDelayRunn(StickyLayout stickyLayout) {
            mWRStickyLayout = new WeakReference<>(stickyLayout);
        }

        @Override
        public void run() {
            StickyLayout sl = mWRStickyLayout.get();
            if (sl == null) return;
            XLog.d("delay update stickgroup y");
            sl.update();
        }
    }

    /**
     * 不能实时获取NextViewHolder的坐标
     */
    private UpdateDelayRunn mUpdateDelayRunn = new UpdateDelayRunn(this);


    /**
     * @param stickyGroup 是否支持group悬浮效果
     */
    public void init(boolean stickyGroup) {
        mStickyGroup = stickyGroup;
        mRecyclerView = (RecyclerView) getChildAt(0);
        mExpandableAdapter = (ExpandableAdapter) mRecyclerView.getAdapter();
        if (mExpandableAdapter == null) {
            throw new RuntimeException("请先设置RecyclerView的Adapter！");
        } else if (!(mExpandableAdapter instanceof StickyListener)) {
            throw new RuntimeException("Adapter 必须实现 StickyListener！");
        }
        mStickyListener = (StickyListener) mExpandableAdapter;
        mRecyclerView.addOnScrollListener(mStickyScrollListener);
    }

    private boolean update() {
        if (!mStickyGroup) {
            mStickyGroupY = 0;
            return false;
        }
        int firstVisibleItemPosition = PositionUtil.findFirstVisibleItemPosition(mRecyclerView);
        if (RecyclerView.NO_POSITION == firstVisibleItemPosition) {
            if (XLog.isEnabled()) XLog.w("no visible item");
            return false;
        }
        int groupPosition = -1;
        int groupViewType = -1;
        int groupAdapterPosition = -1;

        if (mStickyListener.isPostionGroup(firstVisibleItemPosition)) {
            groupAdapterPosition = firstVisibleItemPosition;
            groupPosition = mExpandableAdapter.getGroupPosition(firstVisibleItemPosition);
            groupViewType = mExpandableAdapter.getItemViewType(firstVisibleItemPosition);
        } else if (mStickyListener.isPostionGroupChild(firstVisibleItemPosition)) {
            int[] poss = mExpandableAdapter.getGroupChildPosition(firstVisibleItemPosition);
            groupAdapterPosition = firstVisibleItemPosition - (poss[1] + 1);
            groupPosition = poss[0];
            groupViewType = mExpandableAdapter.getItemViewType(groupAdapterPosition);
        }
        if (groupPosition == -1 || groupViewType == -1 || groupAdapterPosition == -1) {
            mStickyGroupHelper.removeGroupViewHolder(this);
            mStickyGroupY = 0;
            return false;
        }

        int groupCount = mExpandableAdapter.getGroupCount();

        int nextGroupPosition = groupPosition + 1;
        int nextAdapterPosition = -1;
        RecyclerView.ViewHolder nextVh = null;//下一个需要判断是否相交的holder
        if (nextGroupPosition < groupCount) {//group下面还有group
            nextAdapterPosition = mExpandableAdapter.convertGroupPosition(nextGroupPosition);
            if (XLog.isEnabled()) XLog.v("NextGroup, next adap pos [%d]", nextAdapterPosition);
        } else {
            int fcount = mExpandableAdapter.getFooterCount();
            if (XLog.isEnabled())
                XLog.d("group[%d] is the last.footer count [%d]", groupPosition, fcount);
            if (fcount > 0) {//group下面还有footer
                nextAdapterPosition = mExpandableAdapter.convertFooterPosition(0);
                if (XLog.isEnabled()) XLog.v("NextFooter, next adap pos [%d]", nextAdapterPosition);
            }
        }

        if (nextAdapterPosition != -1) {
            nextVh = mRecyclerView.findViewHolderForAdapterPosition(nextAdapterPosition);
        }

        if (nextVh == null) {
            if (XLog.isEnabled()) XLog.v("next viewholder is null");
            mStickyGroupY = 0;
        } else {
            int nextHolderTop = nextVh.itemView.getTop();
            int groupHolderHeight = mStickyListener.getStickyGroupViewHolderHeight(groupViewType);
            if (XLog.isEnabled())
                XLog.v("next rect top[%d], sticky rect height[%d]", nextHolderTop, groupHolderHeight);
            if (nextHolderTop >= groupHolderHeight) {
                mStickyGroupY = 0;
            } else {
                mStickyGroupY = nextHolderTop - groupHolderHeight;
            }
        }

        if (mStickyGroupHelper.getGroupType() != groupViewType) {
            mStickyGroupHelper.addGroupViewHolder(this,
                    firstVisibleItemPosition,
                    groupPosition,
                    groupViewType,
                    groupCount,
                    mStickyListener.onCreateStickyGroupViewHolder(groupViewType, this),
                    mStickyListener);
        } else {
            mStickyGroupHelper.bindGroupViewHolder(this, firstVisibleItemPosition,
                    groupPosition,
                    groupViewType,
                    groupCount,
                    mStickyListener);
        }

        if (getChildCount() > 1) {
            if (getChildAt(1).getTop() == mStickyGroupY) {
                if (XLog.isEnabled()) XLog.v("equal sticky group layout y [%d]", mStickyGroupY);
                return false;
            }
            requestLayout();
            return true;
        }
        return false;
    }

    private class StickyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (XLog.isEnabled()) XLog.v("dx[%d],dy[%d]", dx, dy);
            if (update()) {
                removeCallbacks(mUpdateDelayRunn);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (XLog.isEnabled()) XLog.v("newState[%d]", newState);
            if (update()) {
                removeCallbacks(mUpdateDelayRunn);
            }
        }
    }

    public interface StickyListener {
        boolean isPostionGroup(int adapterPosition);

        boolean isPostionGroupChild(int adapterPosition);

        boolean isPositionFooter(int adapterPosition);

        RecyclerView.ViewHolder onCreateStickyGroupViewHolder(int groupType, ViewGroup parent);

        void onBindStickyGroupViewHolder(int adapterPosition,
                                         int groupPosition,
                                         RecyclerView.ViewHolder stickyGroupViewHolder);

        /**
         * 返回指定group的高度
         */
        int getStickyGroupViewHolderHeight(int groupType);

        /**
         * 返回指定group的marginLeft和marginRight
         */
        int[] getStickyGroupViewHolderHorizontalMargin(int groupType);
    }
}
