var bxmap = bxmap || {};

/**
 * WFS图层编辑器
 * @param options
 * 		bmap - {bxmap.Map} 地图
 * 		wfsLayerItems - [{"id":"1","name":"贮罐区","visible":true,data:{bxmap.layer.WFSLayer}]
 */
bxmap.WFSEditor=function(options){
	var opt_options = options || {};
	/***/
	this._bmap = opt_options.bmap;
	/**wfs图层列表*/
	this._wfsLayerItems = opt_options.wfsLayerItems || [];
	/**当前选择的wfs图层索引*/
	this._currentSelectedIndex = 0;
	/**上次选择的wfs图层索引*/
	this._lastSelectedIndex = 0;
	
	/**wfs图层编辑器工具条*/
	this.wfsLayerEditorToolbar = new ToolBar({className:"wfsLayerEditToolBar"});
	
	//工具条工具按钮界面
	/**图层下拉列表工具带有可见控制*/
	this.wfsLayerListTool = null;
	/**开始编辑按钮*/
	this.wfsLayerStartEditTool = null;
	/**保存按钮按钮*/
	this.wfsLayerSaveTool = null;
	/**新增要素按钮*/
	this.wfsLayerNewFeaturetool = null;
	/**编辑要素按钮*/
	this.wfsLayerModifyFeatureTool = null;
	/**要素变换按钮*/
	this.wfsLayerTrasformTool = null;
	/**删除选中要素按钮*/
	this.wfsLayerRemoveTool = null;
	/**选择要素下拉工具*/
	this.wfsLayerSelectFeatureTool = null;
	/**清空选中要素按钮*/
	this.wfsLayerClearSeletedTool = null;
	
	//地图交互工具
	//由于切换图层时，绘制工具无法设置图层，必须重新初始化，绘制交互工具的管理需要移除后重新new新对象
	/**新增要素交互工具*/
	this._newFeatureInteraction = null;
	/**编辑要素交互工具*/
	this._modifyFeatureInteraction = null;
	/**变换要素交互工具*/
	this._transformFeatureInteraction = null;
	/**选择要素交互工具*/
	this._polygonSelectInteraction = null;//多边形选择
	this._boxSelectInteraction = null;//矩形选择
	this._circleSelectInteraction = null;//圆形选择
	
	/**选择要素下拉工具绑定items*/
	this.wfsLayerSelectFeatureToolItems = [
		{className:"wfsselectfeatureEnvelope",title:"矩形选择",data:this._boxSelectInteraction},
		{className:"wfsselectfeaturePolygon",title:"多边形选择",data:this._polygonSelectInteraction},
		{className:"wfsselectfeatureCircle",title:"圆形选择",data:this._circleSelectInteraction}
	];
	//创建选择要素工具
	this.rebuildSelectFeatureInteraction();
	
	/**新增要素工具样式，根据图层几何类型，工具样式可能为点/线/面样式*/
	var nftStyles = this._newFeatureToolStyles = {};
	nftStyles["point"] = {className:"wfsnewpointtool", title:"新增点"};
	nftStyles["multipoint"] = nftStyles["point"];
	nftStyles["linestring"] = {className:"wfsnewpolylinetool", title:"新增线"};
	nftStyles["multilinestring"] = nftStyles["linestring"];
	nftStyles["polygon"] = {className:"wfsnewpolygontool", title:"新增面"};
	nftStyles["multipolygon"] = nftStyles["polygon"];
	
	//初始化工具
	this._initializeTools(opt_options);
	//初始化交互工具
	this._initializeInteractions();
	//绑定事件
	this._bindingEvent();
}

/**获取工具条*/
bxmap.WFSEditor.prototype.getToolbar = function(){
	return this.wfsLayerEditorToolbar;
}

/**
 * 初始化工具
 * @param options
 */
