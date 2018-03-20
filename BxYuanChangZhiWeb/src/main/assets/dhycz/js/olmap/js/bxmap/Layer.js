/**
 * @require ol.js
 * @require Common.js
 */

/**
*@namespace bxmap.layer
*/
bxmap.layer = bxmap.layer || {};

/*--------------------------------------------------------*/
/*            定义默认图层类{bxmap.layer.Defaults}             */
/*--------------------------------------------------------*/
 /**
 *@classdesc 
 *地图默认图层类
 *@constructor
 *@version version 1.0
 *@extends {ol.Object}
 */
bxmap.layer.Defaults = function(){
	/** 
	*@readonly
	*@description 默认图层数组
	*/
	var layers = this.layers = [];
	
	ol.Object.call(this);
	
	var imageLabelLayer = this.createImageLabelLayer();
	this.set("image_label", imageLabelLayer);
	layers.push(imageLabelLayer);
	
	var imageABCDLayer = this.createPointABCDLayer();
	this.set("image_abcd", imageABCDLayer);
	layers.push(imageABCDLayer);
}
ol.inherits(bxmap.layer.Defaults, ol.Object);


/**
 * @description 
 *创建图片标注图层,要素的图片标注参数由feature.get("opt")获取
 * opt对象属性:
 * 		img - {Image} new Image()对象
 * 		scale - {Number} 默认为1
 * 		rotation - {Number} 旋转角度(单位弧度),默认为0
 *@returns {ol.layer.Vector}
 */
bxmap.layer.Defaults.prototype.createImageLabelLayer = function(){
	var layer = new ol.layer.Vector({
		source : new ol.source.Vector(),
		style : function(feature, resolution) {
			var style = feature.getStyle();
			var opt = bxmap.common.getFeatureIconParams(feature);
			if(style == null){
				var iconParam = {
				  	anchor:[0.5,0.5],
				  	anchorOrigin:"top-left",
				  	anchorXUnits:"fraction",
				  	anchorYUnits:"fraction",
				  	offset:[0,0],
				  	offsetOrigin:"top-left",
				  	opacity:1,
				  	snapToPixel:true,
				  	rotateWithView:false
				};
				//图片不能为null
				if(opt.img == null){
					return style;
				}				
				iconParam.img = opt.img;
				iconParam.imgSize = opt.img ? [opt.img.width, opt.img.height] : undefined;
				iconParam.scale = opt.scale || 1;
				iconParam.rotation = opt.rotation || 0;
				
				style = new ol.style.Style({
					image: new ol.style.Icon(iconParam)
				});
			}else if(style instanceof ol.style.Style){
				var iconStyle = style.getImage();
				if(iconStyle instanceof ol.style.Icon){
					//旋转角度
					var rotation = iconStyle.getRotation();
					if(opt.rotation != null) {
						rotation = opt.rotation;
					}
					iconStyle.setRotation(rotation);
					//比例
					var scale = iconStyle.getScale();
					if(opt.scale != null){
						scale = opt.scale;
					}
					iconStyle.setScale(scale);
				}
			}
			return style;
		}
	});
	//设置索引ID
	layer.set(bxmap.INDEX_LAYER_ID, bxmap.DEFAULT_IMG_LABEL_LAYER_ID);
	
	return layer;
}

/**
 * @description 
 * 创建以ABCD图片符号化的点图层
 * @returns {bxmap.layer.Vector}
 */
bxmap.layer.Defaults.prototype.createPointABCDLayer = function(){
	var source = new ol.source.Vector();
    var layer = new bxmap.layer.Vector({source: source});
    //关联原始要素
    
    var abcdStyles = bxmap.common.getDefaultAbcdPointStyles();
    var symbol = new bxmap.symbol.UniqueValueSymbolizer({
    	//defaultStyle: bxmap.common.getDefaultVectorLayerStyle(),
    	property: "symbolValue",
    	context: function(feature){
    		return feature["symbolValue"] || feature.getProperties();
    	}
    });
    
    symbol.addRule({ruleName: "selected",styles: abcdStyles["selected"]});
    symbol.addRule({ruleName: "unselected",styles: abcdStyles["unselected"]});
    layer.setSymbolizer(symbol);
    
    //设置索引ID
	layer.set(bxmap.INDEX_LAYER_ID, bxmap.DEFAULT_IMG_ABCD_LAYER_ID);
	return layer;
}

/*----------图层容器基类{bxmap.layer.LayerContainer}---------*/
/**
 * @classdesc 
 * 定义图层配置基类，继承类必须实现_defineLayers()方法。
 * @constructor
 * @extends {ol.Object}
 * @param {Object} options  
 * @param  {bxmap.reader.MapConfigReader} options.reader  对象
 * @param	{String}	map_id   对应于{bxmap.config.MapConfig}.maps.map_id
 */
bxmap.layer.LayerContainer = function(options) {
	ol.Object.call(this);
	
	var opt_options = options ? options:{};
	this.reader = options.reader;
	this.mapId = options.map_id;
	
	//初始化
	this.initialize();
}
ol.inherits(bxmap.layer.LayerContainer, ol.Object);

/**
 * @description 设置图层组列表
 * @param {ol.Collection.<ol.layer.Group>} layerGroups 
 */
bxmap.layer.LayerContainer.prototype.setLayerGroups = function(layerGroups) {
	this.set("layer_groups",layerGroups);
}

/**
 * @description 获取图层组列表
 * @returns {ol.Collection.<ol.layer.Group>}
 */
bxmap.layer.LayerContainer.prototype.getLayerGroups = function() {
	return this.get("layer_groups");
}

