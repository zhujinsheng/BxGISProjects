$(function(){
	//下拉
	var Accordion = function(el, multiple) {
		this.el = el || {};
		this.multiple = multiple || false;
		// Variables privadas
		var links = this.el.find('.link');
		// Evento
		links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown)
	}
	Accordion.prototype.dropdown = function(e) {
		var $el = e.data.el;
			$this = $(this),
			$next = $this.next();
		$next.slideToggle();
		$this.parent().toggleClass('open');
		if (!e.data.multiple) {
			$el.find('.submenu').not($next).slideUp().parent().removeClass('open');
		};
	}	
	var accordion = new Accordion($('#accordion'), false);
	
	//条件筛选
	$(".screen").click(function(){
		if($(".condition-screen").css("right","-5.6rem")){
			$(".screenShade").css("display","block");
			$(".condition-screen").animate({"right":"0rem"});
		}
	});
	$(".screenShade").click(function(){
		if($(".condition-screen").css("right","0rem")){
			$(".condition-screen").animate({"right":"-5.6rem"});
			$(this).css("display","none");
		}
	});
	$(".select_list a").click(function(){
		$(this).addClass("selected").siblings().removeClass("selected");
	});
	$("#xjClick").click(function(){
		if($(".condition-screen").css("right","0rem")){
			$(".condition-screen").animate({"right":"-5.6rem"});
			$(".screenShade").css("display","none");
			$(".county").css("display","block");
			$(".county").siblings().hide();
		}
	});
	$("#tab3 .select_list a").click(function(){
		$(".condition-screen").animate({"right":"-5.6rem"});
		$(".screenShade").css("display","none");
		$(".river").css("display","block");
		$(".river").siblings().hide();
	});
	$("#tab2 .select_list a").click(function(){
		$(".condition-screen").animate({"right":"-5.6rem"});
		$(".screenShade").css("display","none");
		$(".town").css("display","block");
		$(".town").siblings().hide();
	});
	//事件管理筛选
	$(".event-container a").click(function(){
		$(".condition-screen").animate({"right":"-5.6rem"});
		$(".screenShade").css("display","none");
		var value = $(this).html();
		$("#scree-nameV").text(value);
	});
	//下拉时间
	$("#picker").picker({
	  title: "请选择年份",
	  cols: [
	    {
	      textAlign: 'center',
	      values: ['2018', '2017', '2016', '2015', '2014', '2013', '2012', '2011', '2010']
	    }
	  ]
	});
	$(".map-zhezhao").click(function(){
		if($(".map-zhezhao").css("display")=="block"){
			$(".map-zhezhao").css("display","none");
			$(".map-nav-menu").css("display","none");
			$(".map-container").css("display","none")
			$(".map-bottom-dialog").animate({bottom:'-8rem'});
		}
	});
	//河流详情菜单切换 
	$(".map-nav-menu>ul>li").click(function(){
		$(".map-bottom-dialog").animate({bottom:'0'});
		var name=$(this).attr("name");
		if(name=="basicMsg"){
			$(".basic-message").css("display","block");
			$(".basic-message").siblings().hide();
		}
		if(name=="publiMsg"){
			$(".public-message").css("display","block");
			$(".public-message").siblings().hide();
		}
		if(name=="policy"){
			$(".policy-message").css("display","block");
			$(".policy-message").siblings().hide();
		}
		if(name=="drainRecord"){
			$(".drain-message").css("display","block");
			$(".drain-message").siblings().hide();
		}
	});
	//底部下拉弹出
	$("#area-active").click(function(){
		if($(".map-zhezhao").css("display")=="none"){
			$(".map-zhezhao").css("display","block");
			$(".map-nav-menu").css("display","block");
			$(".map-container").css("display","block")
		}
	});
	//图例
	$("#tlClick").click(function(){
		if($(".map-tlbar").css("display")=="none"){
			$(".map-tlbar").css("display","block");
		}
		else{
			$(".map-tlbar").css("display","none");
		}
	})
});
//日历
$("#date").calendar({
    onChange: function (p, values, displayValues) {
      console.log(values, displayValues);
    }
  });
   $("#date2").calendar({
    onChange: function (p, values, displayValues) {
      console.log(values, displayValues);
    }
  });
//信息公开切换
function show(obj){	
	var v=obj.value;
    if(v=='1'){
		$(".event-lists").css("display","block");
		$(".record-lists").css("display","none");
		/*$(".time-select").css("color","#666")*/
	}
	if(v=='2'){
		$(".event-lists").css("display","none");
		$(".record-lists").css("display","block");
	}
} 
//三级下拉菜单
 $(function() {  
    $('.d-firstNav').click(function() {  
        dropSwift($(this), '.d-firstDrop');  
    });  
    $('.d-secondNav').click(function() {  
        dropSwift($(this), '.d-secondDrop');  
    });  
    function dropSwift(dom, drop) {  
        dom.next().slideToggle();  
        dom.parent().siblings().find('.icon-arrow-up').removeClass('iconRotate');  
        dom.parent().siblings().find(drop).slideUp();  
        var iconChevron = dom.find('.icon-arrow-up');  
        if (iconChevron.hasClass('iconRotate')) {  
            iconChevron.removeClass('iconRotate');  
        } else {  
            iconChevron.addClass('iconRotate');  
        }  
    }  
})

//河道选择
$('.report_table tbody tr').each(function(index){
	$('.report_table tbody tr').eq(index).click(function(){
		for(var i=0;i<$('.report_table tbody tr').length;i++){
			$('.report_table tbody tr').eq(i).removeClass('selected');
		}
		$(this).addClass('selected');
	})
})

//上报选择
$('.sb_types>li').each(function(index){
	$('.sb_types>li').eq(index).click(function(){
		for(var i=0;i<$('.sb_types>li').length;i++){
			$('.sb_types>li').eq(i).removeClass('selected');
		}
		$(this).addClass('selected');
		if(index==0){
			$('.all_pwk').css('display','none');
			$(".my_pwk").css('display','block');
		}else{
			$('.all_pwk').css('display','block');
			$(".my_pwk").css('display','none');
		}
	})
})


//定义后台接口全局变量
var GLOBAL={
    domainRest : "http://localhost:18082"
}

//多级手风琴菜单
$(document).ready(function() {
	  var mtree = $('ul.mtree');
	  // Skin selector for demo
	  mtree.wrap('<div class=mtree-demo></div>');
	  var skins = ['bubba','skinny','transit','jet','nix'];
	  mtree.addClass(skins[0]);
});