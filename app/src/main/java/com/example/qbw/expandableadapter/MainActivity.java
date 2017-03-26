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

import com.example.qbw.expandableadapter.entity.BaseEntity;
import com.example.qbw.expandableadapter.entity.Child;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.Group1;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.qbw.log.XLog;
import com.qbw.recyclerview.expandable.StickyLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private StickyLayout mStickyLayout;

    private TextView mTextView;

    private boolean l = true;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        XLog.setEnabled(true);//show log

        Context appCtx = getApplicationContext();//要养成好的习惯，除非需要Activity作为Context，否则能用ApplicationContext就尽量使用，减少对Activity的强引用

        mRecyclerView.setLayoutManager(new LinearLayoutManager(appCtx));
        mRecyclerView.setAdapter(mAdapter = new Adapter(appCtx));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int adapPos = parent.getChildAdapterPosition(view);
                if (Adapter.Type.GROUP1 == mAdapter.getItemViewType(adapPos)) {
                    outRect.left = 50;
                    outRect.right = 150;
                }
            }
        });

        mStickyLayout.init(true);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (l) {
                    // 如果你使用的是GridLayoutManager，那么Group必须是占有一整行，否则会报错
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this.getApplicationContext(),
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
        test();
        //test1();
        //test2();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.change_txt);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mStickyLayout = (StickyLayout) findViewById(R.id.stickylayout);
    }

    private void test2() {
        mAdapter.addGroup(new Group("g1"));
        mAdapter.addGroupChild(0, new GroupChild("gc"));
        mAdapter.addGroup(new Group("g2"));
        mAdapter.addGroupChild(1, new GroupChild("gc"));
        mAdapter.addGroup(new Group("g3"));
        mAdapter.addGroupChild(2, new GroupChild("gc"));
        mAdapter.addGroup(new Group("g4"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
        mAdapter.addGroupChild(3, new GroupChild("gc"));
    }

    private void test1() {
        for (int i = 0; i < 100; i++) {
            mAdapter.addGroup(new Group("group" + i));
            for (int j = 0; j < 100; j++) {
                mAdapter.addGroupChild(i, new GroupChild("groupchild" + j));
            }
        }
    }

    private void test() {
        mAdapter.addGroup(new Group("group0"));
        mAdapter.addGroupChild(0, new GroupChild("groupchild"));
        mAdapter.addGroup(new Group("group1"));
        for (int i = 0; i < 20; i++) {
            mAdapter.addGroupChild(1, new GroupChild("groupchild" + i));
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addGroup(1, new Group1("group insert"));
            }
        }, 3000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addGroupChild(0, new GroupChild("groupchild1"));

            }
        }, 5000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addGroup(0, new Group("group insert 2"));
                mAdapter.addGroupChild(0, new GroupChild("groupchild1"));
                mAdapter.addGroupChild(0, new GroupChild("groupchild2"));
                mAdapter.addGroupChild(0, new GroupChild("groupchild3"));
            }
        }, 8000);

        for (int i = 0; i < 10; i++) {
            mAdapter.addFooter(new Footer("footer" + i));
        }

        for (int i = 0; i < 4; i++) {
            mAdapter.addHeader(new Header("header" + i));
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    mAdapter.addChild(new Child("child" + i));


                }
                List<Child> groupChildren = new ArrayList<Child>();
                groupChildren.add(new Child("child-3"));
                groupChildren.add(new Child("child-3"));
                groupChildren.add(new Child("child-3"));
                mAdapter.addChild(3, Arrays.asList(groupChildren.toArray(new BaseEntity[groupChildren.size()])));

                groupChildren = new ArrayList<Child>();
                groupChildren.add(new Child("child-0"));
                groupChildren.add(new Child("child-0"));
                groupChildren.add(new Child("child-0"));
                mAdapter.addChild(0, Arrays.asList(groupChildren.toArray(new BaseEntity[groupChildren.size()])));
                mAdapter.addChild(-1,  new Child("err"));
                mAdapter.addChild(100, new Child("end"));
            }
        }, 10000);
    }
}
