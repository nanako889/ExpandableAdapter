package com.example.qbw.expandableadapter;

import android.app.Activity;
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
        //test1();
        //test2();
        //test3();
        //test4();
        //test5();
        testHeader();
        testFooter();
        testGroupChild();
        //testGroup();
    }

    private void testGroup() {
        adapter.addGroup(new Group("1"));
        adapter.addGroupChild(0, new GroupChild("1"));
        adapter.addGroup(new Group("2"));
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                adapter.removeGroup(0);
//                adapter.removeGroup(0);
//            }
//        }, 3000);
        adapter.addGroup(1, new Group("3"));
        adapter.addGroup(0, new Group("4"));
//        adapter.addGroupChild(0, new GroupChild("gp1"));
//        adapter.addGroupChild(0, new GroupChild("gp2"));
//        adapter.addGroupChild(0, new GroupChild("gp3"));
//        adapter.addGroupChild(0, new GroupChild("gp4"));
//        adapter.addGroupChild(0, new GroupChild("gp5"));
//        adapter.addGroupChild(0, new GroupChild("gp6"));
//        adapter.addGroupChild(0, new GroupChild("gp7"));
//        adapter.addGroupChild(0, new GroupChild("gp8"));
//        adapter.addGroupChild(0, new GroupChild("gp9"));
//        adapter.addGroupChild(0, new GroupChild("gp10"));
//        adapter.addGroupChild(0, new GroupChild("gp11"));
        adapter.addGroup(100, new Group("5"));
        adapter.addGroup(new Group("6"));
        adapter.addGroup(-1, new Group("7"));
    }

    private void testGroupChild() {
        adapter.addGroup(new Group("1"));
        final List<GroupChild> groupChildList = new ArrayList<>();
        for (int i=0;i<11;i++) {
            groupChildList.add(new GroupChild("gp"+(i+1)));
        }

        adapter.addGroup(new Group1("2"));
        adapter.addGroupChild(1, new GroupChild("gp1"));
        adapter.addGroupChild(1, new GroupChild("gp2"));
        adapter.addGroupChild(1, new GroupChild("gp3"));
        adapter.addGroupChild(1, new GroupChild("gp4"));
        adapter.addGroupChild(1, new GroupChild("gp5"));
        adapter.addGroupChild(1, new GroupChild("gp6"));
        adapter.addGroupChild(1, new GroupChild("gp7"));
        adapter.addGroupChild(1, new GroupChild("gp8"));
        adapter.addGroupChild(1, new GroupChild("gp9"));
        adapter.addGroupChild(1, new GroupChild("gp10"));
        adapter.addGroupChild(1, new GroupChild("gp11"));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<GroupChild> groupChildList1 = groupChildList.subList(0,8);
                adapter.addGroupChild(0, Arrays.asList(groupChildList1.toArray(new BaseEntity[groupChildList1.size()])));
                XLog.d("group 0 size = %d", adapter.getGroupChildCount(0));
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<GroupChild> groupChildList1 = groupChildList.subList(8,groupChildList.size());
                adapter.addGroupChild(0, Arrays.asList(groupChildList1.toArray(new BaseEntity[groupChildList1.size()])));
                XLog.d("group 0 size = %d", adapter.getGroupChildCount(0));
            }
        }, 6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<GroupChild> groupChildList1 = groupChildList.subList(8,groupChildList.size());
                //adapter.addGroupChild(0, Arrays.asList(groupChildList1.toArray(new BaseEntity[groupChildList1.size()])));
                adapter.clearGroupChild(0, 8);
                XLog.d("group 0 size = %d", adapter.getGroupChildCount(0));
            }
        }, 9000);
    }

    private void testHeader() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addHeader(new Header("header1"));
                adapter.addHeader(new Header("header2"));
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addHeader(2, new Header("header3"));
                adapter.addHeader(0, new Header("header4"));
            }
        }, 4000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Header> headerList = new ArrayList<Header>();
                headerList.add(new Header("header5"));
                headerList.add(new Header("header6"));
                headerList.add(new Header("header7"));
                adapter.addHeader(Arrays.asList(headerList.toArray(new BaseEntity[headerList.size()])));
            }
        }, 6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Header> headerList = new ArrayList<Header>();
                headerList.add(new Header("header8"));
                headerList.add(new Header("header9"));
                headerList.add(new Header("header10"));
                adapter.addHeader(1, Arrays.asList(headerList.toArray(new BaseEntity[headerList.size()])));

                headerList = new ArrayList<Header>();
                headerList.add(new Header("header a"));
                headerList.add(new Header("header b"));
                headerList.add(new Header("header c"));
                adapter.addHeader(6, Arrays.asList(headerList.toArray(new BaseEntity[headerList.size()])));
            }
        }, 8000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeHeader(0);
                adapter.removeHeader(1, 1);
                adapter.clearHeader(5);
            }
        }, 10000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //adapter.clearHeader();
            }
        }, 12000);
    }

    private void testFooter() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addFooter(new Footer("footer1"));
                adapter.addFooter(new Footer("footer2"));
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addFooter(2, new Footer("footer3"));
                adapter.addFooter(0, new Footer("footer4"));
            }
        }, 4000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Footer> footerList = new ArrayList<Footer>();
                footerList.add(new Footer("footer5"));
                footerList.add(new Footer("footer6"));
                footerList.add(new Footer("footer7"));
                adapter.addFooter(Arrays.asList(footerList.toArray(new BaseEntity[footerList.size()])));
            }
        }, 6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Footer> footerList = new ArrayList<Footer>();
                footerList.add(new Footer("footer8"));
                footerList.add(new Footer("footer9"));
                footerList.add(new Footer("footer10"));
                adapter.addFooter(1, Arrays.asList(footerList.toArray(new BaseEntity[footerList.size()])));

                footerList = new ArrayList<Footer>();
                footerList.add(new Footer("footer a"));
                footerList.add(new Footer("footer b"));
                footerList.add(new Footer("footer c"));
                adapter.addFooter(6, Arrays.asList(footerList.toArray(new BaseEntity[footerList.size()])));
            }
        }, 8000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeFooter(0);
                adapter.removeFooter(1, 1);
                adapter.clearFooter(5);
            }
        }, 10000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //adapter.clearFooter();
            }
        }, 12000);
    }

    private void test1() {
        int r = new Random(System.currentTimeMillis()).nextInt(50);
//        for (int i = 0; i < r; i++) {
//            adapter.addHeader(new Header("h" + i));
//        }

        adapter.addChild(new Child("c1"));
        adapter.addChild(new Child("c2"));

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
            adapter.addGroupChild(4, new GroupChild("gc" + i));
        }
//
//        r = new Random(System.currentTimeMillis()).nextInt(50);
//        adapter.addGroup(new Group("g6"));
//        for (int i = 0; i < r; i++) {
//            adapter.addGroupChild(3, new GroupChild("gc" + i));
//        }


//        r = new Random(System.currentTimeMillis()).nextInt(50);
//        for (int i = 0; i < r; i++) {
//            adapter.addFooter(new Footer("f" + i));
//        }
    }

    private void test2() {
        adapter.removeGroup(4);

        adapter.clearGroup(4);
    }

    private void test3() {
        Group group = (Group) adapter.getGroup(0);
        group.text = "update e";
        GroupChild groupChild = (GroupChild) adapter.getGroupChild(0, 0);
        groupChild.text = "update";
        //adapter.updateGroupChild(0, true);
        adapter.updateGroupChild(0);
    }

    private void test4() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeChild(0, 2);
            }
        }, 3000);
    }

    private void test5() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //adapter.removeGroupChild(0, 0, 100);
                //adapter.removeGroup(4);
                //adapter.removeGroupChild(0, 0);
                //adapter.clearGroupChild(0);
                adapter.clearGroupChild(0, 4);
            }
        }, 6000);
    }
}
