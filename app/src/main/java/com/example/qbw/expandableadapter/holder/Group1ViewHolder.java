package com.example.qbw.expandableadapter.holder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qbw.expandableadapter.BaseViewHolder;
import com.example.qbw.expandableadapter.R;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.Group1;

/**
 * @author QBW
 * @createtime 2016/04/05 11:29
 */


public class Group1ViewHolder extends BaseViewHolder<Group1> {
    private TextView text;

    public Group1ViewHolder(Context context, ViewGroup parent) {
        super(context, R.layout.holder_group1, parent);
    }

    @Override
    public void findView() {
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @Override
    public void bindData(int adapPos, Group1 group) {
        text.setText(group.text);
    }
}
