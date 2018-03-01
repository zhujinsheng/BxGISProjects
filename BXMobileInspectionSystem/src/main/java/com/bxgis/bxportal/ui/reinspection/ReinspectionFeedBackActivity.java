package com.bxgis.bxportal.ui.reinspection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.event.UpdateEvent;
import com.bxgis.bxportal.myInterface.IMakePic;
import com.bxgis.bxportal.myInterface.OnImageUploadListener;
import com.bxgis.bxportal.ui.home.adpter.AccessoryAdapter;
import com.bxgis.bxportal.ui.home.adpter.MakePicAdapter;
import com.bxgis.bxportal.ui.home.adpter.ShowDetailProjectAdapter;
import com.bxgis.bxportal.ui.reinspection.contract.ReinspectionContract;
import com.bxgis.bxportal.ui.reinspection.model.ReinspectionModel;
import com.bxgis.bxportal.ui.reinspection.presenter.ReinspectionPresenter;
import com.bxgis.bxportal.utils.BitMapUtils;
import com.bxgis.bxportal.utils.FileProviderUtils;
import com.bxgis.bxportal.utils.LeFileDirUtil;
import com.bxgis.bxportal.utils.PermissionUtils;
import com.bxgis.bxportal.utils.SPUtils;
import com.bxgis.bxportal.utils.SystemBarTintManager;
import com.bxgis.bxportal.utils.date.TimestampUtils;
import com.bxgis.bxportal.widget.Constatnt;
import com.bxgis.bxportal.widget.CustomDatePicker;
import com.bxgis.bxportal.widget.CustomEditText;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class ReinspectionFeedBackActivity extends BaseActivity<ReinspectionPresenter> implements ReinspectionContract.View, View.OnClickListener, IMakePic {

    @Bind(R.id.release_review)
    Button release;
    @Bind(R.id.rv_feedback_accessory)
    RecyclerView mRecyclerView;
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @Bind(R.id.radio_yes)
    RadioButton yes;
    @Bind(R.id.radio_no)
    RadioButton no;
    @Bind(R.id.feedback_review)
    CustomEditText reViewFeedBack;
    @Bind(R.id.reinspection_time)
    TextView reInspectiontime;
    @Bind(R.id.tv_inspector)
    TextView inspector;

    @Bind(R.id.back_detail)
    ImageView backDetail;
    @Bind(R.id.reinspection_state)
    TextView reinspectionState;
    @Bind(R.id.et_hidden_level)
    TextView hiddenLevel;
    @Bind(R.id.reinspection_name)
    TextView reinspection_name;
    @Bind(R.id.reinspection_code)
    TextView reInspectionCode; //隐患单号
    @Bind(R.id.rectify_finish_time)
    TextView rectifyFinishTime; //整改完成时间


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


    List<String> companyList = new ArrayList<>();
    List<String> requestPath = new ArrayList<>(); //保存巡检要求的附件


    private String time1 = "";
    private String state = "3"; //通过 rectify_init_unit:3 不通过 rectify_init_unit:2
    String filePath = "";
    HiddenDangerReViewBean mHiddenDangerReViewBean;
    AccessoryAdapter requestAccessAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_reinspection_feedback;
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
        mHiddenDangerReViewBean = (HiddenDangerReViewBean) getIntent().getSerializableExtra("ReInspection");

//        mPresenter.getSubProjects(mInspectionProjectBean.getId());

        backDetail.setOnClickListener(this);
        release.setOnClickListener(this);
        reInspectiontime.setOnClickListener(this);

        //图片列表
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.HORIZONTAL));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.VERTICAL));
        mMakePicAdapter = new MakePicAdapter(this, images, this, Constatnt.EDIT);
        mRecyclerView.setAdapter(mMakePicAdapter);
        initDate();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //在这个函数里面用来改变选择的radioButton的数值，以及与其值相关的
                if (yes.getId() == checkedId) {
                    state="3";
                } else if (no.getId() == checkedId) {
                    state="2";
                }

            }
        });
        initDatetime();
    }

    @Override
    protected ReinspectionPresenter initPresenter() {
        return new ReinspectionPresenter(this, new ReinspectionModel());
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
        reInspectionCode.setText(mHiddenDangerReViewBean.getRectify_code());
        String level = mHiddenDangerReViewBean.getHid_level();
        if (level.equals("0")) {
            level = "一般";
        } else if (level.equals("1")) {
            level = "重大";
        } else {
            level = "未知";
        }
        String state = mHiddenDangerReViewBean.getRectify_init_unit();////整改状态（0，待整改;1，已整改，2待复查;3，复查完成;）
        //// 整改状态
        if (state.equals("2")) {
            state = "已整改待复查";
            reinspectionState.setTextColor(Color.RED);
        } else if (state.equals("3")) {
            state = "复查完成";

        }
        hiddenLevel.setText(level);
        reinspectionState.setText(state);
        reinspection_name.setText(mHiddenDangerReViewBean.getHid_name());
        inspector.setText(SPUtils.get(mContext,"real_name","").toString());
        String finishtime = mHiddenDangerReViewBean.getRectify_finish_time();
        if (!TextUtils.isEmpty(finishtime)) {
            rectifyFinishTime.setText(TimestampUtils.timestamp2String(Long.parseLong(finishtime), "yyyy-MM-dd"));
        }

    }
    private void initDatetime(){
        String now = sdf.format(new Date());
        startDatePicker = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                reInspectiontime.setText(time.split(" ")[0]);
                time1 = time;
            }
        }, now, "2099-01-01 00:00");
        startDatePicker.showSpecificTime(false); // 不显示时和分
        startDatePicker.setIsLoop(false); // 不允许循环滚动
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
            case R.id.release_review:

                final List<File> files = new ArrayList<>();
                if (null != images && images.size() > 0) {
                    for (int i = 0; i < images.size(); i++) {
                        files.add(new File(images.get(i)));
                    }
                }
                if (TextUtils.isEmpty(time1)) {
                    if(null!=promptDialog){
                        promptDialog.dismiss();
                    }
                    Toast.makeText(mContext, "复查完成日期不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(reViewFeedBack.getText().toString())) {
                    if(null!=promptDialog){
                        promptDialog.dismiss();
                    }
                    Toast.makeText(mContext, "复查情况反馈不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                promptDialog.showWarnAlert("确定提交复查反馈？",new PromptButton("否", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        promptDialog.dismiss();
                    }
                }),new PromptButton("提交", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        if(files.size()>0){
                            promptDialog.showLoading("正在提交...", true);
                            mPresenter.commitFile(files);

                        }else{
                            initCommitDate(filePath);
                        }

                    }
                }),true);


                break;
            case R.id.reinspection_time:

                String time1 = reInspectiontime.getText().toString();
                String temp = "";
                if (null != time1 && !time1.equals("")) {
                    temp = time1;
                } else {
                    temp = sdf.format(new Date());
                }
                startDatePicker.show(temp);
                break;
        }
    }

    private void initCommitDate(String filePath) {

        HiddenDangerReViewBean temH = new HiddenDangerReViewBean();
        temH.setId(mHiddenDangerReViewBean.getId());
        temH.setRectify_code(mHiddenDangerReViewBean.getRectify_code());
        temH.setRectify_review_finish_desc(reViewFeedBack.getText().toString());
        temH.setRectify_finish_time(time1);
        temH.setRectify_init_unit(state);
        temH.setRectify_review_picture(filePath);
        temH.setRectify_review_person(inspector.getText().toString());
//        String userId = SPUtils.get(mContext, "user_id", "").toString();
        promptDialog.showLoading("正在提交...", true);
        mPresenter.commitReinspection(temH);

    }

    /*
     * 从相册获取
    */
    public void opentPicture() {
        if (hasSdcard()) {
            tempFile = new File(MISystemApplication.mainPath,
                    createPhotoName());
        } else {
            Toast.makeText(ReinspectionFeedBackActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
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
            Uri uri = FileProviderUtils.getUriForFile(mContext,tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            Toast.makeText(ReinspectionFeedBackActivity.this, "存贮卡不可用", Toast.LENGTH_SHORT).show();
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
    public void onSucceedFile(String path) {
        filePath=path;
        initCommitDate(filePath);
    }

    @Override
    public void onSucceed() {
        EventBus.getDefault().post(new UpdateEvent("3")); //通知完成列表进行刷新
        promptDialog.showSuccess("巡检反馈成功！", true);
        finish();
    }


    @Override
    public void onFaild(String msg) {
        promptDialog.dismiss();
        promptDialog.showError(msg, true);
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
