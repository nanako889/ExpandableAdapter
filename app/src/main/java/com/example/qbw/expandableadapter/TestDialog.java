package com.example.qbw.expandableadapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Bond on 2016/4/9.
 */
public class TestDialog extends Dialog {

    @Bind(R.id.childEt)
    EditText childEt;
    @Bind(R.id.groupEt)
    EditText groupEt;
    @Bind(R.id.groupChildGEt)
    EditText groupchildGEt;
    @Bind(R.id.groupChildCEt)
    EditText groupchildCEt;
    @Bind(R.id.item_et)
    EditText itemEt;


    private Listener listener;

    public TestDialog(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_header_btn)
    void addHeader() {
        listener.onAddHeader();
    }

    @OnClick(R.id.remove_header_btn)
    void removeHeader() {
        listener.onRemoveHeader();
    }

    @OnClick(R.id.add_child_btn)
    void addChild() {
        listener.onAddChild();
    }

    @OnClick(R.id.remove_child_btn)
    void removeChild() {
        listener.onRemoveChild(Integer.parseInt(childEt.getText().toString()));
    }

    @OnClick(R.id.add_group_btn)
    void addGroup() {
        listener.onAddGroup();
    }

    @OnClick(R.id.remove_group_btn)
    void removeGroup() {
        listener.onRemoveGroup(Integer.parseInt(groupEt.getText().toString()));
    }

    @OnClick(R.id.add_group_child_btn)
    void addGroupChild() {
        listener.onAddGroupChild(Integer.parseInt(groupchildGEt.getText().toString()));
    }

    @OnClick(R.id.remove_group_child_btn)
    void removeGroupChild() {
        listener.onRemoveGroupChild(Integer.parseInt(groupchildGEt.getText().toString()), Integer.parseInt(groupchildCEt.getText().toString()));
    }

    @OnClick(R.id.add_footer_btn)
    void addFooter() {
        listener.onAddFooter();
    }

    @OnClick(R.id.remove_footer_btn)
    void removeFooter() {
        listener.onRemoveFooter();
    }

    @OnClick(R.id.remove_item_btn)
    void removeItem() {
        listener.onRemoveItem(Integer.parseInt(itemEt.getText().toString()));
    }

    public interface Listener {
        void onAddHeader();

        void onRemoveHeader();

        void onAddChild();

        void onRemoveChild(int pos);

        void onAddGroup();

        void onRemoveGroup(int pos);

        void onAddGroupChild(int gpos);

        void onRemoveGroupChild(int gpos, int cpos);

        void onAddFooter();

        void onRemoveFooter();

        void onRemoveItem(int pos);
    }
}
