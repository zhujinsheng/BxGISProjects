/**
 * @require Common.js
 */

/**
 * @namespace bxmap.protocol
 */
bxmap.protocol = bxmap.protocol || {};


/**
 * @classdesc wfs，用于对wfs数据（包括属性数据和几何数据）执行增删改查，执行增删改时需要后台服务支持编辑功能
 * @constructor 
 * @param {Object} options 
 * @param {String} options.url     (必需)示例http://localhost:8888/geoserver/wfs
 * @param {String} options.featureNS    (必需)要素的命名空间URI, 当featurePrefix提供并匹配服务器端用于featureType的prefix，那它就能够被自动检测
 * @param {String} options.featurePrefix     (必需)要素图层前缀
 * @param {String} options.featureTypes   (必需)图层(不带前缀)
 * @param {String} options.geometryName  几何图形字段名称。
 * @param {String} options.srsName 坐标系
 * @extends {ol.Object}	
 * 
 * @example
 * var wfs = new bxmap.protocol.WFS({
 * 	url:'http://localhost:8888/geoserver/wfs',
 * 	featureNS:'http://localhost:8888/MSSQL'
 * 	srsName: 'EPSG:3857',
 * 	featurePrefix: 'GD',
 * 	featureTypes: 'GH_POINT',
 * 	geometryName:'Shape',
 * 	outputFormat: 'application/json'
 * });
 */
bxmap.protocol.WFS = function(options){
    /**
	 * @description 用于管理增删改的数据
     * @type {{c: Array, u: Array, d: Array}}
     * @private
     */
	this._f = {
		c: [],
		u: [],
		d: []
	};//编辑要素

    /**
	 * @description 保存初始化参数
     * @private
     */
	this._initOptions = options;

    /**
     * @private
     */
	this._url = options.url;
    /**
     * @private
     */
	this._featureNS = options.featureNS;
    /**
     * @private
     */
	this._featurePrefix = options.featurePrefix;
    /**
     * @private
     */
	this._featureTypes = [options.featureTypes];
    /**
     * @private
     */
	this._srsName = options.srsName;
    /**
     * @private
     */
	this._geometryName = options.geometryName;

	ol.Object.call(this);
}
ol.inherits(bxmap.protocol.WFS, ol.Object);

/**
 * @callback WFS_read bxmap.protocol.WFS.prototype.read回调方法
 * @param {Array<ol.Feature>} feature 要素
 */
/**
 * @description 根据过滤等条件获取要素
 * @param options {Object} 
 * @param options.filter  {ol.format.filter.Filter | undefined} 过滤条件
 * @param options.maxFeatures  {number | undefined} 返回结果最大值
 * @param options.startIndex   {number | undefined} 起始索引
 * @param options.count  {number | undefined} 返回个数
 * @param options.resultType  {string | undefined} 'hits'仅返回要素个数
 * @param options.propertyNames {Array.<string> | undefined} 返回结果包含的属性名
 * @param options.outputFormat  {String} 默认'application/json',GML2,GML3,application/json
 * @param options.keepString {boolean} 是否保持字符串类型，默认为false
 * @param options.async {boolean} 默认为true，true：异步方式，false:同步方式
 * @param options.callback {WFS_read} 回调方法
 */
bxmap.protocol.WFS.prototype.read=function(options){
	var opt_options=$.extend({},this._initOptions,options);
	opt_options.outputFormat = options.outputFormat || 'application/json';//设定默认值
	opt_options.geometryName = this.getGeometryName();
	var featureRequest = this.featureRequest = new ol.format.WFS().writeGetFeature(opt_options);
	
	var callback = opt_options.callback;
	var keepString = options.keepString == null ? false : options.keepString;
	var asyn = opt_options.async == null ? true : opt_options.async;
	
	//request body
	var body = new XMLSerializer().serializeToString(featureRequest);
	_this=this;
	$.ajax({   
	    url: this._url,   
	    type: "POST", 
	    dataType: "text",
	    contentType: 'text/plain;charset=UTF-8', // 这里必须设置，否则会默认以form表单数据进行发送      
	    async: asyn,
	    data: body,   
	    success: function(result){
	    	var data = result;
	    	if(!keepString){
	    		//尝试转格式
	    		try{
		    		data= _this._parseResponse(result, opt_options);
		    	}catch(e){
		    		//TODO:异常不做任何处理
		    	}
	    	}
	        if(callback){
	        	callback(data);
	        }
	    }  
	}); 
}

