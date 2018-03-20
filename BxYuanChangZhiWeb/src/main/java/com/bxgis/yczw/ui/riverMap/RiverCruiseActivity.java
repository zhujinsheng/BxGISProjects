package com.bxgis.yczw.ui.riverMap;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.bxgis.yczw.R;
import com.bxgis.yczw.base.BaseActivity;
import com.bxgis.yczw.bean.BeachAndBenchlandBean;
import com.bxgis.yczw.bean.BeachTrackBean;
import com.bxgis.yczw.bean.CommonBeachBean;
import com.bxgis.yczw.event.UpdateICountEvent;
import com.bxgis.yczw.event.UpdateMileageEvent;
import com.bxgis.yczw.myInterface.MyOnItemClickListener;
import com.bxgis.yczw.service.LocationService;
import com.bxgis.yczw.service.RomoteService;
import com.bxgis.yczw.ui.adapter.MapBeachPopupWAdapter;
import com.bxgis.yczw.ui.main.MainActivity;
import com.bxgis.yczw.ui.riverMap.contract.RiverCruiseContract;
import com.bxgis.yczw.ui.riverMap.model.RiverCruiseModel;
import com.bxgis.yczw.ui.riverMap.presenter.RiverCruisePresenter;
import com.bxgis.yczw.utils.GsonUtil;
import com.bxgis.yczw.utils.NoDoubleClickListener;
import com.bxgis.yczw.utils.SHA1Util;
import com.bxgis.yczw.utils.SensorEventHelper;
import com.bxgis.yczw.utils.StringUtil;
import com.bxgis.yczw.utils.SystemBarTintManager;
import com.bxgis.yczw.utils.WindowUtils;
import com.bxgis.yczw.utils.douglas.Douglas;
import com.bxgis.yczw.widget.SpaceItemDecoration;
import com.bxgis.yczw.widget.toast.ToastCompat;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class RiverCruiseActivity extends BaseActivity<RiverCruisePresenter> implements RiverCruiseContract.View, View.OnClickListener, LocationSource, AMapLocationListener {
    LinearLayout inspectLog; //巡河日志
    LinearLayout outFallRegister; //排污口登记
    LinearLayout historyTrack; //巡河历史记录

    ImageButton initLocation;   //定位初始
    ImageButton takePhoto;   //拍照
    ImageButton switchLayer; //地图切换
    ImageButton inspection;  //开始巡河
    ImageView back;
    TextView topTitle, timeTitle, mileage, mileageTitle;
    Chronometer timeText;
    private PopupWindow popupWindow;  //自定义对话框
    private PromptDialog customDialog;  //自定义弹出对话框

    MapView mMapView = null;
    AMap mAMap = null;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
//    private boolean isRunningTest; //标记用户是否正在进行 运动轨迹 测试

    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    private String address = "";
    private String latitudeS = "";
    private String longitudeS = "";
    AMapLocation mAapLocation = null;

    /**
     * PolyLine 线条的宽度
     */
    private float width = 10f;
    //用户自己踩点的可视化区域缩放
    private LatLngBounds.Builder boundsBuilder;
    // 从服务器获取的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilderAlready;

    private List<Polyline> getAlreadyPolylineList;//已有项目 的用户轨迹集合
    private PolylineOptions getAlreadyPolylineOptions;//已有项目 的用户轨迹线参数
    //颜色集合
    List<Integer> colorList = new ArrayList<Integer>();
    /**
     * 位置更新时的 标记点
     */
    private LatLng newLatLng = new LatLng(0, 0);//新的坐标点
    private boolean isRunningTest = false; //标记用户是否正在进行 运动轨迹 测试

    /**
     * 实时定位 存储轨迹线
     */
    private List<Polyline> runningPolylineList = new ArrayList<>();//存储轨迹线对象的集合
    /**
     * 实时定位展示运动轨迹
     */
    private PolylineOptions polylineRunningOptions;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> filterLatLng = new ArrayList<>(); //经过过滤后的轨迹集合
    List<LatLng> tempList = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    List<String> uploadTracks= new ArrayList<>(); //最后上传的轨迹集合（注意 经纬度以空格分开 ）

    List<LatLng> totalLatlngLists = new ArrayList<>();
    double lat = 23.196486d;
    double longtude = 113.463252d;
    boolean islocation = true;

    /**
     * 用于存储 点击地图 时 添加当前的 Marker 标记 集合
     */
    private List<Marker> markerList = new ArrayList<>();

    private boolean isStart = false; //是否发起巡查的标记
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    CommonBeachBean<BeachAndBenchlandBean> mCommonBeachBean = new CommonBeachBean<>(); //附近河滩类
    List<BeachAndBenchlandBean> listData = new ArrayList<>();
    List<BeachAndBenchlandBean> listData2 = new ArrayList<>();
    BeachTrackBean mBeachTrackBea =null;  //轨迹



    @Override
    public void doBeforeSetContentView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rever_cruise;
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
    protected void initView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.gaode_mapview);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        initMap();
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

        initFindView(savedInstanceState);
        mPresenter.getReverInfo(12, 14);
        EventBus.getDefault().register(this);
        //创建对象
        customDialog = new PromptDialog(this);
        //设置自定义属性
        customDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);

        //颜色数组 用于轨迹纠正
        int[] colors = new int[]{Color.argb(255, 0, 255, 0), Color.argb(255, 255, 255, 0), Color.argb(255, 255, 0, 0)};
        Random random = new Random();

        //随机颜色赋值
        for (int i = 0; i < 30; i = i + 2) {
            int color = colors[random.nextInt(3)];
            colorList.add(color);//添加颜色
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event1(UpdateICountEvent messageEvent) {
        Toast.makeText(mContext, "接收返回来的数据" + messageEvent.getCountComplete(), Toast.LENGTH_SHORT).show();
    }

    //更新路程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDistance(UpdateMileageEvent messageEvent) {
        mileage.setText(messageEvent.mileage);
    }

    private void initFindView(Bundle savedInstanceState) {
        inspectLog = (LinearLayout) findViewById(R.id.ll_fill_log);
        outFallRegister = (LinearLayout) findViewById(R.id.ll_outfall_register);
        historyTrack = (LinearLayout) findViewById(R.id.ll_history_track);
        initLocation = (ImageButton) findViewById(R.id.iv_location);
        takePhoto = (ImageButton) findViewById(R.id.iv_take_photo);
        switchLayer = (ImageButton) findViewById(R.id.iv_switch_map);
        inspection = (ImageButton) findViewById(R.id.iv_inspect_switch);
        back = (ImageView) findViewById(R.id.iv_map_back);
        timeTitle = (TextView) findViewById(R.id.tv_time_title);
        mileage = (TextView) findViewById(R.id.tv_mileage);
        mileageTitle = (TextView) findViewById(R.id.tv_mileage_title);
        timeText = (Chronometer) findViewById(R.id.cm_timer);
        topTitle = (TextView) findViewById(R.id.tiv_top_title);

        outFallRegister.setOnClickListener(this);
        inspectLog.setOnClickListener(this);
        historyTrack.setOnClickListener(this);
        initLocation.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        switchLayer.setOnClickListener(this);
        inspection.setOnClickListener(this);
        back.setOnClickListener(this);
        Log.d("SHA1", SHA1Util.sHA1(mContext));
//        for(int i=0;i<20;i++){
//            BeachAndBenchlandBean benchlandBean = new BeachAndBenchlandBean();
//            benchlandBean.setDistance("120.33 Km");
//            benchlandBean.setName("东源村河滩");
//            listData.add(benchlandBean);
//
//        }
//        mCommonBeachBean.setHas(listData);
//
//        for(int i=0;i<10;i++){
//            BeachAndBenchlandBean benchlandBean1 = new BeachAndBenchlandBean();
//            benchlandBean1.setDistance("88.222 Km");
//            benchlandBean1.setName("木某某某某某某某某等等等等等等");
//            listData2.add(benchlandBean1);
//        }
//        mCommonBeachBean.setHws(listData2);

    }

    /**
     * 地图初始化
     */
    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_icon1));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setLogoBottomMargin(-50);  //隐藏logo
        uiSettings.setZoomControlsEnabled(false); //隐藏缩放按钮
        uiSettings.setCompassEnabled(true);   //显示指南针
        uiSettings.setMyLocationButtonEnabled(false); //隐藏默认的定位按钮
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        mAMap.setMyLocationStyle(myLocationStyle); //添加自定义定位图标样式
        // aMap.setMyLocationType()
    }

    public void startTimeClick(View view) {
        timeText.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timeText.getBase()) / 1000 / 60);
        timeText.setFormat("0" + String.valueOf(hour) + ":%s");
        timeText.start();

    }

    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    public void stopClick1(View view) {
        timeText.stop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_fill_log:
//                ToastCompat.makeText(mContext,"开启服务",Toast.LENGTH_SHORT).show();
//                openLocationService();
                break;
            case R.id.ll_outfall_register:

//                closeLocationService();
//                ToastCompat.makeText(mContext,"关闭服务",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_history_track:
//                ToastCompat.makeText(mContext,"发送关闭",Toast.LENGTH_SHORT).show();
//                EventBus.getDefault().post(new UpdateEvent("主动发送关闭定位上传"));
                break;
            case R.id.iv_location:
                if (mAMap != null) {
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(newLatLng)); //移动地图位置
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                }
                break;
            case R.id.iv_take_photo:
                break;
            case R.id.iv_switch_map:
                if (null != mAMap) {
                    if (mAMap.getMapType() == AMap.MAP_TYPE_SATELLITE) {
                        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                    } else {
                        mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                    }
                }
                break;
            case R.id.iv_inspect_switch:
                if (isStart) {
                    customDialog.showAlertSheet("是否结束巡查？", true, new PromptButton("确定", new PromptButtonListener() {
                        @Override
                        public void onClick(PromptButton button) {
                            isStart = false;
                            inspection.setBackgroundResource(R.mipmap.map_inspec_start);
                            startNoteLatLng();
                            stopClick1(switchLayer);
                            customDialog.dismiss();
                            if(StringUtil.isNotEmpty(mBeachTrackBea,true)){
                                customDialog.showLoading("",false);
                                mPresenter.endLocationData(mBeachTrackBea.getPatrol_id(),mAapLocation.getAddress(),String.valueOf(mAapLocation.getLongitude()),String.valueOf(mAapLocation.getLatitude()), GsonUtil.GsonString(uploadTracks));
                            }

                        }
                    }), new PromptButton("取消", new PromptButtonListener() {
                        @Override
                        public void onClick(PromptButton button) {
                            customDialog.dismiss();
                        }
                    }));

                } else { //发起轨迹巡查
                    initPopupWindow(view);
                }
                break;
            case R.id.iv_map_back:
                Intent toHome = new Intent(RiverCruiseActivity.this, MainActivity.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(toHome);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化底部导航栏的中间按钮的弹出效果
     */
    private void initPopupWindow(View v) {
        View popupWidownView = LayoutInflater.from(mContext).inflate(R.layout.popupwin_map_beach, null);
        RecyclerView cotentRV = (RecyclerView) popupWidownView.findViewById(R.id.rv_map_beachType);
        cotentRV.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.y3)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cotentRV.setLayoutManager(linearLayoutManager);
        final MapBeachPopupWAdapter popupWindowAdapter = new MapBeachPopupWAdapter(mContext, mCommonBeachBean != null ? mCommonBeachBean.getHws() : null);
        cotentRV.setAdapter(popupWindowAdapter);
        popupWindowAdapter.setOnMyItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onBeanchClick(View v, int position) {

            }

            @Override
            public void onItemClick(View v, int position, final BeachAndBenchlandBean benchlandBean) {

                if (null != popupWindow) {
                    popupWindow.dismiss();
                }
                customDialog.showAlertSheet("是否现在开始巡查？", true, new PromptButton("确定", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        isStart = true;
                        inspection.setBackgroundResource(R.mipmap.map_inspec_end);
                        startTimeClick(switchLayer);
                        startNoteLatLng();
                        topTitle.setText(benchlandBean.getName());
                        customDialog.dismiss();
//                        if(StringUtil.isNotEmpty(longitudeS,true)){
                        if (StringUtil.isNotEmpty(mAapLocation, true) && mAapLocation.getErrorCode() == 0) {
                            customDialog.showLoading("",false);
                            mPresenter.sendLocationData(benchlandBean.getBay_id(), mAapLocation.getAddress(), String.valueOf(mAapLocation.getLongitude()), String.valueOf(mAapLocation.getLatitude()));
                        }


                    }
                }), new PromptButton("取消", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        customDialog.dismiss();
                    }
                }));

            }

            @Override
            public void onBenchlandClick(View v, int position) {

            }
        });
        ImageView close = (ImageView) popupWidownView.findViewById(R.id.iv_close);
        final TextView beachType = popupWidownView.findViewById(R.id.tv_map_beach);
        final TextView benchlandType = popupWidownView.findViewById(R.id.tv_map_benchland);
        final TextView itemTitle = popupWidownView.findViewById(R.id.tv_beach_title);
