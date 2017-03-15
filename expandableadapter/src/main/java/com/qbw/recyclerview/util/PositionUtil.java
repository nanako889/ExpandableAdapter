package com.qbw.recyclerview.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qbw.log.XLog;

/**
 * @author qbw
 * @createtime 2016/04/22 13:48
 */


public class PositionUtil {

    public static int findFirstCompletelyVisibleItemPosition(RecyclerView recyclerView) {
        int pos = RecyclerView.NO_POSITION;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            pos = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else {
            if (XLog.isEnabled()) XLog.w("请知悉:暂时只支持LinearLayoutManager 垂直方向!");
        }
        return pos;
    }

    /**
     * 因为考虑到Group必须是一整行，所以不会对GridLayoutManager返回多个position做处理
     */

    public static int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        int pos = RecyclerView.NO_POSITION;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            pos = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else {
            if (XLog.isEnabled()) XLog.w("请知悉:暂时只支持LinearLayoutManager 垂直方向!");
        }
        return pos;
    }
}
