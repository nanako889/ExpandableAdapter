package com.qbw.recyclerview.expandable;

import com.qbw.log.XLog;
import com.qbw.recyclerview.base.BaseExpandableAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bond on 2016/4/2.
 */
public abstract class ExpandableAdapter<T> extends BaseExpandableAdapter<T> {

    private List<T> mList;

    private int mHeaderCount;
    private int mChildCount;
    private int mGroupCount;
    private List<Integer> mGroupChildCount;
    private int mFooterCount;

    private List<Integer> mHeaderViewTypePositionConstraints;
    private List<Integer> mChildViewTypePositionConstraints;
    private List<Integer> mGroupViewTypePositionConstraints;
    private List<Integer> mFooterViewTypePositionConstraints;

    public ExpandableAdapter() {
        mList = new ArrayList<>();
        mGroupChildCount = new ArrayList<>();
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
    public final T getItem(int itemPosition) {
        return mList.get(itemPosition);
    }

    public final int getItemPosition(T item) {
        return mList.indexOf(item);
    }

    public final void removeItem(int itemPosition) {
        int pos;
        int[] poss;
        if (-1 != (pos = getChildPosition(itemPosition))) {
            removeChild(pos);
        } else if (-1 != (pos = getFooterPosition(itemPosition))) {
            removeFooter(pos);
        } else if (-1 != (pos = getHeaderPosition(itemPosition))) {
            removeHeader(pos);
        } else if (-1 != (poss = getGroupChildPosition(itemPosition))[0]) {
            removeGroupChild(poss[0], poss[1]);
        } else if (-1 != (pos = getGroupPosition(itemPosition))) {
            removeGroup(pos);
        } else {
            if (XLog.isEnabled()) XLog.e("wrong item position[%d]", itemPosition);
        }
    }

    public final void swapItem(int sourcePosition, int targetPosition) {
        if (sourcePosition < 0 || sourcePosition >= getItemCount()) {
            if (XLog.isEnabled()) XLog.e("invalid sourcePosition:%d", sourcePosition);
            return;
        } else if (targetPosition < 0 || targetPosition >= getItemCount()) {
            if (XLog.isEnabled()) XLog.e("invalid targetPosition:%d", targetPosition);
            return;
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
        notifyDataSetChanged();
    }

    public final int addHeader(T header) {
        return addHeader(mHeaderCount, header, null);
    }

    public final int addHeader(int headerPosition, T header) {
        return addHeader(headerPosition, header, null);
    }

    public final int addHeader(List<T> headerList) {
        return addHeader(mHeaderCount, null, headerList);
    }

    public final int addHeader(int headerPosition, List<T> headerList) {
        return addHeader(headerPosition, null, headerList);
    }

    private final int addHeader(int headerPosition, T header, List<T> headerList) {
        if (headerPosition < 0) {
            XLog.e("Invalid header position %d", headerPosition);
            return -1;
        } else if (header == null && (headerList == null || headerList.isEmpty())) {
            XLog.e("Invalid header parameter");
            return -1;
        }
        if (headerPosition > mHeaderCount) {
            headerPosition = mHeaderCount;
        }
        int itemPosition = headerPosition;
        int itemAddSize;
        if (header != null) {
            mList.add(itemPosition, header);
            itemAddSize = 1;
        } else {
            mList.addAll(itemPosition, headerList);
            itemAddSize = headerList.size();
        }
        XLog.v("Notify item from %d, count is %d", itemPosition, itemAddSize);
        notifyItemRangeInserted(itemPosition, itemAddSize);
        mHeaderCount += itemAddSize;
        return headerPosition;
    }

    public final List<T> getHeaders() {
        if (mHeaderCount <= 0 || getItemCount() <= 0) {
            XLog.w("No header items");
            return null;
        }
        return new ArrayList<>(mList.subList(0, mHeaderCount));
    }

    public final T getHeader(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return null;
        }
        return mList.get(headerPosition);
    }

    public final void removeHeader(T header) {
        removeHeader(mList.indexOf(header), 1);
    }

    public final void removeHeaders(List<T> headers) {
        int size = headers != null ? headers.size() : 0;
        for (int i = 0; i < size; i++) {
            removeHeader(mList.indexOf(headers.get(i)), 1);
        }
    }

    public final void removeHeader(int headerPosition) {
        removeHeader(headerPosition, 1);
    }

    public final void clearHeader() {
        removeHeader(0, mHeaderCount);
    }

    public final void clearHeader(int headerBeginPosition) {
        removeHeader(headerBeginPosition, mHeaderCount - headerBeginPosition);
    }

    public final void removeHeader(int headerBeginPosition, int removeCount) {
        if (!checkHeaderPosition(headerBeginPosition)) {
            return;
        } else if (removeCount <= 0) {
            XLog.w("Invalid header removeCount %d", removeCount);
            return;
        }
        int itemBeginPosition = headerBeginPosition;
        if (headerBeginPosition + removeCount > mHeaderCount) {
            int oldRemoveCount = removeCount;
            removeCount = mHeaderCount - headerBeginPosition;
            XLog.i("Reset removeCount from %d to %d", oldRemoveCount, removeCount);
        }
        mList.subList(itemBeginPosition, itemBeginPosition + removeCount).clear();
        notifyItemRangeRemoved(itemBeginPosition, removeCount);
        mHeaderCount -= removeCount;
    }

    public final void updateHeader(int headerPosition, T t) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        mList.set(headerPosition, t);
        notifyItemChanged(headerPosition);
    }

