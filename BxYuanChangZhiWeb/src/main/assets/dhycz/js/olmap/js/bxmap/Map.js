/**
 * @require ol.js
 * @require Base.js
 * @require Layer.js
 * @require Resource.js
 */

/*----------------------地图类{bxmap.Map}---------------------*/
/**
 * 地图类，实现了地图联动功能，可以设置地图是否具有联动能力
 * 
 * @param options
 *            target - {Element|String}地图dom
 *            container - {bxmap.layer.LayerContainer}形式的对象
 *            linkable - boolean 是否可联动,默认为true
 */
bxmap.Map = function(options) {
	ol.Object.call(this);
	
	/**
	 * @private
	 * @description 设置
	 */
	this.setting = {
		"bx_add_mouse_layer":"bx_add_mouse_layer",//是否已添加鼠标样式图层
		"wrap":"wrap"//地图包装类
	};
	/**
	 * @readonly
	 * @description 存储临时变量
	 */
	this._tempVars = {};
	/**
	 * @private
	 * @description ol地图对象
	 * @type {ol.Map}
	 */
	this._map = null; //{ol.Map}
	/**
	 * @public
	 * @description 清除对象容器
	 * @type {bxmap.DataClear}
	 */
	this.dataClear = null;//new bxmap.DataClear();
	
	var opt_options = options || {};
	this._layerContainer = opt_options.container; //{bxmap.layer.LayerContainer}
	this._linkable = opt_options.linkable == undefined ? true :opt_options.linkable;
	if(this._layerContainer != null){
		var view = this._layerContainer.getView();
		this.initializeMap(opt_options.target, view);
		this.updateBaseLayerGroup(this._layerContainer);
	}
	this._linkMouseStyleLayer = null;//地图联动鼠标样式
}
ol.inherits(bxmap.Map, ol.Object);

/**
 * 根据配置文件创建地图
 * @param target target - {Element|String}地图dom
 * @param mapId 配置文件{bxmap.config.MapConfig}中map_id
 * @returns {bxmap.Map} 返回地图
 */
bxmap.Map.createDefaultMap = function(target, mapId){
	var reader = new bxmap.reader.MapConfigReader(bxmap.config.MapConfig, [mapId]);
	var container = new bxmap.layer.DefaultLayerContainer({
		"reader": reader,
		"map_id": mapId
	});
	var bmap = new bxmap.Map({
		"target" : target,
		"container" : container
	});
	return bmap;
}

/**
 * 初始化
 * 
 * @param options
 *            target - 地图dom
 */
bxmap.Map.prototype.initializeMap = function(target, view) {
	var target = target ? target : 'map';
	var map = this._map = new ol.CanvasMap({
		controls : ol.control.defaults({
			attribution : false, // 禁用默认属性
			zoom : false// 禁用默认zoom控件
		}),
		interactions : ol.interaction.defaults({
			shiftDragZoom : false,// 禁用shift矩形放大功能
			doubleClickZoom: false //禁用双击放大，由于双击放大地图为找到监听事件
		}),
		loadTilesWhileInteracting: true,
		target : target,
		view : view
	});

	map.getViewport().setAttribute('tabindex', '0');
	
	//map.addControl(new ol.control.ZoomSlider());
	//初始化添加鼠标样式图层为fasle
	map.set(this.setting.bx_add_mouse_layer,false);
	//包装对象
	map.set(this.setting.wrap,this);
}

///**
// * 勿删 初始化联动地图鼠标样式
// */
//bxmap.Map.prototype.getLinkMouseStyle = function(){
//	if(this._linkMouseStyle == null){
//		this._linkMouseStyle = new ol.interaction.Draw({
//		    type: 'Polygon',
//		    source: bxmap.layer.Defaults.getTempLayer().getSource(),
//		    style: new ol.style.Style({
//		        image: new ol.style.Icon({
//		           anchor: [0.5, 46],
//		           anchorXUnits: 'fraction',
//		           anchorYUnits: 'pixels',
//		           src: 'https://openlayers.org/en/v3.19.0/examples/data/icon.png'
//		        })
//		    })
//		});
//	}
//	
//	return this._linkMouseStyle;
//}