/**
 * @description 根据id获取图层组
 * @param  {String} group_id 图层组id
 * @returns {ol.layer.Group}
 */
bxmap.layer.LayerContainer.prototype.getLayerGroup = function(group_id) {
	var output = null;
	var groups = this.getLayerGroups();
	if(groups){
		var groupsArray = groups.getArray();
		var elem_group,elem_group_id;
		var finded = false;
		for(var i = 0, length = groupsArray.length;i < length;i++){
			elem_group = groupsArray[i];
			elem_group_id = elem_group.get(bxmap.CONFIG_GROUP_ID);
			
			if(elem_group_id == group_id){
				output = elem_group;
				break;
			}
		}
	}
	return output;
}

/**
 * @description 获取当前地图底图
 * @returns {ol.layer.Group}
 */
bxmap.layer.LayerContainer.prototype.getCurrentBaseLayerGroup = function() {
	return this.get("current_layer_group");
}

/**
 * @description 获取上一个底图
 * @returns {ol.layer.Group}
 */
bxmap.layer.LayerContainer.prototype.getLastBaseLayerGroup = function() {
	return this.get("last_layer_group");
}

/**
 * @description 根据MapConfigReader数组序列获取下一个Index的Group；
 * 闭合队列循环获取，当前为最后一个时，则返回第一个。如果仅有一个，则返回自己。
 * length = 0 时，则返回null
 * @returns {ol.layer.Group}
 */
bxmap.layer.LayerContainer.prototype.getNextIndexBaseLayerGroup = function () {
    var output = null;
    var groups = this.getLayerGroups();
    var currentGroup = this.getCurrentBaseLayerGroup(); //当前底图
    //数组长度为0
    if(!groups || groups.getLength() <= 0){
        output = null;
	}else if(groups.getLength() == 1 || currentGroup == null){
        output = groups.item(0);
	}else{
		//当前底图groupid
		var group_id = currentGroup.get(bxmap.CONFIG_GROUP_ID);
		//遍历数组找到当前底图所在索引
        var groupsArray = groups.getArray();
        var elem_group,elem_group_id;
        var currentIndex = 0;
        for(var i = 0, length = groupsArray.length;i < length;i++){
            elem_group = groupsArray[i];
            elem_group_id = elem_group.get(bxmap.CONFIG_GROUP_ID);

            if(elem_group_id == group_id){
                currentIndex = i;
                break;
            }
        }
        //当前底图为最后一个
		if(currentIndex == groups.getLength() -1){
            currentIndex = 0;
		}else{
            currentIndex += 1;
		}
        output = groups.item(currentIndex);
	}

    return output;
}

/**
 * @description 获取当前基础底图包含的图层
 * @returns {ol.Collection.<ol.layer.layer>}
 */
bxmap.layer.LayerContainer.prototype.getCurrentLayers = function(){
	var group = this.getCurrentBaseLayerGroup();
	return group.getLayers();
}
/**
 * @description 设置当前地图底图
 * @param  {String | ol.layer.Group} baseLayerGroup String类型时为group_id
 */
bxmap.layer.LayerContainer.prototype.setCurrentBaseLayerGroup = function(baseLayerGroup) {
	var tmp = this.getCurrentBaseLayerGroup();
	var last = this.getLastBaseLayerGroup();
	
	var basegroup = baseLayerGroup;
	if(typeof baseLayerGroup === 'string'){
		basegroup = this.getLayerGroup(baseLayerGroup);
	}
	this.set("current_layer_group", basegroup);
	this.set("last_layer_group", tmp);
}

/**
 * @description 设置地图视图olview
 * @returns {ol.View}
 */
bxmap.layer.LayerContainer.prototype.getView = function(){
	return this.get("view");
}

/**
 * @description 设置地图视图olview
 * @param {ol.View} view 
 */
bxmap.layer.LayerContainer.prototype.setView = function(view){
	this.set("view",view);
}

/*--------------默认图层容器类{bxmap.layer.DefaultLayerContainer}-------------*/
/**
 * @classdesc 
 * 默认图层容器类
 * @extends {bxmap.layer.LayerContainer}
 * @constructor
 * @param {Object} options
 * @param {bxmap.reader.MapConfigReader} options.reader  
 * @param {String}	options.map_id  对应于{bxmap.config.MapConfig}.maps.map_id
 */
bxmap.layer.DefaultLayerContainer = function(options) {
	bxmap.layer.LayerContainer.call(this, options);
}

ol.inherits(bxmap.layer.DefaultLayerContainer, bxmap.layer.LayerContainer);


/**
 * @description 初始化
 */
bxmap.layer.DefaultLayerContainer.prototype.initialize = function(){
	var reader = this.reader;
	if(reader == null) return;
	
	//设置地图视图
	var map_id = this.mapId;
	var map = reader.getMap(map_id);
	if(map == null) return;
	//设置视图
	this.setView(map["olview"]);
	//设置图层组
	var groupsArray = map["layergroups"];
	var groupCollection = new ol.Collection();
	var group, groupsObj;
	for(var i = 0, length = groupsArray.length; i < length; i++){
		groupsObj = groupsArray[i];
		group = new ol.layer.Group({
	        layers: groupsObj["layers"]
	    });
		group.set(bxmap.CONFIG_GROUP_ID, groupsObj["group_id"]);
		groupCollection.push(group);
	}
	this.setLayerGroups(groupCollection);
	//设置当前显示的基础底图图层组,groups[0]为默认底图
	var curr_group = groupCollection.getArray()[0];
	this.setCurrentBaseLayerGroup(curr_group);
}

/**
 * @classdesc
 * 可符号化的矢量图层
 * @extends {ol.layer.Vector}
 * @constructor
 * @param options 与{ol.layer.Vector}参数一致，请参考{ol.layer.Vector}
 */