bxmap.WFSEditor.prototype._initializeTools = function(){
	var toolbar = this.wfsLayerEditorToolbar;
	
	var currentLayer = null;
	//wfs图层下拉选项工具
	if(this._wfsLayerItems){
		var wfsLayerItems = this._wfsLayerItems;
		//初始化当前编辑WFS图层
		currentLayer = this._getCurrentLayer();
		
		var wfsLayerListTool = this.wfsLayerListTool = new WFSLayerListTool({
			className: "wfslayerlisttool",
			comboxClassName: "wfslayercombox",
			items : wfsLayerItems
		});
		//禁用下拉列表
		//wfsLayerListTool.setItemComboxEnabled(false);
		//设置可见状态
		if(currentLayer){
			wfsLayerListTool.setItemVisible(this._currentSelectedIndex, currentLayer.getVisible());
		}
		
		toolbar.addTool(wfsLayerListTool);
	}

	//开始编辑按钮
	var wfsLayerStartEditTool = this.wfsLayerStartEditTool = new WFSLayerButtonTool({
		className:"wfsstartedittool",
		title:"开始编辑",
		enabled: true,
		clickToggleActive : false
	});
	toolbar.addTool(wfsLayerStartEditTool);
	
	//保存按钮工具
	var wfsLayerSaveTool = this.wfsLayerSaveTool = new WFSLayerButtonTool({
		className:"wfssavetool",
		title:"保存",
		clickToggleActive : false
	});
	toolbar.addTool(wfsLayerSaveTool);
	
	//新增要素工具css样式
	var nftStyles = this._newFeatureToolStyles;
	var style = this._getNewFeatureToolStyle(currentLayer);
	//新增要素工具
	var wfsLayerNewFeaturetool = this.wfsLayerNewFeaturetool = new WFSLayerButtonTool(style);
	toolbar.addTool(wfsLayerNewFeaturetool);
	
	//编辑要素
	var wfsLayerModifyFeatureTool = this.wfsLayerModifyFeatureTool = new WFSLayerButtonTool({
		className:"wfsnodeedittool",
		title:"编辑要素"
	});
	toolbar.addTool(wfsLayerModifyFeatureTool);
	
	//要素变换
	var wfsLayerTrasformTool = this.wfsLayerTrasformTool = new WFSLayerButtonTool({
		className:"wfstrasformtool",
		title:"变换"
	});
	toolbar.addTool(wfsLayerTrasformTool);
	
	//删除选中要素
	var wfsLayerRemoveTool = this.wfsLayerRemoveTool = new WFSLayerButtonTool({
		className:"wfsremovetool",
		title:"删除"
	});
	toolbar.addTool(wfsLayerRemoveTool);
	
	//选择要素
	var wfsLayerSelectFeatureTool = this.wfsLayerSelectFeatureTool = new WFSLayerComboboxTool({
		items: this.wfsLayerSelectFeatureToolItems
	});
	toolbar.addTool(wfsLayerSelectFeatureTool);
	
	//清空选中要素
	var wfsLayerClearSeletedTool = this.wfsLayerClearSeletedTool = new WFSLayerButtonTool({
		className:"wfsclearseletedtool",
		title:"清空选中",
		enabled: true,
		clickToggleActive : false
	});
	toolbar.addTool(wfsLayerClearSeletedTool);
	
	//绘制toolbar
	toolbar.redraw();
}

/**
 * 初始化交互工具
 * @param bmap - {bxmap.Map}
 */
bxmap.WFSEditor.prototype._initializeInteractions = function(){
	this.rebuildNewFeatureInteraction();//绘制要素交互工具
	this.rebuildModifyFeatureInteraction();//修改要素交互工具
	this.rebuildTransformFeatureInteraction();//要素变换交互工具
}

/**
 * 重建绘制要素交互工具，由于绘制几何类型不能动态改变
 */
