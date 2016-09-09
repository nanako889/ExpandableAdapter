package com.example.qbw.expandableadapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.Group1;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.qbw.log.XLog;
import com.qbw.recyclerview.expandable.StickyLayout;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    private Adapter adapter;

    @Bind(R.id.stickylayout)
    StickyLayout stickyLayout;

    @Bind(R.id.change_txt)
    TextView mTextView;

    private Handler handler = new Handler();

    private boolean l = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        XLog.setDebug(true);//show log


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new Adapter(this));

        stickyLayout.init(true);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (l) {
                    // 如果你使用的是GridLayoutManager，那么Group必须是占有一整行，否则会报错
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            int type = adapter.getItemViewType(position);
                            return Adapter.Type.GROUP1 == type || Adapter.Type.GROUP == type ? 3 : 1;
                        }
                    });
                    recyclerView.setLayoutManager(gridLayoutManager);
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
                l = !l;
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        test1();
        test2();
        test3();
    }

    private void test1() {
        int r = new Random(System.currentTimeMillis()).nextInt(50);
        for (int i = 0; i < r; i++) {
            adapter.addHeader(new Header("h" + i));
        }

        adapter.addGroup(new Group("g1"));
        r = new Random(System.currentTimeMillis()).nextInt(50);
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(0, new GroupChild("gc" + i));
        }

        r = new Random(System.currentTimeMillis()).nextInt(50);
        adapter.addGroup(new Group1("g2"));
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(1, new GroupChild("gc" + i));
        }

        r = new Random(System.currentTimeMillis()).nextInt(50);
        adapter.addGroup(new Group("g3"));
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(2, new GroupChild("gc" + i));
        }

        r = new Random(System.currentTimeMillis()).nextInt(50);
        adapter.addGroup(new Group1("g4"));
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(3, new GroupChild("gc" + i));
        }

        r = new Random(System.currentTimeMillis()).nextInt(50);
        adapter.addGroup(new Group("g5"));
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(3, new GroupChild("gc" + i));
        }

        r = new Random(System.currentTimeMillis()).nextInt(50);
        adapter.addGroup(new Group("g6"));
        for (int i = 0; i < r; i++) {
            adapter.addGroupChild(3, new GroupChild("gc" + i));
        }


        r = new Random(System.currentTimeMillis()).nextInt(50);
        for (int i = 0; i < r; i++) {
            adapter.addFooter(new Footer("f" + i));
        }
    }

    private void test2() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               adapter.removeGroup(4);
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clearGroup(4);
            }
        }, 5000);
    }

    private void test3() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Group group = (Group) adapter.getGroup(0);
                group.text = "update e";
                GroupChild groupChild = (GroupChild) adapter.getGroupChild(0, 0);
                groupChild.text = "update";
                //adapter.updateGroupChild(0, true);
                adapter.updateGroupChild(0);
            }
        }, 3000);
    }
}
