package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.qbw.log.XLog;

/**
 * @author qbw
 * @createtime 2016/04/22 16:02
 */


class StickyGroupLayout extends FrameLayout {
    private int groupPos = -1;
    private RecyclerView.ViewHolder groupViewHolder;

    public StickyGroupLayout(Context context) {
        super(context);
    }

    public StickyGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyGroupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addGroupViewHolder(int groupPos, RecyclerView.ViewHolder groupViewHolder) {
        if (getChildCount() > 0) {
            XLog.d("Sticky group layout contains group[%d] view", this.groupPos);
            return;
        }
        this.groupPos = groupPos;
        this.groupViewHolder = groupViewHolder;
        this.addView(groupViewHolder.itemView, 0);
        XLog.d("Add group view, group position[%d]", groupPos);
    }

    public void removeGroupView() {
        if (getChildCount() <= 0) {
            XLog.d("Sticky group layout contains no group view");
            return;
        }
        XLog.d("Remove group view");
        removeAllViews();
        groupPos = -1;
        groupViewHolder = null;
    }

    public RecyclerView.ViewHolder getGroupViewHolder() {
        return groupViewHolder;
    }

    public void bindGroupViewHolder(int groupPos, ExpandableAdapter expandableAdapter) {
        if (null == groupViewHolder) {
            return;
        }
        this.groupPos = groupPos;
        expandableAdapter.bindStickyGroupData(groupPos, groupViewHolder);
    }

    public int getGroupPos() {
        return groupPos;
    }

//    public void setGroupPos(int groupPos) {
//        this.groupPos = groupPos;
//    }
}