bxmap.WFSEditor.prototype.rebuildNewFeatureInteraction = function(){
	var bmap = this._bmap;
	if(bmap == null) return;
	var currentLayer = this._getCurrentLayer();
	if(currentLayer == null) return;
	//移除上一次交互工具
	bmap.removeInteraction(this._newFeatureInteraction);//新增要素交互工具
	//重新创建
	var interaction = this._newFeatureInteraction = new bxmap.interaction.Draw({
		source: new ol.source.Vector(),
		type: currentLayer.getGeometryType(),
		geometryName: currentLayer.getGeometryName()
	});
	interaction.on('drawend', function (event) {
		var feature = event.feature;
		var geometry = feature.getGeometry();
		var iserror = false;
		//判断多边形是否自相交
		if(geometry instanceof ol.geom.Polygon || geometry instanceof ol.geom.MultiPolygon){
			if(geometry.isSelfIntersection()){
				jDialog.message('错误多边形',{
					autoClose : 1000,    // 1s后自动关闭
					padding : '30px',    // 设置内部padding
					modal: false         // 非模态，即不显示遮罩层
		        });
				iserror = true;
			}
		}
		if(!iserror){
			currentLayer.addFeatures([feature]);
		}
    });
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	//绑定图层事件
	this._bindingInteractionLayerFeatureChangedEvent(currentLayer);
}

/**
 * 重建选择要素交互工具，由于选择的数据源source不能动态改变
 */
bxmap.WFSEditor.prototype.rebuildSelectFeatureInteraction = function(){
	var bmap = this._bmap;
	if(bmap == null) return;
	var currentLayer = this._getCurrentLayer();
	if(currentLayer == null) return;
	
	//移除上一次交互工具
	bmap.removeInteraction(this._polygonSelectInteraction);
	//面选工具
	var interaction = this._polygonSelectInteraction = new bxmap.interaction.Select({
		type: "Polygon",
		source: currentLayer.getSource()
	});
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	
	//移除上一次交互工具
	bmap.removeInteraction(this._boxSelectInteraction);
	//矩形选择工具
	interaction = this._boxSelectInteraction = new bxmap.interaction.Select({
		type: "Box",
		source: currentLayer.getSource()
	});
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	
	//移除上一次交互工具
	bmap.removeInteraction(this._circleSelectInteraction);
	//圆选工具
	interaction = this._circleSelectInteraction = new bxmap.interaction.Select({
		type: "Circle",
		source: currentLayer.getSource()
	});
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	
	//为选择要素工具选项重新赋值
	var sfItems = this.wfsLayerSelectFeatureToolItems;
	sfItems[0].data = this._boxSelectInteraction;//矩形选择
	sfItems[1].data = this._polygonSelectInteraction;//多边形选择
	sfItems[2].data = this._circleSelectInteraction;//圆形选择
	
	//监控选择要素工具选择要素改变事件
	this._listenSelectFeatureChangeEvent();
}

/**
 * 重建修改要素交互工具，由于修改的数据源source不能动态改变
 */
bxmap.WFSEditor.prototype.rebuildModifyFeatureInteraction = function(){
	var bmap = this._bmap;
	if(bmap == null) return;
	var currentLayer = this._getCurrentLayer();
	if(currentLayer == null) return;

	//移除上一次交互工具
	bmap.removeInteraction(this._modifyFeatureInteraction);
	var interaction = this._modifyFeatureInteraction = new bxmap.interaction.Modify({
		layers:[currentLayer]
	});
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	interaction.on('modifyend', function (evt) {
		if(evt.features == null || evt.features.getLength() == 0) return;
		currentLayer.updateFeatures(evt.features.getArray());
    });
	//绑定图层事件
	this._bindingInteractionLayerFeatureChangedEvent(currentLayer);
}

/**
 * 重建要素变换交互工具，由于变换的图层数据layers不能动态改变
 */
bxmap.WFSEditor.prototype.rebuildTransformFeatureInteraction = function(){
	var bmap = this._bmap;
	if(bmap == null) return;
	var currentLayer = this._getCurrentLayer();
	if(currentLayer == null) return;

	//移除上一次交互工具
	bmap.removeInteraction(this._transformFeatureInteraction);
	var interaction = this._transformFeatureInteraction = new bxmap.interaction.TransformClickable({
		layers:[currentLayer]
	});
	bmap.addMutexInteraction(interaction);
	interaction.setActive(false);
	interaction.on(['translateend'], function (evt) {
		var features = evt.features;
		currentLayer.updateFeatures(features);
	});
	interaction.on(['scaleend','rotateend'], function (evt) {
		var feature = evt.feature;
		currentLayer.updateFeatures([feature]);
	});
	//绑定图层事件
	this._bindingInteractionLayerFeatureChangedEvent(currentLayer);
}

