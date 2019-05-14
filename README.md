# expandableadapter



继承自 RecyclerView.Adapter，支持header，child, group, groupchild, footer 分组,并且支持group悬浮效果(Sticky效果).



![image](https://github.com/qbaowei/ExpandableAdapter/raw/master/screenshots/ExpandableAdapter.gif)


#使用说明


1.需要Group悬浮,将RecyclerView包裹在'StickyLayout'里面,并且调用'stickylayout'的init函数传入参数true(true,表示group悬浮,false表示不悬浮)，同时Adapter要实现StickyListener


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

compile 'com.qbw.recyclerview:expandableadapter:4.0.4'

# V4.0.4

1.Fix bug

# V4.0.3

1.faster

# V4.0.2

1.Fix some bug

# V4.0

1.Rewrite some logic to make it faster

# V3.6.2

1.增加设置header，child，group，footer的viewType显示顺序的函数，处理那些页面并发插入多条数据，但是又需要保持一定的顺序

# V3.5.2

1.swapItem bug fix

# V3.5.0

1.增加函数swapItem（使用RecyclerView 的 ItemTouchHelper 时会用到）

# V3.4.0

1.增加几个常用函数

# V3.3.2

1.修复bug

# V3.3.0

1.增加一些常用的函数

# V3.2.1

1.修复updateGroup导致getGroupChildCount空指针问题

# v3.2.0

1.构造函数参数去掉Context（如果需要，添加到自己的Adapter基类中）

2.添加回调函数返回StickyGroupViewHolder的水平margin值

3.优化convertXXXPosition函数

# v3.1.0

1.将StickyLayout需要的回调抽离成独立的一个StickyListener

2.优化getXXXPosition函数

# v3.0.0

1.重写group悬浮逻辑

2.修复v2.9.0bug

# v2.10.0

1.addGroup(int, T)增加返回值，返回实际group插入的位置


# v2.9.0

1.增加addGroup(int, T)将group插入指定的位置

已知影响：addGroup(int, T)会导致group悬浮有问题


# v2.8.1


1.修复Group没有child时无法删除Group


# v2.8.0


1.增加clearChild(beginPos)


# v2.7.1


1.clearGroupChild,bug修复


# Author:


qbaowei@foxmail.com

