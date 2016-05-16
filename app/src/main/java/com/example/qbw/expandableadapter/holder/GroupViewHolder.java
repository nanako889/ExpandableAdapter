package com.example.qbw.expandableadapter.holder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qbw.expandableadapter.R;
import com.example.qbw.expandableadapter.entity.Group;
import com.qbw.recyclerview.base.BaseViewHolder;

/**
 * @author QBW
 * @createtime 2016/04/05 11:29
 */


public class GroupViewHolder extends BaseViewHolder<Group> {
    private TextView text;

    public GroupViewHolder(Context context, ViewGroup parent) {
        super(context, R.layout.holder_group, parent);
    }

    @Override
    public void findView() {
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @Override
    public void bindData(int adapPos, Group group) {
        text.setText(group.text);
    }
}
