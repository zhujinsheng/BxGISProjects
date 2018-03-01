package com.bxgis.bxportal.ui.inspection;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.myInterface.IMakePic;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.myInterface.OnImageUploadListener;
import com.bxgis.bxportal.ui.home.adpter.AccessoryAdapter;
import com.bxgis.bxportal.ui.home.adpter.ExpandableListViewAdapter;
import com.bxgis.bxportal.ui.home.adpter.ShowDetailProjectAdapter;
import com.bxgis.bxportal.ui.inspection.contract.InspectionDetailContract;
import com.bxgis.bxportal.ui.inspection.model.InspectionDetailModel;
import com.bxgis.bxportal.ui.inspection.presenter.InspectionDetailPresenter;
import com.bxgis.bxportal.utils.ImageLoader;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.utils.OpenFileUtil;
import com.bxgis.bxportal.utils.StringUtil;
import com.bxgis.bxportal.utils.SystemBarTintManager;
import com.bxgis.bxportal.utils.date.TimestampUtils;
import com.bxgis.bxportal.widget.CustomDatePicker;
import com.bxgis.bxportal.widget.NestedExpandaleListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptDialog;

public class InspectionDetailActivity extends BaseActivity<InspectionDetailPresenter> implements InspectionDetailContract.View, IMakePic {
    @Bind(R.id.inspection_end_time)
    TextView endTime;
    @Bind(R.id.inspection_begin_time)
    TextView startTime;
    @Bind(R.id.rv_addphoto)
    RecyclerView mRecyclerView;
    @Bind(R.id.el_content)
    NestedExpandaleListView mRecyclerViewContent; //事项列表
    //    @Bind(R.id.back_detail)
    ImageView backDetail;

    @Bind(R.id.et_inspection_name)
    TextView inspectionName;
    @Bind(R.id.tv_inspector)
    TextView inspector;
    @Bind(R.id.tv_other_inspectors)
    TextView otherInspector;
    @Bind(R.id.tv_other_requests)
    TextView otherRequests;
    @Bind(R.id.tv_review)
    TextView btnReview;

    @Bind(R.id.inspection_location)
    TextView companySite; //巡检地点
    @Bind(R.id.feedback_other_requests)
    TextView inspection_feedback;  //巡检结果内容反馈
    @Bind(R.id.rv_feedback_accessory)
    RecyclerView feedbackRV; //附件反馈列表
    @Bind(R.id.detail_title)
    TextView title; //巡检地点
    @Bind(R.id.iv_signature)
    ImageView showSignature; //展示签名

    @Bind(R.id.ll_image)
    LinearLayout llSignature;

    ShowDetailProjectAdapter mShowSubProjectAdapter;
    private CustomDatePicker startDatePicker, endDatePicker;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    List<String> images = new ArrayList<>();

    List<String> feedBackPath = new ArrayList<>(); //保存反馈的附件
    List<String> requestPath = new ArrayList<>(); //保存巡检要求的附件
    List<BaseInspectionSubProject> subProjects = new ArrayList<>(); //选中的事项
    List<BaseInspectionSubProject> projects = new ArrayList<>(); //去除重复的项目
    private HashMap<Integer, List<BaseInspectionSubProject>> map = new HashMap<>();

    InspectionProjectBean mInspectionProjectBean;

    String companySelect = "";
    BaseInspection mBaseInspection;
    AccessoryAdapter mAccessoryAdapter;
    AccessoryAdapter requestAccessAdapter;
    private PromptDialog promptDialog;  //自定义对话框
    private int site = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_inspection_detail;
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {            //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.btn_blue_login);
        Class clazz = this.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (true) {
                extraFlagField.invoke(this.getWindow(), darkModeFlag, 0);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(this.getWindow(), 0, darkModeFlag);//清除黑色字体
            }
        } catch (Exception e) {

        }
        site = getIntent().getIntExtra("InspectionSite", 0);
        mBaseInspection = (BaseInspection) getIntent().getSerializableExtra("BaseInspection");
//创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        //反馈附件列表
        feedbackRV.setHasFixedSize(true);
        feedbackRV.setLayoutManager(new LinearLayoutManager(mContext));
        mAccessoryAdapter = new AccessoryAdapter(mContext, feedBackPath);
        feedbackRV.setAdapter(mAccessoryAdapter);

        //请求附件列表
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        requestAccessAdapter = new AccessoryAdapter(mContext, requestPath);
        mRecyclerView.setAdapter(requestAccessAdapter);

