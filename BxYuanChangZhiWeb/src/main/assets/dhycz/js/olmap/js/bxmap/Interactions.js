/**
 * @require ol.js
 * @require Base.js
 * @require Layer.js
 * @require Style.js
 */

/**
 * @namespace bxmap.interaction
 */
bxmap.interaction = bxmap.interaction || {};

/*----------默认交互工具类{bxmap.interaction.Defaults}---------*/
/**
 * @classdesc 默认交互工具类
 * @constructor
 * @extends {ol.Object}
 * @example <caption> 获取交互工具 </caption>
 * var tools = new bxmap.interaction.Defaults();
 * //放大工具
 * var zoomIn = tools.get("zoom_in");
 * @example <caption> 默认交互工具有效值 </caption>
 *  zoom_in - 放大
 *  zoom_out - 缩小
 *  measure_area - 测面积
 *  measure_distance - 测距离
 *  plot - 标绘工具
 *  pan - 平移/漫游
 */
bxmap.interaction.Defaults = function () {
	/**默认交互工具*/
	var interactions = this.interactions = [];
	
    ol.Object.call(this);
    //矩形放大/缩小工具
    var zoom_in = new bxmap.interaction.ZoomIn();
    zoom_in.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_ZOOM_IN_ID);
    this.set("zoom_in", zoom_in);
    interactions.push(zoom_in);
    
    var zoom_out = new bxmap.interaction.ZoomOut();
    zoom_out.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_ZOOM_OUT_ID);
    this.set("zoom_out", zoom_out);
    interactions.push(zoom_out);
    
    //量测工具
    var measure_area = new bxmap.interaction.Measure({ "type": "area" });
    measure_area.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_MEASURE_AREA_ID);
    this.set("measure_area", measure_area);
    interactions.push(measure_area);
    
    var measure_distance = new bxmap.interaction.Measure({ "type": "distance" });
    measure_distance.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_MEASURE_DIST_ID);
    this.set("measure_distance", measure_distance);
    interactions.push(measure_distance);
    
    //标绘工具
    var plot = new bxmap.interaction.Plot({ "type": "polygon" });
    plot.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_PLOT_ID);
    this.set("plot", plot);
    interactions.push(plot);
    
    //平移工具
    var pan = new bxmap.interaction.DragPan();
    pan.set(bxmap.INDEX_INTERACTION_ID, bxmap.DEFAULT_INTER_DRAG_PAN_ID);
    this.set("pan", pan);
    interactions.push(pan);
}
ol.inherits(bxmap.interaction.Defaults, ol.Object);

/*----------平移类{bxmap.interaction.DragPan}---------*/

/**
 * @classdesc 平移工具
 * @constructor
 * @extends {ol.interaction.Interaction}
 */
bxmap.interaction.DragPan = function() {
    ol.interaction.Interaction.call(this, {
        handleEvent: function() { return true; }
	});
}
ol.inherits(bxmap.interaction.DragPan, ol.interaction.Interaction);

/**
 * @inheritDoc
 * @description 使工具激活或失效
 * @param {Boolean} active true-激活，false-失效
 */
bxmap.interaction.DragPan.prototype.setActive = function(active){
	ol.interaction.Interaction.prototype.setActive.call(this, active);
	
	var cursor = active ? "url("+bxmap.Resource.DragPanCursor+"),auto" : undefined;
	this.setCursor(cursor);
}

/*----------矩形放大类{bxmap.interaction.ZoomIn}---------*/
/**
 * @classdesc 拉框矩形放大地图
 * @constructor
 * @extends {ol.interaction.DragZoom}
 */
bxmap.interaction.ZoomIn = function() {
	ol.interaction.DragZoom.call(this, {
		condition : ol.events.condition.always,
		out : false
	});
}

ol.inherits(bxmap.interaction.ZoomIn, ol.interaction.DragZoom);

/**
 * @inheritDoc
 * @description 使工具激活或失效
 * @param {Boolean} active true-激活，false-失效
 */
bxmap.interaction.ZoomIn.prototype.setActive = function(active){
	ol.interaction.DragZoom.prototype.setActive.call(this, active);
	
	var cursor = active ? "url("+bxmap.Resource.ZoomInCursor+"),auto" : undefined;
	this.setCursor(cursor);
}

/*----------矩形缩小类{bxmap.interaction.ZoomOut}---------*/
/**
 * @classdesc 拉框矩形缩小地图
 * @constructor
 * @extends {ol.interaction.DragZoom}
 */
bxmap.interaction.ZoomOut = function() {
	ol.interaction.DragZoom.call(this, {
		condition : ol.events.condition.always,
		out : true
	});
}

ol.inherits(bxmap.interaction.ZoomOut, ol.interaction.DragZoom);

/**
 * @inheritDoc
 * @description 使工具激活或失效
 * @param {Boolean} active true-激活，false-失效
 */
bxmap.interaction.ZoomOut.prototype.setActive = function(active){
	ol.interaction.DragZoom.prototype.setActive.call(this, active);
	
	var cursor = active ? "url("+bxmap.Resource.ZoomOutCursor+"),auto" : undefined;
	this.setCursor(cursor);
}

/*----------量测工具类{bxmap.interaction.Measure}---------*/
/**
 * @classdesc 测距、测面积工具
 * @constructor
 * @extends {ol.interaction.Draw}
 * @param options {Object}
 * @param options.type {String} 有效值为distance，area，默认为测距离
 * @param options.once {Boolean} 默认为true，执行一次测量，但该工具不会销毁，只是执行一次后，需要重新激活工具才可以测量
 */
bxmap.interaction.Measure = function (options) {

    var option = (options === undefined) ? { "type": "distance", "once": true} : options;
    var type = option.type === undefined ? 'distance' : option.type; // 默认测距离
    var once = option.once === undefined ? true : option.type; // 默认执行一次绘制

    switch (type) {
        case 'distance':
            type = 'LineString';
            break;
        case 'area':
            type = 'Polygon';
            break;
        default:
            type = 'LineString';
    }

    // 创建临时图层
    var layer = bxmap.interaction.Measure._createLayer();
    ol.interaction.Draw.call(this, {
        source: layer.getSource(),
        type: type, //{ol.geom.GeometryType}
        style: layer.getStyle()
    });

    this._once = once;
    this._layer = layer; //当前要素图层 {ol.layer.Vector}
    this._sketch = null; //当前绘制的要素 {ol.Feature}
    this._helpTooltipElement = null; //操作帮助Element {Element}
    this._helpTooltip = null; //操作帮助Overlay {ol.Overlay}
    this._listener = null; //监听绘制几何
    //绘制要素覆盖层，{uid:要素uid，values:{overlays:[{ol.Overlay}],feature:sketch}}
    this._sketchMeasureOverlays = { "coorIndex": 0 };

    this.on("drawstart", this._drawStart, this);
    this.on("drawend", this._drawEnd, this);
}

ol.inherits(bxmap.interaction.Measure, ol.interaction.Draw);

/**
 * @description "单击确定起点"提示文本
 * @const
 * @type {String}
 */
bxmap.interaction.Measure.HELP_MSG = '单击确定起点';
/**
 * @description "单击确定起点，双击结束"提示文本
 * @const
 * @type {String}
 */
bxmap.interaction.Measure.CONTINUE_MSG = '单击确定起点，双击结束';

/**
 * @inheritDoc
 * @description 使工具激活或失效
 * @param {Boolean} active true-激活，false-失效
 */
bxmap.interaction.Measure.prototype.setActive = function(active){
	ol.interaction.Draw.prototype.setActive.call(this, active);
	this._updateState();
}

/**
 * @inheritDoc
 * @description 重写setMap方法
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.Measure.prototype.setMap = function(map) {
	ol.interaction.Draw.prototype.setMap.call(this, map);
	this._updateState();
};

/**
 * @private
 * @description 工具添加/移除地图时，更新状态
 */
bxmap.interaction.Measure.prototype._updateState = function() {
	//设置鼠标样式
	this.setCursor("");
	
	var map = this.getMap();
	if(map == null) return;
	var active = this.getActive();
	
	if(map && active == false){
		map.un('pointermove', bxmap.interaction.Measure._pointerMoveHandler, this);
		//移除帮助操作
		map.removeOverlay(this._helpTooltip);
		this._helpTooltip = null;
		this._helpTooltipElement = null;
		//移除未完成绘制遗留的overlay
		if(this._sketch){
			var uid = bxmap.common.getCounterUid(this._sketch);
			var sketchValues = this._sketchMeasureOverlays[uid];
			var sketchOverlays = sketchValues["overlays"];

			for(var i = 0; i < sketchOverlays.length;i++){
				map.removeOverlay(sketchOverlays[i]);
			}
		}
		
		return;
	}
	
	this._layer.setMap(map);

	if (this._helpTooltipElement == null) {
		this._createHelpTooltip();
	}
	map.on('pointermove', bxmap.interaction.Measure._pointerMoveHandler, this);
};

/**
 * @description 仅清空数据，工具如果是激活状态，依然可用，并没有使之设置为失效状态
 */
bxmap.interaction.Measure.prototype.clear = function(){
	this._layer.getSource().clear(true);
	var map = this.getMap();
	if(map != null){
		var overlays;
		for(var key in this._sketchMeasureOverlays){
			overlays = this._sketchMeasureOverlays[key].overlays;
			if(overlays === undefined) continue;
			for(var i=0; i< overlays.length; i++){
				overlays[i].setMap(null);
			}
		}
		this._sketchMeasureOverlays = {"coorIndex" : 0};
	}
}

/**
 * @private
 * @description 创建临时矢量图层
 * @returns {ol.layer.Vector}
 */
bxmap.interaction.Measure._createLayer = function() {
	// 设置样式
	var measure_style = new ol.style.Style({
		fill : new ol.style.Fill({
			color : 'rgba(255, 255, 255, 0.2)'
		}),
		stroke : new ol.style.Stroke({
			color : '#f27b39',
			width : 3
		}),
		image : new ol.style.RegularShape({
			fill : new ol.style.Fill({
				color : 'red'
			}),
			points : 4,
			radius1 : 15,
			radius2 : 1
		})
	});
	var source = new ol.source.Vector();

	var vector = new ol.layer.Vector({
		source : source,
		style : measure_style
	});
	vector.setZIndex(bxmap.CONFIG_LEVEL_MAX + 1);
	return vector;
}

