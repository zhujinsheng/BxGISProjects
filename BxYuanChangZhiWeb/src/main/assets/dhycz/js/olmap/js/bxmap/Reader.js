/**
 * @require ol.js
 */

/**
 * @namespace bxmap.reader
 */
bxmap.reader = bxmap.reader || {};

/**
 * @description 读取geoserver WMS/WMS-C图层
 * @param options
 * @param options.projection {ol.ProjectionLike<ol.proj.Projection>|String|undefined} 坐标系
 * @param options.url {String} 服务地址
 * @param options.tile_grid {ol.tilegrid.TileGrid} 瓦片方案
 * @param options.name {String} 图层名称
 * @param options.version {String} 服务版本1.1.0/1.3.0，默认是1.1.0
 * @param param.format {String} 服务图片格式
 * @static
 * @function
 * @memberof bxmap.reader
 * @retruns {ol.source.TileWMS}
 * @example <caption> 读取WMS图层 </caption>
 * //具体的值需要看geoserver服务提供的能力
 * var layer = bxmap.reader.readGeoserverTileWMS({
 *	projection:"EPSG:3857",
 *	url: "http://demo.opengeo.org/geoserver/wms",
 *	name: "ne:ne_10m_admin_1_states_provinces_lines_shp",
 *	version: "1.3.0",
 *	format: "image/jpeg"
 * });
 * @example <caption> 读取WMS-C图层 </caption>
 * //wms服务和瓦片服务主要区别是：
 * // 1.url wms服务是/wms，瓦片服务是/gwc/service/wms
 * // 2.tilegrid 瓦片服务需要瓦片方案
 * var tilegrid = new ol.tilegrid.TileGrid({
 * 	"origin" : [-2.0037508342787E7,2.0037508342787E7],
 * 	"resolutions" : [156543.03392800014,78271.51696399994,39135.75848200009,...],
 * 	"tileSize" : [256,256]
 * });
 * var layer = bxmap.reader.readGeoserverTileWMS({
 *	projection:"EPSG:3857",
 *	url: "http://demo.opengeo.org/geoserver/gwc/service/wms",
 *	name: "ne:ne_10m_admin_1_states_provinces_lines_shp",
 *	version: "1.3.0",
 *	format: "image/jpeg"
 * });
 */
bxmap.reader.readGeoserverTileWMS = function(options){
	var projection = options["projection"];
	var url = options["url"];
	var tileGrid = options["tile_grid"];
	var layerName = options["name"];
	var version = options["version"];
	var format = options["format"];
	
	var params = {
		"LAYERS" : layerName,
		"TILED" : true,
		"VERSION" : "1.1.0"
	};
	if(version){
		params["VERSION"] = version;
	}
	if(format){
		params["FORMAT"] = format;
	}
	
	//gerserver发布的图层WMS/WMS-C
	var source = new ol.source.TileWMS({
		projection : projection,
		url : url,
		params: params,
		tileGrid : tileGrid
	});

	var layer = new ol.layer.Tile({
		source : source
	});
	
	return layer;
}

/**
 * @description 读取ArcGIS Server 瓦片服务
 * @param options
 * @param options.projection {ol.ProjectionLike<ol.proj.Projection>|String|undefined} 坐标系
 * @param options.url {String} 服务地址
 * @param options.tile_grid {ol.tilegrid.TileGrid} 瓦片方案
 * @param options.dpi {Number} dpi 默认96
 * @static
 * @function
 * @memberof bxmap.reader
 * @returns {ol.layer.Tile}
 *
 * @example <caption> ArcGIS Server 瓦片服务图层 </caption>
 * var tilegrid = new ol.tilegrid.TileGrid({...});
 * var layer = bxmap.reader.readArcGISTile({
 *	projection:"EPSG:3857",
 *	dpi: 96,
 *	url: ".../Specialty/ESRI_StateCityHighway_USA/MapServer",
 *	tile_grid: tilegrid
 * });
 */
