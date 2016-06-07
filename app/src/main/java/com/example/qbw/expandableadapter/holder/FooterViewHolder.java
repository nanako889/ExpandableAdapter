package com.example.qbw.expandableadapter.holder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qbw.expandableadapter.R;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.BaseViewHolder;

/**
 * @author QBW
 * @createtime 2016/04/05 11:29
 */


public class FooterViewHolder extends BaseViewHolder<Footer> {
    private TextView text;

    public FooterViewHolder(Context context, ViewGroup parent) {
        super(context, R.layout.holder_footer, parent);
    }

    @Override
    public void findView() {
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @Override
    public void bindData(int adapPos, Footer footer) {
        text.setText(footer.text);
    }
}