    public final void notifyHeaderChanged(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return;
        }
        notifyItemChanged(headerPosition);
    }

    @Override
    public final int getHeaderCount() {
        return mHeaderCount;
    }

    private boolean checkHeaderPosition(int headerPosition) {
        if (headerPosition < 0) {
            XLog.w("Invalid header position %d", headerPosition);
            return false;
        } else if (headerPosition >= mHeaderCount) {
            XLog.w("Invalid header position %d, header size is %d", headerPosition, mHeaderCount);
            return false;
        }
        return true;
    }

    public final int getHeaderPosition(int itemPosition) {
        if (!checkItemPosition(itemPosition) || itemPosition >= mHeaderCount) {
            return -1;
        }
        return itemPosition;
    }

    public final int getHeaderPosition(T header) {
        return mList.indexOf(header);
    }

    public final int convertHeaderPosition(int headerPosition) {
        if (!checkHeaderPosition(headerPosition)) {
            return -1;
        }
        return headerPosition;
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
            XLog.e("Wrong param tList");
            return -1;
        } else if (childPosition < 0) {
            XLog.e("Invalid child position %d", childPosition);
            return -1;
        }
        if (childPosition > mChildCount) {
            childPosition = mChildCount;
        }
        int itemPosition = mHeaderCount + childPosition;
        mList.addAll(itemPosition, childList);
        int addSize = childList.size();
        XLog.v("Notify item from %d, count is %d", itemPosition, addSize);
        notifyItemRangeInserted(itemPosition, addSize);
        return childPosition;
    }

    public final List<T> getChilds() {
        if (mChildCount <= 0 || getItemCount() <= 0) {
            XLog.w("No child items");
            return null;
        }
        return new ArrayList<>(mList.subList(mHeaderCount, mHeaderCount + mChildCount));
    }

    public final T getChild(int childPosition) {
        if (!checkChildPosition(childPosition)) {
            return null;
        }
        return mList.get(mHeaderCount + childPosition);
    }

    public final void removeChild(int childPos) {
        removeChild(childPos, 1);
    }

    public final void removeChild(int childBeginPosition, int removeCount) {
        if (!checkChildPosition(childBeginPosition)) {
            return;
        } else if (removeCount <= 0) {
            XLog.e("Invalid child removeCount %d", removeCount);
            return;
        }
        int end = childBeginPosition + removeCount;
        if (end > mChildCount) {
            int oldRemoveCount = removeCount;
            removeCount = mChildCount - childBeginPosition;
            XLog.i("Reset child removeCount from %d to %d", oldRemoveCount, removeCount);
        }
        int itemPosition = convertChildPosition(childBeginPosition);
        mList.subList(itemPosition, itemPosition + removeCount).clear();
        notifyItemRangeRemoved(itemPosition, removeCount);
        mChildCount -= removeCount;
    }

    public final void clearChild(int childBeginPosition) {
        removeChild(childBeginPosition, mChildCount - childBeginPosition);
    }

    public final void clearChild() {
        removeChild(0, mChildCount);
    }

    public final void updateChild(int childPosition, T t) {
        if (!checkChildPosition(childPosition)) {
            return;
        }
        int itemPosition = convertChildPosition(childPosition);
        mList.set(itemPosition, t);
        notifyItemChanged(itemPosition);
    }

    public final void removeChild(T child) {
        removeChild(getChildPosition(child), 1);
    }

    public final void removeChilds(List<T> childs) {
        int size = childs != null ? childs.size() : 0;
        for (int i = 0; i < size; i++) {
            removeChild(getChildPosition(childs.get(i)), 1);
        }
    }

    public final void notifyChildChanged(int childPosition) {
        if (!checkChildPosition(childPosition)) {
            return;
        }
        notifyItemChanged(convertChildPosition(childPosition));
    }

    @Override
    public final int getChildCount() {
        return mChildCount;
    }

    private boolean checkChildPosition(int childPosition) {
        if (childPosition < 0) {
            XLog.w("Invalid child position %d", childPosition);
            return false;
        } else if (childPosition >= mChildCount) {
            XLog.w("invalid child position %d, child size is %d", childPosition, mChildCount);
            return false;
        }
        return true;
    }

    public final int getChildPosition(int itemPosition) {
        if (!checkItemPosition(itemPosition)) {
            if (XLog.isEnabled()) XLog.e("invalid adapterPosition %d", itemPosition);
            return -1;
        }
        return getChildPosition(getItem(itemPosition));
    }

    public final int getChildPosition(T child) {
        int itemPosition = mList.indexOf(child);
        if (itemPosition == -1) {
            return -1;
        }
        return itemPosition - mHeaderCount;
    }

    public final int convertChildPosition(int childPosition) {
        if (!checkChildPosition(childPosition)) {
            return -1;
        }
        return mHeaderCount + childPosition;
    }

    public final int addGroup(T t) {
        return addGroup(getGroupCount(), t);
    }

    public final int addGroup(int groupPosition, T group) {
        if (groupPosition < 0) {
            XLog.e("Invalid group position %d", groupPosition);
            return -1;
        } else if (mList.indexOf(group) != -1) {
            XLog.e("Group t is alread exist! You must use a different t to create a new group");
            return -1;
        }
        if (groupPosition > mGroupCount) {
            XLog.w("Reset group position from %d to %d", groupPosition, mGroupCount);
            groupPosition = mGroupCount;
        }
        int itemPosition = 0;
        for (int i = 0; i < groupPosition; i++) {
            itemPosition += mGroupChildCount.get(i);
        }
        mList.add(itemPosition, group);
        notifyItemInserted(itemPosition);
        mGroupCount += 1;
        if (groupPosition >= mGroupChildCount.size()) {
            mGroupChildCount.add(groupPosition, 0);
        }
        return groupPosition;
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
        int itemPosition = 0;
        for (int i = 0; i < groupPosition; i++) {
            itemPosition += mGroupChildCount.get(i);
        }
        return mList.get(mHeaderCount + mChildCount + itemPosition);
    }

    public final void removeAllGroup() {
        for (int i = 0; i < mGroupCount; i++) {
            removeGroup(0);
        }
    }

    private final void removeGroup(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return;
        }
        int itemPosition = convertGroupPosition(groupPosition);
        int groupChildCount = mGroupChildCount.get(groupPosition);
        mList.subList(itemPosition, itemPosition + groupChildCount + 1).clear();
        notifyItemRangeRemoved(itemPosition, groupChildCount + 1);
        mGroupCount--;
        mGroupChildCount.remove(groupPosition);
    }

    private final void removeGroupChild(int groupPosition,
                                        int groupChildBeingPosition,
                                        int removeCount) {
        if (!checkGroupChildPosition(groupPosition, groupChildBeingPosition)) {
            return;
        }
        if (removeCount <= 0) {
            XLog.e("Invalid group remove count %d", removeCount);
            return;
        }
        final int groupChildCount = mGroupChildCount.get(groupPosition);
        int groupChildEnd = groupChildBeingPosition + removeCount;
        if (groupChildEnd > groupChildCount) {
            int oldRemoveCount = removeCount;
            removeCount = groupChildCount - groupChildBeingPosition;
            groupChildEnd = groupChildCount;
            XLog.i("Reset group removeCount from %d to %d", oldRemoveCount, removeCount);
        }
        XLog.d("groupPosition=%d, childStarPosition=%d, count=%d, childEnd=%d",
               groupPosition,
               groupChildBeingPosition,
               removeCount,
               groupChildEnd);
        int itemPosition = convertGroupPosition(groupPosition);
        int itemBeginPosition = itemPosition + groupChildBeingPosition + 1;
        mList.subList(itemBeginPosition, itemBeginPosition + removeCount).clear();
        notifyItemRangeRemoved(itemBeginPosition, removeCount);
        mGroupChildCount.set(groupPosition, groupChildCount - removeCount);
    }

    public final void updateGroup(int groupPosition, T group) {
        int itemPosition = convertGroupPosition(groupPosition);
        if (itemPosition == -1) {
            return;
        }
        mList.set(itemPosition, group);
        notifyItemChanged(itemPosition);
    }

    public final int getGroupPosition(int itemPosition) {
        if (!checkItemPosition(itemPosition)) {
            XLog.e("Invalid itemPosition %d", itemPosition);
            return -1;
        }
        int groupPosition = -1;
        for (int i = 0; i < mGroupCount; i++) {
            if (itemPosition == convertGroupPosition(i)) {
                groupPosition = i;
                break;
            }
        }
        return groupPosition;
    }

    public final int getGroupPosition(T group) {
        return getGroupPosition(mList.indexOf(group));
    }

    public final int convertGroupPosition(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            XLog.e("Invalid group position %d", groupPosition);
            return -1;
        }
        int itemPosition = 0;
        for (int i = 0; i < groupPosition; i++) {
            itemPosition += mGroupChildCount.get(i);
        }
        return mHeaderCount + mChildCount + itemPosition;
    }

    public final void updateGroupChild(int groupPosition, int groupChildPosition) {
        int itemPosition = convertGroupChildPosition(groupPosition, groupChildPosition);
        if (itemPosition != -1) {
            notifyItemChanged(itemPosition);
        }
    }

    @Override
    public final int getGroupCount() {
        return mGroupCount;
    }

    private boolean checkGroupPosition(int groupPosition) {
        if (groupPosition < 0) {
            XLog.w("Invalid group position %d", groupPosition);
            return false;
        } else if (groupPosition >= mGroupCount) {
            XLog.w("Invalid group position %d, group size is %d", groupPosition, mGroupCount);
            return false;
        }
        return true;
    }

    public final int[] addGroupChild(int groupPosition, T groupChild) {
        if (!checkGroupPosition(groupPosition)) {
            return new int[]{-1, -1};
        }
        ArrayList<T> ts = new ArrayList<>();
        ts.add(groupChild);
        return addGroupChild(groupPosition, getGroupChildCount(groupPosition), ts);
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
        return addGroupChild(groupPosition, mGroupChildCount.get(groupPosition), childList);
    }

    public final int[] addGroupChild(int groupPosition, int groupChildPosition, List<T> childList) {
        if (!checkGroupPosition(groupPosition)) {
            return new int[]{-1, -1};
        } else if (childList == null || childList.isEmpty()) {
            XLog.e("Invalid group child mList");
            return new int[]{-1, -1};
        } else if (groupChildPosition < 0) {
            XLog.e("Invalid child position %d", groupChildPosition);
            return new int[]{-1, -1};
        }
        final int oldGroupChildCount = mGroupChildCount.get(groupPosition);
        if (groupChildPosition > oldGroupChildCount) {
            groupChildPosition = oldGroupChildCount;
        }
        int itemPosition = convertGroupPosition(groupPosition) + 1 + groupChildPosition;
        mList.addAll(itemPosition, childList);
        notifyItemRangeInserted(itemPosition, childList.size());
        mGroupChildCount.set(groupPosition, oldGroupChildCount + childList.size());
        return new int[]{groupPosition, groupChildPosition};
    }

    public final List<T> getGroups() {
        if (mGroupCount <= 0) {
            return null;
        }
        List<T> groups = new ArrayList<>(mGroupCount);
        for (int i = 0; i < mGroupCount; i++) {
            groups.add(getItem(convertGroupPosition(i)));
        }
        return groups;
    }

    public final List<T> getGroupChilds(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return null;
        }
        int groupChildCount = mGroupChildCount.get(groupPosition);
        if (groupChildCount <= 0) {
            return null;
        }
        int itemPosition = convertGroupPosition(groupPosition);
        List<T> groupChilds = new ArrayList<>(groupChildCount);
        for (int i = 0; i < groupChildCount; i++) {
            groupChilds.add(getItem(itemPosition + 1 + i));
        }
        return groupChilds;
    }

    public final T getGroupChild(int groupPosition, int groupChildPosition) {
        if (!checkGroupChildPosition(groupPosition, groupChildPosition)) {
            return null;
        }
        int itemPosition = convertGroupChildPosition(groupPosition, groupChildPosition);
        if (itemPosition == -1) {
            return null;
        }
        return mList.get(itemPosition);
    }

    public final void removeGroupChild(int groupPosition, int groupChildPosition) {
        removeGroupChild(groupPosition, groupChildPosition, 1);
    }


    public final void clearGroupChild(int groupPosition) {
        clearGroupChild(groupPosition, 0);
    }

    public final void clearGroupChild(int groupPosition, int childStarPosition) {
        if (!checkGroupChildPosition(groupPosition, childStarPosition)) {
            return;
        }
        int groupChildCount = getGroupChildCount(groupPosition);
        if (groupChildCount > 0) {
            removeGroupChild(groupPosition, childStarPosition, groupChildCount - childStarPosition);
        }
    }

    public final void updateGroupChild(int groupPosition, int groupChildPosition, T groupChild) {
        if (!checkGroupChildPosition(groupPosition, groupChildPosition)) {
            return;
        }
        int itemPosition = convertGroupChildPosition(groupPosition, groupChildPosition);
        if (itemPosition == -1) {
            return;
        }
        mList.set(itemPosition, groupChild);
        notifyItemChanged(itemPosition);
    }

    @Override
    public final int getGroupChildCount(int groupPosition) {
        if (!checkGroupPosition(groupPosition)) {
            return 0;
        }
        return mGroupChildCount.get(groupPosition);
    }

    public final void notifyGroupChildChanged(int groupPosition, int childPosition) {
        int itemPosition = convertGroupChildPosition(groupPosition, childPosition);
        if (itemPosition == -1) {
            return;
        }
        notifyItemChanged(itemPosition);
    }

    private boolean checkGroupChildPosition(int groupPosition, int groupChildPosition) {
        int groupChildCount;
        if (groupChildPosition < 0) {
            XLog.w("Invalid group child position %d, %d", groupPosition, groupChildPosition);
            return false;
        } else if (!checkGroupPosition(groupPosition)) {
            return false;
        } else if (groupChildPosition >= (groupChildCount = mGroupChildCount.get(groupPosition))) {
            XLog.w("Invalid group child position %d, %d, group %d child size is %d",
                   groupPosition,
                   groupChildPosition,
                   groupPosition,
                   groupChildCount);
            return false;
        }
        return true;
    }

    public final int[] getGroupChildPosition(int itemPosition) {
        int[] groupChildPosition = new int[]{-1, -1};
        if (!checkItemPosition(itemPosition)) {
            XLog.e("Invalid item position %d", itemPosition);
            return groupChildPosition;
        }
        int groupItemPosition;
        for (int i = 0; i < mGroupCount; i++) {
            groupItemPosition = convertGroupPosition(i);
            for (int j = 0; j < mGroupChildCount.get(i); j++) {
                if (groupItemPosition + 1 + j == itemPosition) {
                    groupChildPosition[0] = i;
                    groupChildPosition[1] = j;
                    break;
                }
            }
        }
        return groupChildPosition;
    }

    public final int[] getGroupChildPosition(T groupChild) {
        return getGroupChildPosition(indexOfGroupChild(groupChild));
    }

    public final int indexOfGroupChild(T groupChild) {
        if (groupChild == null || mGroupCount == 0) {
            return -1;
        }
        int itemPosition = -1;
        int itemCount = getItemCount();
        int itemBeginPosition = mHeaderCount + mChildCount;
        int itemEndPosition = itemCount - mFooterCount;
        for (int i = itemBeginPosition; i < itemEndPosition; i++) {
            if (mList.get(i).equals(groupChild)) {
                itemPosition = i;
                break;
            }
        }
        return itemPosition;
    }

    public final int convertGroupChildPosition(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return -1;
        }
        return convertGroupPosition(groupPosition) + 1 + childPosition;
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
            XLog.e("Wrong footer param");
            return -1;
        } else if (footerPosition < 0) {
            XLog.e("Invalid footer position %d", footerPosition);
            return -1;
        }
        int oldFooterCount = getFooterCount();
        if (footerPosition > oldFooterCount) {
            footerPosition = oldFooterCount;
        }
        int itemPosition = convertFooterPosition(footerPosition, false);
        mList.addAll(itemPosition, footerList);
        int addSize = footerList.size();
        XLog.v("Notify item from %d, count is %d", itemPosition, addSize);
        notifyItemRangeInserted(itemPosition, addSize);
        mFooterCount += addSize;
        return footerPosition;
    }

    public final List<T> getFooters() {
        int footerItemBeginPosition = convertFooterPosition(0);
        if (footerItemBeginPosition == -1) {
            return null;
        }
        return new ArrayList<>(mList.subList(footerItemBeginPosition, mFooterCount));
    }


    public final T getFooter(int footerPosition) {
        if (!checkFooterPosition(footerPosition)) {
            return null;
        }
        return mList.get(convertFooterPosition(footerPosition));
    }

    public final void updateFooter(int footerPosition, T t) {
        if (!checkFooterPosition(footerPosition)) {
            return;
        }
        int itemPosition = convertFooterPosition(footerPosition);
        mList.set(itemPosition, t);
        notifyItemChanged(itemPosition);
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

    public final void removeFooter(int footerBeginPosition, int removeCount) {
        if (!checkFooterPosition(footerBeginPosition)) {
            return;
        }
        int footerCount = getFooterCount();
        int footerItemBeginPosition = convertFooterPosition(footerBeginPosition);
        if (footerBeginPosition + removeCount > footerCount) {
            int oldRemoveCount = removeCount;
            removeCount = footerCount - footerBeginPosition;
            XLog.i("Reset removeCount from %d to %d", oldRemoveCount, removeCount);
        }
        mList.subList(footerItemBeginPosition, footerItemBeginPosition + removeCount).clear();
        notifyItemRangeRemoved(footerItemBeginPosition, removeCount);
    }

    @Override
    public final int getFooterCount() {
        return mFooterCount;
    }

    private boolean checkFooterPosition(int footerPosition) {
        if (footerPosition < 0) {
            XLog.w("Invalid footer position %d", footerPosition);
            return false;
        } else if (footerPosition >= mFooterCount) {
            XLog.w("Invalid footer position %d, footer size is %d", footerPosition, mFooterCount);
            return false;
        }
        return true;
    }

    public final int getFooterPosition(int itemPosition) {
        if (!checkItemPosition(itemPosition)) {
            XLog.e("Invalid item position %d", itemPosition);
            return -1;
        }
        return getFooterPosition(getItem(itemPosition));
    }

    public final int getFooterPosition(T footer) {
        return getFooterPosition(indexOfFooter(footer));
    }

    private int indexOfFooter(T footer) {
        if (footer == null) {
            return -1;
        }
        int itemPosition = -1;
        int footerItemBeginPosition = convertFooterPosition(0);
        int itemCount = getItemCount();
        for (int i = footerItemBeginPosition; i < itemCount; i++) {
            if (mList.get(i).equals(footer)) {
                itemPosition = i;
                break;
            }
        }
        return itemPosition;
    }

    public final int convertFooterPosition(int footerPosition) {
        return convertFooterPosition(footerPosition, true);
    }

    private final int convertFooterPosition(int footerPosition, boolean check) {
        if (check && !checkFooterPosition(footerPosition)) {
            return -1;
        }
        int group_groupChildCount = mGroupCount;
        for (int i = 0; i < mGroupCount; i++) {
            group_groupChildCount += mGroupChildCount.get(i);
        }
        return mHeaderCount + mChildCount + group_groupChildCount + footerPosition;
    }

    @Override
    public final int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private boolean checkItemPosition(int itemPosition) {
        int itemCount = getItemCount();
        if (itemPosition < 0 || itemPosition >= itemCount) {
            XLog.e("Invalid itemPosition %d, item count is %d", itemPosition, itemCount);
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

    public final int getLastHeaderPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int hsize = getHeaderCount();
        for (i = hsize - 1; i >= 0; i--) {
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

    public final int getChildPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int csize = getChildCount();
        for (i = 0; i < csize; i++) {
            if (viewType == getItemViewType(getChild(i)) || viewType == getItemViewType(
                    getItemPosition(getChild(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final int getLastChildPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int csize = getChildCount();
        for (i = csize - 1; i >= 0; i--) {
            if (viewType == getItemViewType(getChild(i)) || viewType == getItemViewType(
                    getItemPosition(getChild(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final void removeChildByViewType(int viewType) {
        int p = getChildPositionByViewType(viewType);
        if (p != -1) {
            removeChild(p);
        } else {
            XLog.w("no child's viewType is %d", viewType);
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

    public final int getLastGroupPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int gsize = getGroupCount();
        for (i = gsize - 1; i >= 0; i--) {
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

    public final int getFooterPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int fsize = getFooterCount();
        for (i = 0; i < fsize; i++) {
            if (viewType == getItemViewType(getFooter(i)) || viewType == getItemViewType(
                    getItemPosition(getFooter(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final int getLastFooterPositionByViewType(int viewType) {
        int p = -1;
        int i;
        int fsize = getFooterCount();
        for (i = fsize - 1; i >= 0; i--) {
            if (viewType == getItemViewType(getFooter(i)) || viewType == getItemViewType(
                    getItemPosition(getFooter(i)))) {
                p = i;
                break;
            }
        }
        return p;
    }

    public final void removeFooterByViewType(int viewType) {
        int p = getFooterPositionByViewType(viewType);
        if (p != -1) {
            removeFooter(p);
        } else {
            XLog.w("no footer's viewType is %d", viewType);
        }
    }

    public void setHeaderViewTypePositionConstraints(List<Integer> headerViewTypePositionConstraints) {
        mHeaderViewTypePositionConstraints = headerViewTypePositionConstraints;
    }

    public int getCorrectHeaderConstraintPosition(int currViewType) {
        return getCorrectConstraintPosition(0, mHeaderViewTypePositionConstraints, currViewType);
    }

    public void setChildViewTypePositionConstraints(List<Integer> childViewTypePositionConstraints) {
        mChildViewTypePositionConstraints = childViewTypePositionConstraints;
    }

    public int getCorrectChildConstraintPosition(int currViewType) {
        return getCorrectConstraintPosition(1, mChildViewTypePositionConstraints, currViewType);
    }

    public void setGroupViewTypePositionConstraints(List<Integer> groupViewTypePositionConstraints) {
        mGroupViewTypePositionConstraints = groupViewTypePositionConstraints;
    }

    public int getCorrectGroupConstraintPosition(int currViewType) {
        return getCorrectConstraintPosition(2, mGroupViewTypePositionConstraints, currViewType);
    }

    public void setFooterViewTypePositionConstraints(List<Integer> footerViewTypePositionConstraints) {
        mFooterViewTypePositionConstraints = footerViewTypePositionConstraints;
    }

    public int getCorrectFooterConstraintPosition(int currViewType) {
        return getCorrectConstraintPosition(3, mFooterViewTypePositionConstraints, currViewType);
    }

    /**
     * @param type         0,header;1,child;2,group,3,footer
     * @param viewTypes    constrainted viewtype list
     * @param currViewType the viewType which you need to calculate the right position
     */
    private int getCorrectConstraintPosition(int type, List<Integer> viewTypes, int currViewType) {
        if (viewTypes == null || viewTypes.isEmpty()) {
            XLog.w("Please call method setXXXViewTypePositionConstraints");
            return -1;
        }
        int currViewTypePosition = viewTypes.indexOf(currViewType);
        if (currViewTypePosition == -1) {
            XLog.w("ViewType %d not find in XXX constraint viewType list");
            return -1;
        }
        if (currViewTypePosition == 0) {
            return 0;
        }
        int tmp;
        int targetPos = -1;
        for (int i = currViewTypePosition - 1; i >= 0; i--) {
            switch (type) {
                case 0:
                    tmp = getLastHeaderPositionByViewType(viewTypes.get(i));
                    break;
                case 1:
                    tmp = getLastChildPositionByViewType(viewTypes.get(i));
                    break;
                case 2:
                    tmp = getLastGroupPositionByViewType(viewTypes.get(i));
                    break;
                case 3:
                    tmp = getLastFooterPositionByViewType(viewTypes.get(i));
                    break;
                default:
                    return -1;
            }
            if (tmp >= 0) {
                targetPos = tmp + 1;
                break;
            }
        }
        if (targetPos == -1) {
            targetPos = 0;
        }
        return targetPos;
    }
}