bxmap.reader.readArcGISTile = function(options){
	var projection = options["projection"];
	var url = options["url"];
	var tileGrid = options["tile_grid"];
	var dpi = options["dpi"];
	var format = options["format"];
	var params = {
		"DPI": 96//arcgis瓦片常用96，默认96
	};
	if(dpi){
		params["DPI"] = dpi;
	}
	if(format){
		params["FORMAT"] = format;
	}
	
	var source = new ol.source.TileArcGISRest({
		projection : projection,
		url : url,
		params: params,
		tileGrid : tileGrid
	});
	
	var layer = new ol.layer.Tile({
		source : source
	});

	return layer;
}

/**
 * @description 读取ArcGIS Server xyz访问的瓦片服务
 * @param options
 * @param options.projection {ol.ProjectionLike<ol.proj.Projection>|String|undefined} 坐标系
 * @param options.url {String}
 * @param options.tile_grid {ol.tilegrid.TileGrid}
 * @static
 * @function
 * @memberof bxmap.reader
 * @returns {ol.layer.Tile}
 *
 * @example <caption> ArcGIS Server XYZ 瓦片服务图层 </caption>
 * var layer = bxmap.reader.readArcGISTileXYZ({
 *	projection:"EPSG:3857",
 *	url: ".../MMGK_GH/MapServer/tile/{z}/{y}/{x}"
 * });
 */
bxmap.reader.readArcGISTileXYZ = function(options){
	var projection = options["projection"];
	var url = options["url"];
	var tileGrid = options["tile_grid"];
	var tileSize = tileGrid ? tileGrid.getTileSize() : [256,256]
	
	var z;
	var source =  new ol.source.XYZ({
        projection: projection,
        url: url,
        tileSize: tileSize,
        tileGrid: tileGrid,
        tileUrlFunction: function(tileCoord) {
        	//瓦片256×256时
        	z = tileCoord[0];
        	//瓦片512×512时
        	if(tileSize[0] == 512){
        		z = tileCoord[0] - 1;
        	}
        	return url.replace('{z}', (z).toString())
                            .replace('{x}', tileCoord[1].toString())
                            .replace('{y}', (-tileCoord[2] - 1).toString());
        },
        wrapX: true
     });
	
	var layer = new ol.layer.Tile({
		source : source
	});

	return layer;
}

/**
 * @description 读取ArcGIS Server 动态服务
 * @param options
 * @param options.projection {ol.ProjectionLike<ol.proj.Projection>|String|undefined} 坐标系
 * @param options.url {String} 服务地址
 * @static
 * @function
 * @memberof bxmap.reader
 * @returns {ol.layer.Image}
 *
 * @example <caption> ArcGIS Server 动态服务图层 </caption>
 * var layer = bxmap.reader.readArcGISDynamic({
 *	projection:"EPSG:3857",
 *	url: ".../MMGK_GH/MapServer"
 * });
 */
bxmap.reader.readArcGISDynamic = function(options){
	var projection = options["projection"];
	var url = options["url"];
	
	var source = new ol.source.ImageArcGISRest({
		projection:projection,
        ratio: 1.5,
        params: {},
        url: url
	});
	
	var layer = new ol.layer.Image({
		source : source
	});
	
	return layer;
}

/**
 * @description 读取osm服务
 * @param options
 * @param options.url {String|undefined} url服务地址
 * @static
 * @function
 * @memberof bxmap.reader
 * @returns {ol.layer.Tile}
 *
 * @example <caption> osm 服务图层 </caption>
 * var layer = bxmap.reader.readOSM();
 */
bxmap.reader.readOSM = function(options){
	var url = options["url"];
	var source  =new ol.source.OSM();
	if(url && url != ""){
		source = new ol.source.OSM({
            url: url
        });
	}
	
	var layer = new ol.layer.Tile({
        source: source
    });
	return layer;
}

