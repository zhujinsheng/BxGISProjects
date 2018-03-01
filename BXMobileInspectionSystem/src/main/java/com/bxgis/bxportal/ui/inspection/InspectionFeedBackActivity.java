package com.bxgis.bxportal.ui.inspection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.bxgis.bxportal.bean.UserBean;
import com.bxgis.bxportal.event.UpdateEvent;
import com.bxgis.bxportal.myInterface.IMakePic;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.myInterface.OnImageUploadListener;
import com.bxgis.bxportal.ui.home.adpter.AccessoryAdapter;
import com.bxgis.bxportal.ui.home.adpter.ExpandableListViewAdapter;
import com.bxgis.bxportal.ui.home.adpter.MakePicAdapter;
import com.bxgis.bxportal.ui.home.adpter.ShowDetailProjectAdapter;
import com.bxgis.bxportal.ui.inspection.contract.InspectionFeedBackContract;
import com.bxgis.bxportal.ui.inspection.model.InspectionFeedBackModel;
import com.bxgis.bxportal.ui.inspection.presenter.InspectionFeedBackPresenter;
import com.bxgis.bxportal.utils.BitMapUtils;
import com.bxgis.bxportal.utils.FileProviderUtils;
import com.bxgis.bxportal.utils.ImageLoader;
import com.bxgis.bxportal.utils.LeFileDirUtil;
import com.bxgis.bxportal.utils.OpenFileUtil;
import com.bxgis.bxportal.utils.PermissionUtils;
import com.bxgis.bxportal.utils.SPUtils;
import com.bxgis.bxportal.utils.StringUtil;
import com.bxgis.bxportal.utils.SystemBarTintManager;
import com.bxgis.bxportal.utils.date.TimestampUtils;
import com.bxgis.bxportal.widget.Constatnt;
import com.bxgis.bxportal.widget.CustomDatePicker;
import com.bxgis.bxportal.widget.CustomEditText;
import com.bxgis.bxportal.widget.NestedExpandaleListView;
import com.bxgis.bxportal.widget.SignatureView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class InspectionFeedBackActivity extends BaseActivity<InspectionFeedBackPresenter> implements InspectionFeedBackContract.View, View.OnClickListener, IMakePic {
    @Bind(R.id.inspection_end_time)
    TextView endTime;
    @Bind(R.id.inspection_begin_time)
    TextView startTime;
    @Bind(R.id.rv_feedback_accessory)
    RecyclerView mRecyclerView;

    @Bind(R.id.rv_addphoto)
    RecyclerView requestRecycle;
    @Bind(R.id.el_content)
    NestedExpandaleListView mExpandableListViewContent;
    @Bind(R.id.back_detail)
    ImageView backDetail;
    @Bind(R.id.release)
    Button release;
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
    @Bind(R.id.feedback_other_requests)
    CustomEditText feedBackBZ;
    @Bind(R.id.signature_view)
    SignatureView mSignatureView;
    @Bind(R.id.sig_clean)
    TextView btnClean;
    @Bind(R.id.sig_retract)
    TextView retract;
    @Bind(R.id.inspection_location)
    TextView location;
//    @Bind(R.id.v_review_line)
//    View reviewLine; //整改点击位置对于的隔线
    @Bind(R.id.rl_review)
    RelativeLayout rlReview;

    @Bind(R.id.ll_other1)
    LinearLayout llContent1;//反馈的附件内容
    @Bind(R.id.ll_image)
    LinearLayout llSignature;
    @Bind(R.id.iv_show_signature)
    ImageView showSignature;
    //判断是否缺少一组权限（读写，拍照），
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 2; //多个权限请求Code

    private PromptDialog promptDialog;  //自定义对话框
    String inspectorId; //负责人ID
    SpinnerAdapter mSpinnerAdapter = null;
    MakePicAdapter mMakePicAdapter;
    ShowDetailProjectAdapter mShowSubProjectAdapter;
    private CustomDatePicker startDatePicker, endDatePicker;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    List<String> images = new ArrayList<>();


    List<String> requestPath = new ArrayList<>(); //保存巡检要求的附件
    List<UserBean> userList = new ArrayList<>();
    List<BaseInspectionSubProject> selectSubProjects = new ArrayList<>(); //选中的事项
    List<BaseInspectionSubProject> projects = new ArrayList<>(); //去除重复的项目
    private HashMap<Integer, List<BaseInspectionSubProject>> map = new HashMap<>();
    BaseInspection mBaseInspection;
    AccessoryAdapter requestAccessAdapter;
    private int site = 2;  //0为进行中， 1为已完成页面, 2为整改复查入口

    @Override
    public int getLayoutId() {
        return R.layout.activity_inspection_feedback;
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
        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        mBaseInspection = (BaseInspection) getIntent().getSerializableExtra("BaseInspection");

        site = getIntent().getIntExtra("InspectionSite", 3);
        if (site == 3) {
            rlReview.setVisibility(View.VISIBLE);
//            reviewLine.setVisibility(View.VISIBLE);
        }
//        mPresenter.getSubProjects(mInspectionProjectBean.getId());

        backDetail.setOnClickListener(this);
        release.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        retract.setOnClickListener(this);


        //图片列表
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.HORIZONTAL));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.VERTICAL));
        mMakePicAdapter = new MakePicAdapter(this, images, this, Constatnt.EDIT);
        mRecyclerView.setAdapter(mMakePicAdapter);


        //请求附件列表
        requestRecycle.setLayoutManager(new LinearLayoutManager(mContext));
        requestRecycle.setHasFixedSize(true);
        requestAccessAdapter = new AccessoryAdapter(mContext, requestPath);
        requestRecycle.setAdapter(requestAccessAdapter);
        requestAccessAdapter.setMyOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                promptDialog.showLoading("正在打开中...");
                mPresenter.downFile(requestPath.get(position));
            }
        });
        mPresenter.getReviewInspectionById(mBaseInspection.getId());
        initDate();