/**
 * @private
 * @description 鼠标移动时触发
 * @param evt
 */
bxmap.interaction.Measure._pointerMoveHandler = function(evt) {
	if (evt.dragging || evt.map == null) {
		return;
	}
	var helpMsg = bxmap.interaction.Measure.HELP_MSG;

	if (this._sketch) {
		var geom = (this._sketch.getGeometry());
		helpMsg = bxmap.interaction.Measure.CONTINUE_MSG;
	}

	this._helpTooltipElement.innerHTML = helpMsg;
	this._helpTooltip.setPosition(evt.coordinate);

	this._helpTooltipElement.classList.remove('hidden');
}

/**
 * @private
 * @description 格式化测量距离，大于1000m时单位使用公里
 * @param {ol.geom.LineString} line
 * @param {Boolean} isEnd
 * @return {String}
 */
bxmap.interaction.Measure.prototype._formatLength = function(line, isEnd) {
	var map = this.getMap();
	var length;
	var sourceProj = map.getView().getProjection();
	//为经纬度时，转换为米
	if(sourceProj.getUnits() === 'degrees'){
		var wgs84Sphere = new ol.Sphere(6378137);
		var coordinates = line.getCoordinates();
		length = 0;
		for ( var i = 0, ii = coordinates.length - 1; i < ii; ++i) {
			var c1 = ol.proj.transform(coordinates[i], sourceProj, 'EPSG:4326');
			var c2 = ol.proj.transform(coordinates[i + 1], sourceProj, 'EPSG:4326');
			length += wgs84Sphere.haversineDistance(c1, c2);
		}
	} else {
		length = Math.round(line.getLength() * 100) / 100;
	}
	//大于1000m时单位使用公里
	var output;
	if (length > 1000) {
		output = Math.round(length / 1000 * 100) / 100;
		output = isEnd == true ? ('<span class="tooltip-measure">' + output + '</span>公里'):('<span>' + output + '</span>公里');
	} else {
		output = Math.round(length * 100) / 100;
		output = isEnd == true ? ('<span class="tooltip-measure">' + output + '</span>米'):('<span>' + output + '</span>米');
	}
	return output;
};

/**
 * @private
 * @description 格式化测量面积，大于1000000平方米时使用平方公里
 * @param {ol.geom.Polygon} polygon
 * @return {String}
 */
bxmap.interaction.Measure.prototype._formatArea = function(polygon) {
	var map = this.getMap();
	var area;
	var sourceProj = map.getView().getProjection();
	//单位经纬度时转化为平方米
	if(sourceProj.getUnits() === 'degrees'){
		var wgs84Sphere = new ol.Sphere(6378137);
		var geom = /** @type {ol.geom.Polygon} */
		(polygon.clone().transform(sourceProj, 'EPSG:4326'));
		var coordinates = geom.getLinearRing(0).getCoordinates();
		area = Math.abs(wgs84Sphere.geodesicArea(coordinates));
	} else {
		area = polygon.getArea();
	}
	//大于1000000平方米时使用平方公里
	var output;
	if (area > 1000000) {
		output = '<span class="tooltip-measure">'+(Math.round(area / 1000000 * 1000) / 1000) + '</span>平方公里';
	} else {
		output = '<span class="tooltip-measure">' + (Math.round(area * 100) / 100) + '</span>平方米';
	}
	return output;
};

/**
 * @private
 * @description 获取当前绘制要素的覆盖层
 * @returns {Object} 格式为{"overlays":[{ol.Overlay}],"feature":{ol.Feature}}
 */
bxmap.interaction.Measure.prototype._getCurrentSketchOverlays = function(){
	var uid = bxmap.common.getCounterUid(this._sketch);
	if(this._sketchMeasureOverlays[uid] === undefined){
		this._sketchMeasureOverlays[uid] = {
			"overlays" : [],
			"feature" : this._sketch
		};
	}
	var sketchOverlays = this._sketchMeasureOverlays[uid];
	return sketchOverlays.overlays;
}

/**
 * @private
 * @description 开始量测
 * @param evt
 */
bxmap.interaction.Measure.prototype._drawStart = function(evt) {
	// 绘制要素
	this._sketch = evt.feature;
	var sketchOverlays = this._getCurrentSketchOverlays();
	
	var geom = this._sketch.getGeometry();
	if (geom instanceof ol.geom.Polygon) {
		this._listener = geom.on('change', this._drawAreaStart, this);
	} else if (geom instanceof ol.geom.LineString) {
		//创建起点
		var overlay = this._createMeasureTooltip();
		overlay.getElement().innerHTML = "起点";
		overlay.setPosition(geom.getFirstCoordinate());
		sketchOverlays.push(overlay);
		this._listener = geom.on('change', this._drawLengthStart, this);
	}
}

/**
 * @private
 * @description 开始绘制面状图形
 * @param evt
 */
bxmap.interaction.Measure.prototype._drawAreaStart = function(evt){
	var geom = evt.target;
	var output = this._formatArea(geom);
	tooltipCoord = geom.getLastCoordinate();
	this._helpTooltipElement.innerHTML = '<span>面积：' + output + '</span><br><span style="color:#313131">单击确定地点，双击结束</span>';
}

/**
 * @private
 * @description 开始绘制线状图形
 * @param evt
 */
bxmap.interaction.Measure.prototype._drawLengthStart = function(evt){
	var geom = evt.target;
	var output = this._formatLength(geom);
	var tooltipCoord = geom.getLastCoordinate();
	
	var count = geom.getCoordinates().length ;
	if(count != this._sketchMeasureOverlays.coorIndex && count != 2){
		var overlay = this._createMeasureTooltip();
		overlay.getElement().innerHTML = output;
		overlay.setPosition(tooltipCoord);
		var sketchOverlays = this._getCurrentSketchOverlays();
		sketchOverlays.push(overlay);
		this._sketchMeasureOverlays.coorIndex = count;
	}
	
	this._helpTooltipElement.innerHTML ='<span>总长：' + output + '</span><br><span style="color:#313131">单击确定地点，双击结束</span>';
}

/**
 * @private
 * @description 结束量测
 */
bxmap.interaction.Measure.prototype._drawEnd = function() {
	
	var geom = this._sketch.getGeometry();
	if (geom instanceof ol.geom.Polygon) {
		this._drawAreaEnd(geom);
	} else if (geom instanceof ol.geom.LineString) {
		this._drawLengthEnd(geom);
	}
	//取消监听
	geom.un('change', this._drawAreaStart, this);
	geom.un('change', this._drawLengthStart, this);
	this._sketch = null;
	
	if(this._once == true){
		var map = this.getMap();
		map.un('pointermove', bxmap.interaction.Measure._pointerMoveHandler, this);
		map.removeOverlay(this._helpTooltip);
		this._helpTooltip = null;
		this._helpTooltipElement = null;
		this.setActive(false);
		//map.removeInteraction(this);
	}
}

/**
 * @private
 * @description 结束绘制面状图形
 * @param {ol.geom.Polygon} polygon
 */
bxmap.interaction.Measure.prototype._drawAreaEnd = function(polygon){
	var sketchOverlays = this._getCurrentSketchOverlays();
	//创建起点
	var overlay = this._createMeasureTooltip();
	overlay.getElement().innerHTML = '<span>面积：' + this._formatArea(polygon) + '</span>';
	var position = polygon.getInteriorPoint().getLastCoordinate();
	overlay.setPosition(position);
	sketchOverlays.push(overlay);
	
	var close = this._createCloseOverlay();
	close.setPosition(position);
}

/**
 * @private
 * @description 结束绘制线状图形
 * @param {ol.geom.LineString} line
 */
bxmap.interaction.Measure.prototype._drawLengthEnd = function(line){
	var map = this.getMap();
	var sketchOverlays = this._getCurrentSketchOverlays();
	var lastOverlay = sketchOverlays[sketchOverlays.length - 1];
	
	var c0 = lastOverlay.getPosition();
	var c1 = sketchOverlays[sketchOverlays.length - 2].getPosition();
	if(c0.toString() === c1.toString()){
		map.removeOverlay(sketchOverlays.pop());
		lastOverlay = sketchOverlays[sketchOverlays.length - 1];
	}
	lastOverlay.getElement().innerHTML = '<span>总长：' + this._formatLength(line, true) + '</span>';
	
	var close = this._createCloseOverlay();
	close.setPosition(lastOverlay.getPosition());
}

/**
 * @private
 * @description 创建操作帮助tip覆盖层
 * @returns {ol.Overlay}
 */
bxmap.interaction.Measure.prototype._createHelpTooltip = function() {
	var map = this.getMap();
	this._helpTooltipElement = document.createElement('div');
	this._helpTooltipElement.className = 'tooltip hidden';
	this._helpTooltip = new ol.Overlay({
		element : this._helpTooltipElement,
		offset : [ 15, 0 ],
		positioning : 'center-left'
	});
	map.addOverlay(this._helpTooltip);
	
	return this._helpTooltip;
}

/**
 * @private
 * @description 创建测量结果覆盖层
 * @returns {ol.Overlay}
 */
bxmap.interaction.Measure.prototype._createMeasureTooltip = function() {
	var map = this.getMap();
	var measureTooltipElement = document.createElement('div');
	measureTooltipElement.className = 'tooltip tooltip-static';
	var measureTooltip = new ol.Overlay({
		element : measureTooltipElement,
		offset : [ 0, -15 ],
		positioning : 'bottom-center'
	});
	map.addOverlay(measureTooltip);
	return measureTooltip;
}

/**
 * @private
 * @description 创建测量结果覆盖层
 * @returns {ol.Overlay}
 */
