package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;

import com.qbw.log.XLog;
import com.qbw.recyclerview.base.BaseExpandableAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bond on 2016/4/2.
 */
public abstract class ExpandableAdapter<T> extends BaseExpandableAdapter<T> {
    private List<T> list;
    private List<T> headerList;
    private List<T> childList;
    private List<T> groupList;
    private Map<T, List<T>> groupChildMap;
    private List<T> footerList;

    public ExpandableAdapter(Context context) {
        super(context);
        list = new ArrayList<>();
        headerList = new ArrayList<>();
        childList = new ArrayList<>();
        groupList = new ArrayList<>();
        groupChildMap = new HashMap<>();
        footerList = new ArrayList<>();
    }

    @Override
    public T getItem(int adapPos) {
        return list.get(adapPos);
    }

    public void removeItem(int adapPos) {
        int pos;
        int[] poss;
        if (-1 != (pos = getChildPosition(adapPos))) {
            removeChild(pos);
        } else if (-1 != (pos = getFooterPosition(adapPos))) {
            removeFooter(pos);
        } else if (-1 != (pos = getHeaderPosition(adapPos))) {
            removeHeader(pos);
        } else if (-1 != (poss = getGroupChildPosition(adapPos))[0]) {
            removeGroupChild(poss[0], poss[1]);
        } else if (-1 != (pos = getGroupPosition(adapPos))) {
            removeGroup(pos);
        } else {
            XLog.e("wrong adapter position[%d]", adapPos);
        }
    }

    public void updateItem(int adapPos, T t) {
        int pos;
        int[] poss;
        if (-1 != (pos = getChildPosition(adapPos))) {
            updateChild(pos, t);
        } else if (-1 != (pos = getFooterPosition(adapPos))) {
            updateFooter(pos, t);
        } else if (-1 != (pos = getHeaderPosition(adapPos))) {
            updateHeader(pos, t);
        } else if (-1 != (poss = getGroupChildPosition(adapPos))[0]) {
            updateGroupChild(poss[0], poss[1], t);
        } else if (-1 != (pos = getGroupPosition(adapPos))) {
            updateGroup(pos, t);
        } else {
            XLog.e("wrong adapter position[%d]", adapPos);
        }
    }

    public final void addHeader(T t) {
        XLog.line(true);
        headerList.add(t);
        int adapPos = convertHeaderPosition(getHeaderCount() - 1);
        list.add(adapPos, t);
        notifyItemInserted(adapPos);
        XLog.line(false);
    }

    public final void removeHeader(T t) {
        removeHeader(headerList.indexOf(t));
    }

