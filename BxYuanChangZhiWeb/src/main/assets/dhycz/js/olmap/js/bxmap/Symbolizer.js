/**
 * @require ol.js
 * @requere Common.js
 * @require Resource.js
 */

/**
 * @namespace bxmap.symbol
 */
bxmap.symbol = bxmap.symbol || {};

/**
 * @abstract
 * @classdesc 符号化抽象类
 * @extends {ol.Object}
 */
bxmap.symbol.Symbolizer = function(){
	ol.Object.call(this);
}
ol.inherits(bxmap.symbol.Symbolizer, ol.Object);

/**
 * @description 获取feature的属性，内部实现为feature.getProperties()
 * @returns {Object<String,*>}
 */
bxmap.symbol.Symbolizer.getContext = function(feature){
	return feature.getProperties();
}

/**
 * @abstract
 * @description 计算
 * @param feature
 */
bxmap.symbol.Symbolizer.prototype.evaluate = function(feature){
	return;
}

/**
 * @classdesc 
 * 唯一值符号化
 * @constructor
 * @extends {bxmap.symbol.Symbolizer}
 * @param {Object} options
 * @param options.defaultStyle {undefined | ol.style.Style | Array.<ol.style.Style> | ol.FeatureStyleFunction} 可选。默认样式
 * @param options.property {String} feature字段名，用于唯一值的字段名
 * @param options.context {function(feature)} 可选。如果未指定则默认使用feature.getProperties()
 * @example <caption> 如何使用唯一值符号化类 </caption>
 * var symbol = new bxmap.symbol.UniqueValueSymbolizer({
 * 		property: "symbol", //用于唯一值的字段/属性名
 * 		context: function(feature){
 *			//返回feature值标识同一值的符号化
 *   		return feature["symbol"];
 *  	}
 * });
 * var styleMap = {"selected":{value1:style1,..},"unselected":{value1:style1,..}};
 * //添加选中状态样式规则
 * symbol.addRule({ruleName: "selected",styles: styleMap["selected"]});
 * //添加未选中状态abcd要素样式规则
 * symbol.addRule({ruleName: "unselected",styles: styleMap["unselected"]});
 */
bxmap.symbol.UniqueValueSymbolizer = function(options){
	ol.Object.call(this);
	var opt_options = options || {};
	/**
	 * @readonly
	 * @description 只读。默认使用feature.getProperties()，返回结果数据{Object<String,*>}，如果指定，则使用该值属性进行唯一值计算
	 */
	this.context = opt_options.context || bxmap.symbol.Symbolizer.getContext;
	/**
	 * @readonly
	 * @description 只读。唯一值规则
	 */
	this.rules = [];
	/**
	 * @private
	 * @description 字段名
	 */
	this._property = opt_options.property;
	/**
	 * @private
	 * @description 默认样式
	 */
	this._defaultStyle = opt_options.defaultStyle;
}
ol.inherits(bxmap.symbol.UniqueValueSymbolizer, bxmap.symbol.Symbolizer);

/**
 * @description 添加唯一值样式规则
 * @param {Object} options
 * @param options.ruleName {String|undefined} 规则名称，默认值为"default"
 * @param options.styles {Object} {value1:style1,..} value为字段值，style为{ol.style.Style | Array.<ol.style.Style> | ol.FeatureStyleFunction}类型
 */
bxmap.symbol.UniqueValueSymbolizer.prototype.addRule = function(options){
	if(options == null) return;
	var ruleName = options.ruleName || "default";
	var rule = this._getRuleByName(ruleName);
	var styles = options.styles || {};
	if(rule == null){
		rule = {ruleName:ruleName,styles:styles};
		this.rules.push(rule);
	}else{
		for(key in styles){
			rule.styles[key] = styles[key];
		}
	}
}

/**
 * @description 计算符号为feature设置样式
 * @param {ol.Feature} feature 矢量要素
 */
bxmap.symbol.UniqueValueSymbolizer.prototype.evaluate = function(feature){
	var value = feature.getProperties();
	if(this.context){
		value = this.context(feature);
	}
	var ruleName = feature.getSymbolRuleName() || "default";
	var rule = this._getRuleByName(ruleName);
	var styles = rule ? rule.styles : {};
	var style = styles[value] || this._defaultStyle;
	if(style){
		feature.setStyle(style);
	}
}

/**
 * @private
 * @description 获取定义的规则
 * @param {String} ruleName
 */
bxmap.symbol.UniqueValueSymbolizer.prototype._getRuleByName = function(ruleName){
	if(ruleName == null) return null;
	var rule = null;
	for(var i = 0; i < this.rules.length; i++){
		if(this.rules[i].ruleName == ruleName){
			rule = this.rules[i];
		}
	}
	return rule;
}