/**
 * 绑定交互工具对图层要素增删改要素事件
 */
bxmap.WFSEditor.prototype._bindingInteractionLayerFeatureChangedEvent = function(layer){
	if(layer == null) return;
	//注册事件
	layer.un("featurechanged", this._handleFeatureChanged, this);
	layer.on("featurechanged", this._handleFeatureChanged, this);
}
bxmap.WFSEditor.prototype._handleFeatureChanged = function(evt){
	var layer = evt.target;
	var active = layer.hasEdits() && layer.isEditing();
	this.wfsLayerSaveTool.setEnabled(active);
}

/**
 * 弹出保存对话框
 * @param item - [{bxmap.layer.WFSLayer | undefined}] 如果为null，则为当前编辑图层
 * @param callback - function(dialog, handle) handle:0不保存，1保存，2取消
 */
bxmap.WFSEditor.prototype.showSaveDialog = function(wfslayer, callback){
	var layer = wfslayer;
	if(layer == null){
		var item = this._wfsLayerItems[this._currentSelectedIndex];
		layer = item.data;
	}
	
	var layerName = layer.get("name") || "未命名图层";
	var tip = "是否保存对 " + layerName  + "  的更改?";
	//判断图层是否需要保存
	var hasEdits = layer.hasEdits();
	var handle = 2;
	var dialog = jDialog.dialog({
		content : tip,
	    wobbleEnable : true,
		buttons:
		[
			 {
				type : 'highlight',
				text : '是',
				handler : function(btn, dlg) {
					if(callback){
						callback(dlg, 1);
					}
				}
			 }, 
			 {
				text : '否',
				handler : function(btn, dlg) {
					if(callback){
						callback(dlg, 0);
					}
				}
			 }, 
			 {
				text : '取消',
				handler : function(btn, dlg) {
					if(callback){
						callback(dlg, 2);
					}
				}
			 }
		 ]
	});
}

/**
 * 获取当前编辑的WFS图层
 */
bxmap.WFSEditor.prototype._getCurrentLayer = function(){
	if(this._wfsLayerItems == null || this._wfsLayerItems.length == 0) return null;
	
	var item = this._wfsLayerItems[this._currentSelectedIndex];
	return item.data;
}

/**
 * 根据wfs图层几何类型获取新建要素工具对应的css样式，包括className,title，可能为新建点/线/面
 * @param layer - {bxmap.layer.WFSLayer} WFS图层
 */
bxmap.WFSEditor.prototype._getNewFeatureToolStyle = function(layer){
	if(layer == null) return null;
	
	var geometryType = layer.getGeometryType();
	geometryType = geometryType.toLowerCase();
	return this._newFeatureToolStyles[geometryType];
}

/**
 * 绑定工具事件
 */
bxmap.WFSEditor.prototype._bindingEvent = function(){
	//绑定下拉图层列表事件
	this._bindingLayerListToolEvent();
	//绑定保存按钮事件
	this._bindingSaveToolEvent();
	//绑定开始编辑按钮事件
	this._bindingStartEditToolEvent();
//	//绑定新增要素按钮事件
//	this._bindingButtonInteractionEvent(this.wfsLayerNewFeaturetool, this._newFeatureInteraction);
//	//绑定修改要素按钮事件
//	this._bindingButtonInteractionEvent(this.wfsLayerModifyFeatureTool, this._modifyFeatureInteraction);
//	//绑定要素变换按钮事件
//	this._bindingButtonInteractionEvent(this.wfsLayerTrasformTool, this._transformFeatureInteraction);
	//绑定框选要素事件
	this._bindingSelectFeatureToolEvent();
	//绑定删除选中要素按钮事件
	this._bindingRemoveToolEvent();
	//绑定清空选中要素按钮事件
	this._bindingClearSeletedToolEvent();
	//绑定互斥工具激活事件，新增要素tool、编辑要素tool、要素变换tool、选择要素tool互为互斥
	this._bindingMutexToolActiveChangedEvent();
	//绑定工具条toolbar显示状态事件
	this._bindingToolbarEvent();
}

