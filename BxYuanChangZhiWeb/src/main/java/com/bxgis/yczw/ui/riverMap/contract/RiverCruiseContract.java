package com.bxgis.yczw.ui.riverMap.contract;

import com.bxgis.yczw.base.BaseModel;
import com.bxgis.yczw.base.BaseView;
import com.bxgis.yczw.bean.BeachAndBenchlandBean;
import com.bxgis.yczw.bean.BeachTrackBean;
import com.bxgis.yczw.bean.CommonBeachBean;
import com.bxgis.yczw.bean.UserBean;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/13.
 */
public interface RiverCruiseContract {
    interface Model extends BaseModel {
        Observable<Response<String>>  getReverInfo(int longitude,int latitude); //获取河道信息
        /**
         * 开始巡湾
         * @param bay_id  -- 海湾id
         * @param patrol_start  -- 开始地址名称
         * @param lon --开始地址经度
         * @param lat --开始地址纬度
         * @return
         */
        Observable<Response<String>> sendLocationData(String bay_id, String patrol_start,String lon,String lat);//上传巡检时候的起点
        /**
         * 结束巡海
         * @param patrol_id  -- 巡海记录id
         * @param patrol_end  -- 结束地点名
         * @param lon --结束地址经度
         * @param lat --结束地址纬度
         * @param location  -- 路线点数组字符串["12 14","12 15","12 14","12 16"]
         * @return
         */
        Observable<Response<String>> endLocationData(String patrol_id, String patrol_end,String lon,String lat,String location);

    }

    interface View extends BaseView {
        void showData(CommonBeachBean<BeachAndBenchlandBean> data);
        void uploadLocationSucced(BeachTrackBean trackBean);
        void endLocationSucced(String s); //结束巡检定位返回
        void toRequsetUserInfo(String token);
        void onSucceed();     //成功
        void onFaild(String msg);       //登录失败

    }

    interface Presenter {
//        void login(String login_name, String password);
        void getReverInfo(int longitude,int latitude);
        void sendLocationData(String bay_id, String patrol_start,String lon,String lat);//上传巡检时候的起点
        void  endLocationData(String patrol_id, String patrol_end,String lon,String lat,String location);
    }
}