/**
 * 获取地图联动鼠标样式图层
 */
bxmap.Map.prototype.getLinkMouseStyle = function(){
	if(this._linkMouseStyleLayer == null){
		var mouseFeature = new ol.Feature();
		var layer = this._linkMouseStyleLayer = new ol.layer.Vector({
			source: new ol.source.Vector({
		    	features: [mouseFeature]
		    }),
		    style: new ol.style.Style({
				image: new ol.style.Icon({
					anchor: [0.5, 0.5],
					scale: 0.8,
					src: bxmap.Resource.LinkedMousePng
				})
			})
		});
		layer.setZIndex(bxmap.CONFIG_LEVEL_MAX * 10);
	}
	return this._linkMouseStyleLayer;
}


/**
 * 获取地图
 * @returns {ol.Map}
 */
bxmap.Map.prototype.getMap = function() {
	return this._map;
}

/**
 * 获取当前的图层配置
 * @returns {bxmap.layer.LayerContainer}
 */
bxmap.Map.prototype.getLayerContainer = function(){
	return this._layerContainer;
}

/**
 * 获取基础底图
 * @returns {ol.layer.Group}
 */
bxmap.Map.prototype.getBaseLayerGroup = function(){
	return this.get("bx_default_layer_group");
}

/**
 * @description 获取BaseLayerGroup中图层
 * @returns {Array.<ol.layer.Layer>} 图层数组
 */
bxmap.Map.prototype.getBaseLayers = function(){
	var layerGroup = this.getBaseLayerGroup();
	return this.getLayersInLayerGroup(layerGroup);
}

/**
 * @description 获取LayerGroup中图层，如果group中存在layergroup则递归遍历layergroup中的图层
 * @param {ol.layer.Group} 图层组
 * @returns {Array.<ol.layer.Layer>} 图层数组
 */
bxmap.Map.prototype.getLayersInLayerGroup = function(group){
	var layers = [];
	if(group == null) return layers;
	var collection = group.getLayers();
	collection.forEach(function(layer, index, array){
		//图层
		if(layer instanceof ol.layer.Layer){
			layers.push(layer);
		}
		//图层组
		else if(layer instanceof ol.layer.Group){
			var glayers = this.getLayersInLayerGroup(layer);
			layers = layers.concat(glayers);
		}
	},this);
	return layers;
}

/**
 * 获取当前地图是否可联动
 * @returns boolean
 */
bxmap.Map.prototype.getLinkable = function(){
	return this._linkable;
}

/**
 * 设置是否可联动
 * @param linkable boolean
 */
bxmap.Map.prototype.setLinkable = function(linkable){
	var last = this._linkable;
	this._linkable = linkable;
	if(last == true){
		switch(linkable){
			case true:
				break;
			case false:
				this._unLinkEvent();
				break;
		}
	}else if(last == false){
		switch(linkable){
			case true:
				this._onLinkEvent();
				break;
			case false:
				break;
		}
	}
}

/**
 * 取消联动事件
 */
bxmap.Map.prototype._unLinkEvent = function(){
	var map = this._map;
	var links = map.get(bxmap.MAP_LINKS) || [];
	var tmap = null;
	for(var i = 0; i < links.length; i++){
		tmap = links[i].getMap();
		tmap.un([ 'moveend', 'pointerdrag' ], this._linkEvent);
		tmap.un('pointermove',this._pointerMove);
	}
}

/**
 * 订阅联动事件
 */