/**
 * @description 读取以xyz方式瓦片服务
 * @param url {String} 服务地址
 * @static
 * @function
 * @memberof bxmap.reader
 * @returns {ol.layer.Tile}
 *
 * @example <caption> 读取天地图服务 </caption>
 * var layer = bxmap.reader.readXYZUrl("http:// t{0-7}.tianditu.com/DataServer?T=vec_w&x={x}&y={y}&l={z}");
 *
 * @example <caption> 读取高德地图服务 </caption>
 * var layer = bxmap.reader.readXYZUrl("http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}");
 */
bxmap.reader.readXYZUrl = function(url){
	var source  = new ol.source.XYZ({
		url : url
	});
	var layer = new ol.layer.Tile({
        source: source
    });
	return layer;
}

/*--------------------------------------------------------------------------*/
/*                 地图图层配置读取器{bxmap.reader.MapConfigReader}         */
/*--------------------------------------------------------------------------*/
/**
 * @classdesc 地图图层配置读取器，用于从配置文件中初始化地图图层、视图、瓦片方案等；
 * @constructor
 * @extends {ol.Object}
 * @param {Object} mapconfig JSON结构数据，节点格式参考{bxmap.config.MapConfig}
 * @param {Array<String>} map_ids 地图配置id，当map_ids=null或map_ids=[]时，读取配置文件所有maps
 */
bxmap.reader.MapConfigReader = function(mapconfig, map_ids){
	ol.Object.call(this, mapconfig);
	
	this.initialize(mapconfig, map_ids);
}
ol.inherits(bxmap.reader.MapConfigReader, ol.Object);

/**
 * @private
 * @description 初始化地图配置信息对象
 * @param mapconfig - {bxmap.config.MapConfig}结构数据
 * @param map_ids - [{String}] 地图配置id
 */
bxmap.reader.MapConfigReader.prototype.initialize = function(mapconfig, map_ids){
	/**
	 * @readonly
	 * @type {Array<ol.tilegrid.TileGrid>}
	 * @description 瓦片参数
	 */
	this.tileGrids = [];
	/**
	 * @readonly
	 * @type {Array.<ol.View>}
	 */
	this.mapViews = [];
	/**
	 * @readonly
	 * @type {Array.<Object>}
	 * @description 地图参数
	 */
	this.maps = [];
	/**
	 * @readonly
	 * @type {Array.<ol.layer.Layer>}
	 * @description 共享图层
	 */
	this.shareLayers = [];
	/**
	 * @readonly
	 * @type {Array.<bxmap.layer.WFSLayer>}
	 * @description WFS图层
	 */
	this.wfsLayers=[];
	
	this._loadingMapId = null;//当前正在初始化的地图id
	this._initializeTileGrid();
	this._initializeViews();
	this._initializeMaps(map_ids);
	
	this._initializeWFS();
}


/**
 * @private
 * @description 初始化wfs集合，生成 bxmap.layer.WFSLayer数组
 */