//        final Bitmap backgroudBitmap = Bitmap.createBitmap(WindowUtils.getScreenWidth(mContext) - WindowUtils.dip2px(22), WindowUtils.dip2px(160), Bitmap.Config.ARGB_8888);
//        backgroudBitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充颜色

    }


    // 普通申请多个权限
    private void requestMorePermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            PermissionUtils.checkAndRequestMorePermissions(mContext, PERMISSIONS, REQUEST_CODE_PERMISSIONS,
                    new PermissionUtils.PermissionRequestSuccessCallBack() {
                        @Override
                        public void onHasPermission() {
                            // 权限已被授予
                            initPhoto();
                        }
                    });
        } else {
            initPhoto();
        }
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
        String inspection_accessory = mBaseInspection.getAccessory();
        if (StringUtil.isNotEmpty(inspection_accessory, true)) {
            String[] files = inspection_accessory.split("#");
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String path1 = files[i];
                    if (path1.contains("gisserver")) {
                        path1 = (Api.BASE + path1.split(":")[2]);
                    }
                    requestPath.add(path1);
                }
                requestAccessAdapter.setData(requestPath);
            }
        }
        SharedPreferences sp = MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        String token5 = sp.getString("TOKEN1", "");
        if (mBaseInspection.getType() == 2) { //现场巡检
            llContent1.setVisibility(View.GONE);
            llSignature.setVisibility(View.VISIBLE);
            release.setVisibility(View.GONE);  //若是现场巡检 则政务端只进行查看 不能编辑
            String signPath = mBaseInspection.getPicture_signature();
            if (!TextUtils.isEmpty(signPath)) {
                if (signPath.contains("gisserver")) {
                    signPath = (Api.BASE + signPath.split(":")[2]);
                }
                ImageLoader.showImage(mContext, signPath + "?_sso_token=" + token5, showSignature);
            }
        } else {
            llContent1.setVisibility(View.VISIBLE);
            llSignature.setVisibility(View.GONE);
            release.setVisibility(View.VISIBLE);
            mSignatureView.post(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSignatureView.setBackgroud(null);
                        }
                    });
                }
            });
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
        location.setText(mBaseInspection.getCompany_name());
        otherRequests.setText(mBaseInspection.getOther_requests());
    }

    private void initPhoto() {
        //可创建android效果的底部Sheet选择，默认IOS效果，sheetCellPad=0为Android效果的Sheet
//                promptDialog.getAlertDefaultBuilder().sheetCellPad(0).round(0);
        //设置按钮的特点，颜色大小什么的，具体看PromptButton的成员变量
        PromptButton cancle = new PromptButton("取消", null);
        PromptButton tPhoto = new PromptButton("拍照", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                openCamera();
            }
        });
        PromptButton picture = new PromptButton("相册", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                opentPicture();
            }
        });

        cancle.setTextColor(Color.parseColor("#0076ff"));
        //设置显示的文字大小及颜色