/**
 * 绑定图层下拉列表事件
 */
bxmap.WFSEditor.prototype._bindingLayerListToolEvent = function(){
	if(this.wfsLayerListTool == null) return;
	var tool = this.wfsLayerListTool;
	var _this = this;
	
	//下拉列表改变
	tool.onItemChanged = function(index, item){
		_this._currentSelectedIndex = index;
		
		//保存上一次编辑的图层
		var lastWFSLayer = _this._wfsLayerItems[_this._lastSelectedIndex].data;
		//上一个图层无编辑
		if(!lastWFSLayer.hasEdits()){
			//正在编辑则停止编辑
			if(lastWFSLayer.isEditing()){
				lastWFSLayer.stopEditing({ saveEdits:false });
			}
			//重置选择项，更改按钮状态等
			_this._handleLayerListToolItemChanged(item, _this);
			return;
		}else{
			_this.showSaveDialog(lastWFSLayer,function(dlg, handle){
				if(handle == 1) {
					lastWFSLayer.stopEditing({saveEdits:true,showDialog:true});
				}
				else if(handle == 0) {
					//不保存
					lastWFSLayer.stopEditing({ saveEdits:false });
				}
				
				dlg.close();
				//重置选择项，更改按钮状态等
				_this._handleLayerListToolItemChanged(item, _this);
			});
		}
	}
	//图层可见状态改变
	tool.onItemVisibilityChanged = function(index, item, visible){
		if(item == null || item.data == null) return;
		var layer = item.data;
		layer.setVisible(visible);
	}
}
/**
 * 处理图层下拉列表改变事件，重置图层选择项，更改按钮状态等
 */
bxmap.WFSEditor.prototype._handleLayerListToolItemChanged = function(selectedItem, opt_this){
	var _this = opt_this || this;
	//根据图层状态修改按钮状态
	if(selectedItem == null || selectedItem.data == null) return;
	var layer = selectedItem.data;
	_this._setToolStateByWFSLayer(layer);
	
	//重建新增要素交互工具
	_this.rebuildNewFeatureInteraction();
	//重建修改要素交互工具
	_this.rebuildModifyFeatureInteraction();
	//重建选择要素交互工具
	_this.rebuildSelectFeatureInteraction();
	//重建变化要素交互工具
	_this.rebuildTransformFeatureInteraction();
	
	_this._lastSelectedIndex = _this._currentSelectedIndex;
}

/**
 * 根据WFS图层状态设置工具按钮状态
 * @param layer - {bxmap.layer.WFSLayer}
 */
bxmap.WFSEditor.prototype._setToolStateByWFSLayer = function(layer){
	//是否正在编辑
	var isEditing = layer.isEditing();
	//图层下拉列表项可见状态
	this.wfsLayerListTool.setEyeVisibleState(layer.getVisible());
	//开始编辑按钮状态，避免重复触发active事件
	this.wfsLayerStartEditTool.setActiveState(isEditing);
	//新增要素按钮
	this.wfsLayerNewFeaturetool.setEnabled(isEditing);
	this.wfsLayerNewFeaturetool.setActive(false);
	//编辑要素按钮
	this.wfsLayerModifyFeatureTool.setEnabled(isEditing);
	this.wfsLayerModifyFeatureTool.setActive(false);
	//要素变换按钮
	this.wfsLayerTrasformTool.setEnabled(isEditing);
	this.wfsLayerTrasformTool.setActive(false);
	//选择要素按钮
	this.wfsLayerSelectFeatureTool.setEnabled(false);
	this.wfsLayerSelectFeatureTool.setActive(false);
	//保存按钮
	var hasEdits = layer.hasEdits();
	this.wfsLayerSaveTool.setEnabled(isEditing && hasEdits);
}

/**
 * 绑定开始编辑按钮
 */