bxmap.interaction.Measure.prototype._createCloseOverlay = function() {
	var map = this.getMap();
	var closeTooltipElement = document.createElement('div');
	closeTooltipElement.className = 'tooltip tooltip-close';
	var closeOverlay = new ol.Overlay({
		element : closeTooltipElement,
		offset : [ 0, 6 ],
		positioning : 'bottom-center'
	});
	var uid = bxmap.common.getCounterUid(this._sketch);
	closeTooltipElement.innerHTML = '<a style="color:red;" href:"#" title="清除本次测量">✘</a>';
	map.addOverlay(closeOverlay);
	
	var sketchValues = this._sketchMeasureOverlays[uid];
	var sketchOverlays = sketchValues["overlays"];
	var feature = sketchValues["feature"];
	sketchOverlays.push(closeOverlay);
	var source = this._layer.getSource();
	var _this = this;
	//清除本次测量
	closeTooltipElement.addEventListener('click', function() {
		for(var i = 0; i < sketchOverlays.length;i++){
			map.removeOverlay(sketchOverlays[i]);
		}
		source.removeFeature(feature);
		delete _this._sketchMeasureOverlays[uid];
	});
	return closeOverlay;
}

/*----------标绘，依赖于GISpace动态标绘开源项目{bxmap.interaction.Plot}---------*/
/**
 * @classdesc 用于绘制军事图形，依赖于依赖于GISpace动态标绘开源项目
 * @extends {ol.interaction.Interaction}
 * @param options {Object}
 * @param options.type {String} 默认为polygon
 */
bxmap.interaction.Plot = function (options) {
    ol.interaction.Interaction.call(this, { handleEvent: this._handleKeyboardEvent });

    this.plotDraw = null; //绘制标绘
    this.plotEdit = null; //编辑标绘
    this.plotLayer = null; //标绘图层
    this.editingFeatures = new ol.Collection();//当前选择的要素

    var opt_options = options ? options : {};
    var type = opt_options.type ? opt_options.type : "polygon";
    this.setDrawType(type);
}
ol.inherits(bxmap.interaction.Plot, ol.interaction.Interaction);

/**
 * @inheritdoc
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.Plot.prototype.setMap = function (map) {
    ol.interaction.Interaction.prototype.setMap.call(this, map);
    if (map) {
        // 初始化标绘绘制工具，添加绘制结束事件响应
        var plotDraw = this.plotDraw = new P.PlotDraw(map);
        plotDraw.on("draw_end", this._onDrawEnd, this);
        // 初始化标绘编辑工具
        var plotEdit = this.plotEdit = new P.PlotEdit(map);

        var plotLayer = this.plotLayer = this.getLayer();
        // 添加标绘图层
        map.addLayer(plotLayer);

        map.on('click', function (e) {
            if (plotDraw.isDrawing()) return;

            var feature = map.forEachFeatureAtPixel(e.pixel, function (feature, layer) {
                return feature;
            },{
                layerFilter: function (layer) {
                    return layer == plotLayer;
                },
                hitTolerance: 1 //1像素，仅在canvas render中有效
            });
            if (feature) {
                this.editingFeatures.push(feature);
                // 开始编辑
                this._startEditingFeature(feature);
            } else {
                // 结束编辑
                this._finishEditing();
            }
        },this);
    }
}

/**
 * @description 设置绘制图形类型
 * @param {String} drawType 类型
 * @example <caption> 图形类型有效值 </caption>
 * 	arc - 弧线，curve - 曲线，polyline - 折线，freehandline - 自由线，circle - 圆，
 * 	ellipse - 椭圆，closedcurve - 曲线面，polygon - 多边形，rectangle - 矩形，
 *	freehandpolygon - 自由面，gatheringplace - 聚集地，doublearrow - 钳击双箭头，
 *  straightarrow - 直线箭头，finearrow - 细直箭头，assaultdirection - 直箭头，
 *	attackarrow - 进攻箭头，tailedattackarrow - 燕尾进攻箭头，squadcombat - 斜箭头，
 * 	tailedsquadcombat - 燕尾斜箭头
 */
bxmap.interaction.Plot.prototype.setDrawType = function(drawType){
	var map = this.getMap();
	this.setCursor("default");
	
	this.set("draw_type", drawType);
	if (this.plotEdit && this.plotDraw) {
	    this._finishEditing();
		this.plotDraw.activate(drawType);
	}
}

/**
 * @description 获取绘制图形类型
 * @returns {String} 绘制图形类型
 */
bxmap.interaction.Plot.prototype.getDrawType = function(){
	return this.get("draw_type");
}

/**
 * @private
 * @description 更新状态
 */
bxmap.interaction.Plot.prototype._updateState = function() {
	var map = this.getMap();
	if(map == null) return;
	var active = this.getActive();
	
	var plotDraw = this.plotDraw;
	var plotEdit = this.plotEdit;
	var layer = this.plotLayer;
	//使激活
	if(active){
	    this._finishEditing();
		if(plotDraw){
			var type = this.getDrawType();
			plotDraw.activate(type);
		}
	}else{
		if(plotDraw){
			plotDraw.deactivate();
		}
        this._finishEditing();
	}
};

/**
 * @description 获取标绘图层
 * @returns {ol.layer.Vector}
 */
bxmap.interaction.Plot.prototype.getLayer = function(){
	if(this.plotLayer == null){
		// 设置标绘符号显示的默认样式
	    var stroke = new ol.style.Stroke({color: '#FF0000', width: 2});
	    var fill = new ol.style.Fill({color: 'rgba(0,255,0,0.4)'});
	    var image = new ol.style.Circle({fill: fill, stroke: stroke, radius: 8});
	    drawStyle = new ol.style.Style({image: image, fill:fill, stroke:stroke});
	
	    // 绘制好的标绘符号，添加到FeatureOverlay显示。
	    var layer = this.plotLayer = new ol.layer.Vector({
	        source: new ol.source.Vector()
	    });
	    layer.setStyle(drawStyle);
	    layer.setZIndex(bxmap.CONFIG_LEVEL_MAX + 1);//保持不被其他图层覆盖
	}
    return this.plotLayer;
}

/**
 * @private
 * @param mapBrowserEvent
 * @returns {Boolean}
 */
bxmap.interaction.Plot.prototype._handleKeyboardEvent = function(mapBrowserEvent) {
	var stopEvent = false;
	var map = this.getMap();
	if(map == null) return;
	
	if (mapBrowserEvent.type == "keydown" || mapBrowserEvent.type == "keypress") {
		var keyEvent = mapBrowserEvent.originalEvent;
		var keyCode = keyEvent.keyCode;
		if (keyCode == 46) {//delete
			var plotLayer = this.plotLayer;
		  	this.editingFeatures.forEach(function (elem, index, array) {
		  		var close = elem.get("close_button");
		  		if (close) {
		  			map.removeOverlay(close);
		  		}
		  		var source = plotLayer.getSource();
	            source.removeFeature(elem);
		  	});
		  	this.plotEdit.deactivate();
		  	this.editingFeatures.clear();    
		  	mapBrowserEvent.preventDefault();
		  	stopEvent = true;
	  }
	}
	return !stopEvent;
}

/**
 * @private
 * @description 绘制结束后，添加图层显示。
 */
bxmap.interaction.Plot.prototype._onDrawEnd = function (event) {
    var feature = event.feature;
    if (feature) {
        //添加到编辑要素集合中
        this.editingFeatures.push(feature);
        //添加到标绘图层
        this.plotLayer.getSource().addFeature(feature);
        this._bindingCloseButton(feature);
        //开始编辑
        this._startEditingFeature(feature);
    }
}

/**
 * @private
 * @description 开始编辑要素
 */
bxmap.interaction.Plot.prototype._startEditingFeature = function (feature) {
    if (this.plotEdit == null || feature == null) return;
    this.plotEdit.activate(feature);
    var close = feature.get("close_button");
    var map = this.getMap();
    if (map && close) {
        var exists = map.getOverlayById(close.getId());
        if(exists == null){
            map.addOverlay(close);
        }
        var ctlPoints = this.plotEdit.getControlPoints();
        var point = ctlPoints[ctlPoints.length - 1];
        close.setPosition(point);
    }
}

/**
 * @private
 * @description 结束编辑要素
 */
bxmap.interaction.Plot.prototype._finishEditing = function () {
    if (this.plotEdit) {
        this.plotEdit.deactivate();
    }

    //移除删除按钮
    var map = this.getMap();
    if (map == null) return;
    this.editingFeatures.forEach(function (elem, index, array) {
        var close = elem.get("close_button");
        if (map && close) {
            map.removeOverlay(close);
        }
    });
    this.editingFeatures.clear();
}

/**
 * @private
 * @description 获取删除按钮
 * @returns {ol.Overlay}
 */
bxmap.interaction.Plot.prototype._bindingCloseButton = function (feature) {
    if (feature == null) return;
    var uid = bxmap.common.getCounterUid(feature);
    var closeTooltipElement = document.createElement('div');
    closeTooltipElement.className = 'tooltip tooltip-close';
    var closeOverlay = this.closeButton = new ol.Overlay({
        id: uid,
        element: closeTooltipElement,
        offset: [16, 0],
        positioning: 'bottom-center'
    });
    closeTooltipElement.innerHTML = '<a style="color:red;" href:"#" title="删除">✘</a>';
    feature.set("close_button", closeOverlay);

    //执行删除绘制
    var plotLayer = this.plotLayer;
    var plotEdit = this.plotEdit;
    var map = this.getMap();
    closeTooltipElement.addEventListener('click', function () {
        if (plotEdit && feature && plotLayer) {
            var close = feature.get("close_button");
            if (close && map) {
                map.removeOverlay(closeOverlay);
            }
            var source = plotLayer.getSource();
            source.removeFeature(feature);
            plotEdit.deactivate();
        }
    });
}