bxmap.layer.Vector = function(options) {
	/**
	 * @readonly
	 * @description 符号化样式规则
	 */
	this.symbolizer = null; 
	
	ol.layer.Vector.call(this, options);
	
	this.getSource().on("addfeature",this._handleAddFeature, this);
}
ol.inherits(bxmap.layer.Vector, ol.layer.Vector);

/**
 * @description 设置符号化规则
 * @param {bxmap.symbol.Symbolizer} symbolizer 符号化样式规则
 */
bxmap.layer.Vector.prototype.setSymbolizer = function(symbolizer){
	if(this.symbolizer == symbolizer) return;
	
	var features = this.getSource().getFeatures();
	var feature;
	this.symbolizer = symbolizer; 
	if(symbolizer){
		for(var i = 0; i < features.length; i++){
			feature = features[i];
			symbolizer.evaluate(feature);
		}
	}else{
		var defStyle = this.getStyle();
		for(var i = 0; i < features.length; i++){
			feature = features[i];
			feature.setStyle(defStyle);
		}
	}
}

/**
 * @private
 * @description addfeature事件处理方法
 * @param {Object} evt 
 */
bxmap.layer.Vector.prototype._handleAddFeature = function(evt){
	var feature = evt.feature;
	if(this.symbolizer && feature){
		this.symbolizer.evaluate(feature);
	}
}

/**
 * @description 设置要素符号化规则名称
 * @param {Array<ol.Feature>} features 要素数组
 * @param {String | null} 规则名称
 * @returns {String}
 */
bxmap.layer.Vector.prototype.setFeaturesSymbolRuleName = function(features, ruleName){
	if(features == null) return;
	var defStyle = this.getStyle();
	var symbolizer = this.symbolizer;
	var feature;
	for(var i = 0; i < features.length; i++){
		feature = features[i];
		var context = bxmap.common.getContext(feature);
		context["symbolRuleName"] = ruleName;
		if(ruleName == null){
			feature.setStyle(defStyle);
		}else if(symbolizer){
			symbolizer.evaluate(feature);
		}
	}
}

/*--------------------------------------------------------------------------*/
/*                     WFS图层类{bxmap.layer.WFSLayer}                        */
/*--------------------------------------------------------------------------*/
/**
 * 
 * @classdesc
 * WFS图层，该图层为索引图层
 * @extends {ol.layer.Vector}
 * @constructor
 * @param options
 * @param options.id  {String|Number} 图层ID
 * @param options.name  {String} 容易记忆的图层别名
 * @param options.wfsProtocol  {bxmap.protocol.WFS} 
 * @param options.visible {boolean}  是否可见，默认true可见
 * @param options.lazyLoading {boolean} 是否延迟加载数据，默认为true，当要素数据量较小时，建议设置为false
 * @param options.style  {ol.style.Style} 图层样式
 * @param options.geometryType  {String} 几何类型
 * @param options.source {ol.source.Vector|undefined} 如果该图层用于空间查询，请useSpatialIndex:true，带有空间索引查询速度比较快，默认带有空间索引
 * @fires editingchanged | featurechanged | readystatuschanged
 */
bxmap.layer.WFSLayer = function(options) {
	
	var opt_options = options || {};
	
	/**
	 * @private
	 * @description 是否存在geoid字段，geoid用于要素类与业务表关联的字段名称 
	 */
	this._existGeoId = null;
	/**
	 * @private
	 * @description 字段
	 */
	this._fields = [];//{name:"",type:""}
	/**
	 * @readonly
	 * @description 状态，1：ready；0：正在刷新数据..；-1：未曾加载数据状态
	 */
	this.readystatus = -1;
	/**
	 * @private
	 * @description 几何类型 
	 */
	this._geometryType = opt_options.geometryType;
	/**
	 * @public
	 * @description 针对updateFeatureDict方法更新缓存时，最大要素缓存数量。
	 * 其他操作更新缓存时不计数，例如getFeatureByFid/getFeatureByGeoId更新缓存，则不做个数限制
	 */
	this.maxCacheCountForUpdate = 5000;
	/** 
	 *	@private
	 *	@description 要素数据字典，{key:fid,value:feature} & {key:geoid,value:feature}，其中fid为geoserver定义，geoid为自定义规则
	 */
	this._featureDict ={};
	/** 
	 *	@readonly
	 *	@description wfs协议对象 
	 */
	var wfsprotocol = this.wfsprotocol = opt_options.wfsProtocol;
	
	var defaultStyle = new ol.style.Style({
		stroke:new ol.style.Stroke({
			color:'#ff0000',
			width:1
		})
	});
	 
	opt_options.style = opt_options.style || bxmap.common.getDefaultVectorLayerStyle();

	var source = opt_options.source = options.source || new ol.source.Vector({useSpatialIndex:true});
	bxmap.layer.Vector.call(this, opt_options);
	
	this.set(bxmap.INDEX_LAYER_ID, opt_options.id);
	this.set("name", opt_options.name);

	/**
	 * 
	 * @private
	 * @description 是否正在编辑
	 */
	this._isEditing = false;
	/**
	 * @readonly
	 * @description 增c/删d/改u要素
	 * @member {object} f
	 */
	this.f = {c : [],u : [],d : []};
	
	var lazyLoading = options.lazyLoading == null ? true : options.lazyLoading;
	//visible为true时请求wfs数据
	if (wfsprotocol && (!lazyLoading)) {
		var _this = this;
		this.refresh({
			callback:function(evt){
				//更新要素字典
				_this._updateFeatureDict(evt.features);
			}
		});
	}
}
ol.inherits(bxmap.layer.WFSLayer,bxmap.layer.Vector);

