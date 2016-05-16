package com.qbw.recyclerview.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.qbw.log.XLog;

/**
 * Created by Bond on 2016/4/2.
 * Item Order below:
 * ----------------Header
 * ----------------Child(mainly used in case that we don't need to group item)
 * ----------------Group
 * ----------------GroupChild
 * ----------------......(Group+GroupChild)
 * ----------------Footer
 */
public abstract class BaseExpandableAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {
    protected Context context;

    public BaseExpandableAdapter(Context context) {
        this.context = context;
    }

    @Override
    public abstract BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        holder.bindData(position, getItem(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        itemCount += getHeaderCount();
        itemCount += getChildCount();
        itemCount += getGroupCount();
        for (int i = 0; i < getGroupCount(); i++) {
            itemCount += getGroupChildCount(i);
        }
        itemCount += getFooterCount();
        return itemCount;
    }

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
     * @param position, position in adapter data list
     * @return header position in header data list
     */
    public int getHeaderPosition(int position) {
        int headerCount = getHeaderCount();
        if (headerCount > 0 && position < headerCount) {
            XLog.v("adapter position[%d] -> header position[%d]", position, position);
            return position;
        }
        XLog.v("adapter position[%d] is not Header", position);
        return -1;
    }

    /**
     * @param headerPosition, header position in header data list
     * @return position in adapter data list
     */
    public int convertHeaderPosition(int headerPosition) {
        return headerPosition;
    }

    /**
     * @param position, position in adapter data list
     * @return child position in child data list
     */
    public int getChildPosition(int position) {
        int headerCount = getHeaderCount();
        int childCount = getChildCount();
        if (childCount > 0 && position >= headerCount && position <= headerCount + childCount - 1) {
            int childPos = position - headerCount;
            XLog.v("adapter position[%d] -> child position[%d]", position, childPos);
            return childPos;
        }
        XLog.v("adapter position[%d] is not Child!", position);
        return -1;
    }

    /**
     * @param childPosition, child position in child data list
     * @return position in adapter data list
     */
    public int convertChildPosition(int childPosition) {
        int adapPos = childPosition + getHeaderCount();
        XLog.v("child position[%d] -> adapter position[%d]", childPosition, adapPos);
        return adapPos;
    }

    /**
     * @param position ,position in adapter data list
     * @return footer position in footer data list
     */
    public int getFooterPosition(int position) {
        int itemCount = getItemCount();
        int footerCount = getFooterCount();
        if (footerCount > 0 && position >= itemCount - footerCount) {
            int footerPos = position - (itemCount - footerCount);
            XLog.v("adapter position[%d] -> footer position[%d]", position, footerPos);
            return footerPos;
        }
        XLog.v("adapter position[%d] is not Footer!", position);
        return -1;
    }

    /**
     * @param footerPosition, footer position in footer data list
     * @return position in adapter data list
     */
    public int convertFooterPosition(int footerPosition) {
        int adapPos = footerPosition + (getItemCount() - getFooterCount());
        XLog.v("footer position[%d] -> adapter position[%d]", footerPosition, adapPos);
        return adapPos;
    }

    /**
     * @param position, position in adapter data list
     * @return group position in group data list
     */
    public int getGroupPosition(int position) {
        XLog.v("adapter target group position=%d", position);
        int groupCount = getGroupCount();
        int headerCount = getHeaderCount();
        int childCount = getChildCount();
        XLog.v("headerCount=%d, childCount=%d, groupCount=%d", headerCount, childCount, groupCount);
        if (groupCount > 0) {//some item is grouped.we must check group in the end
            outer:
            for (int i = 0; i < groupCount; i++) {
                int groupAdapPosition = convertGroupPosition(i);
                XLog.v("loop=%d, adapter group position=%d", i, groupAdapPosition);
                if (position == groupAdapPosition) {
                    XLog.v("adapter position[%d] -> group position[%d]", position, i);
                    return i;
                }
            }
        }
        XLog.v("adapter position[%d] is not Group!", position);
        return -1;
    }

    /**
     * @param groupPosition,group position in group data list
     * @return position in adapter data list
     */
    public int convertGroupPosition(int groupPosition) {
        int aboveItemCount = groupPosition;//all item count above this group
        for (int i = 0; i < groupPosition; i++) {
            aboveItemCount += getGroupChildCount(i);
        }
        int adapPos = getHeaderCount() + getChildCount() + aboveItemCount;
        XLog.v("group position[%d] -> adapter position[%d]", groupPosition, adapPos);
        return adapPos;
    }

    /**
     * @param position position in adapter data list
     * @return groupchild position in groupchild data list
     */
    public int[] getGroupChildPosition(int position) {
        int groupCount = getGroupCount();
        if (groupCount > 0) {//some item is grouped.we must check group in the end
            for (int i = 0; i < groupCount; i++) {
                int groupAdapPosition = convertGroupPosition(i);
                int groupChildCount = getGroupChildCount(i);
                for (int j = 0; j < groupChildCount; j++) {
                    int groupChildAdapPosition = groupAdapPosition + 1 + j;
                    if (position == groupChildAdapPosition) {
                        XLog.v("adapter position[%d] -> group[%d],child[%d]!", position, i, j);
                        return new int[]{i, j};
                    }
                }
            }
        }
        XLog.v("adapter position[%d] is not GroupChild!", position);
        return new int[]{-1, -1};
    }

    /**
     * @param groupPostion,group  position in group data list
     * @param childPosition,child position in child data list
     * @return childPosition in adapter data lits
     */
    public int convertGroupChildPosition(int groupPostion, int childPosition) {
        int adapPos = convertGroupPosition(groupPostion) + 1 + childPosition;
        XLog.v("group child pos[%d, %d] -> adapter position[%d]", groupPostion, childPosition, adapPos);
        return adapPos;
    }
}
