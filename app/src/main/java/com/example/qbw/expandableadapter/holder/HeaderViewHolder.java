package com.example.qbw.expandableadapter.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qbw.expandableadapter.R;
import com.example.qbw.expandableadapter.entity.Header;
import com.qbw.recyclerview.base.BaseViewHolder;

/**
 * @author QBW
 * @createtime 2016/04/05 11:29
 */


public class HeaderViewHolder extends BaseViewHolder<Header> {
    private TextView text;

    public HeaderViewHolder(Context context, ViewGroup parent) {
        super(context, R.layout.holder_header, parent);
    }

    @Override
    public void findView() {
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @Override
    public void bindData(int adapPos, Header header) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        text.setText(header.text);
    }
}