bxmap.reader.MapConfigReader.prototype._initializeWFS=function(){
	var wfs = this.get("wfs");
	if(wfs == null) return;
	
	//处理WFS公用配置，数组转为字典
	var cfgs = wfs.cfg || [];
	var cfgDict = {};
	var cfg, cfgkey = "cfg_id";
	for(var i = 0; i < cfgs.length; i++){
		cfg = cfgs[i];
		//不存在
		if(!cfgDict[cfg[cfgkey]]){
			cfgDict[cfg[cfgkey]] = cfg;
		}
	}
	
	var wfslayers = wfs.layers || [];
	for(var i = 0,length = wfslayers.length;i < length; i++){
		var wfsData = wfslayers[i];
		//获取公用配置
		cfg = {};
		if(wfsData["cfg"]){
			cfg = cfgDict[wfsData["cfg"]] || {};
		}
		
		var wfsParams = {};
		wfsParams.url = wfsData["url"] || cfg["url"],
		wfsParams.featureNS = wfsData["featureNS"] || cfg["featureNS"],
		wfsParams.srsName = wfsData["srsName"] || cfg["srsName"],
		wfsParams.featurePrefix=wfsData["featurePrefix"] || cfg["featurePrefix"],
		wfsParams.outputFormat =wfsData["outputFormat"] || cfg["outputFormat"];
		wfsParams.geometryName =wfsData["geometryName"];
		wfsParams.featureTypes=wfsData["featureTypes"];
		
		var id = wfsData["layer_id"];
		var name = wfsData["name"];
		var	visible = wfsData["visible"] == null ? true : wfsData["visible"]; //默认可见
		var geometryType = wfsData["geometryType"];
		var lazyLoading = wfsData["lazyLoading"];
		var displayInEditor = wfsData["inEditor"] == null ? true : wfsData["inEditor"];//默认加载可以加载到WFS编辑器中
		var wfsProtocol = new bxmap.protocol.WFS(wfsParams);
		var wfsLayer = new bxmap.layer.WFSLayer({
			wfsProtocol: wfsProtocol,
			id: id,
			name: name,
			visible: visible,
			lazyLoading: lazyLoading,
			geometryType: geometryType
		});
		wfsLayer.set("displayInEditor", displayInEditor);
		this.wfsLayers.push(wfsLayer);
	}
}

/**
 * @private
 * @description 初始化tile_grids集合
 */
bxmap.reader.MapConfigReader.prototype._initializeTileGrid = function(){
	//初始化tile_grids
	var gridCollection = this.tileGrids
	var grids_data = this.get("tile_grids");
	var grid_data,grid;
	for(var i = 0,length = grids_data.length;i < length; i++){
		grid_data = grids_data[i];
		grid = new ol.tilegrid.TileGrid({
			"extent" : grid_data["extent"],
			"origin" : grid_data["origin"],
			"resolutions" : grid_data["resolutions"],
			"tileSize" : grid_data["tile_size"]
		});
		grid[bxmap.CONFIG_TILE_GRID_ID] =grid_data["grid_id"];
		gridCollection.push(grid);
	}
}

/**
 * @private
 * @description 初始化olviews集合
 */
bxmap.reader.MapConfigReader.prototype._initializeViews = function(){
	//初始化views
	var projection = this.get("projection");
	var viewCollection = this.mapViews;
	var views_data = this.get("olviews");
	var view_data,view;
	var init_center,max_zoom,min_zoom,zoom,rotation;
	for(var i=0,length = views_data.length; i<length ;i++){
		view_data = views_data[i];
		init_center = view_data["center"];
		max_zoom = view_data["max_zoom"] ? view_data["max_zoom"] : 21;
		min_zoom = view_data["min_zoom"] ? view_data["min_zoom"] : 0;
		zoom = view_data["zoom"] ? view_data["zoom"] : 0;
		rotation = view_data["rotation"] ? view_data["rotation"] : 0;
		
		view = new ol.View({
			"projection" : projection, 
			"center" : init_center,
			"zoom" : zoom,
			"maxZoom" : max_zoom,
			"minZoom" : min_zoom
		});
		view.set(bxmap.CONFIG_VIEW_ID,view_data["view_id"]);
		view.set("max_zoom", max_zoom);
		view.set("min_zoom", min_zoom);
		view.set("init_zoom", zoom);
		view.set("init_center", init_center);
		view.set("init_rotation", rotation);
		viewCollection.push(view);
	}
}

/**
 * @private
 * @description 初始化地图配置集合
 * @param {Array<String>} map_ids 地图配置id
 */
