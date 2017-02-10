package com.example.qbw.expandableadapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.qbw.expandableadapter.entity.BaseEntity;
import com.example.qbw.expandableadapter.entity.Child;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.Group1;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.example.qbw.expandableadapter.holder.FooterViewHolder;
import com.example.qbw.expandableadapter.holder.Group1ViewHolder;
import com.example.qbw.expandableadapter.holder.GroupItemViewHolder;
import com.example.qbw.expandableadapter.holder.GroupViewHolder;
import com.example.qbw.expandableadapter.holder.HeaderViewHolder;
import com.example.qbw.expandableadapter.holder.ItemViewHolder;
import com.qbw.log.XLog;
import com.qbw.recyclerview.expandable.ExpandableAdapter;

/**
 * @author QBW
 * @createtime 2016/04/05 10:03
 */


public class Adapter extends ExpandableAdapter<BaseEntity> {

    public Adapter(Context context) {
        super(context);
    }

    //----------------------begin 必须要实现的方法--------------------------------
    @Override
    public BaseViewHolder<BaseEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case Type.HEADER:
                viewHolder = new HeaderViewHolder(mContext, parent);
                break;
            case Type.CHILD:
                viewHolder = new ItemViewHolder(mContext, parent);
                break;
            case Type.GROUP:
                viewHolder = new GroupViewHolder(mContext, parent);
                break;
            case Type.GROUP_CHILD:
                viewHolder = new GroupItemViewHolder(mContext, parent);
                break;
            case Type.FOOTER:
                viewHolder = new FooterViewHolder(mContext, parent);
                break;
            case Type.GROUP1:
                viewHolder = new Group1ViewHolder(mContext, parent);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseViewHolder viewHolder = (BaseViewHolder) holder;
        viewHolder.bindData(position, getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        BaseEntity entity = getItem(position);
        if (entity instanceof Header) {
            return Type.HEADER;
        } else if (entity instanceof Child) {
            return Type.CHILD;
        } else if (entity instanceof Group) {
            return Type.GROUP;
        } else if (entity instanceof GroupChild) {
            return Type.GROUP_CHILD;
        } else if (entity instanceof Footer) {
            return Type.FOOTER;
        } else if (entity instanceof Group1) {
            return Type.GROUP1;
        }
        return super.getItemViewType(position);
    }
    //----------------------end 必须要实现的方法--------------------------------

    //----------------------begin 使用StickyLayout需要实现的方法----------------

    @Override
    public RecyclerView.ViewHolder onCreateStickyGroupViewHolder(int groupPosition) {
        BaseEntity entity = getGroup(groupPosition);
        if (entity instanceof Group) {
            BaseViewHolder groupViewHolder = new GroupViewHolder(mContext, null);
            groupViewHolder.bindData(-1, getGroup(groupPosition));
            return groupViewHolder;
        } else if (entity instanceof Group1) {
            BaseViewHolder groupViewHolder = new Group1ViewHolder(mContext, null);
            groupViewHolder.bindData(-1, getGroup(groupPosition));
            return groupViewHolder;
        }
        XLog.e("type not match");
        return null;
    }

    @Override
    public void bindStickyGroupData(int groupPosition,
                                    RecyclerView.ViewHolder stickyGroupViewHolder) {
        BaseViewHolder groupViewHolder = (BaseViewHolder) stickyGroupViewHolder;
        groupViewHolder.bindData(-1, getGroup(groupPosition));
    }

    @Override
    public boolean isPositionHeader(int adapPos) {
        return Type.HEADER == getItemViewType(adapPos);
    }

    @Override
    public boolean isPositionChild(int adapPos) {
        return Type.CHILD == getItemViewType(adapPos);
    }

    @Override
    public boolean isPostionGroup(int adapPos) {
        return Type.GROUP == getItemViewType(adapPos) || Type.GROUP1 == getItemViewType(adapPos);
    }

    /**
     * 返回的类型的值来源于getItemType
     *
     * @see #getItemViewType(int)
     */
    @Override
    public int getGroupItemType(int groupPos) {
        BaseEntity entity = getGroup(groupPos);
        if (entity instanceof Group) {
            return Type.GROUP;
        } else if (entity instanceof Group1) {
            return Type.GROUP1;
        }
        return -1;
    }

    @Override
    public boolean isPostionGroupChild(int adapPos) {
        return Type.GROUP_CHILD == getItemViewType(adapPos);
    }

    @Override
    public boolean isPositionFooter(int adapPos) {
        return Type.FOOTER == getItemViewType(adapPos);
    }

    @Override
    public Point getGroupSize(int groupPosition) {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (3 == groupPosition || 1 == groupPosition) {
            return new Point(display.getWidth(),
                             (int) mContext.getResources().getDimension(R.dimen.group1_height));
        } else {
            return new Point(display.getWidth(),
                             (int) mContext.getResources().getDimension(R.dimen.group_height));
        }
    }

    //----------------------end 使用StickyLayout需要实现的方法----------------


    public static class Type {
        public static final int HEADER = 5;
        public static final int CHILD = 1;
        public static final int GROUP = 2;
        public static final int GROUP_CHILD = 3;
        public static final int FOOTER = 4;
        public static final int GROUP1 = 6;
    }
}