/*-------------------------------------------------------*/
/*   要素平移/缩放变换控制工具  {bxmap.interaction.Transform}     */
/*-------------------------------------------------------*/
/**
 * @classdesc 要素平移/缩放变换控制工具。
 * 1.当stretch/scale时，按下ctrl键则固定对边或对角拉伸，默认是固定中心点拉伸；
 * 2.当stretch/scale时，按下shift键则保持比例缩放几何，默认不保持比例；
 * @constructor
 * @extends {ol.interaction.Pointer}
 * @fires select | rotatestart | rotating | rotateend | translatestart | translating | translateend | scalestart | scaling | scaleend
 * 		var trans = new bxmap.interaction.Transform();
 * 		trans.on("select",function(evt){});
 * @param options {Object}
 * @param options.translate {Boolean} 是否可以平移几何，默认为true
 * @param options.stretch {Boolean} 是否可以拉伸几何，默认为true
 * @param options.scale {Boolean} 是否可以按比例缩放，默认为true
 * @param options.rotate {Boolean} 是否可以旋转角度，默认为true
 * @param options.highlight {Boolean} 是否可以高亮选中要素，默认为true
 * @example <caption> 创建变换工具 </caption>
 * var transform = new bxmap.interaction.Transform();
 * bmap.setCurrentMutexInteraction(transform);
 * //选中要素
 * transform.select(features);
 */
bxmap.interaction.Transform = function(options){
	var _this = this;

    /**
	 * @description 创建一个临时图层存储选中的要素
     * @type {ol.layer.Vector}
     * @private
     */
	this._selectedFeatureLayer = new ol.layer.Vector({
		source: new ol.source.Vector({
			useSpatialIndex: false
		}),
		// 通过handle设置控制器样式
		style: function (feature){
			if (!feature.getGeometry()) {
				return null;
			}
			return _this.selectedStyle[feature.getGeometry().getType()];
		}
	});
	this._selectedFeatureLayer.setZIndex(bxmap.CONFIG_LEVEL_MAX + 9);

    /**
	 * @description 创建一个临时图层存储控制器
     * @type {ol.layer.Vector}
     * @private
     */
	this._overlayLayer = new ol.layer.Vector({
		source: new ol.source.Vector({
			useSpatialIndex: false
		}),
		name:'temp Transform overlay',
		displayInLayerSwitcher: false,
		// 通过handle设置控制器样式
		style: function (feature){
			return (_this.style[(feature.get('handle')||'default')+(feature.get('constraint')||'')+(feature.get('option')||'')]);
		}
	});
	this._overlayLayer.setZIndex(bxmap.CONFIG_LEVEL_MAX + 10);
	
	ol.interaction.Pointer.call(this, {	
		handleDownEvent: this._handleDownEvent,
		handleDragEvent: this._handleDragEvent,
		handleMoveEvent: this._handleMoveEvent,
		handleUpEvent: this._handleUpEvent
	});
	
	this.on ('propertychange', function(){
		this._drawSketch();
	});
	var opt_options = options || {};
    /**
	 * @description 选中的要素
     * @private
     */
	this._selectedFeatures = null;
    /**
     * @description 临时存储选中要素，用于激活状态改变的中转
     * @private
     */
	this._tmp_selectedFeatures = null;
    /**
     * @description 是否由控制器平移要素
     * @private
     */
	this._canTranslate = opt_options.translate!==false;
	this.set('translate', this._canTranslate);
    /**
     * @description 是否可以拉伸要素,仅对选中一个要素时有效
     * @private
     */
	this._canStretch = opt_options.stretch!==false;
	this.set('stretch', this._canStretch);
    /**
     * @description 是否可以按比例放大要素,仅对选中一个要素时有效
     * @private
     */
	this._canScale = opt_options.scale!==false;
	this.set('scale', this._canScale);
    /**
     * @description 是否可以旋转要素，仅对选中一个要素时有效
     * @private
     */
	this._canRotate = opt_options.rotate!==false;
	this.set('rotate', this._canRotate);
    /**
     * @description 控制器样式
     * @readonly
     */
	this.style = {};
    /**
     * @description 选中要素样式
     * @readonly
     */
	this.selectedStyle = {};
    /**
     * @description 选中要素是否高亮
     * @private
     */
	this._highlight = (opt_options.highlight!==false);
    /**
     * @description 控制器范围
     * @private
     */
	this._handleExtent = null;
    /**
     * @description 控制器范围矢量要素
     * @private
     */
	this._bboxFeature = null;
}
ol.inherits(bxmap.interaction.Transform, ol.interaction.Pointer);

/**
 * @const
 * @description Cursors鼠标样式
 */
bxmap.interaction.Transform.prototype.Cursors = {	
	'default':	'auto',
	'select':	'pointer',
	'translate':'move',
	'rotate':	'move',
	'scale':	'ne-resize', 
	'scale1':	'nw-resize', 
	'scale2':	'ne-resize', 
	'scale3':	'nw-resize',
	'scalev':	'e-resize', 
	'scaleh1':	'n-resize', 
	'scalev2':	'e-resize', 
	'scaleh3':	'n-resize'
};

/**
 * @description 选中要素
 * @param {Array.<ol.Feature> | null} features 参数为null时表示取消选中
 */
bxmap.interaction.Transform.prototype.select = function(features){
	//重置数据
	this._selectedFeatures = features;
	this._tmp_selectedFeatures = features;
	this._isPointType = false;
	this._bboxFeature = null;
	
	if(features){
		if(features.length === 1){
			/** 判断是否为点类型*/
			this._isPointType = this._isPointFeature(features[0]);
		}
		//赋值features0的extent，防止ol.extent.extend修改
		if(features[0]){
			var ext = features[0].getGeometry().getExtent();
			this._handleExtent = [ext[0],ext[1],ext[2],ext[3]];
		}
		for(var i=1;i<features.length;i++){
			ol.extent.extend(this._handleExtent,features[i].getGeometry().getExtent());
		}
		
		//多个要素时，禁用拉伸/比例/旋转
		if(features.length > 1){
			this.set("stretch", false);
			this.set("scale", false);
			this.set("rotate", false);
		}else{
			this.set("stretch", this._canStretch);
			this.set("scale", this._canScale);
			this.set("rotate", this._canRotate);
		}
	}
	
	//设置要素是否高亮状态
	this.setHighlight(this._highlight);

	this._drawSketch();
	this.dispatchEvent({ type:'select',features:this._selectedFeatures });
}

/**
 * @description 设置要素是否高亮状态
 * @param {Boolean} highlight true-高亮；false-不高亮
 */
bxmap.interaction.Transform.prototype.setHighlight = function(highlight){
	var selectedSource = this._selectedFeatureLayer.getSource();
	selectedSource.clear();
	if(highlight && this._selectedFeatures){
		selectedSource.addFeatures(this._selectedFeatures);
	}
	this._highlight = highlight;
}

/**
 * @inheritDoc
 * @description 设置工具激活状态
 * @param {Boolean} active true-激活；false-失效
 */
bxmap.interaction.Transform.prototype.setActive = function(active){
	ol.interaction.Pointer.prototype.setActive.call(this, active);

	//重绘控制器
	this._selectedFeatures = active ? this._tmp_selectedFeatures : null;
	this._drawSketch();
	//重置高亮状态
	this.setHighlight(this._highlight);
}

/**
 * @inheritDoc
 * @param {ol.CanvasMap} map 地图对象
 */
bxmap.interaction.Transform.prototype.setMap = function(map) {
	var overlayLayer = this._overlayLayer;
	var selectedLayer = this._selectedFeatureLayer;
	if (this.getMap()) {
		this.getMap().removeLayer(overlayLayer);
		this.getMap().removeLayer(selectedLayer);
	}
	ol.interaction.Pointer.prototype.setMap.call (this, map);
	selectedLayer.setMap(map);
	overlayLayer.setMap(map);
 	if (map !== null) {
		this.isTouch = /touch/.test(map.getViewport().className);
		this.selectedStyle = bxmap.common.getDefaultSelectedStyle();
		this.setDefaultStyle();
	}
};

/** 
 * @description 设置控制器默认样式
 */
bxmap.interaction.Transform.prototype.setDefaultStyle = function() {
	var stroke = new ol.style.Stroke({ color: [255,0,0,1], width: 1 });
	var strokedash = new ol.style.Stroke({ color: [255,0,0,1], width: 1, lineDash:[4,4] });
	var fill0 = new ol.style.Fill({ color:[255,0,0,0.01] });
	var fill = new ol.style.Fill({ color:[255,255,255,0.8] });
	var circle = new ol.style.RegularShape({
		fill: fill,
		stroke: stroke,
		radius: this.isTouch ? 12 : 6,
		points: 15
	});
	circle.getAnchor()[0] = this.isTouch ? -10 : -5;
	var bigpt = new ol.style.RegularShape({
		fill: fill,
		stroke: stroke,
		radius: this.isTouch ? 16 : 8,
		points: 4,
		angle: Math.PI/4
	});
	var smallpt = new ol.style.RegularShape({
		fill: fill,
		stroke: stroke,
		radius: this.isTouch ? 12 : 6,
		points: 4,
		angle: Math.PI/4
	});
	function createStyle (img, stroke, fill) {
		return [ new ol.style.Style({image:img, stroke:stroke, fill:fill}) ];
	}
	/** 控制器样式 */
	this.style = {	
		'default': createStyle (bigpt, strokedash, fill0),
		'translate': createStyle (bigpt, stroke, fill),//平移时中心位置样式
		'rotate': createStyle (circle, stroke, fill),//旋转控制
		'rotate0': createStyle (bigpt, stroke, fill),//旋转时中心位置
		'scale': createStyle (bigpt, stroke, fill),//左-下
		'scale1': createStyle (bigpt, stroke, fill),//左-上
		'scale2': createStyle (bigpt, stroke, fill),//右-上
		'scale3': createStyle (bigpt, stroke, fill),//右-下
		'scalev': createStyle (smallpt, stroke, fill),//左-中
		'scaleh1': createStyle (smallpt, stroke, fill),//上-中
		'scalev2': createStyle (smallpt, stroke, fill),//右-中
		'scaleh3': createStyle (smallpt, stroke, fill)//下-中
	};
	
	this._drawSketch();
}