/**
 * @inherited
 * @description 设置是否可见。如果readystatus==-1 && visible==true，会刷新数据。
 * @param {boolean} visible true，可见；fasle，不可见
 */
bxmap.layer.WFSLayer.prototype.setVisible = function(visible){
	ol.layer.Vector.prototype.setVisible.call(this, visible);
	if(this.readystatus == -1 && visible){
		var _this = this;
		this.refresh({
			callback:function(evt){
				//更新要素字典
				_this._updateFeatureDict(evt.features);
			}
		});
	}
}

/**
 *   
 * @description 如果有需要保存的编辑操作，则为true。
 * @returns {boolean}
 */
bxmap.layer.WFSLayer.prototype.hasEdits = function(){
	var f = this.f;
	if(f.c.length > 0 || f.u.length > 0 || f.d.length > 0){
		return true;
	}else {
		return false;
	}
}

/**
 * @description 判断是否正在编辑中，true为正在编辑
 * @returns {boolean}
 */
bxmap.layer.WFSLayer.prototype.isEditing = function(){
	return this._isEditing;
}

/**
 * @description 开始编辑
 */
bxmap.layer.WFSLayer.prototype.startEditing = function(){
	this._isEditing = true;
	this.dispatchEvent({type:'editingchanged',target:this, isediting: this._isEditing});
}

/**
 * @description 停止编辑
 * @param options
 * @param options.saveEdits  {Boolean} 是否保存，默认为true
 * @param options.showDialog {Boolean} 是否显示保存成功或失败信息框，默认为true，依赖jDialog.js
 * @param options.success {function(evt)} 成功回调函数
 * @param options.fail  {function(evt)} 失败回调函数
 */
bxmap.layer.WFSLayer.prototype.stopEditing = function(options){
	var opt_options = options || {};
	var saveEdits = opt_options.saveEdits == null ? true:opt_options.saveEdits;
	var wfsprotocol = this.wfsprotocol;
	if(saveEdits){
		//保存编辑的数据
		this.save(opt_options);
	}else{
		//不保存处理过程
		if(this.hasEdits()){
			//不保存也要刷新图层，保持和后台数据一致
			this.refresh();
		}
		//清空数组
		this.f = {c:[],u:[],d:[]};
		wfsprotocol.clear();
		
		this.dispatchEvent({type:'featurechanged',target:this,handle:"remove",features: []});
	}
	
	this._isEditing = false;
	//触发editingchanged事件
	this.dispatchEvent({type:'editingchanged',target:this,isediting:this._isEditing});
}

/**
 * @description 保存编辑
 * @param options
 * @param options.showDialog {Boolean} 是否显示保存成功或失败信息框，默认为true，依赖jDialog.js
 * @param options.success  {function(evt)}  成功回调函数
 * @param options.fail  {function(evt)}  失败回调函数
 */
bxmap.layer.WFSLayer.prototype.save = function(options){
	//是否显示保存信息框	
	options.showDialog = options.showDialog == null ? true : options.showDialog;
	
	//是否有编辑
	if(!this.hasEdits()) return;
	
	var f = this.f;
	var wfsprotocol = this.wfsprotocol;
	var _this = this;
	
	if(f.c.length > 0){
		wfsprotocol.createFeatures(f.c);
	}
	if(f.d.length > 0){
		wfsprotocol.deleteFeatures(f.d);
	}
	if(f.u.length > 0){
		wfsprotocol.updateFeatures(f.u);
	}
	//保存wfs修改数据后刷新图层保存与后台一致
	wfsprotocol.commit({
		success: function (evt){
			if(options.showDialog){
				jDialog.message('保存成功',{
					autoClose : 1000,	// 1s后自动关闭
					padding : '30px',	// 设置内部padding
					modal: false		// 非模态，即不显示遮罩层
			    });
			}
			if(options.success){
				options.success(evt);
			}
			//耗时操作
			_this.refresh({
				target: _this
			});
		},
		fail: function(evt){
			if(options.showDialog){
				jDialog.message('保存失败',{
					autoClose : 1000,	// 1s后自动关闭
					padding : '30px',	// 设置内部padding
					modal: false		// 非模态，即不显示遮罩层
			    });
			}
			if(options.fail){
				options.fail(evt);
			}
		}
	});
	//清空数组
	this.f = {c:[],u:[],d:[]};
	wfsprotocol.clear();
	
	this.dispatchEvent({type:'featurechanged',target:this,handle:"remove",features: []});
}

/**
 * @description 新增要素
 * 
 * @param features {Array.<ol.Feature>}
 */
bxmap.layer.WFSLayer.prototype.addFeatures = function(features) {
	//判断编辑状态
	if(!this._isEditing || features == null || features.length == 0) return;
	var createFeatures = this.f.c;
	this.f.c = createFeatures.concat(features);
	this.getSource().addFeatures(features);
	//触发事件
	this.dispatchEvent({type: 'featurechanged',target:this,handle:"add",features: features});
}

/**
 * @description 删除要素
 * 
 * @param  {Array.<ol.Feature>} feature
 */
