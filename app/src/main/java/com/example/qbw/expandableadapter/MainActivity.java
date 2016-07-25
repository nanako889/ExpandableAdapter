package com.example.qbw.expandableadapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.qbw.expandableadapter.entity.BaseEntity;
import com.example.qbw.expandableadapter.entity.Child;
import com.example.qbw.expandableadapter.entity.Footer;
import com.example.qbw.expandableadapter.entity.Group;
import com.example.qbw.expandableadapter.entity.GroupChild;
import com.example.qbw.expandableadapter.entity.Header;
import com.qbw.log.XLog;
import com.qbw.recyclerview.expandable.StickyLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements TestDialog.Listener {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    private Adapter adapter;

    @Bind(R.id.stickylayout)
    StickyLayout stickyLayout;

    private TestDialog testDialog;

    private Handler handler = new Handler();

    private boolean testUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        XLog.setDebug(true);//show log
        //XLog.setFilterTag("expandableadapter");
        XLog.setSaveToFile("expandableadapter");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new Adapter(this));

        stickyLayout.init(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        onAddHeader();
//        onAddHeader();
//        onAddHeader();
        onAddChild();
        onAddChild();
        onAddChild();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onAddChildList();
            }
        }, 3000);

        onAddGroup();
        onAddGroupChild(0);
        onAddGroupChild(0);
        onAddGroupChild(0);
        onAddGroup();
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroupChild(1);
        onAddGroup();
        onAddGroupChild(2);
        onAddGroupChild(2);
        onAddGroupChild(2);
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
        onAddFooter();
    }

    @OnClick(R.id.testTxt)
    void onTest() {
        testDialog = new TestDialog(this, this);
        testDialog.show();
    }

    @Override
    public void onAddHeader() {
        adapter.addHeader(new Header("header" + adapter.getHeaderCount()));

        if (!testUpdate) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Header header = (Header) adapter.getHeader(adapter.getHeaderCount() - 1);
                header.text = "header";
                adapter.updateHeader(adapter.getHeaderCount() - 1, header);
            }
        }, 1000);
    }

    @Override
    public void onRemoveHeader() {
        adapter.removeHeader(adapter.getHeaderCount() - 1);
    }

    @Override
    public void onAddChild() {
        adapter.addChild(new Child("child" + adapter.getChildCount()));
        if (!testUpdate) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Child child = (Child) adapter.getChild(adapter.getChildCount() - 1);
                child.text = "child";
                adapter.updateChild(adapter.getChildCount() - 1, child);
            }
        }, 1000);
    }

    public void onAddChildList() {
        List<BaseEntity> childList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            childList.add(new Child("child list " + i));
        }
        adapter.addChild(childList);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeChild(0, 3);
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clearChild();
            }
        }, 6000);
    }

    @Override
    public void onRemoveChild(int pos) {
        adapter.removeChild(pos);
    }

    @Override
    public void onAddGroup() {
        adapter.addGroup(new Group("group" + adapter.getGroupCount()));
        if (!testUpdate) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Group group = (Group) adapter.getGroup(adapter.getGroupCount() - 1);
                group.text = "group";
                adapter.updateGroup(adapter.getGroupCount() - 1, group);
            }
        }, 1000);
    }

    @Override
    public void onRemoveGroup(int pos) {
        adapter.removeGroup(pos);
    }

    @Override
    public void onAddGroupChild(final int gpos) {
        adapter.addGroupChild(gpos, new GroupChild("group" + gpos + " child" + adapter.getGroupChildCount(gpos)));
        if (!testUpdate) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GroupChild groupChild = (GroupChild) adapter.getGroupChild(gpos, adapter.getGroupChildCount(gpos) - 1);
                groupChild.text = "groupChild";
                adapter.updateGroupChild(gpos, adapter.getGroupChildCount(gpos) - 1, groupChild);
            }
        }, 1000);
    }

    @Override
    public void onRemoveGroupChild(int gpos, int cpos) {
        adapter.removeGroupChild(gpos, cpos);
    }

    @Override
    public void onAddFooter() {
        adapter.addFooter(new Footer("footer" + adapter.getFooterCount()));
        if (!testUpdate) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Footer footer = (Footer) adapter.getFooter(adapter.getFooterCount() - 1);
                footer.text = "footer";
                adapter.updateFooter(adapter.getFooterCount() - 1, footer);
            }
        }, 1000);
    }

    @Override
    public void onRemoveFooter() {
        adapter.removeFooter(adapter.getFooterCount() - 1);
    }

    @Override
    public void onRemoveItem(int pos) {
        adapter.removeItem(pos);
    }
}