bxmap.WFSEditor.prototype._bindingStartEditToolEvent = function(){
	if(this.wfsLayerStartEditTool == null) return;
	var tool = this.wfsLayerStartEditTool;
	var _this = this;

	//重置工具激活状态
	function resetStatus(active){
		//设置title
		var title = active ? "结束编辑" : "开始编辑";
		tool.title(title);
		
		//修改按钮状态
		_this.wfsLayerNewFeaturetool.setEnabled(active);//新增要素按钮状态
		_this.wfsLayerNewFeaturetool.setActive(false);
		_this.wfsLayerModifyFeatureTool.setEnabled(active);//编辑要素按钮状态
		_this.wfsLayerModifyFeatureTool.setActive(false);
		_this.wfsLayerTrasformTool.setEnabled(active);//变换按钮状态
		_this.wfsLayerTrasformTool.setActive(false);
	}
	
	//开始编辑按钮点击事件，开始编辑按钮状态有当前图层是否正在编辑状态控制
	tool.addEvent({
		events: "click",
		handler:function(){
			//获取当前正在编辑的图层
			var currentLayer = _this._getCurrentLayer();
			if(currentLayer == null) return;
			//由于禁用了自动切换激活状态clickToggleActive
			var isEditing = currentLayer.isEditing();
			var toolActive = tool.getActive();
			
			if(isEditing && toolActive){
				//正在编辑且按钮处于激活状态，点击按钮结束编辑
				tool.setActive(false);
			}else if(!isEditing && !toolActive){
				//未开始编辑且按钮处于未激活状态，点击按钮开始编辑
				tool.setActive(true);
			}else{
				//按钮与激活状态不匹配
				tool.setActiveState(isEditing);
			}
		}
	});
	
	//绑定开始/结束编辑按钮激活状态改变事件
	tool.addEventListener("activeChanged", function(active){		
		//获取当前正在编辑的图层
		var currentLayer = _this._getCurrentLayer();
		if(active){
			//开始编辑
			currentLayer.startEditing();
			//重置工具激活状态
			resetStatus(true);
		}else if(!currentLayer.hasEdits()){
			//结束编辑，currentLayer无修改要素
			currentLayer.stopEditing({ saveEdits:false });
			//重置工具激活状态
			resetStatus(false);
		}else{
			//结束编辑有修改要素，弹出对话框是否保存
			_this.showSaveDialog(currentLayer,function(dlg, handle){
				if(handle == 1){
					//保存
					currentLayer.stopEditing({saveEdits:true,showDialog:true});
					
					//重置工具激活状态
					resetStatus(false);
				}else if(handle == 0){
					//不保存
					currentLayer.stopEditing({ saveEdits:false });
					//重置工具激活状态
					resetStatus(false);
				}
				
				dlg.close();
			});
		}
	});
}

/**
 * 绑定保存按钮事件
 */
bxmap.WFSEditor.prototype._bindingSaveToolEvent = function(){
	if(this.wfsLayerSaveTool == null) return;
	
	//获取当前正在编辑的图层
	var currentLayer = this._getCurrentLayer();
	
	var tool = this.wfsLayerSaveTool;
	var _this = this;
	//保存按钮点击事件
	tool.addEvent({
		events: "click",
		handler:function(){
			if(currentLayer.hasEdits()){
				//保存
				currentLayer.save({showDialog:true});
			}
		}
	});
}

///**
// * 绑定新增要素/修改要素/要素变换按钮事件
// */
//bxmap.WFSEditor.prototype._bindingButtonInteractionEvent = function(tool, interaction){
//	if(tool == null) return;
//	
//	//获取当前正在编辑的图层
//	var currentLayer = this._getCurrentLayer();
//	
//	var _this = this;
//	//绑定tool是否可用 enabled，激活工具事件绑定见_bindingMutexToolActiveChangedEvent方法
//	tool.addEventListener("enabledChanged", function(enabled){
//		if(!enabled){
//			interaction.clear();
//			interaction.setActive(false);
//		}
//	});
//}

/**
 * 绑定选择要素事件
 */