bxmap.layer.WFSLayer.prototype.removeFeatures = function(features) {
	//判断编辑状态
	if(!this._isEditing || features == null || features.length == 0) return;
	
	var createFeatures = this.f.c;
	var updateFeatures = this.f.u;
	var removeFeatures = this.f.d;
	var feature;
	for(var i = 0; i < features.length; i++){
		feature = features[i];
		//判断是否为新增要素
		var index = createFeatures.indexOf(feature);
		var found = index > -1;
		if (found) {
			//删除新增要素
			createFeatures.splice(index, 1);
		}else{
			//判断是否为更新要素
			index = updateFeatures.indexOf(feature);
			found = index > -1;
			if (found) {
				//删除更新要素
				updateFeatures.splice(index, 1);
			}else{
				//标记为删除原数据要素
				removeFeatures.push(feature);
			}
		}
		this.getSource().removeFeature(feature);
	}
	
	this.dispatchEvent({type:'featurechanged',target:this, handle:"remove",features: features});
}

/**
 * @description 更新要素
 * 
 * @param   {Array.<ol.Feature>} feature
 */
bxmap.layer.WFSLayer.prototype.updateFeatures = function(features) {
	//判断编辑状态
	if(!this._isEditing || features == null || features.length == 0) return;
	
	var f = this.f;
	var feature;
	for(var i = 0; i < features.length; i++){
		feature = features[i];
		if (f.c.indexOf(feature) == -1 && f.u.indexOf(feature) == -1){
			f.u.push(feature);
		}
	}
	
	this.dispatchEvent({type:'featurechanged',target:this, handle:"update",features: features});
}

/**
 * @description 获取几何字段名
 * returns {string}
 */
bxmap.layer.WFSLayer.prototype.getGeometryName = function(){
	return this.wfsprotocol.getGeometryName();
}

/**
 * @description 获取图层中要素的几何类型
 * @returns {String}
 */
bxmap.layer.WFSLayer.prototype.getGeometryType=function(){
	return this._geometryType;
}

/**
 * 刷新图层之后回调方法
 * @callback WFSLayer_AfterRefresh
 * @param {object} evt
 * @param {bxmap.layer.WFSLayer} evt.target this实例对象
 * @param {Array<ol.Feature>} evt.features 要素数组
 */
/**
 * @description 强制刷新图层，执行WFS请求数据
 * @param options
 * @param options.target {bxmap.layer.WFSLayer|undefined} 指向bxmap.layer.WFSLayer实例，避免this指向问题
 * @param options.callback {WFSLayer_AfterRefresh} 刷新之后回调方法
 */
bxmap.layer.WFSLayer.prototype.refresh = function(options){
	var opt_options = options || {};
	var _this = opt_options.target || this;
	var source = _this.getSource();
	source.clear();
	
	//fire readystatechanged
	if(_this.readystatus == 1 || _this.readystatus == -1){
		_this.readystatus = 0;
		_this.dispatchEvent({type:'readystatuschanged',target:_this, state: _this.readystatus});
	}
	
	//重新获取数据
	_this.wfsprotocol.read({
		callback : function(features) {
			source.addFeatures(features);
			
			//fire readystatechanged
			_this.readystatus = 1;
			_this.dispatchEvent({type:'readystatuschanged',target:_this, state: _this.readystatus});
			
			//执行回调
			if(opt_options.callback){
				opt_options.callback({target:_this,features:features});
			}
		}
	});
}

/**
 * @description 根据fid获取要素
 * @param fid {String} geoserver发布wfs后，fid一般为typename.id形式，例如"GEO_MM_ZGQ.2"
 * @param {Boolean} fast 默认为true，优先从要素字典中获取数据，如果数据字典中不存在，则请求wfs服务，
 * 如果为false，则直接请求wfs服务。数据字典获取速度更快，但如果执行了save/refresh/stopEditing方法后，
 * 要素字典中的要素不能保证与Layer中要素完全相等(==)，可能只是属性值相等，几何重合，但是两个{ol.Feature}对象。
 * updateFeatureDict方法用于将Layer中要素更新至数据字典中。
 * @retruns {ol.Feature}
 */
bxmap.layer.WFSLayer.prototype.getFeatureByFid = function(fid, fast){
	if(fid == null || fid == "") return;
	
	var feat = null;
	var isFast = fast == null ? true : fast;
	
	var _this = this;
	//优先使用字典查询
	if(isFast){
		feat = this._featureDict[fid];
	}
	if(feat == null){
		this.wfsprotocol.readFeatureByFid({
			fid: fid,
			async: false,
			getFeature: function(feature){
				feat = feature;

				if(feature){
					_this._updateFeatureDictByFeature(feature, _this);
				}
			}
		});
	}
	
	return feat;
}

/**
 * @description 根据图层与业务表关联id获取要素，如果有多条数据，则为找到的最后一个
 * @param {String|Number} 字段值
 */
bxmap.layer.WFSLayer.prototype.getFeatureByGeoId = function(geoid){
	if(geoid == null) return null;
	//不存在关联字段，则为null
	var exist = this.existsGeoId();
	if(!exist) return null;
	
	//如果数据有多条，则数据字典中多次赋值，存储的为最后一个
	var feature = null;
	if(this.readystatus == 1){
		feature = this._featureDict[geoid];
	}

	if(feature == null){
		//执行WFS请求查询
		var _this = this;
		var fieldName = bxmap.WFS_ASSOCIATION_GEO_FIELD;
		this.wfsprotocol.read({
			async: false,
			filter: ol.format.filter.equalTo(fieldName, geoid, true),
			callback: function(data){
				if(data == null || data.length == 0) return;
				feature = data[data.length - 1];
				
				//更新要素字典
				if(feature){
					_this._updateFeatureDictByFeature(feature, _this);
				}
			}
		});
	}
	
	return feature;
}

/**
 * @description 根据图层与业务表关联id获取要素数组
 * @param {String|Number} 字段值
 */
