package com.bxgis.yczw.api;

/**
 * @Author:zjs
 * @Description: 接口地址配置     备注：所有请求都要token  一定要加上token不然会请求不到就产生未登录页面
 * @Created in  2017/11/11.
 * @modified By:
 */

public class Api {
//    public static final String BASE_URL = "http://192.168.3.4:18096/mobile/app/";
    public static final String BASE_URL = "http://139.159.214.42:15080/mobile/app/"; //公网IP
//public static final String BASE_URL = "http://10.0.3.100:18080/mobile/app/";
//    public static final String BASE_URL = "http://192.168.3.39:18096/mobile/app/";
//    public static final String BASE = "http://10.0.3.78:";

//    public static final String BASE = "http://10.0.3.100:";
    public static final String BASE = "http://192.168.3.40:18082";
    public static final String WEB_BASE = "file:///android_asset/";


    //接口地址-登陆验证 获取用户临牌token
    public static final String TOKEN_VERIFY =   BASE+"/sys/auth/login";
    //接口地址-获取用户信息
    public static final String USESR_INFO =  BASE+ "/sys/auth/sso.js";
    //接口地址-获取附近的河滩和海滩
    public static final String BEACHS_LIST =  BASE+ "/patrol/getBayList";
    //开始巡海位置上报
    public static final String BEACHS_START =  BASE+ "/patrol/start";
    //结束巡海所有轨迹上报
    public static final String BEACHS_END =  BASE+ "/patrol/end";




    //接口地址-获取现场巡检匹配的项目类型
    public static final String SUBTYPE_BY_BASICID =  BASE_URL+ "inspection/selectSubType";
    //接口地址-获取现场巡检项目类型
    public static final String PROJECT_LIST =  BASE_URL+ "getProjectsType";
    //接口地址-获取现场巡检事项类型
    public static final String SUBPROJECT_LIST =  BASE_URL+ "getSubProjectsType";
    //接口地址-获取巡检的任务和制定的计划列表
    public static final String INSPECTION_LIST =  BASE_URL+ "getInspectionList";
    //接口地址-巡检任务/现场巡检完成确认
    public static final String INSPECTION_OK =  BASE_URL+ "inspection/editSave";
    //接口地址-现场巡检发布
    public static final String NOW_INSPECTION_RELEASE =  BASE_URL+ "inspection/release";
    public static final String NOW_INSPECTION_RELEASE1 =  BASE_URL+ "inspection/release1";
    //接口地址-添加意见反馈
    public static final String FEEDBACK =  BASE_URL+ "insertFB";
    //接口地址-获取意见反馈列表
    public static final String FEEDBACK_LIST_ =  BASE_URL+ "getFreeBackList";
    //接口地址-APP在线更新
    public static final String APP_UPDATE =  BASE_URL+ "getApkUpdate";
    //返回用户巡检数量
    public static final String COUNT_INSPECTION =  BASE_URL+ "getInspectionCount";

    //获取隐患整改列表
    public static  final String HIDDEN_DANGER_LIST =BASE+"15080/hid/hidRectify/hidRectifyList/queryList2";
    //反馈整改复检意见(通过 rectify_init_unit:3 不通过 rectify_init_unit:2)
    public static  final String HIDDEN_DANGER_COMMIT =BASE+"15080/hid/hidRectifyApproval/saveOrUpdate";
    //反馈整改附近上传接口
    public static  final String UPLOAD_FILE =BASE+"15080/common/upload";



    //反馈
    public static  final String MAIN_BEACH_PATH =BASE+"15080/common/upload";
}
