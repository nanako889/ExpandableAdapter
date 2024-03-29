package com.example.qbw.expandableadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.qbw.expandableadapter.entity.Child;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.example.qbw.expandableadapter.entity.Header1;
import com.qbw.l.L;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private TextView mTextView;

    private boolean l = true;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        L.GL.setEnabled(true);//show log
        Context appCtx = getApplicationContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(appCtx));
        mRecyclerView.setAdapter(mAdapter = new Adapter(appCtx));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect,
                                       View view,
                                       RecyclerView parent,
                                       RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int adapPos = parent.getChildAdapterPosition(view);
                if (RecyclerView.NO_POSITION == adapPos) {
                    return;
                }
                if (Adapter.Type.GROUP1 == mAdapter.getItemViewType(adapPos)) {
                    outRect.left = 50;
                    outRect.right = 150;
                } else if (Adapter.Type.GROUP_CHILD == mAdapter.getItemViewType(adapPos)) {
                    int[] gcp = mAdapter.getGroupChildPosition(adapPos);
                    L.GL.d("item position:%d, pos:%s", adapPos, Arrays.toString(gcp));
                    if (gcp[1] % 2 == 0) {
                        outRect.left = 150;
                    }
                } else if (Adapter.Type.HEADER == mAdapter.getItemViewType(adapPos)) {
                    int hp = mAdapter.getHeaderPosition(adapPos);
                    if (hp % 2 == 0) {
                        outRect.left = 150;
                    }
                } else if (Adapter.Type.CHILD == mAdapter.getItemViewType(adapPos)) {
                    int hp = mAdapter.getChildPosition(adapPos);
                    if (hp % 2 == 0) {
                        outRect.left = 150;
                    }
                } else if (Adapter.Type.FOOTER == mAdapter.getItemViewType(adapPos)) {
                    int hp = mAdapter.getFooterPosition(adapPos);
                    if (hp % 2 == 0) {
                        outRect.left = 150;
                    }
                }
            }
        });
        //InnerItemTouchHelper touchHelper = new InnerItemTouchHelper(new InnerItemTouchCallback());
        //touchHelper.attachToRecyclerView(mRecyclerView);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (l) {
                    GridLayoutManager gridLayoutManager =
                            new GridLayoutManager(MainActivity.this.getApplicationContext(),
                                    3);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            int type = mAdapter.getItemViewType(position);
                            return Adapter.Type.GROUP1 == type || Adapter.Type.GROUP == type ? 3 : 1;
                        }
                    });
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                } else {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this.getApplicationContext()));
                }
                l = !l;
            }

        });

        //test();
        testDiff();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.change_txt);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    private void testDiff() {
        mAdapter.addHeader(new Header("h1"));
        mAdapter.addHeader(new Header("h2"));
        mAdapter.addChild(new Child("c1"));
        mAdapter.addChild(new Child("c2"));
        mAdapter.addGroup(new Group("group"));
        mAdapter.addGroupChild(0, new GroupChild("gc1"));
        mAdapter.addFooter(new Footer("f1"));
        mAdapter.addFooter(new Footer("f2"));
        mAdapter.setBottom(new Footer("bottom"));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Header> headerList = new ArrayList<>();
                headerList.add(new Header("update h1"));
                headerList.add(new Header("h2"));
                headerList.add(new Header("update h3"));
                headerList.add(new Header("update h4"));
                mAdapter.setHeader(headerList);
                List<Child> children = new ArrayList<>();
                children.add(new Child("update c1"));
                children.add(new Child("c2"));
                children.add(new Child("update c3"));
                children.add(new Child("update c4"));
                mAdapter.setChild(children);
                List<Footer> footerList = new ArrayList<>();
                footerList.add(new Footer("update f1"));
                footerList.add(new Footer("f2"));
                mAdapter.setFooter(footerList);
                List<GroupChild> groupChildren = new ArrayList<>();
                groupChildren.add(new GroupChild("update gc1"));
                mAdapter.setGroupChild(0, groupChildren);
                mAdapter.removeBottom();
            }
        }, 2000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Header> headerList = new ArrayList<>();
                headerList.add(new Header("uh11"));
                mAdapter.setHeader(headerList);
                List<Child> children = new ArrayList<>();
                children.add(new Child("c1"));
                children.add(new Child("c2"));
                children.add(new Child("c3"));
                children.add(new Child("c4"));
                mAdapter.setChild(children);
                List<Footer> footerList = new ArrayList<>();
                footerList.add(new Footer("uf1"));
                footerList.add(new Footer("uf2"));
                footerList.add(new Footer("uf3"));
                mAdapter.setFooter(footerList);
                List<GroupChild> groupChildren = new ArrayList<>();
                groupChildren.add(new GroupChild("update gc1"));
                groupChildren.add(new GroupChild("gc1"));
                mAdapter.setGroupChild(0, groupChildren);
                mAdapter.setBottom(new Footer("botoom back"));

            }
        }, 10000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearFooter();
                mAdapter.setBottom(new Footer("botoom 111 back"));
            }
        }, 15000);
    }

    private void test() {
        L.GL.d(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss").format(System.currentTimeMillis()));
        for (int i = 0; i < 5; i++) {
            mAdapter.addHeader(new Header("header " + i));
        }
        for (int i = 0; i < 5; i++) {
            mAdapter.addChild(new Child("child " + i));
        }

        for (int i = 0; i < 100; i++) {
            int groupPos = mAdapter.addGroup(new Group("group " + i));
            for (int j = 0; j < 100; j++) {
                mAdapter.addGroupChild(groupPos, new GroupChild("groupchild " + i + "," + j));
            }
        }

        for (int i = 0; i < 5; i++) {
            mAdapter.addFooter(new Footer("footer " + i));
        }

        mAdapter.addHeader(0, new Header("random header 0"));
        mAdapter.addHeader(2, new Header("random header 2"));
        mAdapter.addHeader(mAdapter.getHeaderCount(), new Header("random header last"));

        mAdapter.addChild(0, new Child("random child 0"));
        mAdapter.addChild(2, new Child("random child 2"));
        mAdapter.addChild(mAdapter.getChildCount(), new Child("random child last"));

        int gp;
        gp = mAdapter.addGroup(0, new Group("random group 0"));
        for (int i = 0; i < 2; i++) {
            mAdapter.addGroupChild(gp, new GroupChild("random group 0, " + i));
        }
        gp = mAdapter.addGroup(3, new Group("random group 3"));
        for (int i = 0; i < 10; i++) {
            mAdapter.addGroupChild(gp, new GroupChild("random group 3, " + i));
        }
        gp = mAdapter.addGroup(mAdapter.getGroupCount(), new Group("random group last"));
        for (int i = 0; i < 5; i++) {
            mAdapter.addGroupChild(gp, new GroupChild("random group last, " + i));
        }

        mAdapter.addFooter(0, new Footer("random footer 0"));
        mAdapter.addFooter(1, new Footer("random footer 1"));
        mAdapter.addFooter(new Footer("random footer last"));
        L.GL.d(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss").format(System.currentTimeMillis()));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearChild(5);
            }
        }, 300);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.removeFooter(5, mAdapter.getFooterCount() - 6);
            }
        }, 320);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearHeader(5);
            }
        }, 310);
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.removeAllGroup();
                mAdapter.clearHeader();
                mAdapter.clearChild();
                mAdapter.clearFooter();
            }
        }, 2500);*/
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearHeader(5);
                mAdapter.clearChild(5);
                mAdapter.removeAllGroup();
                mAdapter.clearFooter(5);
                mAdapter.addHeader(new Header1("header1 test"));
                L.GL.d("last header position by viewType:%d",
                        mAdapter.getLastHeaderPositionByViewType(Adapter.Type.HEADER1));
            }
        }, 15500);
    }

    /*private class InnerItemTouchHelper extends ItemTouchHelper {

     *//**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     *//*
        public InnerItemTouchHelper(Callback callback) {
            super(callback);
        }
    }

    private class InnerItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0;
            int flags = makeMovementFlags(dragFlags, swipeFlags);
            return flags;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
        target) {
            mAdapter.swapItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }*/
}