bxmap.layer.WFSLayer.prototype.getFeaturesByGeoId = function(geoid){
	if(geoid == null) return null;
	//不存在关联字段，则为null
	var exist = this.existsGeoId();
	if(!exist) return null;	
	//如果数据有多条，则数据字典中多次赋值
	var features = null;
	//执行WFS请求查询
	var _this = this;
	var fieldName = bxmap.WFS_ASSOCIATION_GEO_FIELD;
	this.wfsprotocol.read({
		async: false,
		filter: ol.format.filter.equalTo(fieldName, geoid, true),
		callback: function(data){
			if(data == null || data.length == 0) return;
			features = data;				
		}
	});	
	return features;
}


/**
 * @description 更新要素数据字典，由于遍历要素更新字典，尽量不要频繁调用
 */
bxmap.layer.WFSLayer.prototype.updateFeatureDict = function(){
	var features = this.getSource().getFeatures();
	this._updateFeatureDict(features, this);
}

/**
 * @private
 * @description 更新要素数据字典
 * @param {ol.Feature[]} features wfs请求获得的要素数组
 * @param {bxmap.layer.WFSLayer} [opt_this=this] 指向bxmap.layer.WFSLayer实例，避免this指向问题
 */
bxmap.layer.WFSLayer.prototype._updateFeatureDict = function(features, opt_this){
	this._featureDict = {};
	if(features == null || features.length == 0) return;
	var _this = opt_this || this;
	
	var length = Math.min(features.length, _this.maxCacheCountForUpdate);
	var feature, fid , geoid;
	for(var i = 0; i < length; i++){
		feature = features[i];
		if(feature == null) continue;
		//更新要素字典
		_this._updateFeatureDictByFeature(feature, _this);
	}
}

/**
 * @private
 * @description 更新要素数据字典
 * @param {ol.Feature} feature wfs请求获得的要素数组
 * @param {bxmap.layer.WFSLayer} [opt_this=this] 指向bxmap.layer.WFSLayer实例，避免this指向问题
 */
bxmap.layer.WFSLayer.prototype._updateFeatureDictByFeature = function(feature, opt_this){
	var _this = opt_this || this;
	
	//fid,例如"GEO_MM_ZGQ.2"
	var fid = feature.getId();
	if(fid){
		_this._featureDict[fid] = feature;
	}
	
	//判断是否存在geoid
	if(_this.existsGeoId()){
		//geoid，存在与业务表关联的字段
		var geoid = feature.get(bxmap.WFS_ASSOCIATION_GEO_FIELD);
		if(geoid){
			_this._featureDict[geoid] = feature;
		}
	}
}

/**
 * @description 判断是否存在GeoId
 * @returns {Boolean} true:存在，false:不存在
 */
bxmap.layer.WFSLayer.prototype.existsGeoId = function(){
	if(this._existGeoId == null){
		this._existGeoId = this.existsField(bxmap.WFS_ASSOCIATION_GEO_FIELD);
	}
	return this._existGeoId;
}

/**
 * @description 获取字段信息
 * @returns {Array} 数组格式为[{name:"name",type:"type"}]
 */
bxmap.layer.WFSLayer.prototype.getFieldsInfo = function(){
	if(this._fields.length == 0){
		var fields = this._fields;
		this.wfsprotocol.describeFeatureType({
			async: false,
			describeFeatureType: function(response){
				if(response == null) return;
			
				var featureTypes = response.featureTypes[0] || {};
				var properties = featureTypes.properties || [];
				var prop;
				for(var i = 0; i < properties.length; i++){
					prop = properties[i];
					fields.push({
						"name": prop["name"],
						"type": prop["type"]
					});
				}
			}
		});
	}
	
	return this._fields;
}

/**
 * @description 判断字段是否存在
 * @param {String} fieldName 字段名
 * @returns {Boolean}
 */
bxmap.layer.WFSLayer.prototype.existsField = function(fieldName){
	var exist = false;
	if(fieldName == null || fieldName == "") return exist;
	
	var fieldsInfo = this.getFieldsInfo();
	var info;
	for(var i = 0 ; i < fieldsInfo.length; i++){
		info = fieldsInfo[i];
		if(info["name"] == fieldName){
			exist = true;
			break;
		}
	}
	return exist;
}

/*--------------------------------------------------------------------------*/
/*   以ABCD符号化的图层类{bxmap.layer.AbcdSymbolicLayer}，专用于查询n条数据以Abcd显示           */
/*--------------------------------------------------------------------------*/
/**
 * @classdesc
 * 可符号化的矢量图层
 * @extends {ol.layer.Vector}
 * @constructor
 * @param {Object} options
 * @param options.style {ol.style.Style | undefined} 默认显示样式，未选中样式
 * @param options.property {String} 用于标识要素值的字段，可以为feature字段名或属性
 * @param options.context {function(feature)} 用于返回要素携带的值。例如返回feature.get("type")
 * @fires beforeselect | afterselect
 */
bxmap.layer.AbcdSymbolicLayer = function(options) {
	var opt_options = options || {};
	/**
	 * @private
	 * @description 用于标识要素值的字段，可以为feature字段名或属性名
	 */
	this._property = opt_options.property;
	/**
	 * @private
	 * @description 用于获取标识值
	 */
	this._contextFunc = opt_options.context;
	/**
	 * @private
	 * @description 选中要素样式
	 */
	this._selectedStyle = null;
	/**
	 * @private
	 * @description 未选中要素样式
	 */
	this._unselectedStyle = opt_options.style = opt_options.style || bxmap.common.getDefaultVectorLayerStyle();
	/**
	 * @private
	 * @description 用于缓存要素，key：feature.bx_uid,value:[真实要素,abcd符号化要素]
	 */
	this._featureCache = {};
	//存储要素
	opt_options.source = new ol.source.Vector();
	bxmap.layer.Vector.call(this, opt_options);
	
	//避免该事件被注销
	var tag = [], _this = this;
	//监听清空要素数据
	this.getSource().on("clear",function(){
		if(_this._lastSelected){
			_this.select(null);
		}
		_this._featureCache = {};
	}, tag);
	//初始化唯一值符号化
	var symbol = this._initSymbolizer();
	this.setSymbolizer(symbol);
}
ol.inherits(bxmap.layer.AbcdSymbolicLayer, bxmap.layer.Vector);