bxmap.Map.prototype._onLinkEvent = function(){
	var map = this._map;
	var links = map.get(bxmap.MAP_LINKS) || [];
	var bmap = null;
	var is_add_mouse_layer = this.setting.bx_add_mouse_layer;
	for(var i = 0; i < links.length; i++){
		bmap = links[i];
		//判断关联地图是否支持联动
		var tmap = null;
		if(bmap.getLinkable()){
			tmap = bmap.getMap();
			var mouselayer = bmap.getLinkMouseStyle();
			if(tmap.get(is_add_mouse_layer) == false){
				tmap.addLayer(mouselayer);
				tmap.set(is_add_mouse_layer,true);
			}
			tmap.un([ 'moveend', 'pointerdrag' ], this._linkEvent, this);
			tmap.un('pointermove',this._pointerMove, this);
			tmap.on([ 'moveend', 'pointerdrag' ], this._linkEvent, this);
			tmap.on('pointermove',this._pointerMove, this);
		}
	}
}

/**
 * 联动地图，关联顺序不重要，只要能关联即可
 * 
 * @param bmap {bxmap.Map}
 */
bxmap.Map.prototype.linkPosition = function(bmap) {
	// 设置关联关系
	this.bindMap(bmap);
	// 订阅联动事件
	this._onLinkEvent();
}

/**
 * 绑定联动地图，设置关联关系
 * 
 * @param bmap {bxmap.Map}
 */
bxmap.Map.prototype.bindMap = function(bmap){
	//获取所有联动地图
	var data = this.getMergeLinks(this,bmap);
	
	for(var i = 0; i < data.length; i++){
		this._setTargetLinks(data, i);
	}
}

/**
 * 获取融合去重后的联动地图集合
 * @param bmapA {bxmap.Map}
 * @param bmapB {bxmap.Map}
 * @returns  {Array} //合并后联动地图的数组
 */
bxmap.Map.prototype.getMergeLinks = function(bmapA, bmapB){
	var map = bmapA.getMap();
	var olmap = bmapB.getMap();
	
	// map添加联动地图
	var A = map.get(bxmap.MAP_LINKS) || [];
	A.push(bmapB);

	// olmap添加联动地图
	var B = olmap.get(bxmap.MAP_LINKS) || [];
	B.push(bmapA);
	
	//处理A关联B，B关联C...的情况
	//A+B去重
	var links = A.concat(B);
	//{"1":{bx.Map}}
	var links_uids = {
		"total":[]
	};
	for(var i = 0;i < links.length; i++){
		var obj = links[i];
		//随机id
		var uid = bxmap.common.getRandomStringUid(obj);
		
		if(links_uids[uid] == null){
			links_uids["total"].push(obj);
			links_uids[uid] = obj;
		}
	}
	return links_uids["total"];
}

/**
 * 设置联动地图 到 主地图对象
 * @param mergeLinkObj 联动地图数据
 * @param index 主地图索引
 */
bxmap.Map.prototype._setTargetLinks = function(mergeLinks, index){
	//if(index < 0) return;
	var bmap = mergeLinks[index];
	var map = bmap.getMap();
	var links = [];
	//将主地图提前至第一个要素
	if(index == 0){
		links = links.concat(mergeLinks);
	}else if(index == mergeLinks.length - 1){
		var part = mergeLinks.slice(0, mergeLinks.length - 1);
		links = [bmap].concat(part);
	}else{
		var partA = mergeLinks.slice(0, index);
		var partB = mergeLinks.slice(index + 1);
		links = [bmap].concat(partA).concat(partB);
	}
	map.set(bxmap.MAP_LINKS, links);
}

/**
 * 联动事件
 * 
 * @param e
 */
bxmap.Map.prototype._linkEvent = function(e) {
	var links = e.target.get(bxmap.MAP_LINKS);//links[{bx.Map}]
	if (links === undefined)
		return;

	if (links instanceof Array && links.length > 0) {
		var bmap = links[0];
		var mainMap = bmap.getMap();
		
		// 获取view参数
		var mapview = mainMap.getView();
		var rotation = mapview.getRotation();
		var zoom = mapview.getZoom();
		var center = mapview.getCenter();
		// 设置为主地图
		mainMap.set(bxmap.IS_MAIN_MAP, true);
		
		var exMap, exMapView;
		for ( var i = 1; i < links.length; i++) {
			bmap = links[i];
			exMap = bmap.getMap();
			exMap.set(bxmap.IS_MAIN_MAP, false);
			exMapView = exMap.getView();
			exMapView.setRotation(rotation);
			exMapView.setZoom(zoom);
			exMapView.setCenter(center);
		}
	}
}

