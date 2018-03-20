/*
 *  作者：余昌纯
 *  时间： 2017-06-28
 * 	使用方法 : new Showpositions({参数1:"",参数2:"",参数3:""})
 *  必须参数: target 用来放置位置的容器.type表示
 */

(function(){
	var defaults={
		ak:"q7BvNSGbg2WH3HT640sGV3CmLLDp5mhp",
		type:"detail",
		target:""
	}
	
	function Showpositions(option){
		var option=$.extend(defaults,option);
		this.ak=option.ak;
		this.target=$("#"+option.target)
		this.getlocation();
		this.result='' ;
		this.type=option.type;
	}
	
	Showpositions.prototype.getlocation=function(){
		var self=this;
		if(this.ak==""){
			alert('请传入ak')
		}
		if (navigator.geolocation){
			navigator.geolocation.getCurrentPosition(showPosition,showError);
		}
		else{
			console.log("Geolocation is not supported by this browser.")
		}
		
		function showPosition(position){
			var latlon = position.coords.latitude+','+position.coords.longitude;
			var url="http://api.map.baidu.com/geocoder/v2/?ak="+self.ak+"&callback=renderReverse&location="+latlon+"&output=json&pois=0";
			$.ajax({
				url:url,
				type:"POST",
				dataType:"jsonp",
				success:function(data){
					console.log(data);
					console.log(self.target);
				

					if(self.type=="city"){
						self.target.text(data.result.addressComponent.city)
					};
					if(self.type=="province"){
						self.target.text(data.result.addressComponent.province)
					}
					if(self.type=="detail"){
						self.target.text(data.result.formatted_address)
					}
					
				}
			})
		}
		function showError(error){
		  switch(error.code)
		    {
		    case error.PERMISSION_DENIED:
		      console.log("用户拒绝获取位置信息.")
		      break;
		    case error.POSITION_UNAVAILABLE:
		      console.log("位置信息不可用")
		      break;
		    case error.TIMEOUT:
		      console.log("获取用户位置超时")
		      break;
		    case error.UNKNOWN_ERROR:
		     console.log("一个未知的错误")
		      break;
		    }
  		}
	}
	
	
	window.Showpositions=Showpositions;
	
	
})()