bxmap.reader.MapConfigReader.prototype._initializeMaps = function(map_ids){
	var mapsObj = this.get("maps");//地图配置项
	var mapCollection = this.maps;
	var layergroupsCollection = this.layergroups;//图层组{ol.Collection.<ol.layer.Group>}
	var mapObj, map, view;
	var exist,mapId;
	for(var i = 0,length = mapsObj.length; i < length; i++){
		mapObj = mapsObj[i];
		mapId = mapObj["map_id"];
		//初始化指定的地图集合map_ids
		if(map_ids && map_ids.length !=0){
			exist = false;
			exist = bxmap.common.exist(mapId,map_ids);
			if(!exist) continue;
		}
		this._loadingMapId = mapId;
		map = this._newMapStructure(mapObj);
		mapCollection.push(map);
		//初始化地图的基础底图
		var layergroupsObj = mapObj["layer_groups"];//图层组配置项
		this._initializeMapBaseLayerGroups(layergroupsObj);
	}
}

/**
 * @private
 * @description 初始化地图基础底图图层组配置
 * @param {Object} layergroupObj 参考图层组{bxmap.config.MapConfig}.maps.layer_groups
 */
bxmap.reader.MapConfigReader.prototype._initializeMapBaseLayerGroups = function(layergroups){
	//获取当前正在初始化的地图
	var loadingMap = this._getLoadingMap();
	if(loadingMap == null) return;
	var layergroupsCollection = loadingMap.layergroups;
	var layergroupObj,layergroup;
	for(var i = 0, length = layergroups.length; i <　length; i++){
		layergroupObj = layergroups[i];
		//根据图层组配置项读取基础底图
		layergroup = this.readLayerGroup(layergroupObj);
		if(layergroup == null) continue;
		//基础底图关联至至地图
		layergroupsCollection.push(layergroup);
	}
}

/**
 * @private
 * @description 创建地图结构
 * @param {Object} mapObj 参考{bxmap.config.MapConfig}.maps[index]
 * @returns {Object}
 */
bxmap.reader.MapConfigReader.prototype._newMapStructure = function(mapObj){
	var map_view = this.getMapView(mapObj["olview"]);
	var map = {
	    "map_id": mapObj["map_id"],
	    "description": mapObj["description"],
	    "layergroups": [],
	    "olview": map_view
	};
	return map;
}

/**
 * @description 读取图层，支持geoserver发布的WMS，瓦片服务；
 * arcgisserver发布的动态服务，瓦片服务，XYZ形式的服务，天地图服务，
 * 高德地图服务，OSM
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Layer}
 */
bxmap.reader.MapConfigReader.prototype.readLayer = function(data){
	var layer = null;
	var server = data["server"];
	switch(server){
		case "geoserver":
			layer = this.readGeoserverTileWMS(data);
			break;
		case "arcgisserver":
			if(data["subtype"] === "dynamic"){
				layer = this.readArcGISDynamic(data);
			}else if(data["subtype"] === "xyz"){
				layer = this.readArcGISTileXYZ(data);
			}else{
				layer = this.readArcGISTileRest(data);
			}
			break;
		case "osm":
			layer = this.readOSM(data);
			break;
		case "tianditu":
			layer = this.readTianditu(data);
			break;
		case "gaode":
			layer = this.readGaode(data);
			break;
		default:
			break;
	}
	if(layer){
		layer.setVisible((data["visible"] == null ? true:data["visible"]));//是否可见
		layer.setZIndex((data["level"] == null ? 0:data["level"]));//设置图层等级
		layer.set(bxmap.CONFIG_LAYER_ID,data["layer_id"]);//图层id
		layer.set("swipetype",data["swipetype"]);//swipetype，有效值left,right
	}
	return layer;
}

/**
 * @description 根据图层组配置项读取图层组
 * @param {Object} data 参考{bxmap.config.MapConfig}.maps.layer_groups[index]
 * @returns {Object} 格式是{"group_id": {String},"layers": []}
 */
