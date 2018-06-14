package com.qbw.recyclerview.expandable;

import com.qbw.log.XLog;
import com.qbw.recyclerview.base.BaseExpandableAdapter;

import java.util.ArrayList;
import java.util.Collections;
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

    public ExpandableAdapter() {
        mList = new ArrayList<>();
        mHeaderList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mGroupChildMap = new HashMap<>();
        mFooterList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        int vt = getItemViewType(getItem(position));
        return vt == -1 ? super.getItemViewType(position) : vt;
    }

    public int getItemViewType(T t) {
        return -1;
    }

    @Override
    public final T getItem(int adapPos) {
        return mList.get(adapPos);
    }

    public final int getItemPosition(T item) {
        return mList.indexOf(item);
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

    public final void swapItem(int sourcePosition, int targetPosition) {
        if(sourcePosition < 0 || sourcePosition >= getItemCount()) {
            if (XLog.isEnabled()) XLog.e("invalid sourcePosition:%d", sourcePosition);
            return;
        } else if(targetPosition < 0 || targetPosition >= getItemCount()) {
            if (XLog.isEnabled()) XLog.e("invalid targetPosition:%d", targetPosition);
            return;
        }
        T source = getItem(sourcePosition);
        T target = getItem(targetPosition);
        int posSource;
        int[] possSource = null;
        int posTarget;
        int[] possTarget = null;
        int flagSource = 0;//从低位到高位，分别是child,footer,header,groupchild,group
        int flagTarget = 0;

        if (-1 != (posSource = getChildPosition(source))) {
            flagSource |= 1;
        } else if (-1 != (posSource = getFooterPosition(source))) {
            flagSource |= 1<<1;
        } else if (-1 != (posSource = getHeaderPosition(source))) {
            flagSource |= 1<<2;
        } else if (-1 != (possSource = getGroupChildPosition(source))[0]) {
            flagSource |= 1<<3;
        } else if (-1 != (posSource = getGroupPosition(source))) {
            flagSource |= 1<<4;
        } else {
            if (XLog.isEnabled()) XLog.e("wrong adapter position[%d]", sourcePosition);
        }

        if (-1 != (posTarget = getChildPosition(target))) {
            flagTarget |= 1;
        } else if (-1 != (posTarget = getFooterPosition(target))) {
            flagTarget |= 1<<1;
        } else if (-1 != (posTarget = getHeaderPosition(target))) {
            flagTarget |= 1<<2;
        } else if (-1 != (possTarget = getGroupChildPosition(target))[0]) {
            flagTarget |= 1<<3;
        } else if (-1 != (posTarget = getGroupPosition(target))) {
            flagTarget |= 1<<4;
        } else {
            if (XLog.isEnabled()) XLog.e("wrong adapter position[%d]", sourcePosition);
        }

        if ((flagSource & 1) != 0) {
            mChildList.set(posSource, target);
        } else if ((flagSource & (1 << 1)) != 0) {
            mFooterList.set(posSource, target);
        } else if ((flagSource & (1 << 2)) != 0) {
            mHeaderList.set(posSource, target);
        } else if ((flagSource & (1 << 3)) != 0) {
            mGroupChildMap.get(mGroupList.get(possSource[0])).set(possSource[1], target);
        } else if ((flagSource & (1 << 4)) != 0) {
            mGroupList.set(posSource, target);
        }

        if ((flagTarget & 1) != 0) {
            mChildList.set(posTarget, source);
        } else if ((flagTarget & (1 << 1)) != 0) {
            mFooterList.set(posTarget, source);
        } else if ((flagTarget & (1 << 2)) != 0) {
            mHeaderList.set(posTarget, source);
        } else if ((flagTarget & (1 << 3)) != 0) {
            mGroupChildMap.get(mGroupList.get(possTarget[0])).set(possTarget[1], source);
        } else if ((flagTarget & (1 << 4)) != 0) {
            mGroupList.set(posTarget, source);
        }

        Collections.swap(mList, sourcePosition, targetPosition);
        notifyItemMoved(sourcePosition, targetPosition);
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
        } else if (headerPosition < 0) {
            if (XLog.isEnabled()) XLog.e("invalid header position %d", headerPosition);
            return -1;
        }
        int oldHeaderCount = getHeaderCount();

        if (headerPosition > oldHeaderCount) {
            headerPosition = oldHeaderCount;
        }
        if (XLog.isEnabled()) XLog.d("headerPosition = %d", headerPosition);
        mHeaderList.addAll(headerPosition, headerList);
        int adapPos = convertHeaderPositionInner(headerPosition);
        mList.addAll(adapPos, headerList);
        int addSize = headerList.size();
        if (XLog.isEnabled()) XLog.v("notify item from %d, count = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
        return headerPosition;
    }

    public final List<T> getHeaders() {
        return new ArrayList<T>(mHeaderList);
    }

    public final T getHeader(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return null;
        }
        return mHeaderList.get(headerPosition);
    }


    public final void removeHeader(T header) {
        removeHeader(getHeaderPosition(header));
    }

    public final void removeHeaders(List<T> headers) {
        int size = headers != null ? headers.size() : 0;
        for (int i = 0; i < size; i++) {
            removeHeader(getHeaderPosition(headers.get(i)));
        }
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
        return getHeaderPosition(getItem(adapterPosition));
    }

    public final int getHeaderPosition(T header) {
        return mHeaderList.indexOf(header);
    }

    public final int convertHeaderPosition(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return -1;
        }
        return getItemPosition(getHeader(headerPosition));
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
        } else if (childPosition < 0) {
            if (XLog.isEnabled()) XLog.e("invalid child position %d", childPosition);
            return -1;
        }

        int childCount = getChildCount();
        if (childPosition > childCount) {
            childPosition = childCount;
        }
        mChildList.addAll(childPosition, childList);
        int adapPos = convertChildPositionInner(childPosition);
        mList.addAll(adapPos, childList);
        int addSize = childList.size();
        if (XLog.isEnabled()) XLog.v("notify chnaged item from %d, size = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
        return childPosition;
    }

    public final List<T> getChilds() {
        return new ArrayList<T>(mChildList);
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

    public final void removeChild(T child) {
        removeChild(getChildPosition(child));
    }

    public final void removeChilds(List<T> childs) {
        int size = childs != null ? childs.size() : 0;
        for (int i = 0; i < size; i++) {
            removeChild(childs.get(i));
        }
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

    public final int getChildPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", adapterPosition);
            return -1;
        }
        return getChildPosition(getItem(adapterPosition));
    }

    public final int getChildPosition(T child) {
        return mChildList.indexOf(child);
    }

    public final int convertChildPosition(int childPosition) {
        if (!checkChildPosition(childPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid child position %d", childPosition);
            return -1;
        }
        return getItemPosition(getChild(childPosition));
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
        int adapPos = convertGroupPositionInner(targetPosition);
        mList.add(adapPos, t);
        if (XLog.isEnabled()) XLog.v("now group count is %d", getGroupCount());
        notifyItemInserted(adapPos);
        return targetPosition;
    }

    public final void removeGroup(T group) {
        removeGroup(getGroupPosition(group));
    }

    public final void removeGroups(List<T> groups) {
        int size = groups != null ? groups.size() : 0;
        for (int i = 0; i < size; i++) {
            removeGroup(groups.get(i));
        }
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
        removeGroup(groupPosition, 0, 0, true);
    }

    public final void removeAllGroup() {
        int gc = getGroupCount();
        for (int i = 0; i < gc; i++) {
            removeGroup(0);
        }
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
    private final void removeGroup(int groupPosition,
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
        int adapPos = convertGroupPosition(groupPosition);
        if (adapPos == -1) {
            return;
        }
        T oldGroup = mGroupList.get(groupPosition);
        List oldGroupChild = mGroupChildMap.get(oldGroup);
        mGroupChildMap.remove(oldGroup);
        mGroupChildMap.put(t, oldGroupChild);
        mGroupList.set(groupPosition, t);
        mList.set(adapPos, t);
        notifyItemChanged(adapPos);
    }

    public final int getGroupPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", adapterPosition);
            return -1;
        }
        return getGroupPosition(getItem(adapterPosition));
    }

    public final int getGroupPosition(T group) {
        return mGroupList.indexOf(group);
    }

    public final int convertGroupPosition(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid group position %d", groupPosition);
            return -1;
        }
        return getItemPosition(getGroup(groupPosition));
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

        int adapPos = convertGroupPositionInner(groupPosition);
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

    public final int[] addGroupChild(int groupPosition, T t) {
        if (!checkGroupPosition(groupPosition)) {
            return new int[]{-1, -1};
        }
        ArrayList<T> ts = new ArrayList<>();
        ts.add(t);
        return addGroupChild(groupPosition, getGroupChildCount(groupPosition), ts);
    }

    @Deprecated
    public final void addGroupChildPosition(int groupPosition, int childPos, T t) {
        addGroupChild(groupPosition, childPos, t);
    }

    public final int[] addGroupChild(int groupPosition, int childPosition, T t) {
        ArrayList<T> ts = new ArrayList<>();
        ts.add(t);
        return addGroupChild(groupPosition, childPosition, ts);
    }

    public final int[] addGroupChild(int groupPosition, List<T> childList) {
        if (!checkGroupPosition(groupPosition)) {
            return new int[]{-1, -1};
        }
        return addGroupChild(groupPosition, getGroupChildCount(groupPosition), childList);
    }

    public final int[] addGroupChild(int groupPosition, int childPosition, List<T> childList) {
        if (!checkGroupPosition(groupPosition)) {
            return new int[]{-1, -1};
        } else if (childList == null || childList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("invalid child mList");
            return new int[]{-1, -1};
        } else if (childPosition < 0) {
            if (XLog.isEnabled()) XLog.e("invalid child position %d", childPosition);
            return new int[]{-1, -1};
        }
        int oldGroupChildCount = getGroupChildCount(groupPosition);
        if (childPosition > oldGroupChildCount) {
            childPosition = oldGroupChildCount;
        }
        mGroupChildMap.get(mGroupList.get(groupPosition)).addAll(childPosition, childList);
        int adapterPosition = convertGroupChildPositionInner(groupPosition, childPosition);
        mList.addAll(adapterPosition, childList);
        notifyItemRangeInserted(adapterPosition, childList.size());
        return new int[]{groupPosition, childPosition};
    }

    public final List<T> getGroups() {
        return new ArrayList<T>(mGroupList);
    }

    public final List<T> getGroupChilds(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return null;
        }
        return new ArrayList<T>(mGroupChildMap.get(mGroupList.get(groupPosition)));
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
        removeGroup(groupPosition, childStartPosition, count, false);
    }

    public final void removeGroupChild(int groupPosition, int childPosition) {
        removeGroup(groupPosition, childPosition, 1, false);
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
            removeGroup(groupPosition,
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

    public final int[] getGroupChildPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapter position %d", adapterPosition);
            return new int[]{-1, -1};
        }
        return getGroupChildPosition(getItem(adapterPosition));
    }

    public final int[] getGroupChildPosition(T groupChild) {
        int childPos;
        int gcount = getGroupCount();
        for (int i = 0; i < gcount; i++) {
            List<T> cts = mGroupChildMap.get(mGroupList.get(i));
            if (cts == null || cts.isEmpty()) {
                continue;
            }
            if ((childPos = cts.indexOf(groupChild)) != -1) {
                return new int[]{i, childPos};
            }
        }
        return new int[]{-1, -1};
    }

    public final int convertGroupChildPosition(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return -1;
        }
        return getItemPosition(mGroupChildMap.get(getGroup(groupPosition)).get(childPosition));
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

    public final int addFooter(int footerPosition, List<T> footerList) {

        if (null == footerList || footerList.isEmpty()) {
            if (XLog.isEnabled()) XLog.e("wrong param");
            return -1;
        } else if (footerPosition < 0) {
            if (XLog.isEnabled()) XLog.e("invalid footer position %d", footerPosition);
            return -1;
        }
        int oldFooterCount = getFooterCount();

        if (footerPosition > oldFooterCount) {
            footerPosition = oldFooterCount;
        }
        if (XLog.isEnabled()) XLog.d("footerPosition = %d", footerPosition);
        int adapPos = convertFooterPositionInner(footerPosition);
        mFooterList.addAll(footerPosition, footerList);
        mList.addAll(adapPos, footerList);
        int addSize = footerList.size();
        if (XLog.isEnabled()) XLog.v("notify item from %d, count = %d", adapPos, addSize);
        notifyItemRangeInserted(adapPos, addSize);
        return footerPosition;
    }

    public final List<T> getFooters() {
        return new ArrayList<>(mFooterList);
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


    public final void removeFooter(T footer) {
        removeFooter(getFooterPosition(footer));
    }

    public final void removeFooters(List<T> footers) {
        int size = footers != null ? footers.size() : 0;
        for (int i = 0; i < size; i++) {
            removeFooter(footers.get(i));
        }
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

    public final int getFooterPosition(int adapterPosition) {
        if (!checkAdapterPosition(adapterPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapter position %d", adapterPosition);
            return -1;
        }
        return getFooterPosition(getItem(adapterPosition));
    }

    public final int getFooterPosition(T footer) {
        return mFooterList.indexOf(footer);
    }

    public final int convertFooterPosition(int footerPosition) {
        if (!checkFooterPosition(footerPosition)) {
            return -1;
        }
        return getItemPosition(getFooter(footerPosition));
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


    public final int getHeaderPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int hsize = getHeaderCount();
        for (i = 0; i < hsize; i++) {
            if (viewType == getItemViewType(getHeader(i)) || viewType == getItemViewType(
                    getItemPosition(getHeader(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final void removeHeaderByViewType(int viewType) {
        int p = getHeaderPositionByViewType(viewType);
        if (p != -1) {
            removeHeader(p);
        } else {
            XLog.w("no header's viewType is %d", viewType);
        }
    }

    public final int getGroupPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int gsize = getGroupCount();
        for (i = 0; i < gsize; i++) {
            if (viewType == getItemViewType(getGroup(i)) || viewType == getItemViewType(
                    getItemPosition(getGroup(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final void removeGroupByViewType(int viewType) {
        int gpos = getGroupPositionByViewType(viewType);
        if (-1 != gpos) {
            removeGroup(gpos);
        } else {
            XLog.w("no group's viewType is %d", viewType);
        }
    }
}
