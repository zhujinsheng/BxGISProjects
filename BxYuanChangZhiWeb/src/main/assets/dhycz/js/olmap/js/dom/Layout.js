
/**
 * 工具箱 
 * options
 * 		target - {Element|String|undefined} DOM 目标dom
 * 		className - {String} DOM Element.className
 */
BottomBackgroud = function(options) {
	var opt_options = options ? options : {};
	
	var target = this.target = opt_options.target;
	if(target){
		if(typeof target === 'string'){
			this.target = document.getElementById(target);
		}
	}
	
	var element = this.element = document.createElement("div");
	element.className = opt_options.className || "mapfootBottom";
}

/**
 * 添加element
 * @param element {Element}
 */
BottomBackgroud.prototype.addElement = function(element){
	if(element == null) return;
	var itemElement = document.createElement("div");
	itemElement.className = "font_clo";
	
	itemElement.appendChild(element);
	this.element.appendChild(itemElement);
}