/**
 * pointer移动事件，与鼠标移动事件不完全一样
 * 
 * @param e
 */
bxmap.Map.prototype._pointerMove = function(e){
	var coor = e.coordinate;
	var links = e.target.get(bxmap.MAP_LINKS);//links[{bx.Map}]
	if (links === undefined)
		return;

	var is_add_mouse_layer = this.setting.bx_add_mouse_layer;
	if (links instanceof Array && links.length > 0) {
		var tmap = links[0];
		//移除样式图层
		var mainMap = tmap.getMap();
		var tlayer = tmap.getLinkMouseStyle();
		mainMap.removeLayer(tlayer);
		mainMap.set(is_add_mouse_layer,false);
		
		var exMap;
		for (var i = 1; i < links.length; i++) {
			tmap = links[i];
			exMap = tmap.getMap();
			
			//添加鼠标样式图层
			tlayer = tmap.getLinkMouseStyle();
			if(exMap.get(is_add_mouse_layer) == false){
				exMap.addLayer(tlayer);
				exMap.set(is_add_mouse_layer,true);
			}
			
			var features = tlayer.getSource().getFeatures();
			features[0].setGeometry(new ol.geom.Point(coor));
		}
	}
}

/**
 * 使用图层容器对象更新基础底图，参数为null时使用this.getLayerContainer()
 * @param layerContainer {bxmap.layer.LayerContainer}
 */
bxmap.Map.prototype.updateBaseLayerGroup = function(layerContainer){
	var map = this.getMap();
	if(map == null) return;
	if(layerContainer == null){
		layerContainer = this.getLayerContainer();
	}
	//获取地图底图
	var lastBaseGroup = layerContainer.getLastBaseLayerGroup();
	var currentBaseGroup = layerContainer.getCurrentBaseLayerGroup();
	if(currentBaseGroup == null) return;
	
	//使用当前groups作为底图
	var baseLayers = currentBaseGroup.getLayers();
	var defaultLayerGroup;
	if(lastBaseGroup == null){
		defaultLayerGroup = new ol.layer.Group({
			layers : baseLayers
		});

    this.set("bx_default_layer_group", defaultLayerGroup);
    map.addLayer(defaultLayerGroup);
    //若鹰眼与主地图同步，则使用setLayerGroup
    //map.setLayerGroup(defaultLayerGroup);
	}else{
		//更新底图
		defaultLayerGroup = this.get("bx_default_layer_group");
		defaultLayerGroup.setLayers(baseLayers);
	}
	
	this._layerContainer = layerContainer;
}

/**
 * 添加并激活交互工具
 * @param interaction {ol.interaction.Interaction}
 */
bxmap.Map.prototype.addMutexInteraction = function(interaction){
	var map =this.getMap();
	if(map){
		map.addInteraction(interaction);
		this.setCurrentMutexInteraction(interaction);
	}
}

/**
 * 获取当前正在操作的互斥交互工具
 * @param interaction {ol.interaction.Interaction}
 */
bxmap.Map.prototype.getCurrentMutexInteraction = function(){
	return this.get("current_mutex_interaction");
}

/**
 * 激活互斥交互工具
 * @param interaction {ol.interaction.Interaction}
 */
bxmap.Map.prototype.setCurrentMutexInteraction = function(interaction){
	var lastTool = this.get("current_mutex_interaction");
	if(lastTool){
		lastTool.setActive(false);
	}
	
	if(interaction == null) return;
	interaction.setActive(true);
	this.set("current_mutex_interaction",interaction);
}

