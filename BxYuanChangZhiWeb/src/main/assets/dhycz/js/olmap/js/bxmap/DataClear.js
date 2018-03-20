/**
 * @classdesc 用于注册可清除对象，可清除对象必须实现clear()方法，用于管理执行clear()方法
 * @constructor
 * @extends {ol.Object}
 * @param	{String} id 可选。唯一标识
 */
bxmap.DataClear = function(id) {
	ol.Object.call(this);
	
	this.clears = [];
	this.id = id == null ? "" : id;
}
ol.inherits(bxmap.DataClear, ol.Object);

/**
 * @description 注册对象
 * @param clears - {Array|Object} 将对象添加到清除数组中，单个对象或数组中对象必须实现clear()方法
 */
bxmap.DataClear.prototype.register = function(clears){
	if(clears == null || typeof clears != 'object') return;
	//判断是否为数组
	if(clears instanceof Array && Object.prototype.toString.call(clears) === '[object Array]'){
		this.clears = this.clears.concat(clears);
	}else{
		var arr = this.clears;
		var i = arr.indexOf(clears);
		var found = i > -1;
		if(!found){
			this.clears.push(clears);
		}
	}
}

/**
 * @description 注销清除
 * @param clear {Object} 从清除数组中将对象移除
 */
bxmap.DataClear.prototype.unregister = function(clear){
	if(clear == null) return;
	var arr = this.clears;
	var i = arr.indexOf(clear);
	var found = i > -1;
	if (found) {
		arr.splice(i, 1);
	}
}

/**
 * @description 循环遍历可清除对象，执行对象的清除方法。
 */
bxmap.DataClear.prototype.clear = function(){
	var clearObj = null,
		cont = false;
	for(var i = this.clears.length -1 ; i>=0 ;i--){
		clearObj = this.clears[i];
		cont = (clearObj != null) &&  (clearObj.clear != null) && ((clearObj.clear) instanceof Function);
		if(cont){
			try{
				clearObj.clear();
			}
			catch(ex){
				//TODO
				//清除数据出错
			}
		}
	}
}