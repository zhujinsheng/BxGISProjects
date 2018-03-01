package com.bxgis.bxportal.ui.home.adpter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    Map<Integer, List<BaseInspectionSubProject>> dataset = new HashMap<Integer, List<BaseInspectionSubProject>>();
    List<BaseInspectionSubProject> data;
    Context mContext;

    public ExpandableListViewAdapter(Context context,List<BaseInspectionSubProject> data1, Map<Integer, List<BaseInspectionSubProject>> data) {
        this.dataset = data;
        this.data=data1;
        this.mContext = context;
    }

    //  获得某个父项的某个子项
    @Override
    public Object getChild(int parentPos, int childPos) {
        return dataset.get(parentPos).get(childPos);
    }

    //  获得父项的数量
    @Override
    public int getGroupCount() {
        if (dataset == null) {
//            Toast.makeText(ExpandableListViewTestActivity.this, "dataset为空", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return dataset.size();
    }

    //  获得某个父项的子项数目
    @Override
    public int getChildrenCount(int parentPos) {
        if (dataset.get(parentPos) == null) {
//            Toast.makeText(ExpandableListViewTestActivity.this, "\" + parentList[parentPos] + \" + 数据为空", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return dataset.get(parentPos).size();
    }

    //  获得某个父项
    @Override
    public Object getGroup(int parentPos) {
        return dataset.get(parentPos);
    }

    //  获得某个父项的id
    @Override
    public long getGroupId(int parentPos) {
        return parentPos;
    }

    //  获得某个父项的某个子项的id
    @Override
    public long getChildId(int parentPos, int childPos) {
        return childPos;
    }

    //  按函数的名字来理解应该是是否具有稳定的id，这个函数目前一直都是返回false，没有去改动过
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //  获得父项显示的view
    @Override
    public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expand_groupview, viewGroup,false);
        }
        view.setTag(R.layout.expand_groupview, parentPos);
        view.setTag(R.layout.expand_childview, -1);
        TextView text = (TextView) view.findViewById(R.id.tv_project_name1);
        text.setText(parentPos+1+"、"+data.get(parentPos).getProject_name());
        return view;
    }

    //  获得子项显示的view
    @Override
    public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expand_childview, viewGroup,false);
        }
        view.setTag(R.layout.expand_groupview, parentPos);
        view.setTag(R.layout.expand_childview, childPos);
        TextView text1 = (TextView) view.findViewById(R.id.tv_child);
        TextView text2 = (TextView) view.findViewById(R.id.tv_child2);
        String subProjectName =dataset.get(parentPos).get(childPos).getSubproject_name();
        String subComent =dataset.get(parentPos).get(childPos).getInspection_content();
        if(TextUtils.isEmpty(subComent)){
            text2.setVisibility(View.GONE);
        }
        text1.setText(subProjectName);
        text2.setText(subComent);
//        text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(ExpandableListViewTestActivity.this, "点到了内置的textview",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }

    //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