/**
 *  获取带有索引的交互工具
 * @param index {String}
 * @returns {ol.interaction.Interaction|null}
 */
bxmap.Map.prototype.getIndexInteraction = function(index){
	var indexInteractions = this.get("bxmap_index_interactions") || {};
	var interaction = indexInteractions[index];
	if(interaction == null && this.getMap()){
		var arr = this.getMap().getInteractions().getArray();
		for(var i = 0; i < arr.length ; i++){
			if(index == arr[i].get(bxmap.INDEX_INTERACTION_ID)){
				interaction = arr[i];
				indexInteractions[index] = interaction;
				break;
			}
		}
	}
	return interaction;
}

/**
 *  移除交互工具
 * @param interaction {ol.interaction.Interaction}
 * @returns {Boolean} 移除成功返回true
 */
bxmap.Map.prototype.removeInteraction = function(interaction){
	if(interaction == null) return;
	var indexInteractions = this.get("bxmap_index_interactions") || {};
	var index = interaction.get(bxmap.INDEX_INTERACTION_ID);
	if(index != null) {
		delete indexInteractions[index];
	}
	//移除工具
	if(this.getMap()){
		this.getMap().removeInteraction(interaction);
	}
	return true;
}

/**
 * 添加控件
 * @param control {ol.control.Control}
 */
bxmap.Map.prototype.addControl = function(control){
	var map = this.getMap();
	if(map == null || control == null) return;
	
	if(map){
		map.addControl(control);
	}
}

/**
 * 移除控件
 * @param control {ol.control.Control}
 */
bxmap.Map.prototype.removeControl = function(control){
	var map = this.getMap();
	if(map == null || control == null) return;
	
	if(map){
		map.removeControl(control);
	}
}

/**
 * 根据控件id获取控件
 * @param control {ol.control.Control}
 */
bxmap.Map.prototype.getIndexControl = function(index){
	var control = null;
	if(this.getMap()){
		var controls = this.getMap().getControls().getArray();
		for(var i = 0; i < controls.length ; i++){
			if(index == controls[i].get(bxmap.INDEX_CONTROL_ID)){
				control = controls[i];
				break;
			}
		}
	}
	return control;
}

/**
 * 平移
 * @param orientation {String} 方向 up|down|left|right 
 */
bxmap.Map.prototype.pan = function(orientation){
	var pixelDelta = 128;
	
	var map = this.getMap();
	var view = map.getView();
	var mapUnitsDelta = view.getResolution() * pixelDelta;
	var deltaX = 0, deltaY = 0;
	switch(orientation){
		case "up":
			deltaY = mapUnitsDelta;
			break;
		case "down":
			deltaY = -mapUnitsDelta;
			break;
		case "left":
			deltaX = -mapUnitsDelta;
			break;
		case "right":
			deltaX = mapUnitsDelta;
			break;
	}
	var currentCenter = view.getCenter();
	var center = view.constrainCenter([currentCenter[0] + deltaX, currentCenter[1] + deltaY]);
    view.animate({
        center: center,
        duration: 500,
        easing: ol.easing.easeOut
    });
}

/**
 * 缩放至全图
 */
bxmap.Map.prototype.zoomFullExtent = function(){
	var map = this.getMap();
	var view = map.getView();
	var curZoom = view.getZoom();
	var minZoom = view.get("min_zoom");
	if(curZoom !== minZoom){
		view.setZoom(minZoom);	
	}
	var center = view.get("init_center");
	//bxmap.common.panAnimationToCenter(map, center);
    view.animate({
        center: view.constrainCenter(center),
        duration: 500,
        easing: ol.easing.easeOut
    });
}

/**
 * 缩放至初始位置
 */
