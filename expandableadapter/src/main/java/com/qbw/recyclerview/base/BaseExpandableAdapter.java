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
     * Called when setHeader,setChild or setFooter is called
     *
     * @param oldData
     * @param newData
     * @return true[will not update ui] false[update ui]
     */
    public abstract boolean isSameData(Object oldData, Object newData);
}