/**
 * @description 设置自定义样式
 * @param {String}  style 控制名称有效值包括default|translate|rotate|rotate0|scale|scale1|
 * scale2|scale3|scalev|scaleh1|scalev2|scaleh3
 * @param {ol.style.Style}|{Array<ol.style.Style>} olstyle 控制样式
 */
bxmap.interaction.Transform.prototype.setHandleStyle = function(style, olstyle) {
	if (!olstyle) return;
	if (olstyle instanceof Array) 
		this.style[style] = olstyle;
	else 
		this.style[style] = [ olstyle ];
	for (var i=0; i<this.style[style].length; i++){
		var im = this.style[style][i].getImage();
		if (im) 
		{	if (style == 'rotate') im.getAnchor()[0] = -5;
			if (this.isTouch) im.setScale(1.8);
		}
		var tx = this.style[style][i].getText();
		if (tx) 
		{	if (style == 'rotate') tx.setOffsetX(this.isTouch ? 14 : 7);
			if (this.isTouch) tx.setScale(1.8);
		}
	}
	this._drawSketch();
};

/**
 * @description 设置选中要素的样式
 * @param {String} style Point|MultiPoint|LineString|MultiLineString
 * 						Polygon|MultiPolygon|Circle|GeometryCollection
 * @param {ol.style.Style}|{Array<ol.style.Style>} olstyle 要素样式
 */
bxmap.interaction.Transform.prototype.setSelectedFeatureStyle = function(style, olstyle) {
	if (!olstyle) return;
	if (olstyle instanceof Array){
		this.selectedStyle[style] = olstyle;
	}else {
		this.selectedStyle[style] = [ olstyle ];
	}
	this.setHighlight(this._highlight);
};

/**
 * @private
 * @description 绘制控制器
 * @param {Boolean} center 旋转时是否绘制中心控制器
 */
bxmap.interaction.Transform.prototype._drawSketch = function(center){
	var overlayer = this._overlayLayer;
	overlayer.getSource().clear();
	if (!this._selectedFeatures || this._selectedFeatures.length == 0) return;
	if (center===true){	
		//旋转时中心控制器
		overlayer.getSource().addFeature(new ol.Feature({ 
			geometry: new ol.geom.Point(this._center), 
			handle:'rotate0' 
		}));
		if (!this._isPointType) {
			//包络框
			var geom = ol.geom.Polygon.fromExtent(this._handleExtent);
			var f = this._bboxFeature = new ol.Feature(geom);
			overlayer.getSource().addFeature (f);
		}
	} else{	
		var ext = this._handleExtent;
		if (this._isPointType) {	
			var p = this.getMap().getPixelFromCoordinate([ext[0], ext[1]]);
			ext = ol.extent.boundingExtent(
			[	
			 	this.getMap().getCoordinateFromPixel([p[0]-10, p[1]-10]),
				this.getMap().getCoordinateFromPixel([p[0]+10, p[1]+10])
			]);
		}
		var geom = ol.geom.Polygon.fromExtent(ext);
		var f = this._bboxFeature = new ol.Feature(geom);
		var features = [];
		var g = geom.getCoordinates()[0];
		if (!this._isPointType) {	
			features.push(f);
			// Middle
			if (this.get('stretch') && this.get('scale')) {
				for (var i=0; i<g.length-1; i++){
					f = new ol.Feature({ 
						geometry: new ol.geom.Point([(g[i][0]+g[i+1][0])/2,(g[i][1]+g[i+1][1])/2]), 
						handle:'scale', 
						constraint:i%2?"h":"v", 
						option:i
					});
					features.push(f);
				}
			}
			// Handles
			if (this.get('scale')) {
				for (var i=0; i<g.length-1; i++){
					f = new ol.Feature( {
						geometry: new ol.geom.Point(g[i]),
						handle:'scale',
						option:i
					});
					features.push(f);
				}
			}
		}
		// Center
		if (this.get('translate')){
			f = new ol.Feature( {
				geometry: new ol.geom.Point([(g[0][0]+g[2][0])/2, (g[0][1]+g[2][1])/2]),
				handle:'translate'
			});
			features.push(f);
		}
		// Rotate
		if (this.get('rotate')) {
			f = new ol.Feature( {
				geometry: new ol.geom.Point(g[3]),
				handle:'rotate'
			});
			features.push(f);
		}
		overlayer.getSource().addFeatures(features);
	}
};

/**
 * @param evt
 * @returns {boolean}
 * @private
 */
bxmap.interaction.Transform.prototype._handleDownEvent = function(evt){
	var sel = this._getFeatureAtPixel(evt.pixel);
	switch(sel.handle){
		case "translate":
		{
			this._mode = sel.handle;
			this._coordinate = evt.coordinate;
			this._pixel = evt.pixel;
			//触发translatestart事件
			this.dispatchEvent({ type:this._mode+'start',features:this._selectedFeatures, pixel: this._pixel, coordinate: this._coordinate });
			break;
		}
		case "rotate":
		{
			//用于选中一个要素时旋转
			this._mode = sel.handle;
			this._coordinate = evt.coordinate;
			this._pixel = evt.pixel;
			var tFeature = this._selectedFeatures[0];
			//设置feature style参数
			var opt = bxmap.common.getFeatureIconParams(tFeature);
			opt.rotation = opt.rotation || 0;
			
			this._startAngle = opt.rotation;

			this._geom = tFeature.getGeometry().clone();
			var center = this._center = ol.extent.getCenter(this._geom.getExtent());
			//起始方位角：起始线方向evt ——> center顺时针到x轴角度
			this._angle = Math.atan2(center[1]-evt.coordinate[1], center[0]-evt.coordinate[0]);
			//触发rotatestart事件
			this.dispatchEvent({ type:this._mode+'start', feature:tFeature, angle: this._startAngle, pixel: evt.pixel, coordinate: evt.coordinate });
			break;
		}
		case "scale":
		{
			//用于选中一个要素时拉伸
			this._mode = sel.handle;
			this._opt = sel.option;//控制器编号
			this._constraint = sel.constraint;//h(水平)|v(垂直)
			this._coordinate = evt.coordinate;
			this._pixel = evt.pixel;
			var tFeature = this._selectedFeatures[0];
			this._geom = tFeature.getGeometry().clone();
			this._g = (ol.geom.Polygon.fromExtent(this._geom.getExtent())).getCoordinates()[0];
			this._center = ol.extent.getCenter(this._geom.getExtent());
			//触发scalestart事件
			this.dispatchEvent({ type:this._mode+'start',feature:tFeature, pixel: evt.pixel, coordinate: evt.coordinate });
			break;
		}
		default: break;
	}
	return true;
};
/**
 * @param evt
 * @private
 */
bxmap.interaction.Transform.prototype._handleDragEvent = function(evt){
	var selectedFeature, geometry;
	switch (this._mode){
		case 'rotate':
		{	
			var center = this._center;
			//旋转后方位角：旋转线方向evt ——> center顺时针到x轴角度
			var a = Math.atan2(center[1]-evt.coordinate[1], center[0]-evt.coordinate[0]);
			var angle = a-this._angle;
			selectedFeature = this._selectedFeatures[0];
			var canRotate = this._selectedFeatures.length === 1;
			if(canRotate){
				geometry = this._geom.clone();
				geometry.rotate(angle, center);
				//设置当前旋转角度
				var opt = bxmap.common.getFeatureIconParams(selectedFeature);
				opt.rotation = this._startAngle - angle;
				
				selectedFeature.setGeometry(geometry);
				this._handleExtent = geometry.clone().getExtent();
			}
			this._drawSketch(true);
			//触发rotating事件
			this.dispatchEvent({ type:'rotating', feature: selectedFeature, angle: this._startAngle - angle, pixel: evt.pixel, coordinate: evt.coordinate });
			break;
		}
		case 'translate':
		{	
			var deltaX = evt.coordinate[0] - this._coordinate[0];
			var deltaY = evt.coordinate[1] - this._coordinate[1];
			
			var geoExtent, newExtent;
			for(var i=0;i<this._selectedFeatures.length;i++){
				selectedFeature = this._selectedFeatures[i];
				geoExtent = selectedFeature.getGeometry().clone().getExtent();
				newExtent = newExtent ? ol.extent.extend(newExtent, geoExtent) : geoExtent;
				selectedFeature.getGeometry().translate(deltaX, deltaY);
			}
			this._handleExtent = newExtent;
			
			var handles = this._overlayLayer.getSource().getFeatures();
			for(var i=0;i<handles.length;i++){
				handles[i].getGeometry().translate(deltaX, deltaY);
			}

			this._coordinate = evt.coordinate;
			//触发translating事件
			this.dispatchEvent({ type:'translating',features:this._selectedFeatures, delta:[deltaX,deltaY], pixel: evt.pixel, coordinate: evt.coordinate });
			break;
		}
		case 'scale':
		{	
			selectedFeature = this._selectedFeatures[0];
			//是否可以拉伸
			var canScale = this._selectedFeatures.length === 1 && !this._isPointFeature(selectedFeature);
			if(!canScale) break;
			var center = this._center;
			//按下ctrl键固定对边缩放
			if (evt.originalEvent.metaKey || evt.originalEvent.ctrlKey){
				center = this._g[(Number(this._opt)+2)%4];
			}
			//拉伸比例xy方向
			var scx = (evt.coordinate[0] - center[0]) / (this._coordinate[0] - center[0]);
			var scy = (evt.coordinate[1] - center[1]) / (this._coordinate[1] - center[1]);

			if (this._constraint){
				if (this._constraint=="h") scx=1;
				else scy=1;
			}else{
				//shift键保持比例缩放
				if (evt.originalEvent.shiftKey){
					scx = scy = Math.min(scx,scy);
				}
			}

			geometry = this._geom.clone();
			geometry.applyTransform(function(g1, g2, dim){
				if (dim<2) return g2;
				
				for (i=0; i<g1.length; i+=dim)
				{	if (scx!=1) g2[i] = center[0] + (g1[i]-center[0])*scx;
					if (scy!=1) g2[i+1] = center[1] + (g1[i+1]-center[1])*scy;
				}
				return g2;
			});
			selectedFeature.setGeometry(geometry);
			this._handleExtent = geometry.clone().getExtent();
			this._drawSketch();
			//触发scaling事件
			this.dispatchEvent({ type:'scaling', feature: selectedFeature, scale:[scx,scy], pixel: evt.pixel, coordinate: evt.coordinate });
			break;
		}
		default: {
			//执行拖动地图
			this._isdragging = true;
			var centroid = evt.pixel;
			if (this._lastCentroid) {
				var deltaX = this._lastCentroid[0] - centroid[0];
			    var deltaY = centroid[1] - this._lastCentroid[1];
			    var view = evt.map.getView();
			    var resolution = view.getResolution();
			    var center = [deltaX * resolution, deltaY * resolution];
			    ol.coordinate.rotate(center, view.getRotation());
			    ol.coordinate.add(center, view.getCenter());
			    center = view.constrainCenter(center);
			    view.setCenter(center);
			}
			this._lastCentroid = centroid;
			break;
		}
	}
};
/**
 * @param evt
 * @private
 */