/**
 * @callback WFS_getFeature
 * @param {ol.Feature} feature 要素
 */
/**
 * @description 根据要素FID读取数据
 * 
 * @param options
 * @param options.fid {String} featureid，geoserver发布wfs后，fid一般为typename.id形式，例如"GEO_MM_ZGQ.2"
 * @param options.async {Boolean} 默认为true，true：异步方式，false:同步方式
 * @param options.getFeature {WFS_getFeature} 回调
 */
bxmap.protocol.WFS.prototype.readFeatureByFid = function(options){
	var opt_options = options || {};
	if(opt_options.fid == null || opt_options.fid == "") return;
	var asyn = opt_options.async == null ? true : opt_options.async;
	var outputFormat = "application/json";
	var fid = opt_options.fid;
	var typeName =  this._featurePrefix +":" + this._featureTypes[0];
	var geometryName = this.getGeometryName();
	//http://gisserver:18081/geoserver/wfs?request=GetFeature&version=1.1.0&typeName=DEGSRPT:GEO_MM_ZGQ&outputFormat=application/json&FEATUREID=GEO_MM_ZGQ.2
	var url = this._url + "?request=GetFeature&version=1.1.0&typeName=" + typeName + "&outputFormat=application/json&FEATUREID=" + fid;
	var _this = this;
	$.ajax({   
	    url: url,   
	    type: "GET", 
	    dataType: "text",
	    contentType: 'text/plain;charset=UTF-8', // 这里必须设置，否则会默认以form表单数据进行发送      
	    async: asyn, 
	    success: function(result){   
	        var data = _this._parseResponse(result, {
	        	outputFormat: outputFormat,
	        	geometryName: geometryName
	        });
	        var feature = null;
	        if(data && data.length != 0){
	        	feature = data[0];
	        }
	        if(opt_options.getFeature){
	        	opt_options.getFeature(feature);
	        }
	    }  
	}); 
}

/**
 * @callback WFS_describeFeatureType
 * @param {object} JSON对象
 */
/**
 * @description 获取字段描述信息
 * @param options
 * @param options.async {Boolean} 默认为true，true：异步方式，false:同步方式
 * @param options.describeFeatureType {WFS_describeFeatureType} 回调方法
 */
bxmap.protocol.WFS.prototype.describeFeatureType = function(options){
	var opt_options = options || {};
	var asyn = opt_options.async == null ? true : opt_options.async;
	var typeName =  this._featurePrefix +":" + this._featureTypes[0];
	//http://gisserver:18081/geoserver/wfs?service=wfs&version=1.1.0&request=DescribeFeatureType&TypeName=DEGSRPT:GEO_MM_SPD_PT&outputFormat=application/json
	var url = this._url + "?service=wfs&version=1.1.0&request=DescribeFeatureType&TypeName=" + typeName + "&outputFormat=application/json";
	var _this = this;
	$.ajax({   
	    url: url,   
	    type: "GET", 
	    dataType: "text",
	    contentType: 'text/plain;charset=UTF-8',
	    async: asyn, 
	    success: function(result){
	    	var output = null;
	    	try{
	    		output = JSON.parse(result);
	    	}catch(ex) {
	    		//TODO:异常不做处理
	    	}
	    	if(opt_options.describeFeatureType){
	    		opt_options.describeFeatureType(output);
	    	}
	    }  
	}); 
}

/**
 * @private
 * @description
 * 解析请求返回数据
 * @param response {String}
 * @param options
 * @param options.outputFormat {String} 输出格式
 * @param options.resultType {String|undefined} 仅输出个数'hits'
 * @param options.geometryName {String} 几何字段名称
 * @returns Array.<ol.Feature>
 */
