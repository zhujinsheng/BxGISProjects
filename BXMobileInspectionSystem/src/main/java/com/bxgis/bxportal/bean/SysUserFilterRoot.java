package com.bxgis.bxportal.bean;

import com.wzx.filter.FilterGroup;
import com.wzx.filter.FilterNode;
import com.wzx.filter.FilterRoot;

import java.util.List;

/**
 * Created by wang_zx on 2015/1/7.
 */
public class SysUserFilterRoot extends FilterRoot {


    /**
     * @param data
     * @param isSingle true 为单选 ，false 为多选
     */
    public SysUserFilterRoot(List<SysOrganizationBean> data, boolean isSingle) {

        setDisplayName("选择用户");
        for (int i = 0; i < data.size(); i++) {
            FilterGroup group = new FilterGroup();
            group.setDisplayName(data.get(i).getOrgName());
            group.setCharacterCode(String.format("%d", i));
            List<UserBean> users = data.get(i).getSysOrg();
            if (isSingle) {
//                group.setSingleChoice();
                group.addMutexCode(String.format("%d",i));
                for (int j = 0; j < users.size(); j++) {
                    UserBean user = users.get(j);
                    FilterNode node = new FilterNode();
                    node.setData(user);
                    node.setDisplayName(user.getReal_name());
                    node.setCharacterCode(String.format("%d-%d", i, j + 1));
                    node.addMutexCode(String.format("%d-%d", i, j+1));
                    group.addNode(node);
                }
            } else {
                for (int j = 0; j < users.size(); j++) {
                    UserBean user = users.get(j);
                    FilterNode node = new FilterNode();
                    node.setData(user);
                    node.setDisplayName(user.getReal_name());
                    node.setCharacterCode(String.format("%d-%d", i, j + 1));
                    group.addNode(node);
                }
            }
            addNode(group);
        }

        resetFilterTree(true);
    }
}