/**
 * @private 
 * @description 初始化唯一值符号化
 * @returns {bxmap.symbol.UniqueValueSymbolizer}
 */
bxmap.layer.AbcdSymbolicLayer.prototype._initSymbolizer = function(){
	var abcdStyles = bxmap.common.getDefaultAbcdPointStyles();
	var symbol = new bxmap.symbol.UniqueValueSymbolizer({
		property: this._property,
		context: this._contextFunc
	});
	//添加选中状态abcd要素样式规则
	symbol.addRule({ruleName: "selected",styles: abcdStyles["selected"]});
	//添加未选中状态abcd要素样式规则
	symbol.addRule({ruleName: "unselected",styles: abcdStyles["unselected"]});
	return symbol;
}

/**
 * @description 设置要素选中状态样式
 * @param {ol.style.Style | undefined} style
 */
bxmap.layer.AbcdSymbolicLayer.prototype.setSelectedStyle = function(style){
	this._selectedStyle = style;
}

/**
 * @private
 * @description getSource().addfeature()方法触发事件处理方法
 * @param {Object} evt 
 */
bxmap.layer.AbcdSymbolicLayer.prototype._handleAddFeature = function(evt){
	var feature = evt.feature;
	//判断是否为额外添加的abcd要素
	if(this._existsExtraFlags(feature)) return;
	//获取要素uid
	var uid = bxmap.common.getCounterUid(feature).toString();
	
	var pointFeature = feature.clone();
	//添加至缓存
	this._featureCache[uid] = [feature, pointFeature];
	
	pointFeature["bx_uid"] = uid;
	pointFeature[this._property] = this._contextFunc(feature);
	this._addExtraFlags(pointFeature);
	
	//转为点几何
	var geometry = pointFeature.getGeometry();
	var pointGeom = this._toPointGeometry(geometry);
	if(pointGeom == null) return;
	
	pointFeature.setGeometry(pointGeom);
	
	//要素符号化
	pointFeature.setSymbolRuleName("unselected");
	this.getSource().addFeature(pointFeature);
	if(this.symbolizer){
		this.symbolizer.evaluate(pointFeature);
	}
}

/**
 * @private
 * @description 简单几何转为点
 * @param {ol.geom.SimpleGeometry} geometry 几何
 * @returns {ol.geom.Point}
 */
bxmap.layer.AbcdSymbolicLayer.prototype._toPointGeometry = function(geometry){
	var pointGeom;
	switch(geometry.getType()){
		case "MultiPoint":
			geometry = geometry.getPoint(0);
		case "Point":
			pointGeom = geometry;
			break;
		case "MultiLineString":
			geometry = geometry.getLineString(0);
		case "LineString":
			pointGeom = geometry.getCoordinateAt(0.5,"XY");
			break;
		case "MultiPolygon":
			geometry = geometry.getPolygon(0);
		case "Polygon":
			pointGeom = geometry.getInteriorPoint();
			break;
		default:
			break;
	}
	return pointGeom;
}

/**
 * @private
 * @description 为feature添加是额外新增的要素标记
 * @param {ol.Feature} feature
 */
bxmap.layer.AbcdSymbolicLayer.prototype._addExtraFlags = function(feature){
	var context = bxmap.common.getContext(feature);
	context["_add_extra_flag"] = true;
}

/**
 * @private
 * @description 为feature添加是额外新增的要素标记
 * @param {ol.Feature} feature
 */
bxmap.layer.AbcdSymbolicLayer.prototype._existsExtraFlags = function(feature){
	var context = bxmap.common.getContext(feature);
	var exist = context["_add_extra_flag"] == null ? false : context["_add_extra_flag"];
	return exist;
}

/**
 * @description 选中要素，由于仅能选中一个要素，所以会自动取消上一个选中的要素，如果上一个要素存在的话
 * @param {ol.Feature} feature
 */
bxmap.layer.AbcdSymbolicLayer.prototype.select = function(feature){
	//取消lastFeature选中状态
	var lastFeature = this._lastSelected;
	this._removeFeatureClickedState(lastFeature);
	if(lastFeature){
		var event = bxmap.common.getFeatureDetectedParams(lastFeature);
		this.unhighlight([lastFeature]);
	}
	feature = this._getRealFeature(feature);
	lastFeature = this._getRealFeature(lastFeature);
	//beforeSelect
	this.dispatchEvent({type:'beforeselect',target:this,feature:feature,lastfeature:lastFeature});
	
	//取消所有要素选中状态
	if(feature == null){
		this.highlight(null);
	}else{
		//设置当前要素选中状态
		this._addFeatureClickedState(feature);
		this.highlight([feature]);
	}
	//afterSelect
	this.dispatchEvent({type:'afterselect',target:this,feature:feature});
	
	this._lastSelected = feature;
}

/**
 * @description 高亮要素
 * @param {Array<ol.Feature>} features 需要高亮要素的数组，如果为null，则所有要素取消高亮
 */
