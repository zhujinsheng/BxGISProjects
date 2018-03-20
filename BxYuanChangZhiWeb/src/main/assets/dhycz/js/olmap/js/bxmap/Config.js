bxmap.config = bxmap.config || {};

/**
 * 地图图层配置，能力包括
 * 1.地图分屏及地图内容不同；
 * 2.不同平台发布
 * 3.底图切换，及底图多层时分等级（例如底图包括：影像图层、道路图层、注记图层）
 */
bxmap.config.MapConfig = {
    "projection":"EPSG:4326",
    "tile_grids": [],
    "layers": [
        {"layer_id":"sl","server":"gaode","level":0,"url":"http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"},
        {"layer_id":"yx","server":"gaode","level":0,"url":"https://webst01.is.autonavi.com/appmaptile?style=6&x=13363&y=7109&z=14"}
    ],
    "wfs":{},
    "olviews":[
        {"view_id":"dc_view","center":[117.15212,23.59884],"zoom":12,"max_zoom":19,"min_zoom":5}
    ],
    "maps":[
        {
            "map_id":"vecMap",
            "description":"高德地图",
            "layer_groups":[
                {
                    "group_id":"GDImage_map",
                    "layers":[
                        {"id":"dcMap","pid":"root","title":"广东地图","tip":"广东地图"},
                        {"id":"layer-GHTD_QP","pid":"dcMap","title":"广东行政区","tip":"广东行政区","visible":true,"inswitcher":true,"layers":["sl"]}
                    ]
                },
                {
                    "group_id":"SeaMap_map",
                    "layers":[
                        {"id":"ywMap","pid":"root","title":"广东地图","tip":"广东地图"},
                        {"id":"layer-GDImage","pid":"ywMap","title":"全球影像图","tip":"全球影像图","visible":true,"inswitcher":true,"layers":["yx"]}
                    ]
                }
            ],
            "olview":"dc_view"
        }
    ]
};

/**
 * 地图工具条设置，id为内容控件编号
 */
bxmap.config.ToolConfig = [
    {"id":1,"pid":0,"visible":true,"description":"导航工具条"},
    {"id":2,"pid":0,"visible":true,"description":"比例尺工具"},
    {"id":3,"pid":0,"visible":true,"description":"坐标显示工具"},
    {"id":4,"pid":0,"visible":true,"description":"鹰眼工具"},
    {"id":5,"pid":0,"visible":true,"description":"地图图层控制器按钮控件"},
    {"id":6,"pid":0,"visible":true,"description":"二维底图切换按钮控件"},
    {"id":7,"pid":0,"visible":true,"description":"三维切换按钮控件"},
    {"id":8,"pid":0,"visible":true,"description":"全屏按钮控件"},
    {"id":9,"pid":0,"visible":false,"description":"滑块卷帘控件"},
    {"id":999,"pid":0,"visible":true,"description":"Toolbox工具箱控件"}
];