bxmap.Map.prototype.zoomInitialPosition = function(){
	var map = this.getMap();
	var view = map.getView();
	var initRotation = view.get("init_rotation");
	var curZoom = view.getZoom();
	var initZoom = view.get("init_zoom");
	view.setRotation(initRotation);
	if(curZoom !== initZoom){
		view.setZoom(initZoom);	
	}
	var center = view.get("init_center");
	//bxmap.common.panAnimationToCenter(map, center);
    view.animate({
        center: view.constrainCenter(center),
        duration: 500,
        easing: ol.easing.easeOut
    });
}

/**
 * @description 缩放到范围
 * @param extent [minx,miny,maxx,maxy] 要素或者几何
 */
bxmap.Map.prototype.zoomToExtent = function(extent){
	if(extent == null) return;
	
	var map = this.getMap();
	var view = map.getView();
	
	view.fit(extent, {
        size: map.getSize()
    });
}

/**
 * @description 缩放至要素/几何
 * @param feature {ol.Feature | ol.geom.Geometry} 要素或者几何
 */
bxmap.Map.prototype.zoomToFeature = function(feature){
	if(feature == null) return;
	
	var geometry = feature;
	if(geometry instanceof ol.Feature){
		geometry = geometry.getGeometry();
	}
	var map = this.getMap();
	var view = map.getView();
	var geomType = geometry.getType();
	if(geomType == "Point"){
		var minZoom = view.get("min_zoom");
		view.setZoom(minZoom + 2);
		view.setCenter(center);
	}else{
		view.fit(geometry.getExtent(),{
			size: map.getSize()
		});
	}
}

/**
 * @description 缩放至矢量图层/数据源
 * @param layer {ol.layer.Vector | ol.source.Vector} 矢量图层或矢量数据源
 */
bxmap.Map.prototype.zoomToLayer = function(layer){
	if(layer == null) return;
	
	var source = layer;
	if(source instanceof ol.layer.Vector){
		source = source.getSource();
	}
	//无数据
	if(source.getFeatures().length == 0) return;
	
	//只有useSpatialIndex=true时，才可执行source.getExtent()
	var extent;
	try{
		extent = source.getExtent();
	}catch(e){
		//TODO 不做处理
	}
	
	if(!extent){
		var features = source.getFeatures();
		var ext0 = features[0].getGeometry().getExtent();
		extent = [ext0[0],ext0[1],ext0[2],ext0[3]];
		for(var i = 0, geometry; i < features.length; i++){
			geometry = features[i].getGeometry();
			ol.extent.extend(extent, geometry.getExtent());
		}
	}
	
	var map = this.getMap();
	var view = map.getView();
	view.fit(extent, {
        size: map.getSize()
    });
}

/**
 * 添加带有索引的图层
 * @param layer - {ol.layer.Layer} 带有索引图层
 * @returns {Boolean} 添加成功返回true,否则返回false
 */
bxmap.Map.prototype.addIndexLayer = function(layer){
	if(!this.getMap() || layer == null) return false;
	var index = layer.get(bxmap.INDEX_LAYER_ID);
	if(!index) return false;
	
	var indexLayers = this.get("bxmap_index_layers");
	if(indexLayers == null){
		indexLayers = {};
		this.set("bxmap_index_layers", indexLayers);
	}
	indexLayers[index] = layer;
	//添加到地图
	this.getMap().addLayer(layer);
	
	return true;
}

/**
 * 移除带有索引的图层
 * @param layer - {ol.layer.Layer} 带有索引图层
 * @returns {Boolean} 添加成功返回true,否则返回false
 */
bxmap.Map.prototype.removeIndexLayer = function(layer){
	var index = layer.get(bxmap.INDEX_LAYER_ID);
	if(!index) return false;
	var indexLayers = this.get("bxmap_index_layers") || {};
	delete indexLayers[index];
	//移除地图
	if(this.getMap()){
		this.getMap().removeLayer(layer);
	}
	
	return true;
}

/**
 * 获取带有索引图层
 * @param index - {String} 图层索引
 * @returns {ol.layer.Layer} 查找失败返回null
 */