bxmap.WFSEditor.prototype._bindingSelectFeatureToolEvent = function(){
	if(this.wfsLayerSelectFeatureTool == null) return;
	
	var lastSFtoolIndex = 0;
	var tool = this.wfsLayerSelectFeatureTool;
	var _this = this;
	
	//清除上一次选择工具数据
	function clearLastInteraction(){
		var item = _this.wfsLayerSelectFeatureToolItems[lastSFtoolIndex];
		var interaction = item.data;
		if(interaction){
			interaction.clear();
		}
	}
	//设置当前选择工具
	function setCurrentInteraction(active){
		var item = tool.getCurrentItem();
		var interaction = active ? item.data : null;
		_this._bmap.setCurrentMutexInteraction(interaction);
	}

	//下拉列表选择事件
	tool.onItemChanged = function(index, item){
		tool.setActive(true);
		//清除上一次选择工具数据
		clearLastInteraction();
		//激活当前选择工具
		setCurrentInteraction(true);
		
		lastSFtoolIndex = index;
	}
}

/**
 * 绑定移除选中要素事件
 */
bxmap.WFSEditor.prototype._bindingRemoveToolEvent = function(){
	var _this = this;
	//点击删除按钮事件
	var tool = this.wfsLayerRemoveTool;
	tool.addEvent({
		events: "click",
		handler:function(){
			//获取当前正在编辑的图层
			var currentLayer = _this._getCurrentLayer();
			if(currentLayer == null) return;
			//删除要素
			var selectedFeatures = _this._getSelectedFeatures();
			currentLayer.removeFeatures(selectedFeatures);
			//清除选中状态
			var item = _this.wfsLayerSelectFeatureTool.getCurrentItem();
			var interaction = item.data;
			interaction.clear();
		}
	});
	
	//开始编辑按钮点击事件，监控是否存在选中要素，改变删除按钮状态
	tool  = this.wfsLayerStartEditTool;
	tool.addEvent({
		events: "click",
		handler:function(){
			_this._handleRemoveToolEnabled(_this);
		}
	});
	
	//监控选择要素工具选择要素改变事件
	this._listenSelectFeatureChangeEvent();
}
/**
 * 监控选择要素工具选择要素改变事件
 */
bxmap.WFSEditor.prototype._listenSelectFeatureChangeEvent = function(){
	//监控面选工具是否选中要素
	if(this._polygonSelectInteraction){
		var polygonSource = this._polygonSelectInteraction.selectedFeatureLayer.getSource();
		polygonSource.on(["addfeature","changefeature","removefeature"], function(){
			this._handleRemoveToolEnabled(this);
		}, this);
	}
	
	//监控矩形选择是否选中要素
	if(this._boxSelectInteraction){
		var boxSource = this._boxSelectInteraction.selectedFeatureLayer.getSource();
		boxSource.on(["addfeature","changefeature","removefeature"], function(){
			this._handleRemoveToolEnabled(this);
		}, this);
	}
	
	//监控矩形选择是否选中要素
	if(this._circleSelectInteraction){
		var circleSource = this._circleSelectInteraction.selectedFeatureLayer.getSource();
		circleSource.on(["addfeature","changefeature","removefeature"], function(){
			this._handleRemoveToolEnabled(this);
		}, this);
	}
}

/**
 * 获取框选/多边形选择/圆形选择工具选中的要素
 */
bxmap.WFSEditor.prototype._getSelectedFeatures = function(){
	//获取选择要素工具当前工具
	var item = this.wfsLayerSelectFeatureTool.getCurrentItem();
	var interaction = item.data;
	var source = interaction.selectedFeatureLayer.getSource();
	return source.getFeatures();
}

/**
 * 用于处理移除按钮可用状态
 */
bxmap.WFSEditor.prototype._handleRemoveToolEnabled = function(opt_this){
	var _this = opt_this;
	//获取当前正在编辑的图层
	var currentLayer = _this._getCurrentLayer();
	if(currentLayer == null) return;
	//获取当前选中的要素
	var selectedFeatures = _this._getSelectedFeatures();
	
	var removeToolEnable = selectedFeatures.length > 0 && currentLayer.isEditing();
	this.wfsLayerRemoveTool.setEnabled(removeToolEnable);
}

/**
 * 绑定移除选中要素事件
 */
