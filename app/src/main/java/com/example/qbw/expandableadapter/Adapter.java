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
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.example.qbw.expandableadapter.holder.FooterViewHolder;
import com.example.qbw.expandableadapter.holder.GroupItemViewHolder;
import com.example.qbw.expandableadapter.holder.GroupViewHolder;
import com.example.qbw.expandableadapter.holder.HeaderViewHolder;
import com.example.qbw.expandableadapter.holder.ItemViewHolder;
import com.qbw.recyclerview.expandable.ExpandableAdapter;

/**
 * @author QBW
 * @createtime 2016/04/05 10:03
 */


public class Adapter extends ExpandableAdapter<BaseEntity> {

    public Adapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder<BaseEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case Type.HEADER:
                viewHolder = new HeaderViewHolder(context, parent);
                break;
            case Type.CHILD:
                viewHolder = new ItemViewHolder(context, parent);
                break;
            case Type.GROUP:
                viewHolder = new GroupViewHolder(context, parent);
                break;
            case Type.GROUP_CHILD:
                viewHolder = new GroupItemViewHolder(context, parent);
                break;
            case Type.FOOTER:
                viewHolder = new FooterViewHolder(context, parent);
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
    public RecyclerView.ViewHolder onCreateStickyGroupViewHolder(int groupPosition) {
        GroupViewHolder groupViewHolder = new GroupViewHolder(context, null);
        groupViewHolder.bindData(-1, (Group) getGroup(groupPosition));
        //groupViewHolder.itemView.setAlpha(0.8f);
        //groupViewHolder.itemView.setBackgroundColor(Color.parseColor("#aabbcc"));
        return groupViewHolder;
    }

    @Override
    public void bindStickyGroupData(int groupPosition, RecyclerView.ViewHolder stickyGroupViewHolder) {
        GroupViewHolder groupViewHolder = (GroupViewHolder) stickyGroupViewHolder;
        groupViewHolder.bindData(-1, (Group) getGroup(groupPosition));
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
        return Type.GROUP == getItemViewType(adapPos);
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
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return new Point(display.getWidth(), (int) context.getResources().getDimension(R.dimen.group_height));
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
        }
        return super.getItemViewType(position);
    }

    public static class Type {
        public static final int HEADER = 5;
        public static final int CHILD = 1;
        public static final int GROUP = 2;
        public static final int GROUP_CHILD = 3;
        public static final int FOOTER = 4;

    }
}
