package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.graphics.Point;
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
 * @createtime 2016/04/22 14:19
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
        if (XLog.isEnabled()) XLog.d("changed[%b]", changed);
        if (getChildCount() > 1) {
            View childView = getChildAt(1);
            childView.layout(0,
                    mStickyGroupY,
                    childView.getMeasuredWidth(),
                    childView.getMeasuredHeight() + mStickyGroupY);
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

    /**
     * update会被频繁调用，为了避免内存抖动，将update里面用到的变量声明为成员变量
     */

    private int mGPos;
    private int mGType;
    private int mNGpos;
    private int mNAdapPos;
    private int mGCount;
    /**
     * 下一个要判断有没有交叉的holder的top坐标
     */
    private int mNT;
    /**
     * 当前悬浮Group的高度
     */
    private int mSH;
    private int mStickGroupTargetY = -1;
    private int mFVIPos;

    private boolean update() {
        updateStickyGroupTargetY();
        return updateStickyGroupY();
    }

    private void updateStickyGroupTargetY() {
        mStickGroupTargetY = -1;
        if (!mStickyGroup) {
            mStickyGroupY = -1;
            return;
        }
        mFVIPos = PositionUtil.findFirstVisibleItemPosition(mRecyclerView);
        if (RecyclerView.NO_POSITION == mFVIPos) {
            if (XLog.isEnabled()) XLog.w("no visible item");
            return;
        }
        mGPos = -1;
        mGType = -1;
        if (mStickyListener.isPostionGroup(mFVIPos)) {
            mGPos = mExpandableAdapter.getGroupPosition(mFVIPos);
            mGType = mExpandableAdapter.getItemViewType(mFVIPos);
        } else if (mStickyListener.isPostionGroupChild(mFVIPos)) {
            int[] poss = mExpandableAdapter.getGroupChildPosition(mFVIPos);
            mGPos = poss[0];
            mGType = mExpandableAdapter.getItemViewType(mFVIPos - (poss[1] + 1));
        }
        if (mGPos == -1 || mGType == -1) {
            mStickyGroupHelper.removeGroupViewHolder(this);
            mStickyGroupY = -1;
            return;
        }

        mGCount = mExpandableAdapter.getGroupCount();

        mNGpos = mGPos + 1;
        mNAdapPos = -1;
        RecyclerView.ViewHolder nextVh = null;//下一个需要判断是否相交的holder
        if (mNGpos < mGCount) {//group下面还有group
            mNAdapPos = mExpandableAdapter.convertGroupPosition(mNGpos);
            if (XLog.isEnabled()) XLog.v("NextGroup, next adap pos [%d]", mNAdapPos);
        } else {
            int fcount = mExpandableAdapter.getFooterCount();
            if (XLog.isEnabled()) XLog.w("group[%d] is the last.footer count [%d]", mGPos, fcount);
            if (fcount > 0) {//group下面还有footer
                mNAdapPos = mExpandableAdapter.convertFooterPosition(0);
                if (XLog.isEnabled()) XLog.v("NextFooter, next adap pos [%d]", mNAdapPos);
            }
        }

        if (mNAdapPos != -1) {
            nextVh = mRecyclerView.findViewHolderForAdapterPosition(mNAdapPos);
        }

        if (nextVh == null) {
            if (XLog.isEnabled()) XLog.v("next viewholder is null");
            mStickGroupTargetY = 0;
        } else {
            mNT = nextVh.itemView.getTop();
            mSH = mStickyListener.getStickyGroupViewHolderSize(mGType).y;
            if (XLog.isEnabled()) XLog.v("next rect top[%d], sticky rect height[%d]", mNT, mSH);
            if (mNT >= mSH) {
                mStickGroupTargetY = 0;
            } else {
                mStickGroupTargetY = mNT - mSH;
            }
        }

        if (mStickyGroupHelper.getGroupType() != mGType) {
            mStickyGroupHelper.addGroupViewHolder(this,
                    mFVIPos,
                    mGPos,
                    mGType,
                    mGCount,
                    mStickyListener.onCreateStickyGroupViewHolder(
                            mGType,
                            mRecyclerView),
                    mStickyListener);
            if (XLog.isEnabled()) XLog.d("add group[%d] view", mGPos);
        } else {
            mStickyGroupHelper.bindGroupViewHolder(mFVIPos, mGPos, mGType, mGCount, mStickyListener);
            if (XLog.isEnabled()) XLog.v("bind group[%d] view", mGPos);
        }
    }

    private boolean updateStickyGroupY() {
        if (!mStickyGroup) {
            mStickGroupTargetY = -1;
            mStickyGroupY = -1;
            return false;
        }
        if (mStickyGroupY == mStickGroupTargetY) {
            if (XLog.isEnabled()) XLog.v("equal sticky group layout y [%d]", mStickyGroupY);
            return false;
        }
        mStickyGroupY = mStickGroupTargetY;
        if (mStickyGroupY == -1) {
            return false;
        }
        requestLayout();
        return true;
    }

    private class StickyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (update()) {
                removeCallbacks(mUpdateDelayRunn);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (update()) {
                removeCallbacks(mUpdateDelayRunn);
            }
        }
    }

    public interface StickyListener {
        boolean isPostionGroup(int adapterPosition);

        boolean isPostionGroupChild(int adapterPosition);

        boolean isPositionFooter(int adapterPosition);

        RecyclerView.ViewHolder onCreateStickyGroupViewHolder(int groupType,
                                                              ViewGroup parent);

        void onBindStickyGroupViewHolder(int adapterPosition,
                                         int groupPosition,
                                         RecyclerView.ViewHolder stickyGroupViewHolder);

        /**
         * 添加groupview之前需要计算y坐标，所以要提前获得groupview的高度
         */
        Point getStickyGroupViewHolderSize(int groupType);
    }
}
