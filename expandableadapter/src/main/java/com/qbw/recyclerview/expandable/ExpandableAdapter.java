package com.qbw.recyclerview.expandable;

import android.support.v7.widget.RecyclerView;

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

    private int mHeaderCount;
    private int mChildCount;
    private int mGroupCount;
    private List<Integer> mGroupChildCount;

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

    public final void removeGroupChild(int groupPosition, int childPosition) {
        removeGroup(groupPosition, childPosition, 1, false);
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
        return mGroupChildCount.get(groupPosition);
    }

    public final void notifyGroupChildChanged(int groupPosition, int childPosition) {
        if (!checkGroupChildPosition(groupPosition, childPosition)) {
            return;
        }
        int adapPos = convertGroupChildPosition(groupPosition, childPosition);
        notifyItemChanged(adapPos);
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

    public final int[] getGroupChildPosition(int adapterPosition) {
        if (!checkItemPosition(adapterPosition)) {
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
        if (!checkItemPosition(adapterPosition)) {
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
     * @param viewTypes    约束列表（顺序）
     * @param currViewType 需要判断位置的viewType
     */
    private int getCorrectConstraintPosition(int type, List<Integer> viewTypes, int currViewType) {
        if (viewTypes == null || viewTypes.isEmpty()) {
            XLog.w("Please call method setXXXViewTypePositionConstraints");
            return -1;
        }
        int currViewTypePosition = viewTypes.indexOf(currViewType);
        if (currViewTypePosition == -1) {
            XLog.w("ViewType [%d] not find in XXX constraint viewType list");
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
