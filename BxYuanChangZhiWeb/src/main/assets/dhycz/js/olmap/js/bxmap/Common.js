
/**
 * @namespace bxmap
 * @description  预定义一些全局变量
 */
var bxmap = bxmap || {};

/**
 * @private
 * @const
 * @type {String}
 * @default 'IS_MAIN_MAP'
 * @description  用于地图分屏联动时判断主地图时所用的key，属于地图联动全局设置
 */
bxmap.IS_MAIN_MAP = 'IS_MAIN_MAP';
/**
 * @private
 * @const
 * @type {String}
 * @default 'LINKS'
 * @description  用于地图分屏联动时，关联地图所用的key，属于地图联动全局设置
 */
bxmap.MAP_LINKS = 'LINKS'; //关联地图


/**
 * @private
 * @const
 * @type {String} 
 * @default 'view_id'
 * @description  用于地图图层配置Config.js文件中olviews节点标识视图id的key，
 * 对应于bxmap.config.MapConfig.olviews属性名view_id
 */
bxmap.CONFIG_VIEW_ID = "view_id";
/**
 * @private
 * @const
 * @type {String}
 * @default 'grid_id'
 * @description  用于地图图层配置Config.js文件中tile_grids节点标识瓦片方案id的key，
 * 对应于bxmap.config.MapConfig.tile_grids属性名grid_id
 */
bxmap.CONFIG_TILE_GRID_ID = "grid_id";
/**
 * @private
 * @const
 * @type {String}
 * @default 'layer_id'
 * @description  用于地图图层配置Config.js文件中layers节点标识图层id的key，
 * 对应于bxmap.config.MapConfig.layers属性名layer_id
 */
bxmap.CONFIG_LAYER_ID = "layer_id";
/**
 * @private
 * @const
 * @type {String}
 * @default 'group_id'
 * @description  用于地图图层配置Config.js文件中maps.layer_groups.group_id节点标识图层组id的key，
 * 对应于bxmap.config.MapConfig.maps.layer_groups数组项对象的属性名group_id
 */
bxmap.CONFIG_GROUP_ID = "group_id";
/**
 * @private
 * @const
 * @type {String}
 * @default 'map_id'
 * @description  用于地图图层配置Config.js文件中maps节点标识地图配置id的key，
 * 对应于bxmap.config.MapConfig.maps数组项对象的属性名map_id
 */
bxmap.CONFIG_MAP_ID = "map_id";
/**
 * @const
 * @type {number}
 * @default 0
 * @description  用于图层的压盖顺序设置，定义图层级别最小有效值为0。数值越小，压盖在下面，例如底图为0
 */
bxmap.CONFIG_LEVEL_MIN = 0;//
/**
 * @const
 * @type {number}
 * @default 999
 * @description  用于图层的压盖顺序设置，定义图层级别最大有效值为999。数值越大，悬在上面作为覆盖层
 */
bxmap.CONFIG_LEVEL_MAX = 999;

/**
 * @const
 * @type {String}
 * @default "root"
 * @description  用于图层控制器定义根节点标识，图层控制器使用了ztree，统一根节点比较好管理
 */
bxmap.LAYER_SWITCHER_ROOT = "root";
/**
 * @private
 * @const
 * @type {String}
 * @default "layer_switcher_map"
 * @description  用于图层控制器节点为地图类型节点，使用地图图标区分
 */
bxmap.LAYER_SWITCHER_CATEGORY_MAP = "layer_switcher_map";
/**
 * @private
 * @const
 * @type {String}
 * @default "layer_switcher_layers"
 * @description  用于图层控制器节点为图层组类型节点，使用图层组图标区分
 */
bxmap.LAYER_SWITCHER_CATEGORY_GROUP = "layer_switcher_layers";
/**
 * @private
 * @const
 * @type {String}
 * @default "layer_switcher_layer"
 * @description  用于图层控制器节点为图层类型节点，使用图层图标区分
 */
bxmap.LAYER_SWITCHER_CATEGORY_LAYER = "layer_switcher_layer";


