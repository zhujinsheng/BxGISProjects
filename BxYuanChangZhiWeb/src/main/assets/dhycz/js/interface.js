var basic = basic || {};
basic.ajax = {
	
	/**
	 * 事件处理与流转
	 * @param options
	 * @param options.data {} 示例{"process_type":"process","handle_details":texts,"event_id":event_id[1]}
	 * @param options.success {function(data)}
	 */
	handleProcess: function (options){
		var opts = options || {};
		var data = opts.data;
		var success = opts.success;
		
		$.ajax({
		    url: GLOBAL.domainRest + "/wzt/event/process",
		    type: "POST",
		    async: false,
		    dataType: "json",
		    data: data, //{"process_type":"process","handle_details":texts,"event_id":event_id[1]},
		    success: function (data) {
		     	if(data.id!="" && data.id!=null){
		     		if(success){
		     			success(data);
		     		}
		     	}
		    }
		});
	}
}
