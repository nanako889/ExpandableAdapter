package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.WindowManager;
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
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mGroupViewHolder.itemView.getLayoutParams();
        params.height = stickyListener.getStickyGroupViewHolderHeight(groupType);
        int[] hMargins = stickyListener.getStickyGroupViewHolderHorizontalMargin(groupType);
        if (hMargins != null && hMargins.length == 2) {
            params.leftMargin = hMargins[0];
            params.rightMargin = hMargins[1];
            params.width = stickyLayout.getWidth() - params.leftMargin - params.rightMargin;
        } else {
            params.width = stickyLayout.getWidth();
        }
        stickyLayout.addView(mGroupViewHolder.itemView, params);
        stickyListener.onBindStickyGroupViewHolder(adapterPos, mGroupPos, mGroupViewHolder);
        if (XLog.isEnabled()) XLog.d("add group[%d] sticky viewholder", mGroupPos);
    }

    public void removeGroupViewHolder(StickyLayout stickyLayout) {
        if (mGroupPos != -1 && XLog.isEnabled()) XLog.d("remove group[%d] viewholder", mGroupPos);
        if (mGroupViewHolder != null) stickyLayout.removeView(mGroupViewHolder.itemView);
        mGroupPos = RecyclerView.NO_POSITION;
        mGroupType = -1;
        mGroupCount = 0;
        mGroupViewHolder = null;
    }

    public void bindGroupViewHolder(StickyLayout stickyLayout, int adapPos, int groupPos,
                                    int groupType,
                                    int groupCount,
                                    StickyLayout.StickyListener stickyListener) {
        if (mGroupViewHolder == null) {
            if (XLog.isEnabled()) XLog.e("group view holder 不应该为null");
            return;
        } else if (mGroupType != groupType) {
            if (XLog.isEnabled()) XLog.w("item type 一样才可以调用bind");
            return;
        } else {
            checkResetItemViewSize(stickyLayout, groupPos, groupType, stickyListener);
            if (mGroupPos == groupPos && mGroupCount == groupCount) {
                if (XLog.isEnabled()) XLog.v("group[%d] 已经bind过了", groupPos);
                return;
            }
        }
        if (XLog.isEnabled()) XLog.d("bind group %d sticky viewholder", groupPos);
        mGroupPos = groupPos;
        mGroupType = groupType;
        mGroupCount = groupCount;
        stickyListener.onBindStickyGroupViewHolder(adapPos, mGroupPos, mGroupViewHolder);
    }

    /**
     * 有些设备上，添加itemview到StickLayout之后，宽高为0，不知道为什么
     */
    private void checkResetItemViewSize(StickyLayout stickyLayout, int groupPos, int groupType, StickyLayout.StickyListener stickyListener) {
        int targetWidth = stickyLayout.getWidth();
        int[] hMargins = stickyListener.getStickyGroupViewHolderHorizontalMargin(groupType);
        boolean hasHMargin = false;
        if (hMargins != null && hMargins.length == 2) {
            hasHMargin = true;
            targetWidth = targetWidth - hMargins[0] - hMargins[1];
        }
        int targetHeight = stickyListener.getStickyGroupViewHolderHeight(groupType);
        int realWidth = mGroupViewHolder.itemView.getWidth();
        int realHeight = mGroupViewHolder.itemView.getHeight();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mGroupViewHolder.itemView.getLayoutParams();
        if (realWidth != targetWidth || realHeight != targetHeight || (hasHMargin && (params.leftMargin != hMargins[0] || params.rightMargin != hMargins[1]))) {
            if (XLog.isEnabled()) {
                if (realWidth != targetWidth) {
                    XLog.w("realWidth[%d], targetWidth[%d]", realWidth, targetWidth);
                }
                if (realHeight != targetHeight) {
                    XLog.w("realHeight[%d], targetHeight[%d]", realHeight, targetHeight);
                }
                if (hasHMargin && (params.leftMargin != hMargins[0] || params.rightMargin != hMargins[1])) {
                    XLog.w("params margin[%d,%d], targetMargin[%d,%d]", params.leftMargin, params.rightMargin, hMargins[0], hMargins[1]);
                }
            }
            params.width = targetWidth;
            params.height = targetHeight;
            if (hasHMargin) {
                params.leftMargin = hMargins[0];
                params.rightMargin = hMargins[1];
            }
            mGroupViewHolder.itemView.setLayoutParams(params);
            if (XLog.isEnabled()) XLog.w("group[%d] 重新设置宽高", groupPos);
        }
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
