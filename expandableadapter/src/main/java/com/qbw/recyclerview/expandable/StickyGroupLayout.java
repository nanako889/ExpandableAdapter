package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.qbw.log.XLog;

/**
 * @author qbw
 * @createtime 2016/04/22 16:02
 * 悬浮的group布局
 */


class StickyGroupLayout extends FrameLayout {
    private int mGroupPos = RecyclerView.NO_POSITION;
    private int mGroupType = 0;
    private RecyclerView.ViewHolder mGroupViewHolder;

    public StickyGroupLayout(Context context) {
        super(context);
    }

    public StickyGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyGroupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addGroupViewHolder(int groupPos, int groupType, RecyclerView.ViewHolder groupViewHolder) {
        if (getChildCount() > 0) {
            if (mGroupType == groupType) {
                mGroupPos = groupPos;
                XLog.d("Sticky group layout contains group type, position[%d] view", mGroupPos);
                return;
            } else {
                removeGroupView();
            }
        }
        mGroupPos = groupPos;
        mGroupType = groupType;
        mGroupViewHolder = groupViewHolder;
        addView(groupViewHolder.itemView, 0);
        XLog.d("Add group view, group position[%d]", groupPos);
    }

    public void removeGroupView() {
        if (getChildCount() <= 0) {
            XLog.d("Sticky group layout contains no group view");
            return;
        }
        XLog.d("Remove group view");
        removeAllViews();
        mGroupPos = RecyclerView.NO_POSITION;
        mGroupType = 0;
        mGroupViewHolder = null;
    }

    public RecyclerView.ViewHolder getGroupViewHolder() {
        return mGroupViewHolder;
    }

    public void bindGroupViewHolder(int groupPos, ExpandableAdapter expandableAdapter) {
        if (null == mGroupViewHolder) {
            return;
        }
        mGroupPos = groupPos;
        expandableAdapter.bindStickyGroupData(groupPos, mGroupViewHolder);
    }

    public int getGroupPos() {
        return mGroupPos;
    }

    public int getGroupType() {
        return mGroupType;
    }

    //    public void setGroupPos(int mGroupPos) {
//        this.mGroupPos = mGroupPos;
//    }
}
