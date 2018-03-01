package com.bxgis.bxportal.ui.inspection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.CompanyBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.bean.InspectionSubProjectBean;
import com.bxgis.bxportal.bean.SysOrganizationBean;
import com.bxgis.bxportal.bean.UserBean;
import com.bxgis.bxportal.myInterface.IMakePic;
import com.bxgis.bxportal.myInterface.OnImageUploadListener;
import com.bxgis.bxportal.ui.home.adpter.MakePicAdapter;
import com.bxgis.bxportal.ui.home.adpter.ShowSubProjectAdapter;
import com.bxgis.bxportal.ui.inspection.contract.NowInspectionContract;
import com.bxgis.bxportal.ui.inspection.model.NowInspectionModel;
import com.bxgis.bxportal.ui.inspection.presenter.NowInspectionPresenter;
import com.bxgis.bxportal.utils.BitMapUtils;
import com.bxgis.bxportal.utils.FileProviderUtils;
import com.bxgis.bxportal.utils.LeFileDirUtil;
import com.bxgis.bxportal.utils.PermissionUtils;
import com.bxgis.bxportal.utils.SPUtils;
import com.bxgis.bxportal.utils.SystemBarTintManager;
import com.bxgis.bxportal.widget.Constatnt;
import com.bxgis.bxportal.widget.CustomDatePicker;
import com.bxgis.bxportal.widget.CustomEditText;
import com.bxgis.bxportal.widget.SignatureView;
import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class NowInspecrionRelaseActivity extends BaseActivity<NowInspectionPresenter> implements NowInspectionContract.View, View.OnClickListener, IMakePic {
    @Bind(R.id.inspection_end_time)
    TextView endTime;
    @Bind(R.id.inspection_begin_time)
    TextView startTime;
    @Bind(R.id.rv_addphoto)
    RecyclerView mRecyclerView;
    @Bind(R.id.rv_content)
    RecyclerView mRecyclerViewContent; //事项列表

    @Bind(R.id.iv_back)
    ImageView back;
    @Bind(R.id.release)
    Button release;
    @Bind(R.id.et_inspection_name)
    CustomEditText inspectionName;
    @Bind(R.id.tv_inspector)
    TextView inspector;  //默认是当前的用户
    @Bind(R.id.tv_other_inspectors)
    CustomEditText otherInspector; //暂时改为手动输入
    @Bind(R.id.et_other_requests)
    CustomEditText otherRequests;
    Spinner companySpinner;
    @Bind(R.id.detail_title)
    TextView title;

    @Bind(R.id.signature_view)
    SignatureView mSignatureView;
    @Bind(R.id.sig_clean)
    TextView btnClean;
    @Bind(R.id.sig_retract)
    TextView retract;

    @Bind(R.id.tv_sub_project)
    TextView subProjct;
    private PromptDialog promptDialog;  //自定义对话框

    String inspectorId; //负责人ID
    private String time1 = "";
    private String time2 = "";
    private String inspectionProject = "";//项目类型
    SpinnerAdapter mSpinnerAdapter = null;
    MakePicAdapter mMakePicAdapter;
    ShowSubProjectAdapter mShowSubProjectAdapter;
    private CustomDatePicker startDatePicker, endDatePicker;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    List<String> images = new ArrayList<>();

    List<InspectionSubProjectBean> subProjects = new ArrayList<>();
    //    List<String> companyList = new ArrayList<>();
    List<CompanyBean> companyList = new ArrayList<>();
    List<UserBean> userList = new ArrayList<>();
    List<SysOrganizationBean> mSysOrganizations = new ArrayList<>();
    List<InspectionSubProjectBean> selectSubProjects = new ArrayList<>(); //选中的事项
    //    List<InspectionSubProjectBean> notRepeat  = new ArrayList<>(); //去除重复项
    InspectionProjectBean mInspectionProjectBean;
    List<File> files = new ArrayList<>();
    BaseInspection temB = new BaseInspection();
    String companySelect = ""; //位置-公司名称
    String companyCode = ""; //位置-公司编码
    String companyId = ""; //位置-公司Id
    //权限（读写，拍照），
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 3; //多个权限请求Code
    Uri uriCamera;
    @Override
    public int getLayoutId() {
        return R.layout.activity_now_inspecrion_relase;
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

        mInspectionProjectBean = (InspectionProjectBean) getIntent().getSerializableExtra("InspectionProject");
//        promptDialog.showLoading("正在加载", true);
        mPresenter.getSubProjects(mInspectionProjectBean.getId());
        // mPresenter.getOrgUsers(); //暂时不用获取所有的用户
        mPresenter.getOrgCompany();
        inspectionProject = mInspectionProjectBean.getProject_name();
        title.setText(inspectionProject);
        initDatePicker();
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        back.setOnClickListener(this);
        subProjct.setOnClickListener(this);
        release.setOnClickListener(this);
//        inspector.setOnClickListener(this);
        otherInspector.setOnClickListener(this);
        companySpinner = (Spinner) findViewById(R.id.inspection_location);
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
        String readName = SPUtils.get(mContext, "real_name", "").toString();
        inspector.setText(readName);
        //显示事项的列表
        mRecyclerViewContent.setHasFixedSize(true);
//        mRecyclerViewContent.addItemDecoration(new RecycleViewDivider(mContext,LinearLayout.HORIZONTAL));
        mRecyclerViewContent.setLayoutManager(new LinearLayoutManager(mContext));
        mShowSubProjectAdapter = new ShowSubProjectAdapter(mContext, selectSubProjects);
        mRecyclerViewContent.setAdapter(mShowSubProjectAdapter);

        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //i指的是点击的位置,通过position可以取到相应的数据源
                companySelect = companyList.get(position).getCo_name();
                companyCode = companyList.get(position).getCo_code();
                companyId = companyList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        }else{
            initPhoto();
        }
    }

    private void initPhoto() {
        final String[] items = {"拍照", "从相册选择"};
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

    /**
     * Callback received when a permissions request has been completed.
     */
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
                        Toast.makeText(mContext, "需开启相机权限，否则会影响到相机拍照", Toast.LENGTH_SHORT).show();
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

    /**
     * 初始化日期选择器
     */
    private void initDatePicker() {
        String now = sdf.format(new Date());
        startDatePicker = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                startTime.setText(time.split(" ")[0]);
                time1 = time + ":00";
            }
        }, now, "2099-01-01 00:00:00");
        startDatePicker.showSpecificTime(false); // 不显示时和分
        startDatePicker.setIsLoop(false); // 不允许循环滚动
        endDatePicker = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                endTime.setText(time.split(" ")[0]);
                time2 = time + ":00";
            }
        }, now, "2099-01-01 00:00:00");
        endDatePicker.showSpecificTime(false); // 不显示时和分
        startDatePicker.setIsLoop(false); // 不允许循环滚动
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inspection_begin_time:
                String time1 = startTime.getText().toString();
                String temp = "";
                if (null != time1 && !time1.equals("")) {
                    temp = time1;
                } else {
                    temp = sdf.format(new Date());
                }
                startDatePicker.show(temp);
                break;
            case R.id.inspection_end_time:
                String time2 = endTime.getText().toString();
                String temp1 = "";
                if (null != time2 && !time2.equals("")) {
                    temp1 = time2;
                } else {
                    temp1 = sdf.format(new Date());
                }
                endDatePicker.show(temp1);
                break;
            case R.id.tv_sub_project:
                Intent intent = new Intent(NowInspecrionRelaseActivity.this, ProjectSelectActivity.class);
                intent.putExtra("PROJECTlIST", (Serializable) subProjects);
                startActivityForResult(intent, 11);
                break;
            case R.id.iv_back:
                finish();
                break;
