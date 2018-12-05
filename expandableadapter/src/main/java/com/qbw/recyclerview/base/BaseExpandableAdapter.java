package com.qbw.recyclerview.base;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Bond on 2016/4/2.
 * Item Order below:
 * <p>
 * Header
 * Child
 * Group
 * ***GroupChild
 * Footer
 */
public abstract class BaseExpandableAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract T getItem(int adapPos);

    public abstract int getHeaderCount();


    public abstract int getChildCount();


    public abstract int getGroupCount();


    /**
     * @param groupPosition group position in group data list
     * @return
     */
    public abstract int getGroupChildCount(int groupPosition);


    public abstract int getFooterCount();

    /**
     * @param headerPosition, header position in header data list
     * @return position in adapter data list
     *//*
    protected int convertHeaderPositionInner(int headerPosition) {
        return headerPosition;
    }

    *//**
     * @param childPosition, child position in child data list
     * @return position in adapter data list
     *//*
    protected int convertChildPositionInner(int childPosition) {
        int adapPos = childPosition + getHeaderCount();
        XLog.v("child position[%d] -> adapter position[%d]", childPosition, adapPos);
        return adapPos;
    }

    *//**
     * @param footerPosition, footer position in footer data list
     * @return position in adapter data list
     *//*
    protected int convertFooterPositionInner(int footerPosition) {
        int adapPos = footerPosition + (getItemCount() - getFooterCount());
        if (XLog.isEnabled())
            XLog.v("footer position[%d] -> adapter position[%d]", footerPosition, adapPos);
        return adapPos;
    }

    *//**
     * @param groupPosition,group position in group data list
     * @return position in adapter data list
     *//*
    protected int convertGroupPositionInner(int groupPosition) {
        int aboveItemCount = groupPosition;//all item count above this group
        for (int i = 0; i < groupPosition; i++) {
            aboveItemCount += getGroupChildCount(i);
        }
        int adapPos = getHeaderCount() + getChildCount() + aboveItemCount;
        if (XLog.isEnabled())
            XLog.v("group position[%d] -> adapter position[%d]", groupPosition, adapPos);
        return adapPos;
    }

    *//**
     * @param groupPostion,group  position in group data list
     * @param childPosition,child position in child data list
     * @return childPosition in adapter data lits
     *//*
    protected int convertGroupChildPositionInner(int groupPostion, int childPosition) {
        int adapPos = convertGroupPositionInner(groupPostion) + 1 + childPosition;
        if (XLog.isEnabled()) XLog.v("group child pos[%d, %d] -> adapter position[%d]",
                                     groupPostion,
                                     childPosition,
                                     adapPos);
        return adapPos;
    }*/
}
