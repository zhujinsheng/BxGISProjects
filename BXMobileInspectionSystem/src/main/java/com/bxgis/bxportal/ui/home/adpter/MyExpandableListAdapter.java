package com.bxgis.bxportal.ui.home.adpter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;


import com.bxgis.bxportal.R;
import com.bxgis.bxportal.widget.ExpandableViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by haoyan on 2016/4/28.
 */
public abstract class MyExpandableListAdapter<T>implements ExpandableListAdapter {
    private Context mcontext;
    private List<T> mexpand_dtos;
    private Map<Integer, List<T>> mmap = null;
    public MyExpandableListAdapter(Context context, List<T> expand_dtos,Map<Integer, List<T>> map) {
        this.mcontext = context;
        this.mexpand_dtos = expand_dtos;
        this.mmap=map;
    }
    /*注册一个观察者(observer)，当此适配器数据修改时即调用此观察者。*/
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }
    /*取消先前通过registerDataSetObserver(DataSetObserver)方式注册进该适配器中的观察者对象*/
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }
    /*获取组的个数*/
    @Override
    public int getGroupCount() {
        return mexpand_dtos.size();
    }
    /*返回在指定Group的Child数目。*/
    @Override
    public int getChildrenCount(int groupPosition) {
        return mmap.get(groupPosition).size();
    }
    //获取当前父item的数据
    @Override
    public T getGroup(int groupPosition) {
        return mexpand_dtos.get(groupPosition);
    }
    /*获取与在指定group给予child相关的数据。*/
    @Override
    public T getChild(int groupPosition, int childPosition) {
        T t =(mmap.get(groupPosition).get(childPosition));
        return t;
    }
    /*获取指定组的ID*/
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    //得到子item的ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    //组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。(没效果)
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //设置父item组件
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandableViewHolder viewHolder = ExpandableViewHolder.get(mcontext, convertView, parent, R.layout.expand_groupview, groupPosition,true);
        convert(viewHolder, getGroup(groupPosition),groupPosition);
        return ExpandableViewHolder.getmConvertView();
    }
    //自己写
    public abstract void convert(ExpandableViewHolder viewHolder, T t,int groupPosition);

    public abstract void convertchild(ExpandableViewHolder viewHolder, T t,int childPosition );
    //设置子item的组件
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ExpandableViewHolder viewHolder = ExpandableViewHolder.get(mcontext, convertView, parent, R.layout.expand_childview, groupPosition,isLastChild);
        convertchild(viewHolder,getChild(groupPosition,childPosition),childPosition);
        return ExpandableViewHolder.getmConvertView();
        /*子元素是否处于组中的最后一个(对每个组的最后一个进行操作)*/
        //boolean isLastChild
        /*下边是未封装代码，提供参考*/
//        String Childname = (String)(mmap.get(groupPosition).get(childPosition));
//        LayoutInflater inflater = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        convertView = inflater.inflate(R.layout.expand_childview, null);
//        TextView tv_child = (TextView) convertView.findViewById(R.id.tv_child);
//        tv_child.setText(Childcont);
    }
    /*是否选中指定位置上的子元素。*/
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    /*true所有条目可以选择和点击*/
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    /*如果当前适配器不包含任何数据则返回True。经常用来决定一个空视图是否应该被显示。
    一个典型的实现将返回表达式getCount() == 0的结果，但是由于getCount()包含了头部和尾部，适配器可能需要不同的行为。*/
    @Override
    public boolean isEmpty() {
        return false;
    }
    /*当组展开状态的时候此方法被调用。*/
    @Override
    public void onGroupExpanded(int groupPosition) {

    }
    /*当组收缩状态的时候此方法被调用。*/
    @Override
    public void onGroupCollapsed(int groupPosition) {

    }
    /*根据所给的子ID号和组ID号返回唯一的ID。此外，若hasStableIds()是true，那么必须要返回稳定的ID。*/
    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }
    /*同上*/
    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}