/**
 * @const
 * @type {String}
 * @default "bxmap_index_control_id"
 * @description  标识地图控件control ID的key，主要用于设置/获取控件id
 * @example <caption> 设置控件id </caption>
 * var ctl = new bxmap.control.LayerSwitcher();
 * bmap.addControl(ctl);
 * ctl.set(bxmap.INDEX_CONTROL_ID, 5);
 * @example <caption> 获取控件 </caption>
 * var control = bmap.getIndexControl(5);
 */
bxmap.INDEX_CONTROL_ID="bxmap_index_control_id";

/**
*
 * @const
 * @type {String}
 * @default "bxmap_index_layer_id"
 * @description  标识带有自定义索引图层id的key，主要用于设置/获取图层id
 * @example <caption> 设置图层id </caption>
 * //设置layer id
 * var layer = new ol.layer.Vector(...);
 * layer.set(bxmap.INDEX_LAYER_ID, "自定义的id");
 * bmap.addIndexLayer(layer);
 *
 * @example <caption> 获取图层 </caption>
 * //获取预定义的图标标注图层
 * var imgLayer = bmap.getIndexLayer("10001");
 */
bxmap.INDEX_LAYER_ID = "bxmap_index_layer_id"; //key
/**
 * @const
 * @type {String}
 * @default "10001"
 * @description  预定义图标标注图层id，主要用于图片类（如应急车辆、消防人员）渲染到地图上
 */
bxmap.DEFAULT_IMG_LABEL_LAYER_ID = "10001";
/**
 * @const
 * @type {String}
 * @default "10002"
 * @description  预定义以ABCD图片符号化的点图层id，主要用于符号化查询列表，
 * 效果类似于搜狗地图查询结果以A、B、C、D...表示位置
 */
bxmap.DEFAULT_IMG_ABCD_LAYER_ID = "10002";

/**
 * @const
 * @type {String}
 * @default "bxmap_index_interaction_id"
 * @description  标识带有自定义索引交互工具id的key，主要用于设置/获取交互工具id
 * @example <caption> 设置交互工具id </caption>
 * var zoom_in = new bxmap.interaction.ZoomIn();
 * zoom_in.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_ZOOM_IN_ID);
 * bmap.addMutexInteraction(zoom_in);
 *
 * @example <caption> 根据id获取交互工具 </caption>
 * var zoom_in = bmap.getIndexInteraction(bxmap.DEFAULT_INTER_ZOOM_IN_ID);
 * //设置为当前执行工具
 * bmap.setCurrentMutexInteraction(zoom_in);
 */
bxmap.INDEX_INTERACTION_ID = "bxmap_index_interaction_id"; //key
/**
 *
 * @const
 * @type {String}
 * @default "10001"
 * @description  地图拖拽漫游交互工具(drag pan)ID
 * @example <caption> 获取漫游交互工具 </caption>
 * var zoom_in = bmap.getIndexInteraction("10001");
 * bmap.setCurrentMutexInteraction(zoom_in);
 */
bxmap.DEFAULT_INTER_DRAG_PAN_ID = "10001";//drag pan
/**
*
 * @const
 * @type {String}
 * @default "10002"
 * @description  地图放大(zoom in)ID
 */
bxmap.DEFAULT_INTER_ZOOM_IN_ID = "10002";//zoom in
/**
*
 * @const
 * @type {String}
 * @default "10003"
 * @description  地图缩小(zoom out)ID
 */
bxmap.DEFAULT_INTER_ZOOM_OUT_ID = "10003";//zoom out
/**
 * @const
 * @type {String}
 * @default "10004"
 * @description  地图测距(measure distance)ID
 */
bxmap.DEFAULT_INTER_MEASURE_DIST_ID = "10004";//measure distance
/**
 * @const
 * @type {String}
 * @default "10005" 
 * @description  地图测面(measure area)ID
 */
bxmap.DEFAULT_INTER_MEASURE_AREA_ID = "10005";//measure area

/**
 * @const
 * @type {String}
 * @default "10006"
 * @description  军事标绘ID
 */
bxmap.DEFAULT_INTER_PLOT_ID = "10006";