bxmap.reader.MapConfigReader.prototype.readLayerGroup = function (data) {
	var config_groupid = data["group_id"];
	var config_layers = data["layers"];
	
	if(config_layers == null || config_layers.length == 0) return null;
	//根据第一个值类型判断是否具有图层控制的能力
	//字符串类型不具备图层控制的能力,指定配置参数的对象类型具有图层控制的能力
	var data0 = config_layers[0];
	var switcher_capability = (typeof data0 == 'object' && data0 != null ? true : false);

    var group = {
    	"group_id": config_groupid,
    	"layers": []
    };
    if(switcher_capability){
    	//具有图层控制的能力
    	group["layers"] = this._readSwitcherLayersByGroupId(config_groupid,config_layers);
    }else{
    	//不具有图层控制的能力，字符串类型[{String}]
    	group["layers"] = this._readLayersByIDs(config_layers);	
    }
    return group;
}

/**
 * @private
 * @description 根据图层控制器格式数据读取图层组，该方法具备图层控制器的能力
 * @param {String} group_id
 * @param {Array<Object>} switcherLayers 格式如下
 * 		图层组配置 - {"id":12,"pid":1,"title":"图层组示例","tip":"图层组示例","visible":true}
 * 		图层配置 - {"id":12,"pid":1,"title":"图层","tip":"图层","visible":true,"layers":["layer_id1",...]}
 * @returns {Array<ol.layer.layer>}
 */
bxmap.reader.MapConfigReader.prototype._readSwitcherLayersByGroupId = function(group_id, switcherLayers){
	//获取当前正在初始化的地图
	var loadingMap = this._getLoadingMap();	
	
	var layerArray=[];//用于返回底图图层组
	var setting = {"id":"id","pid":"pid","title":"title","tip":"tip","visible":"visible","layers":"layers","inswitcher":"inswitcher","owned_base_group":"owned_base_group","category":"bxmap.LAYER_SWITCHER_CATEGORY"};
	var isLayerGroup = false;
	var displayInSwitcher = true;//是否在图层控制器上显示
	var layer,layerObj,layersProp;
	for(var i=0,length = switcherLayers.length; i<length;i++){
		layerObj = switcherLayers[i];
		displayInSwitcher = layerObj[setting.inswitcher] == null ? true: layerObj[setting.inswitcher];
		//根据layers属性判断是否图层组判断，如果对象带有layers属性则为逻辑图层组
		layersProp = layerObj[setting.layers];
		isLayerGroup = (layersProp == null || layersProp.length == 0) ? true:false;
		if(isLayerGroup){
			layer = new ol.layer.Group();
			var catagory = bxmap.LAYER_SWITCHER_CATEGORY_GROUP;
			//是否为根节点判断，根节点则为地图类型
			if(layerObj[setting.pid] == bxmap.LAYER_SWITCHER_ROOT){
				catagory = bxmap.LAYER_SWITCHER_CATEGORY_MAP;
			}
			layer.set(setting.category,catagory);
		}else{
			//多个实际图层构造一个逻辑图层
			var layers = this._readLayersByIDs(layersProp);
			layer = new ol.layer.Group({
		        layers: layers
		    });
			layer.set(setting.category,bxmap.LAYER_SWITCHER_CATEGORY_LAYER);
		}
		if(layer){
			//图层控制需要的参数设置
			layer.set(setting.inswitcher,displayInSwitcher);//是否显示在图层控制器上
			layer.set(setting.owned_base_group,group_id);//所属图层组
			layer.set(setting.id,layerObj[setting.id]);//图层id
			layer.set(setting.pid,layerObj[setting.pid]);//父图层id
			layer.set(setting.title,layerObj[setting.title]);//显示名称
			layer.set(setting.tip, layerObj[setting.tip] ? layerObj[setting.tip]:"");//提示信息
			layer.setVisible(layerObj[setting.visible] == null ? true:layerObj[setting.visible]);
			//基础底图图层集合
			layerArray.push(layer);
		}
	}
    return layerArray;
}

/**
 * @private
 * @description 根据图层ID数组读取图层组，该方法不具备图层控制器的能力
 * @param {Array<String>} layersIDs
 * @returns {Array<ol.layer.layer>}
 */
