/*	
 *  余昌纯
 */
 
var dataurl=[];

(function(){
	
	var defaults={
		tarinput:"",
		previewTarget:"uploaderFiles",
		tarlimit:8,
		tardelete:'weui-gallery__del',
//		Iscompress:true,
		adr:[],
	}
	
	function Upload(option){
		var option=$.extend(defaults,option);
		this.tarinput=document.getElementById(option.tarinput);
		this.previewTargets=document.getElementById(option.previewTarget);
		this.limit=option.tarlimit;
		this.tardelete=document.getElementById(option.tardelete);
		this.Iscompress=option.Iscompress;
		this.adr=option.adr;
		this.init();
		this.deletes();	
	}
	
	Upload.prototype.init=function(){
		var self=this;
		var numbers=0;
		this.tarinput.onchange=function(){
			var Img=$('<img src="">').on({
						click:function(){
							self.preview(this)
						}
					});
			var Li=$('<li class="weui-uploader__file"></li>');
			var files=this.files;

			var file=files[0];
			var reader=new FileReader();
			reader.onload=function(oFREvent){
				if($(self.previewTargets).find('li').length>=self.limit){
					$.alert("最多上传"+self.limit+"张");
				}else{
					var result=this.result;
					dataurl.push(result);
					numbers++;
					Img.attr({
						'src':result,
						'id':'img'+numbers
					});
					Li.append(Img)
            		$(self.previewTargets).append(Li);
            		$('.weui-uploader__info_done').text($('#uploaderFiles').find("li").length);
				}
			}
			
  			reader.readAsDataURL(file);	
  
			
		}	
	}
	
	Upload.prototype.preview=function(target){
		var src=target.src;
		var Id=target.id;
		$('.weui-gallery').css('display','block');
		$('.weui-gallery__img').css({
			"backgroundImage":'url(" '+src+'")',		
		}).attr("dataId",Id)
		$('.weui-gallery__img').click(function(){
			$(this).parents('.weui-gallery').css('display','none')
		})
	}
	
	Upload.prototype.deletes=function(){
		this.tardelete.onclick=function(){
			var dataId=$(this).parents('.weui-gallery').find('.weui-gallery__img').attr('dataId');
			$('#'+dataId).parent('li').remove();
			$('.weui-uploader__info_done').text($(self.previewTargets).find("li").length);
			$('.weui-uploader__info_done').text($(self.previewTargets).find("li").length);
			$(this).parents('.weui-gallery').css('display','none')
		}
	}
	
//	Upload.prototype.compress=function(img){
//		var self=this;
//		var img=img[0];
//		var initSize = img.src.length;
//		var width=img.width;
//		var height=img.height;
//		var canvas = document.createElement('canvas');
//		var ctx = canvas.getContext('2d');
//		canvas.width = width;
//	    canvas.height = height;
//
//	//  铺底色
//	    ctx.fillStyle = "#fff";
//	    ctx.fillRect(0, 0, canvas.width, canvas.height);
//      ctx.drawImage(img, 0, 0, width, height);
//      
//
//      //进行最小压缩
//      var ndata = canvas.toDataURL('image/jpeg', 0.1);
//      return ndata
//  
//	}
	
	//图片转码
//	Upload.prototype.Transfor=function(ndata,type){
//		var text= window.atob(ndata.split(",")[1])
//		var type=type;
//		 var buffer = new ArrayBuffer(text.length);
//      var ubuffer = new Uint8Array(buffer);
//      var pecent = 0 , loop = null;
//
//      for (var i = 0; i < text.length; i++) {
//          ubuffer[i] = text.charCodeAt(i);
//      }
//      
//      console.log(buffer)
//
//      var Builder = window.WebKitBlobBuilder || window.MozBlobBuilder;
//      var blob;
//
//      if (Builder) {
//          var builder = new Builder();
//          builder.append(buffer);
//          blob = builder.getBlob(type);
//          
//      } else {
//          blob = new window.Blob([buffer], {type: type});
//          console.log(blob)
//      }
//      
//      var formdata = new FormData();
//      formdata.append('imagefile', blob);
//      
//      console.log(formdata)
//	}
//	

	
	window.Upload=Upload
	return {
		dataurl
	}
	
})()



