package com.example.qbw.expandableadapter.holder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qbw.expandableadapter.R;
import com.example.qbw.expandableadapter.entity.Child;
import com.qbw.recyclerview.base.BaseViewHolder;

/**
 * @author QBW
 * @createtime 2016/04/05 11:29
 */


public class ItemViewHolder extends BaseViewHolder<Child> {
    private TextView text;

    public ItemViewHolder(Context context, ViewGroup parent) {
        super(context, R.layout.holder_child, parent);
    }

    @Override
    public void findView() {
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @Override
    public void bindData(int adapPos, Child child) {
        text.setText(child.text);
    }
}