bxmap.WFSEditor.prototype._bindingClearSeletedToolEvent = function(){
	//点击移除选中要素按钮事件
	var tool = this.wfsLayerClearSeletedTool;
	var _this = this;
	tool.addEvent({
		events: "click",
		handler:function(){
			//清除选中状态
			var item = _this.wfsLayerSelectFeatureTool.getCurrentItem();
			var interaction = item.data;
			if(interaction){
				interaction.clear();
			}
		}
	});
}

/**
 * 绑定互斥工具激活事件
 */
bxmap.WFSEditor.prototype._bindingMutexToolActiveChangedEvent = function(){
	var tools = 
		[
		 this.wfsLayerNewFeaturetool,//新增要素工具
		 this.wfsLayerModifyFeatureTool,//修改要素工具
		 this.wfsLayerTrasformTool,//变换要素工具
		 this.wfsLayerSelectFeatureTool//选择要素工具
		];
	
	//激活tool，则其他互斥工具注销激活
	function activeTool(tool){
		var itool,active = false;
		for(var i = 0; i < tools.length; i++){
			itool = tools[i];
			active = (itool == tool);
			itool.setActive(active);
		}
	}
	
	var _this = this;
	//新增要素工具
	var newFeaturetool = this.wfsLayerNewFeaturetool;
	newFeaturetool.addEventListener("activeChanged",function(active){
		var newFeatureInteraction = _this._newFeatureInteraction;
		if(active){
			activeTool(newFeaturetool);
			_this._bmap.setCurrentMutexInteraction(newFeatureInteraction);
		}else{
			newFeatureInteraction.setActive(false);
		}
	});
	//修改要素工具
	var modifyFeatureTool = this.wfsLayerModifyFeatureTool;
	modifyFeatureTool.addEventListener("activeChanged",function(active){
		var modifyFeatureInteraction = _this._modifyFeatureInteraction;
		if(active){
			activeTool(modifyFeatureTool);
			_this._bmap.setCurrentMutexInteraction(modifyFeatureInteraction);
		}else{
			//清空数据
			modifyFeatureInteraction.clear();
			modifyFeatureInteraction.setActive(false);
		}
	});
	//变换要素工具
	var trasformTool = this.wfsLayerTrasformTool;
	trasformTool.addEventListener("activeChanged",function(active){
		var transformFeatureInteraction = _this._transformFeatureInteraction;
		if(active){
			activeTool(trasformTool);
			_this._bmap.setCurrentMutexInteraction(transformFeatureInteraction);
		}else{
			//清空数据
			transformFeatureInteraction.clear();
			transformFeatureInteraction.setActive(false);
		}
	});
	//选择要素工具
	var selectFeatureTool = this.wfsLayerSelectFeatureTool;
	selectFeatureTool.addEventListener("activeChanged",function(active){
		var selectFeatureInteraction = null;
		if(active){
			activeTool(selectFeatureTool);
			var item = selectFeatureTool.getCurrentItem();
			selectFeatureInteraction = item.data;
			_this._bmap.setCurrentMutexInteraction(selectFeatureInteraction);
		}else{
			//清空数据
			_this._boxSelectInteraction.clear();
			_this._boxSelectInteraction.setActive(false);
			_this._polygonSelectInteraction.clear();
			_this._polygonSelectInteraction.setActive(false);
			_this._circleSelectInteraction.clear();
			_this._circleSelectInteraction.setActive(false);
		}
	});
}

/**
 * 绑定工具条可见状态事件
 */
bxmap.WFSEditor.prototype._bindingToolbarEvent = function(){
	var _this = this;
	var toolbar = this.wfsLayerEditorToolbar;
	toolbar.onVisibilityChanged = function(visible){
		if(!visible){
			//停止编辑
			_this.wfsLayerStartEditTool.setActive(false);
			
			//选择要素工具不受开始编辑按钮控制，需单独清除
			_this._boxSelectInteraction.clear();
			_this._boxSelectInteraction.setActive(false);
			_this._polygonSelectInteraction.clear();
			_this._polygonSelectInteraction.setActive(false);
			_this._circleSelectInteraction.clear();
			_this._circleSelectInteraction.setActive(false);
		}
	}
}