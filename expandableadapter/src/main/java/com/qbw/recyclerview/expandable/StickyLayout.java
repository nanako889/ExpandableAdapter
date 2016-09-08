package com.qbw.recyclerview.expandable;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.qbw.log.XLog;
import com.qbw.recyclerview.util.PositionUtil;

/**
 * @author qbw
 * @createtime 2016/04/22 14:19
 * 如果要显示悬浮的Group，需要将RecyclerView包裹到此布局中
 */


public class StickyLayout extends FrameLayout {
    private Context mContext;

    private StickyGroupLayout mStickyGroupLayout;

    private RecyclerView mRecyclerView;

    private StickyScrollListener mStickyScrollListener = new StickyScrollListener();

    public StickyLayout(Context context) {
        super(context);
        initViews(context, null);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        XLog.d("changed=[%s], [%d, %d - %d, %d]", changed, left, top, right, bottom);
        if (getChildCount() > 0) {
            View childView = getChildAt(0);
            if (childView instanceof RecyclerView) {
                childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            }
        }
    }

    private void initViews(Context context, AttributeSet attrs) {
        mContext = context;
    }

    /**
     * @param supportStickyGroup 是否支持group悬浮效果
     */
    public void init(boolean supportStickyGroup) {
        mRecyclerView = (RecyclerView) getChildAt(0);
        if (supportStickyGroup) {
            mRecyclerView.addOnScrollListener(mStickyScrollListener);
            mStickyGroupLayout = new StickyGroupLayout(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            mStickyGroupLayout.setLayoutParams(params);
            mStickyGroupLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //no nothing
                }
            });
            addView(mStickyGroupLayout);
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (null == adapter) {
                throw new RuntimeException("必须在设置RecyclerView的Adapter之后才能调用init函数！");
            } else if (!(adapter instanceof ExpandableAdapter)) {
                throw new RuntimeException("RecyclerView的Adapter必须继承自ExpandableAdapter！");
            }
        }
    }

    /**
     * 第一个可见的item是group类型的时候
     *
     * @param firstVisibleItemPosition
     */
    private void caseGroup(ExpandableAdapter expandableAdapter, int firstVisibleItemPosition) {
        caseGroup_GroupChild(expandableAdapter, expandableAdapter.getGroupPosition(firstVisibleItemPosition));
    }

    /**
     * 第一个可见的itme是groupchild类型的时候
     *
     * @param expandableAdapter
     * @param firstVisibleItemPosition
     */
    private void caseGroupChild(ExpandableAdapter expandableAdapter, int firstVisibleItemPosition) {
        caseGroup_GroupChild(expandableAdapter, expandableAdapter.getGroupChildPosition(firstVisibleItemPosition)[0]);
    }

    private void caseGroup_GroupChild(ExpandableAdapter expandableAdapter, int groupPos) {
        XLog.d("group position=[%d]", groupPos);
        if (mStickyGroupLayout.getGroupPos() != groupPos) {
            if (expandableAdapter.getGroupItemType(groupPos) == mStickyGroupLayout.getGroupType()) {
                mStickyGroupLayout.bindGroupViewHolder(groupPos, expandableAdapter);
            } else {
                mStickyGroupLayout.removeGroupView();
                int groupType = expandableAdapter.getGroupItemType(groupPos);
                RecyclerView.ViewHolder viewHolder = createGroupViewHolder(groupPos);
                expandableAdapter.bindStickyGroupData(groupPos, viewHolder);
                mStickyGroupLayout.addGroupViewHolder(groupPos, groupType, viewHolder);
            }
        }
        int nextGroupPos = groupPos + 1;
        if (nextGroupPos < expandableAdapter.getGroupCount()) {//当前group下面还有分组
            checkIntersectGroup(expandableAdapter, nextGroupPos);
        } else {//当前group下面已经没有分组
            checkIntersectFirstFooter(expandableAdapter);
        }
    }

    /**
     * 'StickyGroup'是否与下一个’Group‘交叉
     */
    private void checkIntersectGroup(ExpandableAdapter expandableAdapter, int nextGroupPos) {
        XLog.d("Next group position=[%d]", nextGroupPos);
        int nextGroupAdapPos = expandableAdapter.convertGroupPosition(nextGroupPos);
        Rect nextGroupLocalR = viewRect(nextGroupAdapPos, false);
        if (null == nextGroupLocalR) {
            XLog.w("null == nextGroupLocalR");
            if (mStickyGroupLayout.getGroupPos() <= 0) {
                XLog.w("invalid group position");
                return;
            }
            Point stickySize = expandableAdapter.getGroupSize(mStickyGroupLayout.getGroupPos());
            layoutStickyGroup(0, 0, stickySize.x, stickySize.y);
        } else {
            Point stickySize = expandableAdapter.getGroupSize(mStickyGroupLayout.getGroupPos());
            int stickyGpTop = nextGroupLocalR.top - stickySize.y;
            if (stickyGpTop > 0) {
                stickyGpTop = 0;
            }
            layoutStickyGroup(0, stickyGpTop, stickySize.x, stickySize.y + stickyGpTop);
        }
    }

    /**
     * 'StickyGroup'是否与第一个’Footer‘交叉
     */
    private void checkIntersectFirstFooter(ExpandableAdapter expandableAdapter) {
        XLog.line(true);
        if (expandableAdapter.getFooterCount() <= 0) {
            XLog.d("footer count <= 0");
        } else {
            int firstFooterAdapPos = expandableAdapter.convertFooterPosition(0);
            Rect firstFooterLocalR = viewRect(firstFooterAdapPos, false);
            if (null == firstFooterLocalR) {
                XLog.w("null == firstFooterLocalR");
                if (mStickyGroupLayout.getGroupPos() <= 0) {
                    XLog.w("invalid group position");
                    return;
                }
                Point stickySize = expandableAdapter.getGroupSize(mStickyGroupLayout.getGroupPos());
                layoutStickyGroup(0, 0, stickySize.x, stickySize.y);
            } else {
                Point stickyGroupSize = expandableAdapter.getGroupSize(mStickyGroupLayout.getGroupPos());
                int stickyGroupTop = firstFooterLocalR.top - stickyGroupSize.y;
                if (stickyGroupTop > 0) {
                    stickyGroupTop = 0;
                }
                layoutStickyGroup(0, stickyGroupTop, stickyGroupSize.x, stickyGroupTop + stickyGroupSize.y);
            }
        }
        XLog.line(false);
    }

    private void layoutStickyGroup(final int left, final int top, final int right, final int bottom) {
        mStickyGroupLayout.layout(left, top, right, bottom);
        mStickyGroupLayout.requestLayout();
        printfStickyRect();
    }

    private String getPullDirection(int dy) {
        if (0 == dy) {
            return "no-dy";
        }
        return dy > 0 ? "up" : "down";
    }

    private void printfStickyRect() {
        if (XLog.isDebug()) {
            Rect rect = stickyViewRect(false);
            XLog.d("Now sticky rect rect = [%s]", rect.toString());
        }
    }

    private class StickyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            ExpandableAdapter expandableAdapter = (ExpandableAdapter) recyclerView.getAdapter();
            int firstVisibleItemPosition = PositionUtil.findFirstVisibleItemPosition(recyclerView);
            if (0 == dy) {
                XLog.w("No Y distance");
                checkUpdateStickyGroup(expandableAdapter, firstVisibleItemPosition);
                return;
            }
            if (mStickyGroupLayout.getChildCount() > 0) {
                if (checkRemoveStickyGroup(expandableAdapter, dy, firstVisibleItemPosition)) {
                    return;
                }
                XLog.line(true);
                if (expandableAdapter.isPostionGroup(firstVisibleItemPosition)) {//第一个可见的item是group类型
                    caseGroup(expandableAdapter, firstVisibleItemPosition);
                } else if (expandableAdapter.isPostionGroupChild(firstVisibleItemPosition)) {//第一个可见的item是groupchild类型
                    caseGroupChild(expandableAdapter, firstVisibleItemPosition);
                }
                XLog.line(false);
            } else {
                checkAddStickyGroup(expandableAdapter, dy, firstVisibleItemPosition);
            }
        }
    }

    private void checkUpdateStickyGroup(ExpandableAdapter expandableAdapter, int firstVisibleItemPosition) {
        XLog.line(true);
        if (checkRemoveStickyGroup(expandableAdapter, 0, firstVisibleItemPosition)) {
        } else {
            if (expandableAdapter.isPostionGroup(firstVisibleItemPosition)) {
                caseGroup(expandableAdapter, firstVisibleItemPosition);
            } else if (expandableAdapter.isPostionGroupChild(firstVisibleItemPosition)) {
                caseGroupChild(expandableAdapter, firstVisibleItemPosition);
            }
        }
        XLog.line(false);
    }

    private boolean checkRemoveStickyGroup(ExpandableAdapter expandableAdapter, int dy, int firstVisibleItemPosition) {
        XLog.line(true);
        boolean b = false;
        if (expandableAdapter.isPositionHeader(firstVisibleItemPosition) || expandableAdapter.isPositionChild(firstVisibleItemPosition)) {
            mStickyGroupLayout.removeGroupView();
            XLog.w("[%s]Remove sticky group", getPullDirection(dy));
            b = true;
        } else if (dy > 0 && expandableAdapter.isPositionFooter(firstVisibleItemPosition)) {
            mStickyGroupLayout.removeGroupView();
            XLog.w("[%s]Remove sticky group, footer", getPullDirection(dy));
            b = true;
        } else if (expandableAdapter.isPostionGroup(firstVisibleItemPosition)) {
            int groupType = expandableAdapter.getItemViewType(firstVisibleItemPosition);
            if (mStickyGroupLayout.getGroupType() != groupType) {
                XLog.w("[%s]Remove sticky group, different group type,new = %d, old = %d", getPullDirection(dy), groupType, mStickyGroupLayout.getGroupType());
                mStickyGroupLayout.removeGroupView();
                b = true;
            }
        }
        XLog.line(false);
        return b;
    }

    private boolean checkAddStickyGroup(ExpandableAdapter expandableAdapter, final int dy, int firstVisibleItemPosition) {
        XLog.line(true);
        boolean b = true;
        if (expandableAdapter.isPostionGroup(firstVisibleItemPosition) || expandableAdapter.isPostionGroupChild(firstVisibleItemPosition)) {
            if (mStickyGroupLayout.getChildCount() > 0) {
                XLog.w("[%s]sticky group layout alreay has child view", getPullDirection(dy));
                b = false;
            } else {
                int groupPos = RecyclerView.NO_POSITION;
                if (expandableAdapter.isPostionGroup(firstVisibleItemPosition)) {
                    groupPos = expandableAdapter.getGroupPosition(firstVisibleItemPosition);
                } else if (expandableAdapter.isPostionGroupChild(firstVisibleItemPosition)) {
                    groupPos = expandableAdapter.getGroupChildPosition(firstVisibleItemPosition)[0];
                }
                if (RecyclerView.NO_POSITION == groupPos) {
                    XLog.w("[%s]groupPos=[%d]", getPullDirection(dy), groupPos);
                    b = false;
                } else {
                    int groupType = expandableAdapter.getGroupItemType(groupPos);
                    RecyclerView.ViewHolder viewHolder = createGroupViewHolder(groupPos);
                    expandableAdapter.bindStickyGroupData(groupPos, viewHolder);
                    mStickyGroupLayout.addGroupViewHolder(groupPos, groupType, viewHolder);
                    final Point size = expandableAdapter.getGroupSize(expandableAdapter.convertGroupPosition(groupPos));
                    mStickyGroupLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mStickyGroupLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            layoutStickyGroup(0, dy > 0 ? 0 : -size.y, size.x, dy > 0 ? size.y : 0);
                        }
                    });
                    XLog.d("[%s]Add sticky group, position=[%d]", getPullDirection(dy), groupPos);
                }
            }
        }
        XLog.line(false);
        return b;
    }

    private RecyclerView.ViewHolder createGroupViewHolder(int groupPosition) {
        ExpandableAdapter expandableAdapter = (ExpandableAdapter) mRecyclerView.getAdapter();
        XLog.d("Create group[%d] viewholder", groupPosition);
        RecyclerView.ViewHolder viewHolder = expandableAdapter.onCreateStickyGroupViewHolder(groupPosition);
        Point point = expandableAdapter.getGroupSize(groupPosition);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(point.x, point.y);
        viewHolder.itemView.setLayoutParams(layoutParams);
        return viewHolder;
    }

    /**
     * @param adapterPosition
     * @param isGlobal        是否获取相对于activity的坐标
     * @return 获取item view的rect坐标
     */
    private Rect viewRect(int adapterPosition, boolean isGlobal) {
        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(adapterPosition);
        if (null == vh) {
            XLog.w("View holder is null, position[%d]", adapterPosition);
            return null;
        }
        Rect r = new Rect();
        if (isGlobal) {
            vh.itemView.getGlobalVisibleRect(r);
        } else {
            r.set(vh.itemView.getLeft(), vh.itemView.getTop(), vh.itemView.getRight(), vh.itemView.getBottom());
        }
        return r;
    }

    /**
     * @param isGlobal
     * @return
     */
    private Rect stickyViewRect(boolean isGlobal) {
        Rect r = new Rect();
        if (isGlobal) {
            mStickyGroupLayout.getGlobalVisibleRect(r);
        } else {
            r.set(mStickyGroupLayout.getLeft(), mStickyGroupLayout.getTop(), mStickyGroupLayout.getRight(), mStickyGroupLayout.getBottom());
        }
        return r;
    }
}