bxmap.reader.MapConfigReader.prototype._readLayersByIDs = function(layersIDs){
	//获取当前正在初始化的地图
	var loadingMap = this._getLoadingMap();	
	var layer, layerArray = [], layer_id;//图层对象{ol.layer.Layer}
	for(var i=0,length=layersIDs.length; i<length;i++){
    	layer_id = layersIDs[i];
    	var layersObj = this.get("layers");
    	var layerObj,
    		isShareLayer = false;//不是共享图层;
    	for(var j = 0, jlength = layersObj.length;j < jlength;j++){
    		layerObj = layersObj[j];
    		if(layerObj["layer_id"] == layer_id){
    			isShareLayer = layerObj["share"] == null ? false : layerObj["share"];
    			break;
    		}
    	}
    	//共享图层
    	if(isShareLayer){
    		layer = this.getShareLayer(layer_id);
    	}
    	if(layer == null){
    		layer = this.readLayer(layerObj);
    	}

        if(layer){
        	//基础底图图层集合
        	layerArray.push(layer);
        	//共享图层
        	if(isShareLayer){
        		this.shareLayers.push(layer);
        	}
        }
    }
    return layerArray;
}

/**
 * @description 根据ID获取tilegrid切片方案
 * @param {String} grid_id 切片方案id
 * @returns {ol.tilegrid.TileGrid}
 */
bxmap.reader.MapConfigReader.prototype.getTileGrid = function(grid_id){
	if(grid_id == null) return null;
	var output = null;
	var grids = this.tileGrids;
	var grid;
	for(var i=0, length = grids.length; i<length; i++){
		grid = grids[i];
		if(grid_id == grid[bxmap.CONFIG_TILE_GRID_ID]){
			output = grid;
			break;
		}
	}
	return output;
}

/**
 * @description 根据ID获取mapview
 * @param {String} view_id 配置的视图id
 * @returns {ol.View}
 */
bxmap.reader.MapConfigReader.prototype.getMapView = function(view_id){
	var output = null;
	var views = this.mapViews;
	var view;
	for(var i=0, length = views.length; i<length; i++){
		view = views[i];
		if(view_id == view.get(bxmap.CONFIG_VIEW_ID)){
			output = view;
			break;
		}
	}
	return output;
}

/**
 * @description 根据ID获取地图配置信息
 * @param {String} map_id 配置的地图id
 * @returns {Object} 格式如：
 * {
 * 	"map_id": {String},
 *  "description": {String},
 *  "layer_groups": [{Object}],
 *  "olview": {ol.View}
 * }
 */
bxmap.reader.MapConfigReader.prototype.getMap = function(map_id){
	var output = null;
	var maps = this.maps;
	var map;
	for(var i=0, length=maps.length; i<length; i++){
		map = maps[i];
		if(map_id == map["map_id"]){
			output = map;
			break;
		}
	}
	return output;
}

/**
 * @description 根据ID获取共享图层
 * @param {String} layer_id 图层id
 * @returns {ol.layer.Layer}
 */
bxmap.reader.MapConfigReader.prototype.getShareLayer = function(layer_id){
	var output = null;
	//从集合中获取图层
	var layers = this.shareLayers;
	var layer;
	for(var i=0, length = layers.length; i<length; i++){
		layer = layers[i];
		if(layer_id == layer.get(bxmap.CONFIG_LAYER_ID)){
			output = layer;
			break;
		}
	}
	
	return output;
}

/**
 * @private
 * @description 获取当前正在初始化的地图
 */
bxmap.reader.MapConfigReader.prototype._getLoadingMap = function(){
	var loadingMapId = this._loadingMapId;
	//获取当前正在初始化的地图
	var loadingMap = this.getMap(loadingMapId);
	return loadingMap;
}

/**
 * @private
 * @description 获取图层url
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {String}
 */