bxmap.Map.prototype.getIndexLayer = function(index){
	var indexLayers = this.get("bxmap_index_layers") || {};
	return indexLayers[index];
}
/**
 * 获取地图配置信息
 * @return {bxmap.reader.MapConfigReader}
 */
bxmap.Map.prototype.getMapConfig=function(){
	return this._layerContainer.reader;
}

/**
 * @description 设置地图鼠标样式
 * @param {String} cursor  {element.style.cursor} --样式，如果为undefined则为上一个使用的样式
 */
bxmap.Map.prototype.setCursor = function(cursor){
	var map = this.getMap();
    if(map){
        var elem = map.getViewport();
        var previousCursor = elem.style.cursor;
        elem.style.cursor = cursor === undefined ? previousCursor : cursor;
    }
}
/**
 * @description 获取地图鼠标样式
 * @returns {String}
 */
bxmap.Map.prototype.getCursor = function(){
    var cursor;
    var map = this.getMap();
    if(map){
        var elem = map.getViewport();
        cursor = elem.style.cursor;
    }
    return cursor;
}

/**
 * @callback bxMap_FeatureDetectingCallback
 * @param {Objcet} evt
 * @param evt.currentFeature {ol.Feature|ol.render.Feature} 当前检测到的要素
 * @param evt.lastFeature {ol.Feature|ol.render.Feature} 上一次检测的要素
 * @param evt.bmap {bxmap.Map} 地图对象
 * @param evt.clickLayer {ol.layer.Vector} 当前click检测到的要素所在的图层
 * @param evt.lastClickLayer {ol.layer.Vector} 上一次click检测的要素所在的图层
 */
/**
 * @callback bxMap_FeatureDetectingFilter
 * @param {ol.layer.Layer} layer
 */
/**
 * @description 设置要素检测功能开启或关闭
 * @param options
 * @param options.type {String} "pointermove"|"click"
 * @param options.target {Object} 设置指定对象开启/关闭检测要素
 * @param options.callback {bxMap_FeatureDetectingCallback} 回调方法
 * @param options.layerFilter {bxMap_FeatureDetectingFilter} 图层过滤
 * @param options.opt_this {Object} options.layerFilter方法中用到的this对象
 * @param options.active {Boolean} true:开启；false：关闭；关闭时仅需要type类型
 */
bxmap.Map.prototype.setFeatureDetectingActive = function(options){
	if(this.getMap() == null) return;
	var map = this.getMap();

	var opt_options = options;
	var type = opt_options.type;
	var active = opt_options.active == null ? true : opt_options.active;
	
	var tempVars = this._tempVars;
	//检测临时变量
	tempVars["featureDetectingVars"] = tempVars["featureDetectingVars"] || {};
	var featureDetectingVars = tempVars["featureDetectingVars"];
	var opt_key = type + "_featureDetectingParams";
	featureDetectingVars[opt_key] = opt_options;
	featureDetectingVars["lastFeatureDetected"] = null;
	
	var target = options.target;
	
	if(active){
		map.on(type, this._handleFeatureDetectingEvent, target);
	}else{
		map.un(type, this._handleFeatureDetectingEvent, target);
	}
}

/**
 * @private
 * @description 处理要素检测事件
 */
