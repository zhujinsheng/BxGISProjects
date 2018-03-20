/* 0级节点 start */
//<ul class="menuCenter">
    /* 1级节点 start */
//	<li>
//    	<a class="tag"><img src="images/icon_d.png"/></a>
         
        /* 2级节点 start */
//      <div class="hidden">
//            <ul class="strk">
//                <li class="tlttle">
//                    <span>绘制点标</span>
//                </li>

				  /* 3级节点 */
//                <li>
//                    <a>点</a>
//                </li>


        /* 2级节点 end */
//            </ul>
//      </div>

    /* 1级节点 end */
//  </li>

/* 0级节点 end */
//</ul>

/**
 * 标绘工具条，支持2级，div示意见上
 * options 
 * 		target - {Element|String} DOM 目标dom
 * 		positioning - {String} left|right，默认为right
 */
PlotToolbar = function(options) {
	var opt_options = options ? options : {};
	
	var target = this.target = opt_options.target;
	if(this.target){
		if(typeof target === 'string'){
			this.target = document.getElementById(target);
		}
	}else{
		this.target = document.createElement("div");
	}
	
	this.root = this.createRoot();
	this.target.appendChild(this.root);
	this._elements = {};
}

/**
 * 
 * @param data id必须唯一
 * [{
 * 	 id:"1",
 *   image:'',
 *   data:"",
 *   onclick:function(element,data){},
 *   items:[
 *   	{
 *         title:"面状箭标",
 *         items:[{
 *         	 id:"100",
 *           title:"绘制点",
 *           data:null,
 *           onclick:function(element,data){}
 *         }]
 *      }
 *   ]
 * }]
 */
PlotToolbar.prototype.setData = function(data){
	this.root.innerHTML = "";
	if(data == null) return;
	
	var rootElem = this.root,
		level_1,level_2,level_3,
		content,
		itemObj,itemsObj;
	for(var i=0, length=data.length; i< length; i++){
		level_1 = null;
		level_2 = null;
		level_3 = null;
		itemObj = data[i];
		
		//1级节点
		level_1 = this.createLevel1(itemObj);
		content = level_1.content;
		rootElem.appendChild(level_1.container);
		//2级节点,level_1.items
		if(itemObj.items){
			itemsObj = itemObj.items;
			for(var j = 0, jlength = itemsObj.length; j<jlength;j++){
				itemObj = itemsObj[j];
				level_2 = this.createLevel2(itemObj);
				content.appendChild(level_2.container);
				content = level_2.content;
				//3级节点,level_2.items
				if(itemObj.items){
					itemsObj = itemObj.items;
					for(var k=0,klength = itemsObj.length; k < klength; k++){
						itemObj = itemsObj[k];
						level_3 = this.createLevel3(itemObj);
						content.appendChild(level_3.container);
					}
				}
			}
		}
		
	}
}

/**
 * 创建根节点
 */
PlotToolbar.prototype.createRoot = function(){
	//<ul class="menuCenter"></ul>
	var root = document.createElement("ul");
	root.className = "menuCenter";
	
	return root;
}

/**
 * 创建1级节点
 * <li><a class="tag"><img src="images/icon_d.png"/></a><li>
 * @param opt_items
 * 		id - {undefined|String|Number} 标识唯一编号
 * 		image - {String} 图片
 * 		data - {undefined|任意值}
 * 		onclick - function(element,data){}
 */
PlotToolbar.prototype.createLevel1 = function(opt_items){
	var id = opt_items.id;
	var img = opt_items.image;
	var onclick = opt_items.onclick;
	var data = opt_items.data;
	
	//<li><li>
	var li = document.createElement("li");
	if(id){
		this._elements[id] = li;
	}
	
	//<a></a>
	var element = document.createElement("a");
	element.className = "tag";
	if(onclick){
		element.onclick = function(){
			onclick(element, data);
		}
	}
	li.appendChild(element);
	
	//<img>
	var imageElem = document.createElement( "img" );
	imageElem.src = img;
	element.appendChild(imageElem);
	
	var output = this._createElementWrap(li, li);
	return output;
}

/**
 * 创建2级节点
 * <div class="hid"><ul class="strk"><li class="tlttle"><span>绘制面标</span></li></ul></div>
 * @param opt_items
 * 		title - {String}
 */
PlotToolbar.prototype.createLevel2 = function(opt_items){
	var title = opt_items.title;
	//var div = document.createElement('<div class="hidden"><ul class="strk"><li class="tlttle"><span>' + title + '</span></li></ul></div>');
	var div = document.createElement("div");
	div.className = "hid";
	
	var ul = document.createElement("ul");
	ul.className = "strk";
	div.appendChild(ul);
	
	var li = document.createElement("li");
	li.className = "tlttle";
	ul.appendChild(li);
	
	var span = document.createElement("span");
	span.innerHTML = title;
	li.appendChild(span);
	
	var output = this._createElementWrap(div, ul);
	return output;
}

/**
 * 创建3级节点
 * <li><a>圆</a></li>
 * @param opt_items
 * 		id - {undefined|String|Number} 标识唯一编号
 * 		title - {String}
 * 		data - {undefined|任意数据}
 * 		onclick - function(element,data){}
 */
PlotToolbar.prototype.createLevel3 = function(opt_items){
	var id = opt_items.id;
	var title = opt_items.title;
	var onclick = opt_items.onclick;
	var data = opt_items.data;
	
	//<li><li>
	var li = document.createElement("li");
	if(id){
		this._elements[id] = li;
	}
	
	//<a></a>
	var element = document.createElement("a");
	//element.setAttribute("href","javascript:void(0)");
	element.innerHTML = title;
	
	if(onclick){
		element.onclick = function(){
			onclick(element, data);
		}
	}
	li.appendChild(element);
	
	var output = this._createElementWrap(li, null);
	
	return output;
}

/**
 * 创建element包装对象
 * @param container {Element} 
 * @param content {Element} 
 */
PlotToolbar.prototype._createElementWrap = function(container, content){
	var output = {
		container:container,
		content:content
	};
	return output;
}

/**
 * 设置是否可见
 * <li><a>圆</a></li>
 * @param opt_items
 * 		id - {undefined|String|Number} 标识唯一编号
 * 		visibility - {Boolean} 是否可见
 */
PlotToolbar.prototype.setVisible = function(id, visibility){
	var elem = this._elements[id];
	if(elem && visibility != null){
		elem.style.display = visibility ? "" : "none";
	}
} 