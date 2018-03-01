package com.bxgis.bxportal.bean;

import com.wzx.filter.FilterGroup;
import com.wzx.filter.FilterNode;
import com.wzx.filter.FilterRoot;

import java.util.List;

/**
 * Created by wang_zx on 2015/1/7.
 */
public class ProjectFilterTreeRoot extends FilterRoot {


    public ProjectFilterTreeRoot(List <InspectionSubProjectBean> subProjects) {
//        List<bean> list = new ArrayList<>();


//        for (int a = 0; a < 9; a++) {
//            bean   mBean = new bean();
//            mBean.setName("现场巡检"+a);
//            mBean.setCode(a);
//            List<Persion> list2 = new ArrayList<>();
//            for (int b = 0; b < 6; b++) {
//                Persion   mPersion = new Persion();
//                mPersion.setPhone("测试测试测试测试测试测试测试测试");
//                mPersion.setContent("rrrrrrrrrrrrrrrrwerw"+a+b);
//                list2.add(mPersion);
//            }
//            mBean.setList(list2);
//            list.add(mBean);
//        }
        setDisplayName("安全检查");
        for (int i = 0; i < subProjects.size(); i++) {
            FilterGroup group = new FilterGroup();
            group.setDisplayName(subProjects.get(i).getSubproject_name());
            group.setCharacterCode(String.valueOf(i));
//            group.setData(subProjects.get(i));
            FilterNode node = new FilterNode();
            node.setDisplayName(subProjects.get(i).getInspection_content());
            node.setCharacterCode(String.format("%d-%d", i,1));
            node.setData(subProjects.get(i));
            group.addNode(node);
//            group.setSingleChoice();
//            for (int j = 0; j < list.get(i).getList().size(); j++) {
//                Persion p = list.get(i).getList().get(j);
//                FilterNode node = new FilterNode();
//                node.setDisplayName(p.content);
//                node.setCharacterCode(String.format("0-%d", j + 1));
//                node.setCharacterCode(String.format("%d-%d", i, j + 1));
//                group.addNode(node);
//            }
            addNode(group);
        }
        resetFilterTree(true);
    }
}