bxmap.Map.prototype._handleFeatureDetectingEvent = function(evt){
	var map = evt.map;
	var bmap = map.get("wrap");
	var tempVars = bmap._tempVars;
	
	//获取要素检测临时变量
	var featureDetectingVars = tempVars["featureDetectingVars"];
	var opt_key = evt.type + "_featureDetectingParams";
	var options = featureDetectingVars[opt_key];
	if(!options.active) return;
	
	var currentFeature = null,
		lastFeature = featureDetectingVars[evt.type + "_lastFeatureDetected"];
	map.forEachFeatureAtPixel(evt.pixel, function(feature, layer){
		currentFeature = feature;
		if(evt.type == "click"){
			options["clickLayer"] = layer;
		}
	}, {
        layerFilter: options.layerFilter,
        hitTolerance: 1 //1像素，仅在canvas render中有效
	});
	
	//设置鼠标样式
	var element = map.getTargetElement();
	if (currentFeature) {
		if (tempVars["previousCursor"] === undefined) {
			tempVars["previousCursor"] = bmap.getCursor();
		}
        bmap.setCursor("pointer");
	} else  {
		if (tempVars["previousCursor"]!==undefined){
            bmap.setCursor(tempVars["previousCursor"]);
		} 
		tempVars["previousCursor"] = undefined;
	}
	
	switch(evt.type){
		case "pointermove":
			var canHandle = currentFeature || lastFeature;
			if(!canHandle) return;
			
			bmap._handlePointerMoveDetectedFeature(currentFeature, lastFeature);
			break;
		case "click":
			bmap._handleClickDetectedFeature(currentFeature, lastFeature);
			break;
		default:
			break;
	}
}

/**
 * @private
 * @description 处理鼠标移动检测到的要素
 * @param currentFeature
 */
bxmap.Map.prototype._handlePointerMoveDetectedFeature = function(currentFeature, lastFeature){
	var type = "pointermove";
	var featureDetectingVars = this._tempVars["featureDetectingVars"];
	var opt_key = type + "_featureDetectingParams";
	var options = featureDetectingVars[opt_key];
	
	//上一次选中的要素移除click检测状态
	this.removeFeatureDetectedState(type, lastFeature);
	//为当前要素添加click检测状态
	this.addFeatureDetectedState(type, currentFeature);
	
	if(options.callback){
		if(currentFeature == lastFeature){
			lastFeature = null;
		}
		options.callback({
			currentFeature: currentFeature,
			lastFeature: lastFeature,
			bmap: this,
			clickLayer: options["clickLayer"],
			lastClickLayer: options["lastClickLayer"]
		});
	}
	featureDetectingVars[type + "_lastFeatureDetected"] = currentFeature;
}

/**
 * @private
 * @description 处理鼠标点击检测到的要素
 * @param currentFeature
 */
bxmap.Map.prototype._handleClickDetectedFeature = function(currentFeature, lastFeature){
	var type = "click";
	var featureDetectingVars = this._tempVars["featureDetectingVars"];
	var opt_key = type + "_featureDetectingParams";
	var options = featureDetectingVars[opt_key];
	
	//上一次选中的要素移除click检测状态
	this.removeFeatureDetectedState(type, lastFeature);
	//为当前要素添加click检测状态
	this.addFeatureDetectedState(type, currentFeature);
	
	if(currentFeature == null){
		options["clickLayer"] = null;
	}
	
	if(options.callback){
		options.callback({
			currentFeature: currentFeature,
			lastFeature: lastFeature,
			bmap: this,
			clickLayer: options["clickLayer"],
			lastClickLayer: options["lastClickLayer"]
		});
	}
	featureDetectingVars[type + "_lastFeatureDetected"] = currentFeature;
	options["lastClickLayer"] = options["clickLayer"];
}

/**
 * @description 添加要素检测状态
 * @param {String} type "click"|"pointermove"
 * @param {ol.Feature} feature
 */
bxmap.Map.prototype.addFeatureDetectedState = function(type, feature){
	if(feature){
		var events = bxmap.common.getFeatureDetectedParams(feature);
		events[type] = true;
	}
}

/**
 * @description 移除要素检测状态
 * @param {String} type "click"|"pointermove"
 * @param {ol.Feature} feature
 */
bxmap.Map.prototype.removeFeatureDetectedState = function(type, feature){
	if(feature){
		var events = bxmap.common.getFeatureDetectedParams(feature);
		delete events[type];
	}
}

/**
 * @description 清除数据
 */
bxmap.Map.prototype.clear = function(){
	if(this.dataClear){
        this.dataClear.clear();
	}
}