bxmap.interaction.Transform.prototype._handleMoveEvent = function(evt){
	//鼠标滑动到控制器要素时鼠标样式
	if (!this._mode) {	
		var map = evt.map;
		var sel = this._getFeatureAtPixel(evt.pixel);
		var element = evt.map.getViewport();
		if (sel.feature) {
			var c = sel.handle ? this.Cursors[(sel.handle||'default')+(sel.constraint||'')+(sel.option||'')] : this.Cursors.select;
			
			if (this.previousCursor_===undefined) {
				this.previousCursor_ = element.style.cursor;
			}
			element.style.cursor = c;
		} else  {
			if (this.previousCursor_!==undefined) element.style.cursor = this.previousCursor_;
			this.previousCursor_ = undefined;
		}
	}
};
/**
 * @private
 * @description 鼠标弹起事件
 * @param evt
 * @returns {Boolean}
 */
bxmap.interaction.Transform.prototype._handleUpEvent = function(evt){
	if(this._isdragging){
		//结束地图拖动行为
		this._isdragging = false;
		this._lastCentroid = null;
		return false;
	}

	switch(this._mode){
		case "translate":
		{
			this.dispatchEvent({ type:this._mode+'end',features: this._selectedFeatures });
			break;
		}
			
		case "rotate":
		case "scale":
		{
			this.dispatchEvent({ type:this._mode+'end',feature: this._selectedFeatures[0],oldgeom: this._geom });
			break;
		}
		default:break;
	}
	//绘制控制器
	this._drawSketch();
	this._mode = null;
	return false;
};

/**
 * @private
 * @description 获取selected Feature或者handle Feature
 */
bxmap.interaction.Transform.prototype._getFeatureAtPixel = function(pixel) {
	var _this = this;
	var sel;
	return this.getMap().forEachFeatureAtPixel(pixel, function (feature, layer) {
		sel = { 
				feature: feature, 
				handle:feature.get('handle'), 
				constraint:feature.get('constraint'), 
				option:feature.get('option') 
		};
        return sel;
    },{
        layerFilter: function (layer) {
            return layer == _this._overlayLayer;
        },
        hitTolerance: 1 //1像素，仅在canvas render中有效
    }) || {};
}

/**
 * @private
 * @description 判断要素是否为点类型
 */
bxmap.interaction.Transform.prototype._isPointFeature = function(feature){
	var ispt = feature.getGeometry().getType() === "Point" || false;
	return ispt;
}

/**
 * @description 清空选中的数据，内部执行为select(null);
 */
bxmap.interaction.Transform.prototype.clear = function(){
	this.select(null);
}

/*--------------------------------------------------------------------------------------------------*/
/*    可单击地图选中要素，对选中要素平移/缩放变换  {bxmap.interaction.TransformClickable}           */
/*--------------------------------------------------------------------------------------------------*/
/**
 * @classdesc 单击地图选中要素，然后对选中要素进行平移/缩放变换
 * @constructor
 * @extends {bxmap.interaction.Transform}
 * @param options {Object} 参考{bxmap.interaction.Transform}
 * @param options.layers {undefined | Array.<ol.layer.Layer>} 可选中要素的图层列表，如果为空则为所有的可见图层
 * @param options.style {ol.style.Style | Array.<ol.style.Style> | ol.StyleFunction | undefined} 选中要素样式
 */
bxmap.interaction.TransformClickable = function(options){
	var opt_options = options || {};
	opt_options.highlight = false;//禁用高亮
	//opt_options.rotate = false;//禁用旋转
	
    /**
	 * @description 点击选择要素交互工具
     * @type {ol.interaction.Select}
     * @private
     */
	this._selectInteraction = new ol.interaction.Select({
		layers: opt_options.layers,
		style: opt_options.style,
		toggleCondition: ol.events.condition.never
	});
	
	bxmap.interaction.Transform.call(this, opt_options);
	
	//选中要素
	this._selectInteraction.on("select",function(evt){
		this.select(evt.selected);
	},this);
}
ol.inherits(bxmap.interaction.TransformClickable, bxmap.interaction.Transform);

/**
 * @inheritDoc
 * @description 设置地图对象
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.TransformClickable.prototype.setMap = function (map) {
	if(this.getMap()){
		this.getMap().removeInteraction(this._selectInteraction);
	}
	if(map){
		map.addInteraction(this._selectInteraction);
	}
	bxmap.interaction.Transform.prototype.setMap.call(this, map);
}

/**
 * @inheritDoc
 * @description 设置激活状态
 * @param {Boolean} active ture-激活；false-失效
 */
bxmap.interaction.TransformClickable.prototype.setActive = function(active){
	this._selectInteraction.setActive(active);
	bxmap.interaction.Transform.prototype.setActive.call(this, active);
}

/**
 * @description 清空选中的数据
 */
bxmap.interaction.TransformClickable.prototype.clear = function(){
	this._selectInteraction.getFeatures().clear();
	bxmap.interaction.Transform.prototype.clear.call(this);
}

/*-----------------------------------------------------------------------------------*/
/*                         绘制工具  {bxmap.interaction.Draw}                        */
/*-----------------------------------------------------------------------------------*/
/**
 * @classdesc 绘制工具
 * @constructor
 * @extends {ol.interaction.Draw}
 * @param options {Object} 更多参数参考{ol.interaction.Draw}
 * @param options.source {ol.source.Vector | undefined}
 * @param param.type {String} 绘制的几何类型'Point'|'LineString'|'Polygon'|'MultiPoint'|
 * 'MultiLineString'|'MultiPolygon'|'Circle'|'Square'|'Box'|'Star'
 * @param options.geometryName {String | undefined} 几何字段名称
 * @param options.style ol.style.Style | Array.<ol.style.Style> | ol.StyleFunction | undefined 绘制几何样式
 * @fire "drawend"|"drawstart"
 */
bxmap.interaction.Draw = function(options){
	this._opt = options || {};
	this._opt.freehandCondition = options.freehandCondition || ol.events.condition.never;//禁用手绘功能
	this._geometryFunction = options.geometryFunction;
	this._processOpt(options.type);
	ol.interaction.Draw.call(this, this._opt);
}
ol.inherits(bxmap.interaction.Draw, ol.interaction.Draw);

/**
 * @inheritDoc
 * @description 设置激活状态
 * @param {Boolean} active ture-激活；false-失效
 */
bxmap.interaction.Draw.prototype.setActive = function(active){
	ol.interaction.Draw.prototype.setActive.call(this, active);
	this.setCursor("default");
}

/**
 * @private
 * @description 处理draw工具参数，绘制矩形、正方形和星形的处理
 */
bxmap.interaction.Draw.prototype._processOpt = function(type){
	var opt_options = this._opt;
	opt_options.type = type;
	opt_options.geometryFunction = this._geometryFunction;
	
	switch(type){
		case "Square":
		{
			opt_options.type = "Circle";
			opt_options.geometryFunction = ol.interaction.Draw.createRegularPolygon(4); 
			break;
		}
		case "Box":
		{
			opt_options.type = "Circle";
			opt_options.geometryFunction = ol.interaction.Draw.createBox(); 
			break;
		}
		case "Star":
		{
			opt_options.type = "Circle";
			opt_options.geometryFunction = bxmap.interaction.Draw.getStarGeometryFunction;
		}
		default: break;
	}
}

/**
 * @description 获取五角星默认几何修改函数
 */
bxmap.interaction.Draw.getStarGeometryFunction = function(coordinates, geometry) {
	if (!geometry) {
		geometry = new ol.geom.Polygon(null);
	}
	var center = coordinates[0];
	var last = coordinates[1];
  	var dx = center[0] - last[0];
 	var dy = center[1] - last[1];
   	var radius = Math.sqrt(dx * dx + dy * dy);
   	var rotation = Math.atan2(dy, dx);
  	var newCoordinates = [];
   	var numPoints = 10;
  	for (var i = 0; i < numPoints; ++i) {
  		var angle = rotation + i * 2 * Math.PI / numPoints;
        var fraction = i % 2 === 0 ? 1 : 0.5;
        var offsetX = radius * fraction * Math.cos(angle);
        var offsetY = radius * fraction * Math.sin(angle);
        newCoordinates.push([center[0] + offsetX, center[1] + offsetY]);
 	}
	newCoordinates.push(newCoordinates[0].slice());
	geometry.setCoordinates([newCoordinates]);
	return geometry;
}

/*---------------------------------------------------------------------------*/
/*          通过绘制几何，查询选择要素工具  {bxmap.interaction.Select}       */
/*---------------------------------------------------------------------------*/
/**
 * @classdesc 从数据源中选择高亮要素，选择规则是与绘制的几何相交
 * @constructor
 * @extends {bxmap.interaction.Draw}
 * @param options {Object}
 * @param options.type {String} 绘制的几何类型'Point'|'LineString'|'Polygon'|'MultiPoint'|
 * 'MultiLineString'|'MultiPolygon'|'Circle'|'Square'|'Box'|'Star'
 * @param options.source {ol.source.Vector} 从数据源中查询选择要素
 * @param options.features {Array.<ol.Feature>} 从数组中查询选择要素，当source存在时该项无效
 * @param options.style {ol.style.Style|Array.<ol.style.Style>|ol.StyleFunction|undefined} 绘制几何样式
 * @fries "select"
 * @example <caption> 高亮与面相交的要素 </caption>
 * var polygonSelectInteraction = new bxmap.interaction.Select({
 * 	type: "Polygon",
 * 	source: currentLayer.getSource()
 * });
 * bmap.addMutexInteraction(polygonSelectInteraction);
 */
