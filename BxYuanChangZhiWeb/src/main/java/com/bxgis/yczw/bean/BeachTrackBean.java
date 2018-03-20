package com.bxgis.yczw.bean;

/**
 * Author : xiaozhu
 * Time:2018/3/20
 * Description:   海岸线或者海湾的轨迹实体
 */
public class BeachTrackBean {


    /**
     * create_time : 2018-03-20 20:04:51
     * creater_id : 5a41e4d59e040d541c95febc
     * id : 5ab0f8e34f590c242423485a
     * images :
     * patrol_id : 5ab0f8e34f590c2424234859
     * patrol_info : {"bay_id":"5aaf70b575f07238c444eec4","bay_info":{"area":"","bay_level":"","bay_manage":"","bay_manage_id":"","bay_type":"0","belong_org":"","belong_org_id":"","coastline_length":"","code":"","create_time":"","creater_id":"","geo_wkt":"","id":"5aaf70b575f07238c444eec4","index_order":3,"kind_code":"","name":"粤港澳大湾区","pid":"5aab7dc49397fad6d0447854","start":"","strategy":"","terminus":"","tree_cnt":6,"tree_full_cnt":"1.6","update_time":"","updater_id":""},"create_time":"2018-03-20 20:04:51","creater_id":"5a41e4d59e040d541c95febc","id":"5ab0f8e34f590c2424234859","location":"","patrol_end":"","patrol_end_location":"","patrol_end_time":"","patrol_star_time":"2018-03-20 20:04:51","patrol_start":"广东省广州市海珠区凤浦中路679号靠近广交会威斯汀酒店","patrol_start_location":"113.363315 23.096438","status":0,"update_time":"","updater_id":"","user_id":"5a41e4d59e040d541c95febc","user_name":"超级管理员"}
     * remarks :
     * situation :
     * update_time :
     * updater_id :
     */

    private String create_time;
    private String creater_id;
    private String id;
    private String images;
    private String patrol_id;
    private PatrolInfoBean patrol_info;
    private String remarks;
    private String situation;
    private String update_time;
    private String updater_id;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCreater_id() {
        return creater_id;
    }

