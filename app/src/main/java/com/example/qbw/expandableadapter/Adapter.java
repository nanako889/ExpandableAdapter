package com.example.qbw.expandableadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.qbw.expandableadapter.entity.BaseEntity;
import com.example.qbw.expandableadapter.entity.Child;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.Group1;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.example.qbw.expandableadapter.entity.Header1;
import com.example.qbw.expandableadapter.holder.FooterViewHolder;
import com.example.qbw.expandableadapter.holder.Group1ViewHolder;
import com.example.qbw.expandableadapter.holder.GroupItemViewHolder;
import com.example.qbw.expandableadapter.holder.GroupViewHolder;
import com.example.qbw.expandableadapter.holder.Header1ViewHolder;
import com.example.qbw.expandableadapter.holder.HeaderViewHolder;
import com.example.qbw.expandableadapter.holder.ItemViewHolder;
import com.qbw.recyclerview.expandable.ExpandableAdapter;

/**
 * @author QBW
 * @createtime 2016/04/05 10:03
 */


public class Adapter extends ExpandableAdapter {

    private Context mContext;

    public Adapter(Context context) {
        mContext = context;
    }

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
            case Type.HEADER1:
                viewHolder = new Header1ViewHolder(mContext, parent);
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
        Object entity = getItem(position);
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
        } else if (entity instanceof Header1) {
            return Type.HEADER1;
        }
        return super.getItemViewType(position);
    }

    @Override
    public boolean isSameData(Object oldData, Object newData) {
        if (oldData instanceof BaseEntity && newData instanceof BaseEntity) {
            return ((BaseEntity) oldData).text.equals(((BaseEntity) newData).text);
        }
        return super.isSameData(oldData, newData);
    }

    public static class Type {

        public static final int CHILD = 1;
        public static final int GROUP = 2;
        public static final int GROUP_CHILD = 3;
        public static final int FOOTER = 4;
        public static final int HEADER = 5;
        public static final int GROUP1 = 6;
        public static final int HEADER1 = 7;
    }
}