bxmap.interaction.Select = function(options){
	var opt_options = options || {};
    /**
	 * @description 从数据源中查询选择要素
     * @type {ol.source.Vector}
     * @private
     */
	this._source = options.source;
    /**
	 * @private
	 * @description 从数组中查询选择要素
     */
	this._features = this._source ? this._source.getFeatures() : (options.features || []);
    /**
	 * @description 用于存储绘制的几何
     * @type {ol.source.Vector}
     */
    this._drawnSource = null;

	var source = opt_options.source = this._drawnSource = new ol.source.Vector({useSpatialIndex:false});
	opt_options.style = options.style;
	bxmap.interaction.Draw.call(this, opt_options);
	
	var _this = this;
    /**
	 * @description 选中要素样式
     * @private
     */
	this._selectedStyle = bxmap.common.getDefaultSelectedStyle();
    /**
	 * @readonly
	 * @description 图层用于存储选中的要素
     * @type {ol.layer.Vector}
     */
	this.selectedFeatureLayer = new ol.layer.Vector({
		source: new ol.source.Vector({useSpatialIndex: false}),
		// 通过handle设置控制器样式
		style: function (feature){
			if (!feature.getGeometry()) {
				return null;
			}
			return _this._selectedStyle[feature.getGeometry().getType()];
		}
	});
	this.selectedFeatureLayer.setZIndex(bxmap.CONFIG_LEVEL_MAX + 9);
	
	//清空数据
	this._drawnSource.on("changefeature",function(){
		this._drawnSource.clear();
	});
	
	var srcFeatures;
	//绘制几何结束
	this.on("drawend",function(evt){
		var feature = evt.feature;
		if(feature == null) return;
		
		var geometry = feature.getGeometry();
		//从source中查询
		if(this._source){
			//带有空间索引查询较快
			srcFeatures = this._getSpatialIndexFeatures(this._source, geometry.getExtent());
		}
		srcFeatures = srcFeatures || this._features;
		//获取相交要素
		var features = this._getIntersectFeatures(srcFeatures, geometry);
		//清空数据
		this.clear();
		this.selectedFeatureLayer.getSource().addFeatures(features);
		//触发选择要素事件
		this.dispatchEvent({type:"select",target:this, features:features});
	},this);
}
ol.inherits(bxmap.interaction.Select, bxmap.interaction.Draw);

/**
 * @private
 * @description 获取范围内要素，要素随机排序，source useSpatialIndex必须为true，如果为fasle则返回null
 * @param source - {ol.source.Vector}
 * @param extent - [minx, miny, maxx, maxy].
 * @return {Array|null} 
 */
bxmap.interaction.Select.prototype._getSpatialIndexFeatures = function(source, extent){
	var features = null;
	try{
		features = source.getFeaturesInExtent(extent);
	}catch(ex){
		features = null;
	}
	return features;
}

/**
 * @private
 * @description 获取与几何相交的要素
 * @param - {ol.geom.SimpleGeometry}
 * @return - {Array<ol.Feature>}
 */
bxmap.interaction.Select.prototype._getIntersectFeatures = function(features, geometry){
	var intersectFeatures = [];
	var srcFeature;
	for(var i = 0; i < features.length; i++){
		srcFeature = features[i];
		if(srcFeature.getGeometry() == null) continue;
		//判断相交
		if(srcFeature.getGeometry().intersects(geometry)){
			intersectFeatures.push(srcFeature);
		}
	}
	return intersectFeatures;
}

/**
 * @description 清空选中的数据
 */
bxmap.interaction.Select.prototype.clear = function(){
	this.selectedFeatureLayer.getSource().clear();
	this._drawnSource.clear();
}

/**
 * @inheritDoc
 * @description 设置地图对象
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.Select.prototype.setMap = function(map) {
	var selectedLayer = this.selectedFeatureLayer;
	if (this.getMap()) {
		this.getMap().removeLayer(selectedLayer);
	}
	bxmap.interaction.Draw.prototype.setMap.call (this, map);
	selectedLayer.setMap(map);
};

/*-------------------------------------------------------*/
/*       文本标注工具  {bxmap.interaction.TextLabel}          */
/*-------------------------------------------------------*/
/**
 * @classdesc 文本标注工具，在地图上点击添加文本标注，文本标注随地图移动而移动
 * @constructor
 * @extends {ol.interaction.Pointer}
 * @param options
 * @param options.once {Boolean} 默认为true，执行一次，但该工具不会销毁，只是执行一次后，需要重新激活工具才可以再次标注
 */
bxmap.interaction.TextLabel = function(options){
	var opt_options = options || {};

    /**
	 * 是否执行一次，默认为true
     * @type {boolean}
	 * @private
     */
	this._once = opt_options.once == null ? true : opt_options.once;

    /**
	 * overlay 开始编辑标识
     * @type {String}
     * @private
     */
	this._EDIT_START_FLAG = "edit_start";//开始编辑标识
    /**
	 * overlay 结束编辑标识
     * @type {String}
     * @private
     */
	this._EDIT_END_FLAG = "edit_end";//结束编辑标识
	/**
	 * @description 文本overlay字典
     * @private
     */
	this._textOverlays = {};
    /**
	 * @description 标识是否悬停在文本标注上
     * @type {Boolean}
     * @private
     */
	this._isHoverOverlay = false;
    /**
	 * @description 文本标注帮助是否已隐藏
     * @type {boolean}
     * @private
     */
	this._isHideHelpTooltip = false;
    /**
	 * @description 操作帮助{ol.Overlay}
     * @private
     */
	this._helpTooltip = null;
	
	ol.interaction.Pointer.call(this, {	
		handleDownEvent: this._handleDownEvent,
		handleMoveEvent: this._handleMoveEvent,
		handleDragEvent: this._handleDragEvent,
		handleUpEvent: this._handleUpEvent
	});
}
ol.inherits(bxmap.interaction.TextLabel, ol.interaction.Pointer);

/**
 * @inheritDoc
 * @description 设置工具激活状态
 * @param {Boolean} active true-激活；false-失效
 */
bxmap.interaction.TextLabel.prototype.setActive = function(active){
	ol.interaction.Pointer.prototype.setActive.call(this, active);
	
	var map = this.getMap();
	if(map){
		var cursor = active ? "text" : "default";
		this.setCursor(cursor);
	
		//显示/隐藏提示信息
		if(this._helpTooltip){
			var element = this._helpTooltip.getElement();
			if(active){
				element.classList.remove('hidden');
				//显示确定/编辑/删除按钮
                this._handleActiveChangedEvent(true);
			}else{
				//隐藏提示信息
				if(!element.classList.contains('hidden')){
					element.classList.add('hidden')
				}
			}
		}
	}
	//控制激活状态改变事件，隐藏确定/编辑/删除按钮
	//this._handleActiveChangedEvent(active);
}

/**
 * @inheritDoc
 * @description 设置地图对象
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.TextLabel.prototype.setMap = function (map) {
	if (this.getMap()) {
		this.getMap().removeOverlay(this._helpTooltip);
	}
    ol.interaction.Pointer.prototype.setMap.call(this, map);
    if(map){
    	var cursor = this.getActive() ? "text" : undefined;
    	this.setCursor(cursor);
    	
	    if(this._helpTooltip == null){
	    	this._createHelpTooltip();
	    }
	    map.addOverlay(this._helpTooltip);
	}
}

/**
 * @description 清除所有text标注
 */
bxmap.interaction.TextLabel.prototype.clear = function(){
	var map = this.getMap();
	if(map){
		var text;
		for(var key in this._textOverlays){
			text = this._textOverlays[key];
			map.removeOverlay(text);
			delete this._textOverlays[key];
		}
	}
}

/**
 * @private
 * @description 鼠标按下事件
 * @param evt
 * @returns {Boolean}
 */
bxmap.interaction.TextLabel.prototype._handleDownEvent = function(evt){
	//是否正在拖动地图
	this._isdragging = false;
	return true;
};
/**
 * @private
 * @description 鼠标移动时样式
 * @param evt
 */
bxmap.interaction.TextLabel.prototype._handleMoveEvent = function(evt){
	//设置鼠标样式-拖动地图/文本标注
	if(evt.map){
		var cursor = evt.dragging ? "default" : "text";
        this.setCursor(cursor);
	}
	//当鼠标悬停在文本标注或正在拖动地图时，隐藏提示信息
	if (evt.map == null || this._isHoverOverlay || evt.dragging) {
		this._hideHelpTooltip();
	}
    //显示文本标注提示信息
    else if(this._helpTooltip){
        this._showHelpTooltip(evt.coordinate);
    }

	//正在拖动地图
	if(evt.dragging){
		this._isdragging = true;
	}
};
/**
 * @private
 * @description 鼠标拖动平移地图
 */
bxmap.interaction.TextLabel.prototype._handleDragEvent = function(evt){
	var centroid = evt.pixel;
	if (this._lastCentroid) {
		var deltaX = this._lastCentroid[0] - centroid[0];
	    var deltaY = centroid[1] - this._lastCentroid[1];
	    var view = evt.map.getView();
	    var resolution = view.getResolution();
	    var center = [deltaX * resolution, deltaY * resolution];
	    ol.coordinate.rotate(center, view.getRotation());
	    ol.coordinate.add(center, view.getCenter());
	    center = view.constrainCenter(center);
	    view.setCenter(center);
	}
	this._lastCentroid = centroid;

	//正在拖动地图时
	// 1.设置正在拖动地图标识
    this._isdragging = true;
	// 2.隐藏提示信息
    this._hideHelpTooltip();
}
/**
 * @private
 * @description 鼠标弹起事件
 * @param evt
 * @returns {Boolean}
 */