    public void setCreater_id(String creater_id) {
        this.creater_id = creater_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getPatrol_id() {
        return patrol_id;
    }

    public void setPatrol_id(String patrol_id) {
        this.patrol_id = patrol_id;
    }

    public PatrolInfoBean getPatrol_info() {
        return patrol_info;
    }

    public void setPatrol_info(PatrolInfoBean patrol_info) {
        this.patrol_info = patrol_info;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getUpdater_id() {
        return updater_id;
    }

    public void setUpdater_id(String updater_id) {
        this.updater_id = updater_id;
    }

    public static class PatrolInfoBean {
        /**
         * bay_id : 5aaf70b575f07238c444eec4
         * bay_info : {"area":"","bay_level":"","bay_manage":"","bay_manage_id":"","bay_type":"0","belong_org":"","belong_org_id":"","coastline_length":"","code":"","create_time":"","creater_id":"","geo_wkt":"","id":"5aaf70b575f07238c444eec4","index_order":3,"kind_code":"","name":"粤港澳大湾区","pid":"5aab7dc49397fad6d0447854","start":"","strategy":"","terminus":"","tree_cnt":6,"tree_full_cnt":"1.6","update_time":"","updater_id":""}
         * create_time : 2018-03-20 20:04:51
         * creater_id : 5a41e4d59e040d541c95febc
         * id : 5ab0f8e34f590c2424234859
         * location :
         * patrol_end :
         * patrol_end_location :
         * patrol_end_time :
         * patrol_star_time : 2018-03-20 20:04:51
         * patrol_start : 广东省广州市海珠区凤浦中路679号靠近广交会威斯汀酒店
         * patrol_start_location : 113.363315 23.096438
         * status : 0
         * update_time :
         * updater_id :
         * user_id : 5a41e4d59e040d541c95febc
         * user_name : 超级管理员
         */

        private String bay_id;
        private BayInfoBean bay_info;
        private String create_time;
        private String creater_id;
        private String id;
        private String location;
        private String patrol_end;
        private String patrol_end_location;
        private String patrol_end_time;
        private String patrol_star_time;
        private String patrol_start;
        private String patrol_start_location;
        private int status;
        private String update_time;
        private String updater_id;
        private String user_id;
        private String user_name;

        public String getBay_id() {
            return bay_id;
        }

        public void setBay_id(String bay_id) {
            this.bay_id = bay_id;
        }

        public BayInfoBean getBay_info() {
            return bay_info;
        }

        public void setBay_info(BayInfoBean bay_info) {
            this.bay_info = bay_info;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getCreater_id() {
            return creater_id;
        }

        public void setCreater_id(String creater_id) {
            this.creater_id = creater_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPatrol_end() {
            return patrol_end;
        }

        public void setPatrol_end(String patrol_end) {
            this.patrol_end = patrol_end;
        }

        public String getPatrol_end_location() {
            return patrol_end_location;
        }

        public void setPatrol_end_location(String patrol_end_location) {
            this.patrol_end_location = patrol_end_location;
        }

        public String getPatrol_end_time() {
            return patrol_end_time;
        }

        public void setPatrol_end_time(String patrol_end_time) {
            this.patrol_end_time = patrol_end_time;
        }

        public String getPatrol_star_time() {
            return patrol_star_time;
        }

        public void setPatrol_star_time(String patrol_star_time) {
            this.patrol_star_time = patrol_star_time;
        }

        public String getPatrol_start() {
            return patrol_start;
        }

        public void setPatrol_start(String patrol_start) {
            this.patrol_start = patrol_start;
        }

        public String getPatrol_start_location() {
            return patrol_start_location;
        }

        public void setPatrol_start_location(String patrol_start_location) {
            this.patrol_start_location = patrol_start_location;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getUpdater_id() {
            return updater_id;
        }

        public void setUpdater_id(String updater_id) {
            this.updater_id = updater_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public static class BayInfoBean {
            /**
             * area :
             * bay_level :
             * bay_manage :
             * bay_manage_id :
             * bay_type : 0
             * belong_org :
             * belong_org_id :
             * coastline_length :
             * code :
             * create_time :
             * creater_id :
             * geo_wkt :
             * id : 5aaf70b575f07238c444eec4
             * index_order : 3
             * kind_code :
             * name : 粤港澳大湾区
             * pid : 5aab7dc49397fad6d0447854
             * start :
             * strategy :
             * terminus :
             * tree_cnt : 6
             * tree_full_cnt : 1.6
             * update_time :
             * updater_id :
             */

            private String area;
            private String bay_level;
            private String bay_manage;
            private String bay_manage_id;
            private String bay_type;
            private String belong_org;
            private String belong_org_id;
            private String coastline_length;
            private String code;
            private String create_time;
            private String creater_id;
            private String geo_wkt;
            private String id;
            private int index_order;
            private String kind_code;
            private String name;
            private String pid;
            private String start;
            private String strategy;
            private String terminus;
            private int tree_cnt;
            private String tree_full_cnt;
            private String update_time;
            private String updater_id;

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getBay_level() {
                return bay_level;
            }

            public void setBay_level(String bay_level) {
                this.bay_level = bay_level;
            }

            public String getBay_manage() {
                return bay_manage;
            }

            public void setBay_manage(String bay_manage) {
                this.bay_manage = bay_manage;
            }

            public String getBay_manage_id() {
                return bay_manage_id;
            }

            public void setBay_manage_id(String bay_manage_id) {
                this.bay_manage_id = bay_manage_id;
            }

            public String getBay_type() {
                return bay_type;
            }

            public void setBay_type(String bay_type) {
                this.bay_type = bay_type;
            }

            public String getBelong_org() {
                return belong_org;
            }

            public void setBelong_org(String belong_org) {
                this.belong_org = belong_org;
            }

            public String getBelong_org_id() {
                return belong_org_id;
            }

            public void setBelong_org_id(String belong_org_id) {
                this.belong_org_id = belong_org_id;
            }

            public String getCoastline_length() {
                return coastline_length;
            }

            public void setCoastline_length(String coastline_length) {
                this.coastline_length = coastline_length;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getCreater_id() {
                return creater_id;
            }

            public void setCreater_id(String creater_id) {
                this.creater_id = creater_id;
            }

            public String getGeo_wkt() {
                return geo_wkt;
            }

            public void setGeo_wkt(String geo_wkt) {
                this.geo_wkt = geo_wkt;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getIndex_order() {
                return index_order;
            }

            public void setIndex_order(int index_order) {
                this.index_order = index_order;
            }

            public String getKind_code() {
                return kind_code;
            }

            public void setKind_code(String kind_code) {
                this.kind_code = kind_code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getStrategy() {
                return strategy;
            }

            public void setStrategy(String strategy) {
                this.strategy = strategy;
            }

            public String getTerminus() {
                return terminus;
            }

            public void setTerminus(String terminus) {
                this.terminus = terminus;
            }

            public int getTree_cnt() {
                return tree_cnt;
            }

            public void setTree_cnt(int tree_cnt) {
                this.tree_cnt = tree_cnt;
            }

            public String getTree_full_cnt() {
                return tree_full_cnt;
            }

            public void setTree_full_cnt(String tree_full_cnt) {
                this.tree_full_cnt = tree_full_cnt;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public String getUpdater_id() {
                return updater_id;
            }

            public void setUpdater_id(String updater_id) {
                this.updater_id = updater_id;
            }
        }
    }
}
