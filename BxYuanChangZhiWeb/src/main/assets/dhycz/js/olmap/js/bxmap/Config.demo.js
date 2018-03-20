bxmap.config = bxmap.config || {};

/**
 * 地图图层配置，能力包括
 * 1.地图分屏及地图内容不同；
 * 2.不同平台发布
 * 3.底图切换，及底图多层时分等级（例如底图包括：影像图层、道路图层、注记图层）
 */
bxmap.config.MapConfig = {
	"projection":"EPSG:3857",
//    "geoserver_url": "http://WQ-20160426HYHE:8888/geoserver",
//    "arcgisserver_url": "http://GISSERVER:6080/arcgis/rest/services",
	"geoserver_url": "http://demo.opengeo.org/geoserver",
    "arcgisserver_url": "https://sampleserver1.arcgisonline.com/ArcGIS/rest/services",
    "tile_grids": [
        {
            "grid_id":"grid_epsg_3857",
            "extent":[-20037508.342787,-20037508.342781033,20037508.342781033,20037508.342787],
            "origin":[-2.0037508342787E7,2.0037508342787E7],
            "resolutions":[156543.03392800014,78271.51696399994,39135.75848200009,19567.87924099992,9783.93962049996,4891.96981024998,2445.98490512499,1222.992452562495,611.4962262813797,305.74811314055756,152.87405657041106,76.43702828507324,38.21851414253662,19.10925707126831,9.554628535634155,4.77731426794937,2.388657133974685,1.1943285668550503,0.5971642835598172,0.29858214164761665],
            "tile_size":[256,256]
        }
	],
    "layers": [
        {"layer_id":"id_gs_1","server":"geoserver","level":0,"name":"TEST:mm25D_QP","visible":true,"format":"image/jpeg","tile_grid":"grid_epsg_3857","url":"/gwc/service/wms"},
        {"layer_id":"id_arc_1","server":"arcgisserver","level":0,"visible":true,"dpi":96,"tile_grid":"grid_epsg_3857","url":"/SeaMap/MapServer"},
        {"layer_id":"id_arcgisonline_1","server":"arcgisserver","level":0,"visible":true, "url":"/Specialty/ESRI_StateCityHighway_USA/MapServer"},
        {"layer_id":"GISSERVER_MMGK_GH","server":"arcgisserver","subtype":"dynamic","share":true,"level":10,"visible":true, "url":"/MMGK_GH/MapServer"},
        {"layer_id":"GISSERVER_MMGK_GH","server":"arcgisserver","subtype":"xyz","share":true,"level":10,"visible":true, "url":"/MMGK_GH/MapServer/tile/{z}/{y}/{x}"},
        {"layer_id":"id_osm_1","server":"osm","level":0,"visible":true},
        {"layer_id":"id_osm_transport-dark","server":"osm","level":0,"visible":true,"url":"http://{a-c}.tile.thunderforest.com/transport-dark/{z}/{x}/{y}.png"},
        {"layer_id":"id_osm_cycle","server":"osm","level":0,"visible":true,"url":"http://{a-c}.tile.thunderforest.com/cycle/{z}/{x}/{y}.png"},
        {"layer_id":"id_osm_outdoors","server":"osm","level":0,"visible":true,"url":"http://{a-c}.tile.thunderforest.com/outdoors/{z}/{x}/{y}.png"},
        {"layer_id":"id_osm_landscape","server":"osm","level":0,"visible":true,"url":"http://{a-c}.tile.thunderforest.com/landscape/{z}/{x}/{y}.png"},
        {"layer_id":"id_tianmap_1","server":"tianditu","level":0,"visible":true,"url":"http://t2.tianditu.com/DataServer?T=vec_w&x={x}&y={y}&l={z}"},
        {"layer_id":"id_tianmap_2","server":"tianditu","level":0,"visible":true,"url":"http://t2.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}"},
        {"layer_id":"id_demo_gs_1","server":"geoserver","level":10,"name":"ne:ne_10m_admin_1_states_provinces_lines_shp","visible":true,"url":"/wms","version":"1.3.0"}
	],
    "olviews":[
        {"view_id":"id_view_1","center":[12373636.93683,2446455.75216],"zoom":12,"max_zoom":16,"min_zoom":12},
        {"view_id":"id_view_arcgisonline_1","center": [-10997148, 4569099], "zoom": 4},
        {"view_id":"id_view_geoserverWMS_1","center": [-102413.93152981169, 6975602.427669527], "zoom": 6}
	],
    "maps":[
        {
            "map_id":"id_map_1",
            "description":"地图1",
            "layer_groups":[
                {"group_id":"base_map","layers":["id_gs_1"]},
                {"group_id":"v_map","layers":["id_arc_1"]}
            ],
            "olview":"id_view_1"
        },
        {
            "map_id":"id_map_2",
            "description":"测试在线arcgis/osm底图",
            "layer_groups":[
            	{"group_id":"v_map","layers":["id_osm_1"]},
            	{"group_id":"base_map","layers":["id_osm_transport-dark","id_arcgisonline_1"]}
            ],
            "olview":"id_view_arcgisonline_1"
        },
        {
            "map_id":"id_map_3",
            "description":"测试在线天地图",
            "layer_groups":[
            	{"group_id":"base_map","layers":["id_tianmap_1","id_tianmap_2"]}
            ],
            "olview":"id_view_arcgisonline_1"
        },
        {
            "map_id":"id_map_4",
            "description":"测试地图控制器",
            "layer_groups":[
            	{
            		"group_id":"v_map",
            		"layers":[
            		    {"id":"id_map_4","pid":"root","title":"测试天地图","tip":"测试天地图"},
            		    {"id":"g-1","pid":"id_map_4","title":"逻辑分组1级","tip":"逻辑分组","visible":true},
            		    {"id":"g-1-1","pid":"g-1","title":"矢量底图","tip":"矢量底图","visible":false,"layers":["id_tianmap_1"]},
            		    {"id":"g-1-2","pid":"g-1","title":"逻辑分组2级","tip":"逻辑分组","visible":true},
            		    {"id":"g-1-2-1","pid":"g-1-2","title":"注记图层","tip":"注记图层","visible":false,"layers":["id_tianmap_2"]}
            		]
            	},
            	{
            		"group_id":"base_map",
            		"layers":[
            	        {"id":"id_map_4","pid":"root","title":"测试osm","tip":"测试osm"},
            	        {"id":"g-1","pid":"id_map_4","title":"影像","tip":"影像底图","visible":true,"layers":["id_osm_transport-dark"]}
            	     ]
            	}
            ],
            "olview":"id_view_arcgisonline_1"
        }
    ]
};