bxmap.layer.AbcdSymbolicLayer.prototype.highlight = function(features){
	if(features == null){
		var tempFeatures = this.getSource().getFeatures();
		this.unhighlight(tempFeatures);
	}else{
		var feature, uid;
		for(var i = 0; i < features.length; i++){
			feature = features[i];
			uid = bxmap.common.getCounterUid(feature).toString();
			
			//从缓存中查找要素
			var tempFeatrues = this._featureCache[uid];
			if(tempFeatrues == null) continue;
			
			var temp;
			for(var ii = 0; ii < tempFeatrues.length; ii++){
				temp = tempFeatrues[ii];
				if(this._existsExtraFlags(temp)){
					this.setFeaturesSymbolRuleName([temp], "selected");
				}else if(this._selectedStyle){
					temp.setStyle(this._selectedStyle);
				}
			}
		}
	}
}

/**
 * @description 取消高亮要素
 * @param {Array<ol.Feature>} features 取消高亮要素的数组
 */
bxmap.layer.AbcdSymbolicLayer.prototype.unhighlight = function(features){
	if(features == null) return;
	
	var feature, uid;
	for(var i = 0; i < features.length; i++){
		feature = features[i];
		uid = bxmap.common.getCounterUid(feature).toString();
		
		//从缓存中查找要素
		var tempFeatrues = this._featureCache[uid];
		if(tempFeatrues == null) continue;
		
		var temp;
		for(var ii = 0; ii < tempFeatrues.length; ii++){
			temp = tempFeatrues[ii];
			if(this._existsExtraFlags(temp)){
				this.setFeaturesSymbolRuleName([temp], "unselected");
			}else {
				temp.setStyle(this._unselectedStyle);
			}
		}
	}
}

/**
 * @description 注册/激活检测要素
 * @param {bxmap.Map} bmap 地图
 * @param {function(feature)} callback 点击要素回调方法
 */
bxmap.layer.AbcdSymbolicLayer.prototype.registerDetectingFeature = function(bmap){
	var _this = this;
	bmap.setFeatureDetectingActive({
		type: "pointermove",
		active : true,
		target: this,
		layerFilter: function(layer){
			return layer == _this;
		},
		callback : function(evt){
			var feature = evt.currentFeature;
			var lastFeature = evt.lastFeature;
			//控制要素选中状态
			_this._handleFeatureState(feature, lastFeature);
		}
	});
	bmap.setFeatureDetectingActive({
		type: "click",
		active : true,
		target: this,
		layerFilter: function(layer){
			return layer == _this;
		},
		callback : function(evt){
			var feature = evt.currentFeature;
			var lastFeature = evt.lastFeature;
			
			//避免select方法为_lastSelected赋值后，与map click要素不一致
			if(_this._lastSelected && _this._lastSelected != lastFeature){
				//TODO
			}else{
				_this._lastSelected = lastFeature;
			}
			
			if(feature || _this._lastSelected){
				//选中要素
				_this.select(feature);
			}
		}
	});
}

/**
 * @description 取消注册/取消激活检测要素
 * @param {bxmap.Map} bmap 地图
 */
bxmap.layer.AbcdSymbolicLayer.prototype.unregisterDetectingFeature = function(bmap){
	var _this = this;
	bmap.setFeatureDetectingActive({
		type: "pointermove",
		target: this,
		active : false
	});
	bmap.setFeatureDetectingActive({
		type: "click",
		target: this,
		active : false
	});
}

/**
 * @private
 * @description 控制要素状态（选中/非选中）
 * @param {ol.Feature} feature 检测到的当前要素 
 * @param {ol.Feature} lastFeature 上一次检测到的要素
 */
bxmap.layer.AbcdSymbolicLayer.prototype._handleFeatureState = function(feature, lastFeature){
	var temp = null;
	//设置当前要素选中状态
	if(feature){
		this.highlight([feature]);
	}
	//设置上一个要素非选中状态
	temp = null;
	if(lastFeature){
		var event = bxmap.common.getFeatureDetectedParams(lastFeature);
		if(!event["click"]){
			this.unhighlight([lastFeature]);
		}
	}
}

/**
 * @private
 * @description 获取真实的要素
 * @param {ol.Featrue} feature 真实的要素或者以abc表示的额外新增的要素 
 * @returns {ol.Feature}
 */
bxmap.layer.AbcdSymbolicLayer.prototype._getRealFeature = function(feature){
	if(feature == null || !this._existsExtraFlags(feature)) return feature;
	
	var real = null;
	var uid = bxmap.common.getCounterUid(feature).toString();
	var features = this._featureCache[uid];
	var temp;
	for(var i = 0; i < features.length; i++){
		temp = features[i];
		if(temp != feature){
			real = temp;
			break;
		}
	}
	return real;
}

/**
 * @private
 * @description 添加要素click检测状态
 * @param {ol.Feature} feature
 */
bxmap.layer.AbcdSymbolicLayer.prototype._addFeatureClickedState = function(feature){
	if(feature){
		var uid = bxmap.common.getCounterUid(feature).toString();
		var features = this._featureCache[uid];
		var temp;
		for(var i = 0; i < features.length; i++){
			temp = features[i];
			var events = bxmap.common.getFeatureDetectedParams(temp);
			events["click"] = true;
		}
	}
}

/**
 * @private
 * @description 移除要素click检测状态
 * @param {ol.Feature} feature
 */
bxmap.layer.AbcdSymbolicLayer.prototype._removeFeatureClickedState = function(feature){
	if(feature){
		var uid = bxmap.common.getCounterUid(feature).toString();
		var features = this._featureCache[uid];
		var temp;
		for(var i = 0; i < features.length; i++){
			temp = features[i];
			var events = bxmap.common.getFeatureDetectedParams(temp);
			delete events["click"];
		}
	}
}