/**
 * @const
 * @type {String}
 * @default "geo_id"
 * @description  用于图层与业务表关联，在矢量数据中，字段统一使用"geo_id"与业务表关联
 */
bxmap.WFS_ASSOCIATION_GEO_FIELD = "geo_id";//用于图层与业务表关联，要素类字段名称

/**
 * @namespace bxmap.common
 * @description 地图通用方法
*/
bxmap.common = (function () {
	
	/**
	 * @private
	 */
    var uidCounter = 0;
    /**
	 * @function
	 * @memberof bxmap.common
     * @description 获取对象计数uid，uid为自增长数值。执行该方法将会为对象增加属性bx_uid，
	 * 例如传入对象为{id:"xx"},执行方法后对象为{id:"xx", bx_uid:12}
     * @param {Object} 任意对象类型
     * @returns {Number}
	 * @static
     */
    function getCounterUid(obj) {
        return obj.bx_uid || (obj.bx_uid = ++uidCounter);
    };

    /**
	 * @function
	 * @memberof bxmap.common
     * @description 获取对象随机字符串，执行该方法将会为对象增加属性bx_uid
     * @param {Object} 任意对象类型
     * @returns {String}
	 * @static
     */
    function getRandomStringUid(obj) {
        if (obj.bx_uid == undefined) {
            obj.bx_uid = getRandomString();
        }
        return obj.bx_uid;
    }
	/**
	 * @function
     * @memberof bxmap.common
	 * @description 随机获取字符串
	 * @returns {String}
	 */
    function getRandomString() {
        var x = 2147483648;
        var str = Math.floor(Math.random() * x).toString(36)
				+ Math.abs(Math.floor(Math.random() * x) ^ Date.now).toString(36);
        return str;
    }

    /**
	 * @function
	 * @memberof bxmap.common
     * @description 生成UUID
     * @returns {String} UUID
     * @static
     */
    function createUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
    
    /**
	 * @function
	 * @memberof bxmap.common
     * @description 获取自定义数据，对象增加obj.bx_context属性
     * @returns {Object} JSON对象
     * @static
     */
    function getContext(obj){
    	if (obj.bx_context == undefined) {
            obj.bx_context = {};
        }
        return obj.bx_context;
    }
    
    /**
   	  * @function
	  * @memberof bxmap.common
      * @description 获取要素图片样式参数，包括img/scale/rotation
      * @returns {Object} JSON对象{"img":img,"scale":1,"rotation":0.001}
      * @static
      */
    function getFeatureIconParams(feature){
    	var context = getContext(feature);
    	if(context["iconStyleParams"] == undefined){
    		context["iconStyleParams"] = {};
    	}
        return context["iconStyleParams"];
    }
    
    /**
   	 * @function
	 * @memberof bxmap.common
     * @description 获取要素检测参数，用于click/pointermove检测要素
     * @returns {Object} JSON对象{"click":true,"pointermove":true}
     * @static
     */
    function getFeatureDetectedParams(feature){
    	var context = getContext(feature);
    	if(context["eventDetected"] == undefined){
    		context["eventDetected"] = {};
    	}
        return context["eventDetected"];
    }

    /**
     * @description 判断元素是否存在
	 * @memberof bxmap.common
	 * @function
     * @param item {*}- 元素
     * @param array {Array<*>} [item] 数组
     * @returns {Boolean} 存在返回true
	 * @static
     */
    function exist(item, array) {
    	var i = array.indexOf(item);
    	var found = i > -1;
    	return found;
    }

    /**
	 * @private
	 * @member
	 */
    var defaultSelectedStyle = null;
    /**
     * @description 获取选中要素默认样式
	 * @function
	 * @memberof bxmap.common
	 * @static
	 * @returns {Object}
     */
    function getDefaultSelectedStyle(){
    	if(defaultSelectedStyle == null){
    		defaultSelectedStyle = styles = {};
    		var white = [255, 255, 255, 1];
    		var blue = [0, 153, 255, 1];
    		var width = 3;
    	    styles["Point"] = [
    	         	          new ol.style.Style({
    	         	        	  image: new ol.style.Circle({
    	         	        		  radius: width * 2,
    	         	        		  fill: new ol.style.Fill({
    	         	        			  color: blue
    	         	        		  }),
    	         	        		  stroke: new ol.style.Stroke({
    	         	        			  color: white,
    	         	        			  width: width / 2
    	         	        		  })
    	         	        	  }),
    	         	        	  zIndex: Infinity
    	         	          })
    	         	    ];
    	    styles["MultiPoint"] = styles["Point"];
    	    styles["LineString"] = [
    	                            new ol.style.Style({
    	                            	stroke: new ol.style.Stroke({
    	                            		color: white,
    	                            		width: width + 2
    	                            	})
    	                            }),
    	                            new ol.style.Style({
    	                            	stroke: new ol.style.Stroke({
    	                            		color: blue,
    	                            		width: width
    	                            	})
    	                            })
    	                        ];
    	    styles["MultiLineString"] =styles["LineString"];
    		
    	    styles["Polygon"] = [
    	               		  new ol.style.Style({
    	               		    fill: new ol.style.Fill({
    	               		      color: [180, 212, 243, 0.6]//[255, 255, 255, 0.5]
    	               		    })
    	               		  })
    	               		];
    	    styles["Polygon"] = styles["Polygon"].concat(styles["LineString"]);
    	    styles["MultiPolygon"] = styles["Polygon"];
    	    styles["Circle"] =styles["Polygon"].concat(styles["LineString"]);
    	    styles["GeometryCollection"] = styles["Polygon"].concat(styles["LineString"], styles["Point"]);
    	}
	
	    return defaultSelectedStyle;
    }
    /**
	 * @private
	 * @member
	 */
    var defaultVectorLayerStyle = null;
    /**
     * @description 获取VectorLayer默认样式
	 * @function
	 * @memberof bxmap.common
	 * @static
	 * @returns {Array<ol.style.Style>}
     */
    function getDefaultVectorLayerStyle(){
    	if(defaultVectorLayerStyle == null){
    		var fill = new ol.style.Fill({
    			color: 'rgba(255,255,255,0.5)'
    		});
    		var stroke = new ol.style.Stroke({
    			color: '#3399CC',
    			width: 1.25
		    });
    		defaultVectorLayerStyle = [
                new ol.style.Style({
                	image: new ol.style.Circle({
                		fill: fill,
                		stroke: stroke,
                		radius: 5
			        }),
			        fill: fill,
			        stroke: stroke
			 	})
            ];
    	}
    	return defaultVectorLayerStyle;
    }
    
    /**
	 * @private
	 * @member
	 */
    var defaultAbcdStyleMap = null;
    /**
     * @description 获取以（1、2、3、4、5、6、7、8、9、10）表示点要素样式
	 * @function
	 * @memberof bxmap.common
	 * @static
	 * @returns {Object} 格式为{"selected":{},"unselected":{}}
     */
    function getDefaultAbcdPointStyles(){
    	if(defaultAbcdStyleMap == null){
    		//defaultAbcdStyleMap = {"selected":{},"unselected":{}};
    		defaultAbcdStyleMap = {};
    		var path = bxmap.Resource.ResourcePath + "abcd/"
    		var src;
    		//选中样式
    		var selected = defaultAbcdStyleMap["selected"] = {};
    		var selectedPNGs = ["dw1.png","dw2.png","dw3.png","dw4.png","dw5.png","dw6.png","dw7.png","dw8.png","dw9.png","dw10.png"];
    		for(var i = 0; i < selectedPNGs.length; i++){
    			src = path + selectedPNGs[i];
    			var style = new ol.style.Style({
    				image: new ol.style.Icon({
    					size: [23, 25],
    					src: src,
    					anchor: [0.4, 1.1],
    					anchorOrigin: "top-left",
    					anchorXUnits: "fraction",
    					anchorYUnits: "fraction",
    					rotateWithView: false
    				})
    			});
    			selected[i] = style;
    		}
    		
    		//取消选中样式
    		var unselected = defaultAbcdStyleMap["unselected"] = {};
    		var unselectedPNGs = ["dw1x.png","dw2x.png","dw3x.png","dw4x.png","dw5x.png","dw6x.png","dw7x.png","dw8x.png","dw9x.png","dw10x.png"];
    		for(var i = 0; i < unselectedPNGs.length; i++){
    			src = path + unselectedPNGs[i];
    			var style = new ol.style.Style({
    				image: new ol.style.Icon({
    					size: [23, 25],
    					src: src,
    					anchor: [0.4, 1.1],
    					anchorOrigin: "top-left",
    					anchorXUnits: "fraction",
    					anchorYUnits: "fraction",
    					rotateWithView: false
    				})
    			});
    			unselected[i] = style;
    		}
    		
    	}
    	return defaultAbcdStyleMap;
    }

    var waterDepthLabelStyles = {};
    function getWaterDepthLabelStyles(depth){
    	var ss = depth.toString();
        var index = ss.indexOf('.');
        var integer = index > 0 ? ss.substr(0, index) : ss,//整数部分
            dot = index > 0 ? ss.toString().substr(index + 1) : ''; //小数部分
		//整数部分样式
        var style = waterDepthLabelStyles['integer_' + integer];
        if (!style) {
            style = new ol.style.Style({
                text: new ol.style.Text({
                    font: 'oblique 12px arial',
                    text: integer,
                    fill: new ol.style.Fill({
                        color: '#000'
                    })
                })
            });
            waterDepthLabelStyles['integer_' + integer] = style;
        }

        var integerLength = integer.length;
        //小数部分样式
        var dotstyle = waterDepthLabelStyles['dot_' + integerLength + '_'+ dot];
        if (dot && !dotstyle) {
        	var offsetX = 11; //整数2位
			if(integerLength == 1) offsetX = 7; //整数1位
            dotstyle = new ol.style.Style({
                text: new ol.style.Text({
                    font: 'oblique 8px arial',
                    offsetX: offsetX,
                    offsetY: 4,
                    text: dot,
                    fill: new ol.style.Fill({
                        color: '#777777'
                    })
                })
            });
            waterDepthLabelStyles['dot_' + integerLength + '_'+ dot] = dotstyle;
        }

        var styles = [];
        if(style){ styles.push(style); }
        if(dotstyle){ styles.push(dotstyle); }
        return styles;
	}

    /**
     * @description 将地图坐标偏移多少像素
	 * @function
	 * @memberof bxmap.common
	 * @static
	 * @param map {ol.Map} 地图
     * @param coordinate [x,y] 坐标
     * @param deltaX {int | undefined} x方向偏移量
     * @param deltaY {int | undefined} y方向偏移量
     * @returns [x,y]
     */
    function offsetCoordinate(map, coordinate, deltaX, deltaY){
    	if(map == null || coordinate == null) return null;
    	if(deltaX == null && deltaY == null) return coordinate;
    	var dx = deltaX == null ? 0 : deltaX;
    	var dy = deltaY == null ? 0 : deltaY;
    	
    	var pixel = map.getPixelFromCoordinate(coordinate);
    	var coor = map.getCoordinateFromPixel([pixel[0] + dx, pixel[1] + dy]);
    	return coor;
    }
    
    return {
        getCounterUid: getCounterUid, //获取对象计数uid
        getRandomStringUid: getRandomStringUid, //获取对象随机字符串uid
        getContext: getContext,//获取对象自定义数据
        getFeatureIconParams: getFeatureIconParams,//获取要素图片样式参数，包括img/scale/rotation
        getFeatureDetectedParams: getFeatureDetectedParams,//获取要素检测参数，用于click/pointermove检测要素
        createUUID: createUUID, //生成UUID字符串
        exist: exist,//判断元素是否存在
        getDefaultSelectedStyle:getDefaultSelectedStyle,//获取选中要素默认样式
        getDefaultVectorLayerStyle:getDefaultVectorLayerStyle,//获取VectorLayer默认样式
        getDefaultAbcdPointStyles:getDefaultAbcdPointStyles,//获取以（1、2、3、4、5、6、7、8、9、10）表示点要素样式
        getWaterDepthLabelStyles:getWaterDepthLabelStyles,
        offsetCoordinate:offsetCoordinate//坐标偏移dx,dx像素
    }
})();