//            case R.id.tv_inspector:
//                Intent intent2 = new Intent(NowInspecrionRelaseActivity.this, SysUserSelectActivity.class);
//                intent2.putExtra("SysUser", (Serializable) mSysOrganizations);
//                intent2.putExtra("isSingle", true);
//                startActivityForResult(intent2, USER_REQUEST);
//                break;
//            case R.id.tv_other_inspectors:
//                Intent intent3 = new Intent(NowInspecrionRelaseActivity.this, SysUserSelectActivity.class);
//                intent3.putExtra("SysUser", (Serializable) mSysOrganizations);
//                intent3.putExtra("isSingle", false);
//                startActivityForResult(intent3, USERS_REQUEST);
//                break;
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

    /**
     * 根据需要处理返回键，这里主要针对Alert和Sheet的对话框的返回处理
     */
    @Override
    public void onBackPressed() {
        if (promptDialog.onBackPressed())
            super.onBackPressed();
    }

    private void initCommitDate() {

        if (null != images && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                files.add(new File(images.get(i)));
            }
        }
        String userId = SPUtils.get(mContext, "user_id", "").toString();
        String userRealName = SPUtils.get(mContext, "real_name", "").toString();
        if (TextUtils.isEmpty(inspectionName.getText())) {
            Toast.makeText(mContext, "巡检主题名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(inspector.getText())) {
            Toast.makeText(mContext, "现场巡检负责人不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(startTime.getText())) {
            Toast.makeText(mContext, "开始日期不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(endTime.getText())) {
            Toast.makeText(mContext, "截至日期不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyId)) {
            Toast.makeText(mContext, "巡检位置不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == selectSubProjects && selectSubProjects.size() <= 0) {
            Toast.makeText(mContext, "巡检事项清单不能为空", Toast.LENGTH_SHORT).show();
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
        BitMapUtils.bitmap2File(signatureBitmap, signImag);
        signatureBitmap.recycle();

        //Date或者String转化为时间戳
//        SimpleDateFormat format = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");

//        temB.setBegin_time(DateUtils.string2Date(startTime.getText().toString(), "yyyy-MM-dd HH:mm:ss").getTime());
//        temB.setEnd_time(DateUtils.string2Date(endTime.getText().toString(), "yyyy-MM-dd HH:mm:ss").getTime());
        temB.setBegin_time(time1);
        temB.setEnd_time(time2);
        temB.setUser_id(userId);
        temB.setInspection_name(inspectionName.getText().toString());
        temB.setInspector(inspector.getText().toString());
        temB.setInspector_id(userId);
        temB.setInitiator(userRealName);
        temB.setInitiator_id(userId);
        temB.setCompany_id(companyId);
        temB.setOther_inspectors(otherInspector.getText().toString());
        temB.setLocation(companySelect);
        temB.setCompany_name(companySelect);
        temB.setType(2);
        temB.setInspection_project(inspectionProject);
        StringBuffer sb = new StringBuffer();
        sb.append(new Gson().toJson(selectSubProjects));
        temB.setProject_content(sb.toString()); //巡检清单内容

        if (!TextUtils.isEmpty(otherRequests.getText().toString())) {
            temB.setOther_requests(otherRequests.getText().toString());
        }
        temB.setOther_inspectors(otherInspector.getText().toString());
        promptDialog.showLoading("正在发布", true);
        List<File> fileP = new ArrayList<File>();
        fileP.add(signImag);
        mPresenter.commitFile2(fileP, 0);
//        mPresenter.commit(temB, files, signImag);
    }

    /*
     * 从相册获取
    */
    public void opentPicture() {
        if (hasSdcard()) {
            tempFile = new File(MISystemApplication.mainPath,
                    createPhotoName());
        } else {
            Toast.makeText(NowInspecrionRelaseActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
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
            //已适配android7.0
          uriCamera = FileProviderUtils.getUriForFile(mContext,tempFile);
            // 从文件中创建uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera);
        } else {
            Toast.makeText(NowInspecrionRelaseActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
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

    //去除重复的类
    public List<InspectionSubProjectBean> removeDuplicate(List<InspectionSubProjectBean> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getProject_name().equalsIgnoreCase(list.get(i).getProject_name())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_CAREMA && resultCode == RESULT_OK) { //拍照
//            LogUtil.d("小朱拍照后路径" + tempFile.getAbsoluteFile());
            BitMapUtils.saveBitmapToFile(tempFile, uriCamera.getPath());
            images.add(tempFile.getAbsolutePath());
            mMakePicAdapter.setDataList(images);

        } else if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {//相册
            Uri uri = data.getData();
            String path1 = LeFileDirUtil.getPath(this, uri);
            BitMapUtils.saveBitmapToFile(new File(path1), tempFile.getAbsolutePath());//通过文件路径进行压缩
            images.add(path1);
            mMakePicAdapter.setDataList(images);
        } else if (requestCode == PROJECT_REQUEST && resultCode == RESULT_OK && null != data) {//获取事项
            mRecyclerViewContent.setVisibility(View.VISIBLE);
            selectSubProjects = (List<InspectionSubProjectBean>) data.getSerializableExtra("SelectSubPro");
//            notRepeat=removeDuplicate(selectSubProjects);

            mShowSubProjectAdapter.setData(selectSubProjects);
        } else if (requestCode == USER_REQUEST && resultCode == RESULT_OK && null != data) {//获取多个用户
            userList.clear();
            userList = (List<UserBean>) data.getSerializableExtra("SelectUser");
            if (null != userList) {
                inspector.setText(userList.get(0).getReal_name());
                inspectorId = userList.get(0).getId();
            }

        } else if (requestCode == USERS_REQUEST && resultCode == RESULT_OK && null != data) {//获取单个
            userList.clear();
            userList = (List<UserBean>) data.getSerializableExtra("SelectUser");
            StringBuffer tempS = new StringBuffer();
            if (null != userList) {
                for (int i = 0; i < userList.size(); i++) {
                    tempS.append(userList.get(i).getReal_name());
                    if (i != userList.size() - 1) {
                        tempS.append(";");
                    }
                }
            }
            otherInspector.setText(tempS);
        }
    }

    @Override
    protected NowInspectionPresenter initPresenter() {
        return new NowInspectionPresenter(this, new NowInspectionModel());
    }

    @Override
    public void showOrgUsers(List<SysOrganizationBean> list) {
        mSysOrganizations = list;
    }

    @Override
    public void showOrgCompany(List<CompanyBean> companys) {
        companyList = companys;
        //增加一条提示选框
        CompanyBean companyBean =new CompanyBean();
        companyBean.setCo_name("请选择公司");
        companyBean.setCo_code("00");
        companyList.add(companyBean);

        mSpinnerAdapter = new ArrayAdapter<CompanyBean>(this, R.layout.company_spinner_item, companyList) {
            @Override
            public long getItemId(int position) {
                return super.getItemId(position);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setlayout(super.getDropDownView(position, convertView, parent), position);
            }

            @Override
            public int getCount() {
                // don't display last item. It is used as hint.
                int count = super.getCount();
                return  count > 0 ? count - 1 : count;
            }

            @Nullable
            @Override
            public CompanyBean getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setlayout(super.getView(position, convertView, parent), position);
            }

            private View setlayout(View v, int position) {
                TextView textView = (TextView) v.findViewById(R.id.show_name);
                textView.setText(companyList.get(position).getCo_name());
                return textView;
            }
        };
        promptDialog.dismiss();
        companySpinner.setAdapter(mSpinnerAdapter);
        companySpinner.setSelection(companyList.size()-1,true);

    }

    @Override
    public void showSubProjectData(List<InspectionSubProjectBean> subProjectBeen) {
        subProjects = subProjectBeen;
        if (null != promptDialog)
            promptDialog.dismiss();
    }

    @Override
    public void onSucceed() {
        promptDialog.showInfo("巡检发布成功！");
        finish();

    }

    @Override
    public void onFaild(String msg) {
        promptDialog.showError("发布失败,请重新发布!", true);
    }

    @Override
    public void onCommitSucced() {
        promptDialog.showInfo("巡检发布成功！");
        finish();
    }

    @Override
    public void onCommitFaild() {
        promptDialog.showError("发布失败,请重新发布!", true);
    }

    @Override
    public void onSucceedUpload(String path) { //附近返回
        temB.setAccessory(path);
        mPresenter.commit(temB);
    }

    @Override
    public void onSucceedUploadSign(String path) { //签名路径返回
        temB.setPicture_signature(path);
        if (files.size() > 0) {
            mPresenter.commitFile2(files, 1);
        } else {
            mPresenter.commit(temB);
        }
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