//                promptDialog.getAlertDefaultBuilder().textSize(12).textColor(Color.GRAY);
        //默认两个按钮为Alert对话框，大于三个按钮的为底部SHeet形式展现
        promptDialog.showAlertSheet("上传图片", true, cancle, tPhoto, picture);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_detail:
                finish();
                break;
            case R.id.release:
                initCommitDate();
                break;
            case R.id.sig_clean: //签名清空
                if (null != mSignatureView)
                    mSignatureView.clearDrawinglBoard();
                break;
            case R.id.sig_retract: //签名笔画撤回
                if (null != mSignatureView)
                    mSignatureView.cancelPath();
                break;
        }
    }

    private void initCommitDate() {
        final List<File> files = new ArrayList<>();
        if (null != images && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                files.add(new File(images.get(i)));
            }
        }
        if (!mSignatureView.isSignature()) {
            Toast.makeText(mContext, "巡检人签名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap signatureBitmap = mSignatureView.getDrawingBitmap();
        final File signImag = new File(MISystemApplication.mainPath + "signature.png");

        if (signImag.exists()) {
            signImag.delete();
        }

        final BaseInspection temB = new BaseInspection();
        BitMapUtils.bitmap2File(signatureBitmap, signImag);
        signatureBitmap.recycle();
        String userId = SPUtils.get(mContext, "user_id", "").toString();
        if (!TextUtils.isEmpty(feedBackBZ.getText())) {
            temB.setInspection_feedback(feedBackBZ.getText().toString());
        }
        temB.setInspection_type(1);  //巡检完成
        temB.setId(mBaseInspection.getId());
        promptDialog.showWarnAlert("确定提交巡检反馈？", new PromptButton("否", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                promptDialog.dismiss();
            }
        }), new PromptButton("提交", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                promptDialog.showLoading("正在提交...", true);
                mPresenter.commit(temB, files, signImag);
            }
        }, true));


    }

    /*
     * 从相册获取
    */
    public void opentPicture() {
        if (hasSdcard()) {
            tempFile = new File(MISystemApplication.mainPath,
                    createPhotoName());
        } else {
            Toast.makeText(InspectionFeedBackActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    private File tempFile;//拍照后的图片保存地址
    private static final int PHOTO_REQUEST_CAREMA = 111;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 222;// 从相册中选择
    private static final int PROJECT_REQUEST = 11;// 从子项目获取
    private static final int USER_REQUEST = 12;// 从子项目获取一个用户
    private static final int USERS_REQUEST = 13;// 从子项目获取多个用户

    /*
     * 从相机获取
     */
    public void openCamera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(MISystemApplication.mainPath,
                    createPhotoName());
            // 从文件中创建uri
//            Uri uri = Uri.fromFile(tempFile);
            //已适配android7.0
            Uri  uriCamera = FileProviderUtils.getUriForFile(mContext,tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera);
        } else {
            Toast.makeText(InspectionFeedBackActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    public String createPhotoName() {
        //以系统的当前时间给图片命名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = format.format(date) + ".jpg";
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_CAREMA && resultCode == RESULT_OK) { //拍照
//            LogUtil.d("小朱拍照后路径" + tempFile.getAbsoluteFile());
            BitMapUtils.saveBitmapToFile(tempFile, tempFile.getAbsolutePath());
            images.add(tempFile.getAbsolutePath());
            mMakePicAdapter.setDataList(images);

        } else if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {//相册
            Uri uri = data.getData();
            String path1 = LeFileDirUtil.getPath(this, uri);
            BitMapUtils.saveBitmapToFile(new File(path1), tempFile.getAbsolutePath());//通过文件路径进行压缩
            images.add(path1);
            mMakePicAdapter.setDataList(images);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_PERMISSIONS:
         PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        initPhoto();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        Toast.makeText(mContext, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        promptDialog.showWarnAlert(getResources().getString(R.string.permission_camera_hint), new PromptButton("取消", null), new PromptButton("去设置", new PromptButtonListener() {
                            @Override
                            public void onClick(PromptButton button) {
                                PermissionUtils.toAppSetting(mContext);
                            }
                        }));
                    }
                });


        }
    }

    @Override
    protected InspectionFeedBackPresenter initPresenter() {
        return new InspectionFeedBackPresenter(this, new InspectionFeedBackModel());
    }


    @Override
    public void showBaseSubProject(List<BaseInspectionSubProject> bis) {
        selectSubProjects = bis;
        initProjectData(selectSubProjects);

    }

    //初始化事项数据
    private void initProjectData(List<BaseInspectionSubProject> list) {
        selectSubProjects = list;
        List<BaseInspectionSubProject> temps = new ArrayList<>();
        temps.addAll(list);
        projects = removeDuplicate(temps);

        for (int i = 0; i < projects.size(); i++) {
            List<BaseInspectionSubProject> Blist = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if (projects.get(i).getProject_name().equalsIgnoreCase(list.get(j).getProject_name())) {
                    Blist.add(list.get(j));
                }
            }
            map.put(i, Blist);
        }
        mExpandableListViewContent.setAdapter(new ExpandableListViewAdapter(mContext, projects, map));
//遍历所有group,将所有项设置成默认展开
        int groupCount = mExpandableListViewContent.getCount();
        for (int i = 0; i < groupCount; i++) {
            mExpandableListViewContent.expandGroup(i);
        }
        //去掉自带箭头
        mExpandableListViewContent.setGroupIndicator(null);
        mExpandableListViewContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSucceed() {
        EventBus.getDefault().post(new UpdateEvent("0")); //通知完成列表进行刷新
        promptDialog.showSuccess("巡检反馈成功！", true);
        finish();

    }

    @Override
    public void downSucceed(String path) {
        startActivity(OpenFileUtil.openFile(path));
        promptDialog.dismiss();
    }

    @Override
    public void onFaild(String msg) {
        promptDialog.dismiss();
        promptDialog.showError(msg, true);
    }

    @Override
    public void openFileFaild(String msg) {
        promptDialog.dismiss();
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 图片处理
     *
     * @param onImageUploadListener
     */
    @Override
    public void setPicUploadListener(OnImageUploadListener onImageUploadListener) {

    }

    @Override
    public void takePic(int position) {
        requestMorePermissions();
    }

    @Override
    public void delPic(String url) {

    }


}
