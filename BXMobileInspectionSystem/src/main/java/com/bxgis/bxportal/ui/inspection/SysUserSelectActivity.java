package com.bxgis.bxportal.ui.inspection;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.SysOrganizationBean;
import com.bxgis.bxportal.bean.SysUserFilterRoot;
import com.bxgis.bxportal.bean.UserBean;
import com.bxgis.bxportal.utils.WindowUtils;
import com.bxgis.bxportal.widget.filterView.FilterTreeView;
import com.wzx.filter.AllFilterNode;
import com.wzx.filter.FilterGroup;
import com.wzx.filter.FilterNode;
import com.wzx.filter.FilterRoot;
import com.wzx.filter.UnlimitedFilterNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SysUserSelectActivity extends BaseActivity implements View.OnClickListener,FilterTreeView.OnItemClickListener{
    @Bind(R.id.tv_back)
    TextView back;
    @Bind(R.id.user_title)
    TextView title;
    @Bind(R.id.tv_ok)
    TextView Ok;
    @Bind(R.id.filter_tree)
    FilterTreeView mFilterTreeView;
    SysUserFilterRoot mFilterRoot ;
    List<SysOrganizationBean> datas = new ArrayList<>();
    List<UserBean> datas2 = new ArrayList<>();
    boolean isSingle =false;

//
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
//        } else {
//            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
//        }
//        win.setAttributes(winParams);
//    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_project_select;
    }

    @Override
    protected void initView() {
        back.setOnClickListener(this);
        Ok.setOnClickListener(this);
        datas = (List<SysOrganizationBean>) getIntent().getSerializableExtra("SysUser");
        isSingle=getIntent().getBooleanExtra("isSingle",true);
        if(isSingle){
            title.setText("请选择巡检负责人");
        }else{
            title.setText("请选择巡检陪同人");
        }
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(layoutParams);
        initFilterTree();
    }

    private void initFilterTree(){
        mFilterTreeView.setOnItemClickListener(this);
        mFilterTreeView.setLazyLoader(mLazyLoader);
//        TestFilterRoot2 mFilterRoot =new TestFilterRoot2();
            mFilterRoot = new SysUserFilterRoot(datas,isSingle);
        bindViewConfig(mFilterRoot);
        mFilterTreeView.setFilterGroup(mFilterRoot);
    }
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
    List<FilterNode> mList;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_ok:
                if(null!=mFilterTreeView){
                    mList= mFilterRoot.getSelectedLeafNodes();

                    if(null!=mList&&mList.size()>0){
                        if(isSingle&&mList.size()>1){
                            Toast.makeText(mContext, "巡检负责人只能单选", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(int a=0;a<mList.size();a++){
                            datas2.add((UserBean) mList.get(a).getData());
                        }
                        Intent intent=new Intent();
                        intent.putExtra("SelectUser", (Serializable) datas2);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else{
                        Toast.makeText(mContext, "请先选择巡检人", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                mFilterTreeView.
                break;
        }
    }

    private FilterTreeView.LazyLoader mLazyLoader = new FilterTreeView.LazyLoader() {

        @Override
        public void lazyLoad(FilterTreeView treeView, FilterGroup group, int position, FilterTreeView.SubTreeLoaderListener listener) {
            new Thread(new OpenTreeTask(group, position, listener)).start();
        }
    };

    private class OpenTreeTask implements Runnable {
        private FilterGroup mFilterGroup;
        private int mPosition;
        FilterTreeView.SubTreeLoaderListener mSubTreeLoaderListener;

        public OpenTreeTask(FilterGroup group,
                            int position, FilterTreeView.SubTreeLoaderListener listener) {
            mFilterGroup = group;
            mPosition = position;
            mSubTreeLoaderListener = listener;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            final boolean result = mFilterGroup.open(null);
            if (result) {
                bindViewConfig(mFilterGroup);
            }
            if (!isFinishing()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (result) {
                            mSubTreeLoaderListener.onLoadSuccess(mFilterGroup, mPosition);
                        } else {
                            mSubTreeLoaderListener.onLoadFail(mFilterGroup, mPosition);
                        }

                    }
                });
            }

        }
    }

    protected void bindViewConfig(FilterGroup group) {
        FilterTreeView.TreeViewConfig config = new FilterTreeView.TreeViewConfig();
        group.setTag(config);

        List<FilterNode> children = group.getChildren(true);
        int childrenCount = children.size();
        boolean hasSetTreeNode = false;
        for (int i = 0; i < childrenCount; i++) {
            FilterNode child = children.get(i);
            if (!hasSetTreeNode && (child instanceof FilterGroup || i == childrenCount - 1)) {
                if (child.isLeaf()) {
                    config.mDividerColor = Color.parseColor("#dddddd");
                    config.mPadding =  WindowUtils.dip2px(5);
                    FilterGroup groupParent = (FilterGroup) group.getParent();
                    if (groupParent instanceof FilterRoot) {
                        config.mItemMinHeight = 80;
                    } else {
                        config.mItemMinHeight = 120;
                    }
                } else if (group instanceof FilterRoot) {
                    config.mIsRoot = true;
                    config.mDividerColor = Color.parseColor("#dddddd");
                    config.mWidthWeight = 0.28f;
                    config.mItemMinHeight = 120;
                } else {
                    config.mWidthWeight = 0.4f;
                    config.mItemMinHeight = 120;
                    config.mPadding = WindowUtils.dip2px(5);
                }
                hasSetTreeNode = true;
            }
            if (child instanceof FilterGroup) {
                FilterGroup childGroup = (FilterGroup) child;
                bindViewConfig(childGroup);
            }
        }
    }

    @Override
    public void onLeafItemClick(FilterTreeView treeView, View view, FilterGroup parent, FilterNode node, int position) {
        if (node.isSelected()
                && (node instanceof UnlimitedFilterNode || node instanceof AllFilterNode)) {
            return;
        }
        if (!node.isSelected() || !parent.isSingleChoice()) {
            node.requestSelect(!node.isSelected());
            mFilterTreeView.refresh();
        }
    }

    @Override
    public void onGroupItemClick(FilterTreeView treeView, View view, FilterGroup parent, FilterGroup group, int position) {
        Object tag = group.getTag();
        if (!(tag instanceof FilterTreeView.TreeViewConfig)) {
            bindViewConfig(group);
        }
        treeView.openSubTree(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSingle=false;
    }
}