        mPresenter.getReviewInspectionById(mBaseInspection.getId()); //获取巡检类型清单
        initDate();
        backDetail = (ImageView) findViewById(R.id.back_detail);
        backDetail.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                finish();
            }
        });
        requestAccessAdapter.setMyOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                promptDialog.showLoading("正在打开中...");
                mPresenter.downFile(requestPath.get(position));
            }
        });

        mAccessoryAdapter.setMyOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(mContext, "反馈" + position, Toast.LENGTH_SHORT).show();
                promptDialog.showLoading("正在打开中...");
                mPresenter.downFile(feedBackPath.get(position));
            }
        });
    }

    //初始化事项数据          //显示事项的列表
    private void initProjectData(List<BaseInspectionSubProject> list) {
        subProjects = list;
        List<BaseInspectionSubProject> tmp =new ArrayList<>();
        tmp.addAll(list);
        projects = removeDuplicate(tmp);

        for (int i = 0; i < projects.size(); i++) {
            List<BaseInspectionSubProject> Blist = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if (projects.get(i).getProject_name().equalsIgnoreCase(list.get(j).getProject_name())) {
                    Blist.add(list.get(j));
                }
            }
            map.put(i, Blist);
        }
        mRecyclerViewContent.setAdapter(new ExpandableListViewAdapter(mContext, projects, map));
    //遍历所有group,将所有项设置成默认展开
        int groupCount = mRecyclerViewContent.getCount();
        for (int i = 0; i < groupCount; i++) {
            mRecyclerViewContent.expandGroup(i);
        }
        //去掉自带箭头
        mRecyclerViewContent.setGroupIndicator(null);
    }

    //去除重复的类
    public List<BaseInspectionSubProject> removeDuplicate(List<BaseInspectionSubProject> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getProject_name().equalsIgnoreCase(list.get(i).getProject_name())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    private void initDate() {
        String feedback_accessory = mBaseInspection.getFeedback_accessory();
        String inspection_accessory = mBaseInspection.getAccessory();
        if (StringUtil.isNotEmpty(feedback_accessory, true)) {
            String[] files = feedback_accessory.split("#");
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String path1 = files[i];
                    if (path1.contains("gisserver")) {
                        path1 = (Api.BASE + path1.split(":")[2]);
                    }
                    feedBackPath.add(path1);
                }
                mAccessoryAdapter.setData(feedBackPath);
            } else {
                feedbackRV.setVisibility(View.GONE);
            }
        }
        if (StringUtil.isNotEmpty(inspection_accessory, true)) {
            String[] files = inspection_accessory.split("#");
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String path = files[i];
                    if (path.contains("gisserver")) {
                        path = (Api.BASE + path.split(":")[2]);
                    }
                    requestPath.add(path);
                }
                requestAccessAdapter.setData(requestPath);
            }
        }

        if (!StringUtil.isNotEmpty(mBaseInspection, true)) {
            return;
        }
        inspectionName.setText(mBaseInspection.getInspection_name());
        inspector.setText(mBaseInspection.getInspector());
        otherInspector.setText(mBaseInspection.getOther_inspectors());
        String time1 = mBaseInspection.getBegin_time();
        String time2 = mBaseInspection.getEnd_time();
        if (!TextUtils.isEmpty(time1)) {
            startTime.setText(TimestampUtils.timestamp2String(Long.parseLong(time1), "yyyy-MM-dd"));
        }
        if (!TextUtils.isEmpty(time2)) {
            endTime.setText(TimestampUtils.timestamp2String(Long.parseLong(time2), "yyyy-MM-dd"));
        }
        companySite.setText(mBaseInspection.getCompany_name());
        inspection_feedback.setText(mBaseInspection.getInspection_feedback());
        otherRequests.setText(mBaseInspection.getOther_requests());
        String imageUrl = mBaseInspection.getPicture_signature();
        SharedPreferences sp = MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        String token5 =  sp.getString("TOKEN1","");
        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.contains("gisserver")) {
                imageUrl = (Api.BASE + imageUrl.split(":")[2]);
            }
            llSignature.setVisibility(View.VISIBLE);
            ImageLoader.showImage(mContext, imageUrl+"?_sso_token="+token5, showSignature);
//            Glide.with(mContext).load("http://gisserver3:18080/static/files/59996803-7f7a-4f64-8035-fc0a0b365ae6.jpg").crossFade().listener(mRequestListener).into(showSignature);
        } else {
            llSignature.setVisibility(View.GONE);

        }

    }

    RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            Log.d(TAG, "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
            showSignature.setImageResource(R.mipmap.ic_launcher);
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e(TAG, "model:" + model + " isFirstResource: " + isFirstResource);
            return false;
        }

    };

    @Override
    protected InspectionDetailPresenter initPresenter() {
        return new InspectionDetailPresenter(this, new InspectionDetailModel());
    }


    @Override
    public void showDetail(List<BaseInspectionSubProject> baseInspections) {
        initProjectData(baseInspections);
        mRecyclerViewContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSucceed() {
        Toast.makeText(mContext, "巡检提交完成", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 文件下载后打开
     *
     * @param path
     */
    @Override
    public void downSucceed(String path) {
       startActivity(OpenFileUtil.openFile(path));
        promptDialog.dismiss();
    }

    @Override
    public void onFaild(String msg) {

    }

    @Override
    public void openFileFaild(String msg) {
        promptDialog.dismiss();
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPicUploadListener(OnImageUploadListener onImageUploadListener) {

    }

    @Override
    public void takePic(int position) {

    }

    @Override
    public void delPic(String url) {

    }
}