bxmap.protocol.WFS.prototype._parseResponse = function(response, options){
	var data = null;
	if(options.resultType === 'hits'){
		data = new ol.format.WFS().readFeatureCollectionMetadata(response);
		return data;
	}
	var outputFormat = options.outputFormat ? options.outputFormat.toLowerCase():"";
	//根据输出格式解析数据
	switch(outputFormat){
		case 'gml2':
			data = new ol.format.GML2().readFeatures(response);
			break;
		case 'gml3':
			data = new ol.format.GML3().readFeatures(response);
			break;
		default:
			data = new ol.format.GeoJSON({geometryName:options.geometryName}).readFeatures(response);
			break;
	}
	
	return data;
}

//增删改查
/**
 * @description 创建要素，不会执行提交操作。
 * @param {Array.<ol.Feature>} features
 */
bxmap.protocol.WFS.prototype.createFeatures=function(features){
	if(features == null) return;
	this._f.c=this._f.c.concat(features);
}
/**
 * @description 更新要素，不会执行提交操作。
 * @param {Array.<ol.Feature>} features
 */
bxmap.protocol.WFS.prototype.updateFeatures=function(features){
	if(features == null) return;
	this._f.u=this._f.u.concat(features);
}
/**
 * @description 删除要素，不会执行提交操作。
 * @param {Array.<ol.Feature>} features
 */
bxmap.protocol.WFS.prototype.deleteFeatures=function(features){
	if(features == null) return;
	this._f.d=this._f.d.concat(features);
}

/**
 * @callback WFS_commit WFS编辑提交回调方法
 * @param {object} evt 
 */
/**
 * @description 执行提交操作，对修改的要素执行保存。
 * @param  options {Object}
 * @param  options.success {WFS_commit}  保存成功的回调函数
 * @param  options.fail {WFS_commit}  保存失败的回调函数
 */
bxmap.protocol.WFS.prototype.commit=function(options){
	options = options || {};
	
	if(this._f.c.length==0&&this._f.u.length==0&&this._f.d.length==0){
		return;
	}
    var formatWFS = new ol.format.WFS();
    var formatGML = new ol.format.GML({
        featureNS: this._featureNS, // Your namespace
        //featurePrefix:'MSSQL',
        featureType: this._featureTypes[0],
        srsName: this._srsName
    });	     
	var node=formatWFS.writeTransaction(this._f.c, this._f.u, this._f.d, formatGML);;
	var s = new XMLSerializer();
	var str = s.serializeToString(node);
	
    $.ajax(this._url,{
        type: 'POST',
        dataType:'xml',
        processData: false,
        contentType: 'text/xml',
        data: str
    }).done(function(evt){
    	var response = new ol.format.WFS().readTransactionResponse(evt);
    	if(options.success){
    		options.success(response);
    	}
    }).fail(function(evt){
    	var response = new ol.format.WFS().readTransactionResponse(evt);
    	if(options.fail){
    		options.fail(response);
    	}
    });
    
    this.clear();
}
/**
 * @description 获取WFS要素中几何字段名字
 * @returns {String}
 */	
bxmap.protocol.WFS.prototype.getGeometryName = function(){
	if(this._geometryName == null || this._geometryName == ""){
		var _this = this;
		this.describeFeatureType({
			async: false,
			describeFeatureType: function(response){
				if(response == null) return;
				var featureTypes = response[0] || {};
				var properties = featureTypes.properties || [];
				var prop, type;
				for(var i = 0; i < properties.length; i++){
					prop = properties[i];
					type = prop["type"].toLowerCase();
					switch(type){
						case "gml:geometry":
						case "gml:point":
						case "gml:multipoint":
						case "gml:linestring":
						case "gml:multilinestring":
						case "gml:linearring":
						case "gml:polygon":
						case "gml:multipolygon":
							_this._geometryName = prop["name"];
							break;
						default:
							break;
					}
				}
			}
		});
	}
	return this._geometryName;
}

/**
 * @description 清空数据，不会执行提交操作，也不会修改源数据，
 * 仅对createFeatures、updateFeatures、deleteFeatures方法操作的临时数据有效，
 * 执行该方法后源数据将不再执行增删改操作，仍保持不变。
 */
bxmap.protocol.WFS.prototype.clear = function(){
	this._f = {
		c: [],
		u: [],
		d: []
	};
}