//        popupWindow = new PopupWindow(popupWidownView, ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.y650), true);

        popupWindow = new PopupWindow(popupWidownView, WindowUtils.getScreenWidth(mContext) * 10 / 11, WindowUtils.getScreenHeight(mContext) * 5 / 7, true);
//        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        popupWindow.setContentView(popupWidownView);
//        popupWindow.showAtLocation(findViewById(R.id.sp_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWidownView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int mShowPopuVWidth = -popupWidownView.getMeasuredWidth();
//        int mShoePopuVHeight = -popupWidownView.getMeasuredHeight();
//        popupWindow.showAsDropDown(v, mShowPopuVWidth, mShoePopuVHeight);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 150);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        //设置背景半透明
        backgroundAlpha(0.8f);
        //关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        close.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                popupWindow.dismiss();
            }
        });
        beachType.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                beachType.setTextColor(ContextCompat.getColor(mContext, R.color.btn_blue_login));
                benchlandType.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black_0));
                itemTitle.setText(getResources().getString(R.string.map_nearby_beach));
                if (null != mCommonBeachBean) {
                    List<BeachAndBenchlandBean> listBeach = mCommonBeachBean.getHws();
//                    listBeach.addAll((Collection<? extends BeachAndBenchlandBean>) mCommonBeachBean.getHas());
                    if (listBeach != null && listBeach.size() > 0) {
                        popupWindowAdapter.setData(listBeach);
                    } else {
                        popupWindowAdapter.setData(null);
                    }
                } else {
                    popupWindowAdapter.setData(null);
                }

            }
        });
        benchlandType.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                beachType.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black_0));
                benchlandType.setTextColor(ContextCompat.getColor(mContext, R.color.btn_blue_login));
                itemTitle.setText(getResources().getString(R.string.map_nearby_benchland));
                if (null != mCommonBeachBean) {
//                    List<BeachAndBenchlandBean> listBenchlang =new ArrayList<BeachAndBenchlandBean>();
//                    listBenchlang.addAll((Collection<? extends BeachAndBenchlandBean>) mCommonBeachBean.getHws());
                    List<BeachAndBenchlandBean> listBenchlang = mCommonBeachBean.getHas();
                    if (listBenchlang != null) {
                        popupWindowAdapter.setData(listBenchlang);
                    } else {
                        popupWindowAdapter.setData(null);
                    }
                } else {
                    popupWindowAdapter.setData(null);
                }

            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    protected RiverCruisePresenter initPresenter() {
        return new RiverCruisePresenter(RiverCruiseActivity.this, new RiverCruiseModel());
    }

    /**
     * 附近海岸
     *
     * @param data
     */
    @Override
    public void showData(CommonBeachBean<BeachAndBenchlandBean> data) {
        mCommonBeachBean = data;
       customDialog.dismiss();
    }

    /**
     * 开始巡查上传第一次位置后返回
     *
     * @param trackBean
     */
    @Override
    public void uploadLocationSucced(BeachTrackBean trackBean) {
        mBeachTrackBea=trackBean;
        customDialog.dismiss();
    }

    /**
     * 巡查结束后
     *
     * @param s
     */
    @Override
    public void endLocationSucced(String s) {
        clearLineOnMap(); //初始化地图
        customDialog.dismiss();
    }

    @Override
    public void toRequsetUserInfo(String token) {

    }

    @Override
    public void onSucceed() {
        customDialog.dismiss();

    }

    @Override
    public void onFaild(String msg) {
        customDialog.showWarn(msg);
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                mAapLocation = amapLocation;
                newLatLng = location;
//                if (!mFirstFix) {
//                    mFirstFix = true;
//                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
//                    addMarker(location);//添加定位图标
//                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
//                } else {
//                    mCircle.setCenter(location);
//                    mCircle.setRadius(amapLocation.getAccuracy());
//                    mLocMarker.setPosition(location);
//                }

//                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));

                if (islocation) {
                    islocation = false;
//                    addMarker(newLatLng, R.mipmap.location_start);
                    //设置手动定位显示当前定位点
                    mListener.onLocationChanged(amapLocation);// 显示系统定位点
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(newLatLng)); //移动地图位置
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                }
                if (isRunningTest) {//判断用户是否正在进行 运动轨迹 测试
                    if (amapLocation.getAccuracy() < 100) {//尽量减小定位的距离误差

//                        lat += 0.01d;
//                        longtude += 0.01d;
//                        newLatLng = new LatLng(lat, longtude);
                        if (islocation) {
                            islocation = false;
                            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(newLatLng)); //移动地图位置
                            //aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom));
                            mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                        }
                        tempList.add(newLatLng);
                        //轨迹纠偏--筛选坐标 4个点筛选一次
                        if (tempList.size() >= 4) {
                            latLngList.addAll(tempList);
                            tempList.clear();
                            filterCoordinate1();
                        }
                    } else {
                        Toast.makeText(mContext, "当前定位误差较大", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                ToastCompat.makeText(this, errText, Toast.LENGTH_SHORT).show();

            }
        }

    }

    Douglas d = new Douglas();

    /**
     * 筛选坐标--道格拉斯算法
     */
    private void filterCoordinate1() {
        //添加新的轨迹之前 先把之前的轨迹清空 不然最后地图后很卡 亲测如此
//        clearPreLine();
        mAMap.clear();

        for (int i = 0; i < latLngList.size(); i++) {

            if (i == 0) {//拆分坐标
                builder.append("LINESTRING(" + latLngList.get(i).latitude + " " + latLngList.get(i).longitude);
            } else {
                builder.append("," + latLngList.get(i).latitude + " " + latLngList.get(i).longitude);
            }

            if (i == latLngList.size() - 1) {

                builder.append(")");
            }

        }

        //LogUtils.showLog(TAG, "builder.toString 纠偏前 ====" + builder);
        //d.readPoint(new StringBuilder("LINESTRING(14.344434 4.444444,2 3,121.333333 2,6 6,7 7,8.4 6,9 5,10 10)"));
        d.readPoint(builder);
        d.compress(d.points.get(0), d.points.get(d.points.size() - 1));
        polylineRunningOptions = new PolylineOptions();
        if (totalLatlngLists.size() > 0) {
            polylineRunningOptions.addAll(totalLatlngLists);
        }
        for (int i = 0; i < d.points.size(); i++) {
            if (d.points.get(i).getIndex() > -1) {
                polylineRunningOptions.add(new LatLng(d.points.get(i).getX(), d.points.get(i).getY())); //追加一批顶点到线段终点。
            }
        }

//        latLngList.clear();
        filterLatLng.addAll(polylineRunningOptions.getPoints());

        addMarker(latLngList.get(0), R.mipmap.location_start);
        System.out.println("latLngList的长度 =" + latLngList.size());
//        runningPolylineList.add(//记录 当前 轨迹线 对象 便于纠正轨迹时清空这条轨迹线
//        runningPolylineList.add(//记录 当前 轨迹线 对象 便于纠正轨迹时清空这条轨迹线
        mAMap.addPolyline(polylineRunningOptions
                        .width(width)
                        .useGradient(true)
                        .colorValues(colorList)
//                        .setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
        );
        //计算路程
        if (filterLatLng.size() > 2) {
            float distance = AMapUtils.calculateLineDistance(filterLatLng.get(0), filterLatLng.get(latLngList.size() - 1))*1/1000;
            EventBus.getDefault().post(new UpdateMileageEvent(String.valueOf(distance)));
        }
        for(int a=0;a<filterLatLng.size();a++){
            uploadTracks.add(String.valueOf(filterLatLng.get(a).longitude)+" "+String.valueOf(filterLatLng.get(a).latitude)); //空格隔开的经纬度
        }
        builder.setLength(0);
    }


    /**
     * 清除 地图上实时定位画出的 用户轨迹
     */
    private void clearLineOnMap() {

//        isFinished = false;
        totalLatlngLists.clear();
        if(null !=latLngList && latLngList.size()>0){
            latLngList.clear();
            latLngList=null;
        }
        mAMap.clear();
        mListener.onLocationChanged(mAapLocation);// 显示系统定位点
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(newLatLng)); //移动地图位置
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        //清空轨迹
        if (runningPolylineList != null && runningPolylineList.size() > 0) {

            for (int i = 0; i < runningPolylineList.size(); i++) {
                runningPolylineList.get(i).remove();
            }
            boundsBuilder = null;//将可视化缩放置为空
            runningPolylineList.clear();
            polylineRunningOptions = null;
        } else {
            if (null != polylineRunningOptions) {
                polylineRunningOptions = null;
            }
        }
    }


    /**
     * 清除地图已完成或出错的轨迹
     */
    private void cleanFinishLine() {
        clearLineOnMap();//清空已有轨迹
//        clearClickMarkerOnMap();//清空点击地图添加的Marker
    }

    /**
     * 开始记录用户的坐标
     */
    private void startNoteLatLng() {

        //将标记置为true
        isRunningTest = !isRunningTest;

        if (isRunningTest) {

            //开启定位前 先请空 已有的轨迹
            cleanFinishLine();
            isRunningTest = true;
            // checkPermissions(needPermissions);
            ToastCompat.makeText(mContext, "轨迹开始", Toast.LENGTH_SHORT).show();
//            tv_start_location.setText("正在定位");
            //显示对话框
//            showDialog(context);
        } else {
            isRunningTest = false;
//            hideDialog();
            ToastCompat.makeText(mContext, "定位关闭", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 画出轨迹线---刷新轨迹
     */
    private void startRefreshLine() {

        if (markerList.size() > 0) {

            clearLineOnMap();//先清空地图上已有轨迹
            if (polylineRunningOptions == null) {
                polylineRunningOptions = new PolylineOptions();
            }
            if (boundsBuilder == null) {
                boundsBuilder = new LatLngBounds.Builder();
            }
            for (int i = 0; i < markerList.size(); i++) {

                polylineRunningOptions.add(markerList.get(i).getPosition());
                boundsBuilder.include(markerList.get(i).getPosition());//将坐标添加到可视化区域
            }
//            polylineRunningOptions.add(markerList.get(0).getPosition());//首尾相连

            runningPolylineList.add(mAMap
                    .addPolyline(polylineRunningOptions
                            //.setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.map_arrow))
                            .width(width)
                            .colorValues(colorList)
                            .useGradient(true)));

//            isFinished = true;
//            showMarker();//隐藏Markser标记物
        } else {
//            isFinished = false;
//            ToastUtils.showToast(context, "当前点数量太少，请选点后再试");
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(1000);
            /**
             * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
             */
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 记录轨迹时 清空上一次的线条
     * 只是在地图上移除了轨迹线 polylineRunningOptions.getPoints()的坐标依然存在  但是再添加到地图上时就不再显示了
     */
    private void clearPreLine() {
        if (runningPolylineList != null && runningPolylineList.size() > 0) {

            totalLatlngLists.clear();

            for (int i = 0; i < runningPolylineList.size(); i++) {
                totalLatlngLists.addAll(runningPolylineList.get(i).getPoints());
                runningPolylineList.get(i).remove();
            }
            runningPolylineList.clear();
            Log.d("小朱", " totalLatlngLists.size()== " + totalLatlngLists.size());
        }
    }

    /**
     * 筛选坐标--道格拉斯算法
     */
    private void filterCoordinate() {
        //添加新的轨迹之前 先把之前的轨迹清空 不然最后地图后很卡 亲测如此
        clearPreLine();
        polylineRunningOptions = new PolylineOptions();
        for (int i = 0; i < latLngList.size(); i++) {
            polylineRunningOptions.add(latLngList.get(i));
//            if (i == 0) {//拆分坐标
//                builder.append("LINESTRING(" + latLngList.get(i).latitude + " " + latLngList.get(i).longitude);
//            } else {
//                builder.append("," + latLngList.get(i).latitude + " " + latLngList.get(i).longitude);
//            }
//            if (i == latLngList.size() - 1) {
//
//                builder.append(")");
//            }
        }

//        //LogUtils.showLog(TAG, "builder.toString 纠偏前 ====" + builder);
//        //d.readPoint(new StringBuilder("LINESTRING(14.344434 4.444444,2 3,121.333333 2,6 6,7 7,8.4 6,9 5,10 10)"));
//        d.readPoint(builder);
//        d.compress(d.points.get(0), d.points.get(d.points.size() - 1));
//        polylineRunningOptions = new PolylineOptions();
//        if (totalLatlngLists.size() > 0) {
//            polylineRunningOptions.addAll(totalLatlngLists);
//        }
//        for (int i = 0; i < d.points.size(); i++) {
//
//            if (d.points.get(i).getIndex() > -1) {
//                polylineRunningOptions.add(new LatLng(d.points.get(i).getX(), d.points.get(i).getY()));
//            }
//        }

        runningPolylineList.add(//记录 当前 轨迹线 对象 便于纠正轨迹时清空这条轨迹线
                mAMap.addPolyline(polylineRunningOptions
                                .width(width)
                                .useGradient(true)
//                        .setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                                .colorValues(colorList)
//                        .useGradient(true)));
                ));

//        polylineRunningOptions
        latLngList.clear();
        builder.setLength(0);
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    private void addMarker(LatLng latlng, int rid) {
//        if (mLocMarker != null) {
//            return;
//        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(), rid);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
//        options.anchor(0.5f, 0.5f);
        options.zIndex(3);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
        mLocMarker.setTitle("起点");
        if (null != bMap) {
            bMap.recycle();
            bMap = null;
        }
//动画效果
//        Animation animation = new RotateAnimation(mLocMarker.getRotateAngle(),mLocMarker.getRotateAngle()+180,0,0,0);
//        long duration = 1000L;
//        animation.setDuration(duration);
//        animation.setInterpolator(new LinearInterpolator());
//
//        mLocMarker.setAnimation(animation);
//        mLocMarker.startAnimation();
    }

    Intent service = null, remoteService = null;

    /**
     * 开启定位轨迹服务
     */
    private void openLocationService() {
        //通过AIDL实现双进程守护
        service = new Intent(this, LocationService.class);
        startService(service);
        remoteService = new Intent(this, RomoteService.class);
        startService(remoteService);
    }

    /**
     * 关闭定位轨迹服务
     */
    private void closeLocationService() {
        if (null != service) {
            stopService(service);
        }
        if (null != remoteService) {
            stopService(remoteService);
        }

    }

    /**
     * 证Activity不被Finish，就得拦击手机返回键
     * 当前界面调用moveTaskToBac(True) 之后，虽然当前界面会被运行的后台，但是栈顶的Activity也不会是上一级界面。所以需要设置当前Activity的启动模式为SingleInstance。
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 把当前Activity运行至后台
            moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    //    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mMapView.onPause();
        mFirstFix = false;
//        deactivate();
//        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
//        mMapView.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RiverCruise Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }
}