bxmap.reader.MapConfigReader.prototype._getLayerUrl = function(data){
	var url = data["url"] ? data["url"]:"";
	//完整的绝对路径
	if(url.indexOf("http://") == 0 || url.indexOf("https://") == 0){
		//TODO 使用完整路径
	}
	//使用geoserver_url + url 组装
	else{
        var server_url = data["server"] + "_url";//geoserver_url/arcgisserver_url
        server_url = this.get(server_url) ? this.get(server_url) : "";
        url = server_url + url;
	}

	return url;
}

/**
 * @description 读取geoserver发布的切片图层
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readGeoserverTileWMS = function(data){
	var url = this._getLayerUrl(data);
	var projection = this.get("projection");
	var tileGrid = this.getTileGrid(data["tile_grid"]);
	var layername = data["name"];
	var version = data["version"];
	var format = data["format"];

	var layer = bxmap.reader.readGeoserverTileWMS({
		projection: projection,
		url: url,
		tile_grid: tileGrid,
		name: layername,
		version: version,
		format: format
	});
	return layer;
}

/**
 * @description 读取ArcGIS Server 瓦片服务
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readArcGISTileRest = function(data){
	var url = this._getLayerUrl(data);
	var projection = this.get("projection");
	var tileGrid = this.getTileGrid(data["tile_grid"]);
	var dpi = data["dpi"];
	var format = data["format"];
	
	var layer = bxmap.reader.readArcGISTile({
		projection: projection,
		url: url,
		tile_grid: tileGrid,
		dpi: dpi,
		format: format
	});
	return layer;
}

/**
 * @description 读取ArcGIS Server xyz访问瓦片服务
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readArcGISTileXYZ = function(data){
	var url = this._getLayerUrl(data);
	var projection = this.get("projection");
	var tileGrid = this.getTileGrid(data["tile_grid"]);
	
	var layer = bxmap.reader.readArcGISTileXYZ({
		 projection: projection,
		 url: url,
		 tile_grid: tileGrid
	});
	return layer;
}

/**
 * @description 读取ArcGIS Server 动态服务
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readArcGISDynamic = function(data){
	var url = this._getLayerUrl(data);
	var projection = this.get("projection");
	
	var layer = bxmap.reader.readArcGISDynamic({
		projection: projection,
		url: url
	});
	return layer;
}

/**
 * @description 读取osm服务
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readOSM = function(data){
	var url = this._getLayerUrl(data);
	var layer = bxmap.reader.readOSM({url:url});
	return layer;
}

/**
 * @description 读取天地图服务
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readTianditu = function(data){
	var url = this._getLayerUrl(data);
	var layer = bxmap.reader.readXYZUrl(url);
	return layer;
}

/**
 * @description 读取高德地图瓦片
 * @param {Object} data 参考{bxmap.config.MapConfig}.layers[index]
 * @returns {ol.layer.Tile}
 */
bxmap.reader.MapConfigReader.prototype.readGaode = function(data){
	var url = this._getLayerUrl(data);
	var layer = bxmap.reader.readXYZUrl(url);
	return layer;
}

/*--------------------------------------------------------------------------*/
/*               地图工具条配置读取器{bxmap.reader.ToolConfigReader}               */
/*--------------------------------------------------------------------------*/
/**
 * @classdesc 地图工具条配置读取器，用于从配置中读取工具条的控制信息（显示/隐藏）
 * @param {Object} toolConfig {bxmap.config.ToolConfig}结构，必须存在id,pid
 */
bxmap.reader.ToolConfigReader = function(toolConfig){
	ol.Object.call(this);
	this.keyValuePair = {};
	
	this.initialize(toolConfig);
}
ol.inherits(bxmap.reader.ToolConfigReader, ol.Object);

/**
 * @private
 * @description 初始化
 */
bxmap.reader.ToolConfigReader.prototype.initialize = function(toolConfig){
	if(toolConfig == null) return;
	
	var key, item;
	for(var i=0, length=toolConfig.length; i<length;i++){
		item = toolConfig[i];
		key = item["id"];
		this.keyValuePair[key] = item;
	}
}