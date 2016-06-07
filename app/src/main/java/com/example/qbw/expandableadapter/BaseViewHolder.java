package com.example.qbw.expandableadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bond on 2016/4/2.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private Context context;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        findView();
    }

    public BaseViewHolder(Context context, int itemViewResId, ViewGroup parent) {
        this(context, LayoutInflater.from(context).inflate(itemViewResId, parent, false));
    }

    public abstract void findView();

    public abstract void bindData(int adapPos, T t);

    public Context getContext() {
        return context;
    }
}
