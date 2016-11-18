# expandableadapter



继承自 RecyclerView.Adapter，支持header，child, group, groupchild, footer 分组,并且支持group悬浮效果(Sticky效果).



![image](https://github.com/qbaowei/ExpandableAdapter/raw/master/screenshots/ExpandableAdapter.gif)


#使用说明


1.需要Group悬浮,将RecyclerView包裹在'StickyLayout'里面,并且调用'stickylayout'的init函数传入参数true(true,表示group悬浮,false表示不悬浮)


    <com.qbw.recyclerview.expandable.StickyLayout
        android:id="@+id/stickylayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </com.qbw.recyclerview.expandable.StickyLayout>


2.不需要Group悬浮,可直接使用'RecyclerView'


    <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />



# Download


Gradle:


compile 'com.qbw.recyclerview:expandableadapter:2.8.1'


# v2.8.1


1.修复Group没有child时无法删除Group


# v2.8.0


1.增加clearChild(beginPos)


# v2.7.1


1.clearGroupChild,bug修复


# Author:


qbaowei@qq.com