    public final T getHeader(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return null;
        }
        return headerList.get(headerPosition);
    }

    public final void removeHeader(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertHeaderPosition(headerPosition);
        headerList.remove(headerPosition);
        list.remove(adapPos);
        notifyItemRemoved(adapPos);
        XLog.line(false);
    }

    public final void updateHeader(int headerPosition, T t) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertHeaderPosition(headerPosition);
        headerList.set(headerPosition, t);
        list.set(adapPos, t);
        notifyItemChanged(headerPosition);
        XLog.line(false);
    }

    @Override
    public final int getHeaderCount() {
        return null == headerList ? 0 : headerList.size();
    }

    private boolean checkHeaderPosition(int headerPos) {
        int headerCount;
        if (headerPos < 0) {
            XLog.e("invalid header position[%d]", headerPos);
            return false;
        } else if (headerPos >= (headerCount = getHeaderCount())) {
            XLog.e("invalid header position[%d], header size is [%d]", headerPos, headerCount);
            return false;
        }
        return true;
    }

    public final void addChild(T t) {
        XLog.line(true);
        childList.add(t);
        int adapPos = convertChildPosition(getChildCount() - 1);
        list.add(adapPos, t);
        notifyItemInserted(adapPos);
        XLog.line(false);
    }

    public final T getChild(int childPos) {
        if (!checkChildPosition(childPos)) {
            return null;
        }
        return childList.get(childPos);
    }

    public final void removeChild(int childPos) {
        if (!checkChildPosition(childPos)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertChildPosition(childPos);
        childList.remove(childPos);
        list.remove(adapPos);
        notifyItemRemoved(adapPos);
        XLog.line(false);
    }

    public final void updateChild(int childPos, T t) {
        if (!checkChildPosition(childPos)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertChildPosition(childPos);
        childList.set(childPos, t);
        list.set(adapPos, t);
        notifyItemChanged(adapPos);
        XLog.line(false);
    }

    @Override
    public final int getChildCount() {
        return null == childList ? 0 : childList.size();
    }

    private boolean checkChildPosition(int childPos) {
        int childCount;
        if (childPos < 0) {
            XLog.e("invalid child position[%d]", childPos);
            return false;
        } else if (childPos >= (childCount = getChildCount())) {
            XLog.e("invalid child position[%d], child size is [%d]", childPos, childCount);
            return false;
        }
        return true;
    }

    public final void addGroup(T t) {
        XLog.line(true);
        groupList.add(t);
        int groupCount = getGroupCount();
        groupChildMap.put(t, new ArrayList<T>());
        XLog.v("groupCount=%d", groupCount);
        int adapPos = convertGroupPosition(groupCount - 1);
        list.add(adapPos, t);
        notifyItemInserted(adapPos);
        XLog.line(false);
    }

    public final void removeGroup(T t) {
        removeGroup(groupList.indexOf(t));
    }

    public final T getGroup(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return null;
        }
        return groupList.get(groupPosition);
    }

    public final void removeGroup(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }

        XLog.line(true);
        int groupChildCount = getGroupChildCount(groupPosition);
        if (groupChildCount > 0) {
            T groupItem = groupList.get(groupPosition);
            List<T> childList = groupChildMap.get(groupItem);
            list.removeAll(childList);
            groupChildMap.remove(groupItem);
        }

        int adapPos = convertGroupPosition(groupPosition);
        groupList.remove(groupPosition);
        list.remove(adapPos);
        notifyItemRangeRemoved(adapPos, groupChildCount + 1);
        XLog.line(false);
    }

    public final void updateGroup(int groupPosition, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }

        XLog.line(true);
        int adapPos = convertGroupPosition(groupPosition);
        groupList.set(groupPosition, t);
        list.set(adapPos, t);
        notifyItemChanged(adapPos);
        XLog.line(false);
    }

    @Override
    public final int getGroupCount() {
        return null == groupList ? 0 : groupList.size();
    }

    private boolean checkGroupPosition(int groupPos) {
        int groupCount;
        if (groupPos < 0) {
            XLog.e("invalid group position[%d]", groupPos);
            return false;
        } else if (groupPos >= (groupCount = getGroupCount())) {
            XLog.e("invalid group position[%d], group size is %d", groupPos, groupCount);
            return false;
        }

        return true;
    }

    public final void addGroupChild(int groupPosition, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        XLog.line(true);
        groupChildMap.get(groupList.get(groupPosition)).add(t);
        int adapPos = convertGroupChildPosition(groupPosition, getGroupChildCount(groupPosition) - 1);
        list.add(adapPos, t);
        notifyItemInserted(adapPos);
        XLog.line(false);
    }

    public final void addGroupChild(int groupPosition, List<T> childList) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        if (null == childList || childList.isEmpty()) {
            XLog.e("invalid child list");
            return;
        }

        XLog.line(true);
        int oldGroupChildCount = getGroupChildCount(groupPosition);
        groupChildMap.get(groupList.get(groupPosition)).addAll(childList);
        int adapPos = convertGroupChildPosition(groupPosition, oldGroupChildCount);
        list.addAll(adapPos, childList);
        notifyItemRangeInserted(adapPos, childList.size());
        XLog.line(false);
    }

    public final T getGroupChild(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return null;
        }
        return groupChildMap.get(groupList.get(groupPosition)).get(childPosition);
    }

    public final void removeGroupChild(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertGroupChildPosition(groupPosition, childPosition);
        XLog.v("groupPosition=%d, childPosition=%d, adapGroupChildPos=%d", groupPosition, childPosition, adapPos);
        groupChildMap.get(groupList.get(groupPosition)).remove(childPosition);
        list.remove(adapPos);
        notifyItemRemoved(adapPos);
        XLog.line(false);
    }

    public final void updateGroupChild(int groupPosition, int childPosition, T t) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return;
        }
        XLog.line(true);
        int adapPos = convertGroupChildPosition(groupPosition, childPosition);
        XLog.v("groupPosition=%d, childPosition=%d, adapGroupChildPos=%d", groupPosition, childPosition, adapPos);
        groupChildMap.get(groupList.get(groupPosition)).set(childPosition, t);
        list.set(adapPos, t);
        notifyItemChanged(adapPos);
        XLog.line(false);
    }

    @Override
    public final int getGroupChildCount(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return 0;
        }
        T groupItem = groupList.get(groupPosition);
        return groupChildMap.get(groupItem).size();
    }

    private boolean checkGroupChildPosition(int groupPos, int childPos) {
        int groupChildCount;
        if (childPos < 0) {
            XLog.e("invalid group child position[%d, %d]", groupPos, childPos);
            return false;
        } else if (!checkGroupPosition(groupPos)) {
            return false;
        } else if (childPos >= (groupChildCount = getGroupChildCount(groupPos))) {
            XLog.e("invalid group child position[%d, %d], group[%d] child size is [%d]", groupPos, childPos, groupPos, groupChildCount);
            return false;
        }
        return true;
    }

    public final void addFooter(T t) {
        XLog.line(true);
        footerList.add(t);
        int adapPos = convertFooterPosition(getFooterCount() - 1);
        list.add(adapPos, t);
        notifyItemInserted(adapPos);
        XLog.line(false);
    }

    public final void removeFooter(T t) {
        removeFooter(footerList.indexOf(t));
    }

    public final T getFooter(int footerPosition) {
        if (!checkFooterPosition(footerPosition)) {
            return null;
        }
        return footerList.get(footerPosition);
    }

    public final void removeFooter(int footerPosition) {
        if (!checkFooterPosition(footerPosition)) {
            return;
        }

        int adapPos = convertFooterPosition(footerPosition);
        footerList.remove(footerPosition);
        list.remove(adapPos);
        notifyItemRemoved(adapPos);
    }

    public final void updateFooter(int footerPosition, T t) {
        if (!checkFooterPosition(footerPosition)) {
            return;
        }

        int adapPos = convertFooterPosition(footerPosition);
        footerList.set(footerPosition, t);
        list.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    @Override
    public final int getFooterCount() {
        return null == footerList ? 0 : footerList.size();
    }

    private boolean checkFooterPosition(int footerPos) {
        int footerCount;
        if (footerPos < 0) {
            XLog.e("invalid footer position[%d]", footerPos);
            return false;
        } else if (footerPos >= (footerCount = getFooterCount())) {
            XLog.e("invalid footer position[%d], footer size is [%d]", footerPos, footerCount);
            return false;
        }
        return true;
    }

    public abstract RecyclerView.ViewHolder onCreateStickyGroupViewHolder(int groupPosition);
    public abstract void bindStickyGroupData(int groupPosition, RecyclerView.ViewHolder stickyGroupViewHolder);

    public abstract boolean isPositionHeader(int adapPos);
    public abstract boolean isPositionChild(int adapPos);
    public abstract boolean isPostionGroup(int adapPos);
    public abstract boolean isPostionGroupChild(int adapPos);
    public abstract boolean isPositionFooter(int adapPos);

    public abstract Point getGroupSize(int groupPosition);
}
