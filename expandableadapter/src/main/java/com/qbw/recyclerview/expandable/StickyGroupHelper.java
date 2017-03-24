package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qbw.log.XLog;

/**
 * @author qbw
 * @createtime 2016/04/22 16:02
 * 悬浮的group布局
 */


class StickyGroupHelper {

    /**
     * GroupViewHolder的itemType（通过Adapter的getItemType获得）
     */
    private int mGroupType = -1;

    /**
     * 为什么不直接用groupPos来判断有没有bindGroupViewHolder？
     * 原因：如果在一个group的位置又插入了一个同样groupType的group，此时groupPos并没有改变，所以要结合groupCount的变化来判断要不要重新bindGroupViewHolder
     */
    private int mGroupCount = 0;
    private int mGroupPos = RecyclerView.NO_POSITION;

    private RecyclerView.ViewHolder mGroupViewHolder;

    public void addGroupViewHolder(StickyLayout stickyLayout,
                                   int adapterPos,
                                   int groupPos,
                                   int groupType,
                                   int groupCount,
                                   RecyclerView.ViewHolder groupViewHolder,
                                   StickyLayout.StickyListener stickyListener) {
        removeGroupViewHolder(stickyLayout);
        mGroupPos = groupPos;
        mGroupType = groupType;
        mGroupCount = groupCount;
        mGroupViewHolder = groupViewHolder;
        stickyLayout.addView(mGroupViewHolder.itemView);
        stickyListener.onBindStickyGroupViewHolder(adapterPos, mGroupPos, mGroupViewHolder);
        if (XLog.isEnabled()) XLog.d("add group[%d] viewholder", mGroupPos);
    }

    public void removeGroupViewHolder(StickyLayout stickyLayout) {
        if (mGroupViewHolder != null) stickyLayout.removeView(mGroupViewHolder.itemView);
        mGroupPos = RecyclerView.NO_POSITION;
        mGroupType = -1;
        mGroupCount = 0;
        mGroupViewHolder = null;
        if (XLog.isEnabled()) XLog.d("remove group[%d] viewholder", mGroupPos);
    }

    public void bindGroupViewHolder(int adapPos, int groupPos,
                                    int groupType,
                                    int groupCount,
                                    StickyLayout.StickyListener stickyListener) {
        if (mGroupViewHolder == null) {
            if (XLog.isEnabled()) XLog.e("group view holder 不应该为null");
            return;
        } else if (mGroupType != groupType) {
            if (XLog.isEnabled()) XLog.w("item type 一样才可以调用bind");
            return;
        } else if (mGroupPos == groupPos && mGroupCount == groupCount) {
            if (XLog.isEnabled()) XLog.v("group[%d] 已经bind过了", groupPos);
            return;
        }
        mGroupPos = groupPos;
        mGroupType = groupType;
        mGroupCount = groupCount;
        stickyListener.onBindStickyGroupViewHolder(adapPos, mGroupPos, mGroupViewHolder);
    }

    public int getGroupPos() {
        return mGroupPos;
    }

    public int getGroupType() {
        return mGroupType;
    }

    public RecyclerView.ViewHolder getGroupViewHolder() {
        return mGroupViewHolder;
    }
}
