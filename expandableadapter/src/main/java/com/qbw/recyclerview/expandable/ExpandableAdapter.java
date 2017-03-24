package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

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
    private List<T> mList;
    private List<T> mHeaderList;
    private List<T> mChildList;
    private List<T> mGroupList;
    private Map<T, List<T>> mGroupChildMap;
    private List<T> mFooterList;

    public ExpandableAdapter(Context context) {
        super(context);
        mList = new ArrayList<>();
        mHeaderList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mGroupChildMap = new HashMap<>();
        mFooterList = new ArrayList<>();
    }


    @Override
    public final T getItem(int adapPos) {
        return mList.get(adapPos);
    }

    public final void removeItem(int adapPos) {
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
            if (XLog.isEnabled()) XLog.e("wrong adapter position[%d]", adapPos);
        }
    }

    public final void updateItem(int adapPos, T t) {
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
            if (XLog.isEnabled()) XLog.e("wrong adapter position[%d]", adapPos);
        }
    }

    public final void clear() {
        mList.clear();
        mHeaderList.clear();
        mChildList.clear();
        mGroupList.clear();
        mGroupChildMap.clear();
        mFooterList.clear();
        notifyDataSetChanged();
    }

    public final int addHeader(T header) {
        List<T> ts = new ArrayList<>();
        ts.add(header);
        return addHeader(getHeaderCount(), ts);
    }

    public final int addHeader(int headerPosition, T header) {
        List<T> ts = new ArrayList<>();
        ts.add(header);
        return addHeader(headerPosition, ts);
    }

    public final int addHeader(List<T> headerList) {
        return addHeader(getHeaderCount(), headerList);
    }

    public final int addHeader(int headerPosition, List<T> headerList) {
        if (null == headerList || headerList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("wrong param");
            return -1;
        }
        int oldHeaderCount = getHeaderCount();

        if (!checkHeaderPosition(headerPosition)) {
            headerPosition = oldHeaderCount;
        }
        if (XLog.isEnabled()) XLog.d("headerPosition = %d", headerPosition);
        mHeaderList.addAll(headerPosition, headerList);
        int adapPos = convertHeaderPosition(headerPosition);
        mList.addAll(adapPos, headerList);
        int addSize = headerList.size();
        if (XLog.isEnabled()) XLog.v("notify item from %d, count = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
        return headerPosition;
    }

    public final T getHeader(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return null;
        }
        return mHeaderList.get(headerPosition);
    }

    /**
     * @see #removeHeader(int)
     */
    @Deprecated
    public final void removeHeader(T t) {
        removeHeader(mHeaderList.indexOf(t));
    }

    public final void removeHeader(int headerPosition) {
        removeHeader(headerPosition, 1);
    }

    public final void clearHeader() {
        clearHeader(0);
    }

    public final void clearHeader(int beginPos) {
        removeHeader(beginPos, getHeaderCount() - beginPos);
    }

    public final void removeHeader(int beginPosition, int removeCount) {
        if (!checkHeaderPosition(beginPosition)) {
            return;
        }
        int headerCount = getHeaderCount();
        int adapPos = convertHeaderPosition(beginPosition);
        if (beginPosition + removeCount > headerCount) {
            removeCount = headerCount - beginPosition;
            if (XLog.isEnabled()) XLog.w("reset removeCount = %d", removeCount);
        }

        mList.subList(adapPos, adapPos + removeCount).clear();
        mHeaderList.subList(beginPosition, beginPosition + removeCount).clear();
        notifyItemRangeRemoved(adapPos, removeCount);
    }

    public final void updateHeader(int headerPosition, T t) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        int adapPos = convertHeaderPosition(headerPosition);
        mHeaderList.set(headerPosition, t);
        mList.set(adapPos, t);
        notifyItemChanged(headerPosition);
    }

    public final void notifyHeaderChanged(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        int adapPos = convertHeaderPosition(headerPosition);
        notifyItemChanged(adapPos);
    }

    @Override
    public final int getHeaderCount() {
        return null == mHeaderList ? 0 : mHeaderList.size();
    }

    private boolean checkHeaderPosition(int headerPos) {
        int headerCount;
        if (headerPos < 0) {
            if (XLog.isEnabled()) XLog.w("invalid header position[%d]", headerPos);
            return false;
        } else if (headerPos >= (headerCount = getHeaderCount())) {
            if (XLog.isEnabled())
                XLog.w("invalid header position[%d], header size is [%d]", headerPos, headerCount);
            return false;
        }
        return true;
    }

    public final int getHeaderPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", adapterPosition);
            return -1;
        }
        return mHeaderList.indexOf(getItem(adapterPosition));
    }

    public final int addChild(T child) {
        return addChild(getChildCount(), child);
    }

    @Deprecated
    public final void addChildPosition(int childPos, T t) {
        addChild(childPos, t);
    }

    public final int addChild(int childPosition, T child) {
        ArrayList<T> ts = new ArrayList<>();
        ts.add(child);
        return addChild(childPosition, ts);
    }

    public final int addChild(List<T> childList) {
        return addChild(getChildCount(), childList);
    }

    public final int addChild(int childPosition, List<T> childList) {
        if (null == childList || childList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("wrong param tList");
            return -1;
        }

        int childCount = getChildCount();
        if (!checkChildPosition(childPosition)) {
            childPosition = childCount;
        }
        mChildList.addAll(childPosition, childList);
        int adapPos = convertChildPosition(childPosition);
        mList.addAll(adapPos, childList);
        int addSize = childList.size();
        if (XLog.isEnabled()) XLog.v("notify chnaged item from %d, size = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
        return childPosition;
    }

    public final T getChild(int childPos) {
        if (!checkChildPosition(childPos)) {
            return null;
        }
        return mChildList.get(childPos);
    }

    public final void removeChild(int childPos) {
        removeChild(childPos, 1);
    }

    public final void removeChild(int childPos, int count) {
        if (!checkChildPosition(childPos)) {
            return;
        }

        if (count <= 0) {
            if (XLog.isEnabled()) XLog.e("wrong count[%d]", count);
            return;
        }

        int childCount = getChildCount();

        int end = childPos + count;
        if (end > childCount) {
            count = childCount - childPos;
            end = childCount;
            if (XLog.isEnabled()) XLog.w("reset count = %d", count);
        }
        int adapPos = convertChildPosition(childPos);
        mChildList.subList(childPos, end).clear();
        mList.subList(adapPos, adapPos + count).clear();
        notifyItemRangeRemoved(adapPos, count);
    }

    public final void clearChild(int beginPos) {
        removeChild(beginPos, getChildCount() - beginPos);
    }

    public final void clearChild() {
        removeChild(0, getChildCount());
    }

    public final void updateChild(int childPos, T t) {
        if (!checkChildPosition(childPos)) {
            return;
        }
        int adapPos = convertChildPosition(childPos);
        mChildList.set(childPos, t);
        mList.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    public final void notifyChildChanged(int childPos) {
        if (!checkChildPosition(childPos)) {
            return;
        }
        notifyItemChanged(convertChildPosition(childPos));
    }

    @Override
    public final int getChildCount() {
        return null == mChildList ? 0 : mChildList.size();
    }

    private boolean checkChildPosition(int childPos) {
        int childCount;
        if (childPos < 0) {
            if (XLog.isEnabled()) XLog.w("invalid child position[%d]", childPos);
            return false;
        } else if (childPos >= (childCount = getChildCount())) {
            if (XLog.isEnabled())
                XLog.w("invalid child position[%d], child size is [%d]", childPos, childCount);
            return false;
        }
        return true;
    }

    public int getChildPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", adapterPosition);
            return -1;
        }
        return mChildList.indexOf(getItem(adapterPosition));
    }

    public final int addGroup(T t) {
        return addGroup(getGroupCount(), t);
    }

    /**
     * @param targetPosition group的目标添加位置
     * @param t
     * @return group实际的添加位置(目标位置大于group数量的时候会不同)
     */
    public final int addGroup(int targetPosition, T t) {
        if (targetPosition < 0) {
            if (XLog.isEnabled()) XLog.e("invalid group position %d", targetPosition);
            return -1;
        } else if (mGroupChildMap.containsKey(t)) {
            if (XLog.isEnabled())
                XLog.e("group t is alread exist!You must use a different t to create a new group");
            return -1;
        }

        int groupCount = getGroupCount();
        if (targetPosition > groupCount) {
            if (XLog.isEnabled()) XLog.w("reset position %d -> %d", targetPosition, groupCount);
            targetPosition = groupCount;
        }
        mGroupList.add(targetPosition, t);
        mGroupChildMap.put(t, new ArrayList<T>());
        int adapPos = convertGroupPosition(targetPosition);
        mList.add(adapPos, t);
        if (XLog.isEnabled()) XLog.v("now group count is %d", getGroupCount());
        notifyItemInserted(adapPos);
        return targetPosition;
    }

    public final void removeGroup(T t) {
        removeGroup(mGroupList.indexOf(t));
    }

    public final T getGroup(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return null;
        }
        return mGroupList.get(groupPosition);
    }

    /**
     * remove all group child(include group item)
     *
     * @param groupPosition
     */
    public final void removeGroup(int groupPosition) {
        _removeGroup(groupPosition, 0, 0, true);
    }

    /**
     * @see #clearGroupChild(int)
     */
    @Deprecated
    public final void clearGroup(int groupPosition) {
        clearGroupChild(groupPosition);
    }


    /**
     * @param removeGroup true means remove group item(ignore child parameters, remove whole group include all child); false means not
     */
    private final void _removeGroup(int groupPosition,
                                    int childStarPosition,
                                    int count,
                                    boolean removeGroup) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }

        int groupChildCount = getGroupChildCount(groupPosition);
        if (removeGroup) {
            childStarPosition = 0;
            count = groupChildCount;
        } else {
            if (!checkGroupChildPosition(groupPosition, childStarPosition)) {
                return;
            }
            if (count <= 0) {
                if (XLog.isEnabled()) XLog.e("invalid count = %d", count);
                return;
            }
        }

        int childEnd = childStarPosition + count;

        if (childEnd > groupChildCount) {
            count = groupChildCount - childStarPosition;
            childEnd = groupChildCount;
            if (XLog.isEnabled()) XLog.w("reset count = %d", count);
        }

        if (XLog.isEnabled()) XLog.d(
                "groupPosition=%d, childStarPosition=%d, count=%d, removeGroup=%b, childEnd=%d",
                groupPosition,
                childStarPosition,
                count,
                removeGroup,
                childEnd);


        T groupItem = mGroupList.get(groupPosition);
        int adapPos = convertGroupPosition(groupPosition);

        if (groupChildCount > 0) {
            mGroupChildMap.get(groupItem).subList(childStarPosition, childEnd).clear();
            int adapBeginPos = adapPos + childStarPosition + 1;
            mList.subList(adapBeginPos, adapBeginPos + count).clear();
            notifyItemRangeRemoved(adapBeginPos, count);
        }

        if (removeGroup) {
            mGroupChildMap.remove(groupItem);
            mGroupList.remove(groupPosition);
            mList.remove(adapPos);
            notifyItemRemoved(adapPos);
        }

    }

    public final void updateGroup(int groupPosition, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }

        int adapPos = convertGroupPosition(groupPosition);
        mGroupList.set(groupPosition, t);
        mList.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    public int getGroupPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", adapterPosition);
            return -1;
        }
        return mGroupList.indexOf(getItem(adapterPosition));
    }

    /**
     * update whole group (exclude group item)
     *
     * @param groupPosition
     */
    public final void updateGroupChild(int groupPosition) {
        updateGroupChild(groupPosition, false);
    }

    /**
     * 是否同时更新group
     *
     * @param groupPosition
     * @param updateGroup
     */
    public final void updateGroupChild(int groupPosition, boolean updateGroup) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }

        int adapPos = convertGroupPosition(groupPosition);
        int childCount = getGroupChildCount(groupPosition);
        if (updateGroup) {
            notifyItemRangeChanged(adapPos, childCount + 1);
        } else {
            if (childCount > 0) {
                notifyItemRangeChanged(adapPos + 1, childCount);
            }
        }
    }

    @Override
    public final int getGroupCount() {
        return null == mGroupList ? 0 : mGroupList.size();
    }

    private boolean checkGroupPosition(int groupPos) {
        int groupCount;
        if (groupPos < 0) {
            if (XLog.isEnabled()) XLog.w("invalid group position[%d]", groupPos);
            return false;
        } else if (groupPos >= (groupCount = getGroupCount())) {
            if (XLog.isEnabled())
                XLog.w("invalid group position[%d], group size is %d", groupPos, groupCount);
            return false;
        }

        return true;
    }

    public final void addGroupChild(int groupPosition, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        mGroupChildMap.get(mGroupList.get(groupPosition)).add(t);
        int adapPos = convertGroupChildPosition(groupPosition,
                getGroupChildCount(groupPosition) - 1);
        mList.add(adapPos, t);
        notifyItemInserted(adapPos);
    }

    @Deprecated
    public final void addGroupChildPosition(int groupPosition, int childPos, T t) {
        addGroupChild(groupPosition, childPos, t);
    }

    public final void addGroupChild(int groupPosition, int childPos, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        int groupChildCount = getGroupChildCount(groupPosition);
        if (childPos >= groupChildCount) {
            addGroupChild(groupPosition, t);
        } else if (childPos >= 0) {
            mGroupChildMap.get(mGroupList.get(groupPosition)).add(childPos, t);
            int adapPos = convertGroupChildPosition(groupPosition, childPos);
            mList.add(adapPos, t);
            notifyItemInserted(adapPos);
        } else {
            if (XLog.isEnabled()) XLog.e("wrong targetChildPos = %d", childPos);
        }
    }

    public final void addGroupChild(int groupPosition, List<T> childList) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        if (null == childList || childList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("invalid child mList");
            return;
        }

        int oldGroupChildCount = getGroupChildCount(groupPosition);
        mGroupChildMap.get(mGroupList.get(groupPosition)).addAll(childList);
        int adapPos = convertGroupChildPosition(groupPosition, oldGroupChildCount);
        mList.addAll(adapPos, childList);
        notifyItemRangeInserted(adapPos, childList.size());
    }

    public final T getGroupChild(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return null;
        }
        return mGroupChildMap.get(mGroupList.get(groupPosition)).get(childPosition);
    }

    /**
     * @param groupPosition
     * @param childStartPosition remove start from here
     * @param count              item count to remove
     */
    public final void removeGroupChild(int groupPosition, int childStartPosition, int count) {
        _removeGroup(groupPosition, childStartPosition, count, false);
    }

    public final void removeGroupChild(int groupPosition, int childPosition) {
        _removeGroup(groupPosition, childPosition, 1, false);
    }

    /**
     * clear all child of group(exclude group item)
     *
     * @param groupPosition
     * @see #clearGroupChild(int, int)
     */
    public final void clearGroupChild(int groupPosition) {
        clearGroupChild(groupPosition, 0);
    }

    /**
     * clear child of group start from childStarPosition
     *
     * @param groupPosition
     * @param childStarPosition
     * @see #clearGroupChild(int)
     */
    public final void clearGroupChild(int groupPosition, int childStarPosition) {
        if (!checkGroupChildPosition(groupPosition, childStarPosition)) {
            return;
        }
        int groupChildCount = getGroupChildCount(groupPosition);
        if (groupChildCount > 0) {
            _removeGroup(groupPosition,
                    childStarPosition,
                    groupChildCount - childStarPosition,
                    false);
        }
    }

    public final void updateGroupChild(int groupPosition, int childPosition, T t) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return;
        }
        int adapPos = convertGroupChildPosition(groupPosition, childPosition);
        if (XLog.isEnabled()) XLog.v("groupPosition=%d, childPosition=%d, adapGroupChildPos=%d",
                groupPosition,
                childPosition,
                adapPos);
        mGroupChildMap.get(mGroupList.get(groupPosition)).set(childPosition, t);
        mList.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    @Override
    public final int getGroupChildCount(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return 0;
        }
        T groupItem = mGroupList.get(groupPosition);
        return mGroupChildMap.get(groupItem).size();
    }

    public final void notifyGroupChildChanged(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return;
        }
        int adapPos = convertGroupChildPosition(groupPosition, childPosition);
        notifyItemChanged(adapPos);
    }

    private boolean checkGroupChildPosition(int groupPos, int childPos) {
        int groupChildCount;
        if (childPos < 0) {
            if (XLog.isEnabled())
                XLog.w("invalid group child position[%d, %d]", groupPos, childPos);
            return false;
        } else if (!checkGroupPosition(groupPos)) {
            return false;
        } else if (childPos >= (groupChildCount = getGroupChildCount(groupPos))) {
            if (XLog.isEnabled()) XLog.w(
                    "invalid group child position[%d, %d], group[%d] child size is [%d]",
                    groupPos,
                    childPos,
                    groupPos,
                    groupChildCount);
            return false;
        }
        return true;
    }

    public int[] getGroupChildPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapter position %d", adapterPosition);
            return new int[]{-1, -1};
        }
        T t = getItem(adapterPosition);
        int childPos;
        int gcount = getGroupCount();
        for (int i = 0; i < gcount; i++) {
            List<T> cts = mGroupChildMap.get(mGroupList.get(i));
            if (cts == null || cts.isEmpty()) {
                continue;
            }
            if ((childPos = cts.indexOf(t)) != -1) {
                return new int[]{i, childPos};
            }
        }
        return new int[]{-1, -1};
    }

    public final void addFooter(T footer) {
        List<T> ts = new ArrayList<>();
        ts.add(footer);
        addFooter(getFooterCount(), ts);
    }

    public final void addFooter(List<T> footerList) {
        addFooter(getFooterCount(), footerList);
    }

    public final void addFooter(int position, T footer) {
        List<T> ts = new ArrayList<>();
        ts.add(footer);
        addFooter(position, ts);
    }

    public final void addFooter(int footerPosition, List<T> footerList) {

        if (null == footerList || footerList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("wrong param");
            return;
        }
        int oldFooterCount = getFooterCount();

        if (!checkFooterPosition(footerPosition)) {
            footerPosition = oldFooterCount;
        }
        if (XLog.isEnabled()) XLog.d("footerPosition = %d", footerPosition);
        int adapPos = convertFooterPosition(footerPosition);
        mFooterList.addAll(footerPosition, footerList);
        mList.addAll(adapPos, footerList);
        int addSize = footerList.size();
        if (XLog.isEnabled()) XLog.v("notify item from %d, count = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
    }

    public final T getFooter(int footerPosition) {
        if (!checkFooterPosition(footerPosition)) {
            return null;
        }
        return mFooterList.get(footerPosition);
    }

    public final void updateFooter(int footerPosition, T t) {
        if (!checkFooterPosition(footerPosition)) {
            return;
        }

        int adapPos = convertFooterPosition(footerPosition);
        mFooterList.set(footerPosition, t);
        mList.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    /**
     * @see #removeFooter(int)
     */
    @Deprecated
    public final void removeFooter(T t) {
        removeFooter(mFooterList.indexOf(t));
    }

    public final void removeFooter(int footerPosition) {
        removeFooter(footerPosition, 1);
    }

    public final void clearFooter() {
        clearFooter(0);
    }

    public final void clearFooter(int beginPosition) {
        removeFooter(beginPosition, getFooterCount() - beginPosition);
    }

    public final void removeFooter(int beginPosition, int removeCount) {
        if (!checkFooterPosition(beginPosition)) {
            return;
        }
        int footerCount = getFooterCount();
        int adapPos = convertFooterPosition(beginPosition);
        if (beginPosition + removeCount > footerCount) {
            removeCount = footerCount - beginPosition;
            if (XLog.isEnabled()) XLog.w("reset removeCount = %d", removeCount);
        }

        mList.subList(adapPos, adapPos + removeCount).clear();
        mFooterList.subList(beginPosition, beginPosition + removeCount).clear();
        notifyItemRangeRemoved(adapPos, removeCount);
    }

    @Override
    public final int getFooterCount() {
        return null == mFooterList ? 0 : mFooterList.size();
    }

    private boolean checkFooterPosition(int footerPos) {
        int footerCount;
        if (footerPos < 0) {
            if (XLog.isEnabled()) XLog.w("invalid footer position[%d]", footerPos);
            return false;
        } else if (footerPos >= (footerCount = getFooterCount())) {
            if (XLog.isEnabled())
                XLog.w("invalid footer position[%d], footer size is [%d]", footerPos, footerCount);
            return false;
        }
        return true;
    }

    public int getFooterPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapter position %d", adapterPosition);
            return -1;
        }
        return mFooterList.indexOf(getItem(adapterPosition));
    }

    @Override
    public final int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private boolean checkAdapterPosition(int adapterPosition) {
        if (adapterPosition < 0 || adapterPosition >= getItemCount()) {
            return false;
        }
        return true;
    }
}