bxmap.interaction.TextLabel.prototype._handleUpEvent = function(evt){
	var map = evt.map;
	//结束拖动地图
	if (evt.map == null || this._isdragging) {
		this._isdragging = false;
		this._lastCentroid = null;
		map.render();
		return false;
	}
	//鼠标弹起文本标绘
	var html = '<div style="font-size: 12px;left: 1521px;top: 149px;">';
	html += '<div style="overflow:hidden;font-size:12px;line-height:16px;border: 1px solid #999999;border-radius:4px; " class="bjContent">';
	html += '<textarea class="bj_cont" style="border:0;width:125px;height:80px;font-size:12px;overflow-y:auto;"></textarea>';
	html += '<div class="rem_cont" style="display: none; overflow: visible; background:#817fd1; color:#fff; border-radius:4px; padding:5px; font-family:"微软雅黑";">hghgghg<br/>fhfggh</div>';
	html += '<div id="text_qsrwz" style="position:absolute;bottom: -19px;left: 0px;width: 129px;height: 20px;background:none repeat scroll 0 0 #FFFECD;border:1px solid #DFB35C;color:#CC0003;line-height:18px;padding: 0px 2px;display:none;">请输入文字</div>';
	html += '</div>';
	html += '<div style="position:absolute;width:45px;height:22px;padding-left:2px;overflow:hidden;bottom:-1px;right:-45px;cursor:default">';
	html += '<span class="bj_remark">';
	html += '	 <i class="glyphicon glyphicon-ok btn_vg" name="ok" title="确定"></i>';
	html += '    <i class="glyphicon glyphicon-pencil btn_bj" name="edit" title="编辑"></i>';
	html += '    <i class="glyphicon glyphicon-remove btn_rem" name="del" title="删除"></i>';
	html += '</span>';
	html += '</div>';
	html += '</div>';
	
	var element = document.createElement('div');
	element.innerHTML = html;
	var overlay = new ol.Overlay({
		element : element,
		offset : [ 0, 0 ],
		positioning : 'top-left'
	});
	element.id = bxmap.common.getRandomStringUid(overlay);
	this._textOverlays[element.id] = overlay;
	//绑定overlay DOM事件(确定/编辑/删除)
	this._bindingDOMEvent(overlay);
	map.addOverlay(overlay);
	//设置位置
	overlay.setPosition(evt.coordinate);

	//执行一次
	if(this._once){
		this.setActive(false);
	}
	
	return false;
};

/**
 * @private
 * @description 隐藏"点击想要标记的位置"文本提示信息
 */
bxmap.interaction.TextLabel.prototype._hideHelpTooltip = function () {
    if(!this._isHideHelpTooltip){
        var element = this._helpTooltip.getElement();
        element.classList.add('hidden');
        this._isHideHelpTooltip = true;
    }
}

/**
 * @description 显示"点击想要标记的位置"文本提示信息
 * @private
 */
bxmap.interaction.TextLabel.prototype._showHelpTooltip = function (coordinate) {
    if(this._helpTooltip){
        this._helpTooltip.setPosition(coordinate);
        var element = this._helpTooltip.getElement();
        element.classList.remove('hidden');
        this._isHideHelpTooltip = false;
    }
}

/**
 * 设置标注是否可修改
 * @param fixed {Boolean} fixed为true时不可更改，表现为标注不再显示确定/编辑/删除按钮，false时可更改
 */
bxmap.interaction.TextLabel.prototype.setFixed = function (fixed) {
    this._handleActiveChangedEvent(!fixed);
}

/**
 * @description
 * active为true时，显示确定/编辑/删除标注按钮；
 * active为false时，隐藏确定/编辑/删除标注按钮
 */
bxmap.interaction.TextLabel.prototype._handleActiveChangedEvent = function(active){
	//var active = this.getActive();
	var text, element;//text overlay
	var $ok,$edit,$del;//确定/编辑/删除按钮	
	for(var key in this._textOverlays){
		text = this._textOverlays[key];
		element = text.getElement();
		$ok = $(element).find("span [name='ok']");//确定按钮
		$edit = $(element).find("span [name='edit']");//编辑按钮
		$del = $(element).find("span [name='del']");//删除按钮
		if(active){
			text._edit_state === this._EDIT_END_FLAG ?  $edit.show() : $ok.show();
			$del.show();
		}else{
			$ok.hide();
			$edit.hide();
			$del.hide();
		}
	}
};

/**
 * @private
 * @description 创建操作帮助提示信息Overlay
 * @returns {ol.Overlay}
 */
bxmap.interaction.TextLabel.prototype._createHelpTooltip = function(){
	var map = this.getMap();
	var helpTooltipElement = document.createElement('div');
	helpTooltipElement.innerHTML = "点击想要标记的位置";
	helpTooltipElement.className = 'tooltip hidden';
	
	this._helpTooltip = new ol.Overlay({
		element : helpTooltipElement,
		offset : [ 15, 0 ],
		positioning : 'center-left'
	});
	map.addOverlay(this._helpTooltip);
	
	return this._helpTooltip;
}

/**
 * @private
 * @description 绑定标注的element事件，确定/编辑/删除事件
 * @param {ol.Overlay} overlay
 */
bxmap.interaction.TextLabel.prototype._bindingDOMEvent = function(overlay){
	var element = overlay.getElement();
	if(!element) return;
	
	var _this = this;
	var $ok = $(element).find("span [name='ok']");//确定按钮
	var $edit = $(element).find("span [name='edit']");//编辑按钮
	var $vtext = $(element).find(".rem_cont");//text标注视图框
	var $etext = $(element).find(".bj_cont");//text标注编辑框
	var $error = $(element).find("#text_qsrwz");//文本输入信息提示框
	function checkText(){
		var valid = true;
		var text = $etext.val();
		var tip = text === "" ? "请输入文字":(text.length>50 ? "您最多可输入50个字": null);
		if(tip){
			$error.text(tip);
			$error.show();
			valid = false;
		}else{
			$error.hide();
		}
		return valid;
	}
	//文本编辑框点击隐藏错误提示信息
	$etext.on("click",function(){
		$error.css("display", "none");
	});
	//点击确定按钮
	$(element).on("click","span [name='ok']",function(evt){
		if(!checkText()) return;
		//设置标注文本
		var label = $etext.val().replace(/\n/g,"<br>").replace(/\r\n/g,"<br>").replace(/\s/g,"&nbsp;");
		$vtext.html(label);
		//设置隐藏/显示
		$ok.hide();
		$edit.show();
		$vtext.show();
		$etext.hide();
		overlay._edit_state = _this._EDIT_END_FLAG;
	});
	//点击编辑按钮
	$(element).on("click","span [name='edit']",function(evt){
		$ok.show();
		$edit.hide();
		$vtext.hide();
		$etext.show();
		overlay._edit_state = _this._EDIT_START_FLAG;
	});
	//点击删除按钮
	$(element).on("click","span [name='del']",function(evt){
		var overlay = _this._textOverlays[element.id];
		_this.getMap().removeOverlay(overlay);
		delete _this._textOverlays[element.id];
		//鼠标不在overlay上
		_this._isHoverOverlay = false;
	});
	//鼠标指针位于元素上方时
	$(element).on("mouseover",function(evt){
		element.parentNode.style.zIndex = 1000;
		//鼠标位于overlay上
		_this._isHoverOverlay = true;
	});
	//鼠标从元素上移开时
	$(element).on("mouseout",function(evt){
		element.parentNode.style.zIndex = "";
		_this._isHoverOverlay = false;
	});
}

/*-----------------------------------------------------------------------------------*/
/*                     修改要素工具  {bxmap.interaction.Modify}                           */
/*-----------------------------------------------------------------------------------*/
/**
 * @classdesc 修改要素工具，点击选中要素，可增加、移动、删除节点。边线上点击增加节点，
 * 拖拽移动节点，按住shift键单击节点可删除。
 * @constructor
 * @extends {ol.interaction.Select}
 * @param options {Object}
 * @param options.layers {undefined | Array.<ol.layer.Layer>} 图层列表，如果为空则为所有的可见图层
 * @param options.style {ol.style.Style|Array.<ol.style.Style>|ol.StyleFunction|undefined} 选中要素样式
 * @fires "modifystart"|"modifyend"|"select"
 */
bxmap.interaction.Modify = function(options){
	var opt_options = options || {};
	var features = opt_options.features = options.features || new ol.Collection();
	
	var modifyInteraction = this._modifyInteraction = new ol.interaction.Modify({
		features : features,
		deleteCondition : function(event) {
			return ol.events.condition.shiftKeyOnly(event) && ol.events.condition.singleClick(event);
		}
	});
	
	ol.interaction.Select.call(this, opt_options);
	
	modifyInteraction.on("modifystart", function(evt){ this.dispatchEvent(evt); },this);
	modifyInteraction.on("modifyend", function(evt){ this.dispatchEvent(evt); },this);
}
ol.inherits(bxmap.interaction.Modify, ol.interaction.Select);

/**
 * @inheritDoc
 * @description 设置地图对象
 * @param {ol.CanvasMap} map
 */
bxmap.interaction.Modify.prototype.setMap = function (map) {
	if(this.getMap()){
		this.getMap().removeInteraction(this._modifyInteraction);
	}
	if(map){
		map.addInteraction(this._modifyInteraction);
	}
	ol.interaction.Select.prototype.setMap.call(this, map);
}

/**
 * @inheritDoc
 * @description 设置工具激活状态
 * @param {Boolean} active true-激活；false-失效
 */
bxmap.interaction.Modify.prototype.setActive = function(active){
	ol.interaction.Select.prototype.setActive.call(this, active);
	this._modifyInteraction.setActive(active);
}

/**
 * @description 清空数据
 */
bxmap.interaction.Modify.prototype.clear = function(){
	this.getFeatures().clear();
}