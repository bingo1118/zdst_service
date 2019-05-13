(function(window) {
    var theUA = window.navigator.userAgent.toLowerCase();
    if ((theUA.match(/msie\s\d+/) && theUA.match(/msie\s\d+/)[0]) || (theUA.match(/trident\s?\d+/) && theUA.match(/trident\s?\d+/)[0])) {
        var ieVersion = theUA.match(/msie\s\d+/)[0].match(/\d+/)[0] || theUA.match(/trident\s?\d+/)[0];
        if (ieVersion < 9) {
            var str = "你的浏览器版本太low了\n已经和时代脱轨了 :(";
            var str2 = "推荐使用:<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E8%B0%B7%E6%AD%8C%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>谷歌</a>,"
            + "<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E7%81%AB%E7%8B%90%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>火狐</a>,"
            + "<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E7%8C%8E%E8%B1%B9%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>猎豹</a>,其他双核急速模式";
            document.writeln("<pre style='text-align:center;color:#fff;background-color:#0cc; height:100%;border:0;position:fixed;top:0;left:0;width:100%;z-index:1234'>" + 
            "<h2 style='padding-top:200px;margin:0'><strong>" + str + "<br/></strong></h2><p>" + 
            str2 + "</p><h2 style='margin:0'><strong>如果你的使用s是双核浏览器,请切换到极速模式访问<br/></strong></h2></pre>");
            document.execCommand("Stop");
        };
    }
})(window);
var left_hidden=false;
//自适应窗口.css('background-color','#d9dfef')color: #b4b4b4;
$(".content_right .content_search select option:eq(0)").css('color','#b4b4b4');
$(".content_table tr:even").css('background-color','#ecf8fd');
$(function(){
	var ua = navigator.userAgent;
	var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
	    isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
	    isAndroid = ua.match(/(Android)\s+([\d.]+)/),
	    isMobile = isIphone || isAndroid;
	    if(isMobile) {
	    	$("body").css('height',$(window).height())
	    	var content_right_height = $(window).height();
	    	$(".content_right,.content_left").css('height',content_right_height);
	    	$(".content_left .fenlei").css('height',content_right_height-30);
	    	$(".equipment").css('height',content_right_height-50);
	    	$(".video").css('min-height','600');
	    	if($(window).width()<=1266){
	    		content_right_width="1026px";
	    			$(".content_right").css('width',content_right_width);
	    			$("html").css('overflow-x','scroll');
	    			$("body").css('overflow-y','hidden');
	    	}
	    	var content_fenlei_height = $(".fenlei").height();
	    	if(content_right_height<content_fenlei_height){
	    		$(".content_left .fenlei").css('overflow-y','scroll');
    			$(".content_left .fenlei").css('overflow-x','hidden');
    			$(".content_left .fenlei").css('height',content_right_height-30);
    		}
	    	$(window).resize(function() {
	    
	    		var content_fenlei_height = $(".fenlei").height();
	    		var content_right_width =  $(window).width()-240;
	    		$(".content_right").css('width',content_right_width);
	    		var content_right_height =  $(window).height()-220;
	    		$(".content_right,.content_left").css('height',content_right_height);
	    		$(".content_left .fenlei").css('height',content_right_height-30)
	    		$(".equipment").css('height',content_right_height-50);
	    		$(".video").css('min-height','600');
	    		if(content_right_height<566){
	    			$(".content_left .fenlei").css('overflow-y','scroll');
	    			$(".content_left .fenlei").css('overflow-x','hidden');
	    			$(".content_left .fenlei").css('height',content_right_height-30);

	    		}
	    		
	    		if(content_right_height<content_fenlei_height){
	    			$(".content_left .fenlei").css('overflow-y','scroll');
	    			$(".content_left .fenlei").css('overflow-x','hidden');
	    			$(".content_left .fenlei").css('height',content_right_height-30);
	    		}
	    		
	    		if($(window).width()<=1266){
	    			content_right_width="1026px";
	    				$(".content_right").css('width',content_right_width);
	    				$("html").css('overflow-x','scroll');
	    				$("body").css('overflow-y','hidden')
	    		}else{
	    			//$(".content_left").css('overflow-y','auto')
	    		}
	    
	    	});
	    			$(window).on("orientationchange",function(){ 

	    			    if(window.orientation == 0)// Portrait

	    			    { 

	    			        //Portrait相关操作

	    			    }else// Landscape

	    			    { 

	    			        //Landscape相关操作

	    			    }

	    			});
	    }else{
	    	var content_right_width =  $(window).width()-240;
	    	$(".content_right").css('width',content_right_width);
	    	var content_right_height =  $(window).height()-180;
	    	$(".content_right,.content_left").css('height',content_right_height);
	    	$(".content_left .fenlei").css('height',content_right_height-30)
	    	$(".equipment").css('height',content_right_height-50);
	    	$(".video").css('min-height','600');
	    	if(content_right_height<566){
	    		$(".content_left .fenlei").css('overflow-y','scroll');
    			$(".content_left .fenlei").css('overflow-x','hidden');
    			$(".content_left .fenlei").css('height',content_right_height-30);
	    	}
	    	
	    	var content_fenlei_height = $(".fenlei").height();
	    	if(content_right_height<content_fenlei_height){
	    		$(".content_left .fenlei").css('overflow-y','scroll');
    			$(".content_left .fenlei").css('overflow-x','hidden');
    			$(".content_left .fenlei").css('height',content_right_height-30);
    		}
	    	
	    	if($(window).width()<=1266){
	    		content_right_width="1026px";
	    			$(".content_right").css('width',content_right_width);
	    			$("html").css('overflow-x','scroll');
	    			$("body").css('overflow-y','hidden')
	    	}
	    	$(window).resize(function() {
	    		var content_right_width =  $(window).width()-240;
	    		$(".content_right").css('width',content_right_width);
	    		var content_right_height =  $(window).height()-220;
	    		$(".content_right,.content_left").css('height',content_right_height);
	    		$(".content_left .fenlei").css('height',content_right_height-30)
	    		$(".equipment").css('height',content_right_height-50);
	    		$(".video").css('min-height','600');
	    		if(content_right_height<566){
	    			$(".content_left .fenlei").css('overflow-y','scroll');
	    			$(".content_left .fenlei").css('overflow-x','hidden');
	    			$(".content_left .fenlei").css('height',content_right_height-30);
	    		}
	    		
	    		var content_fenlei_height = $(".fenlei").height();
		    	if(content_right_height<content_fenlei_height){
		    		$(".content_left .fenlei").css('overflow-y','scroll');
	    			$(".content_left .fenlei").css('overflow-x','hidden');
	    			$(".content_left .fenlei").css('height',content_right_height-30);
	    		}
	    		
	    		if($(window).width()<=1266){
	    			content_right_width="1026px";
	    				$(".content_right").css('width',content_right_width);
	    				$("html").css('overflow-x','scroll');
	    				$("body").css('overflow-y','hidden')
	    		}else{
	    			//$(".content_left").css('overflow-y','auto')
	    		}
	    	
	    		
	    	});
	    }
	    //或者单独判断iphone或android 
	    if(isIphone){
	    	
	    }
	        //code 
	    else if(isAndroid){
	        //code
	    }else{
	        //code
	    }
});


//统计情况的隐藏和显示
$(function(){
	$(".map_right").click(function(){
		$(this).css({"background":"url(images/map_left.png)","right":"0"});
		$(".map_list").toggle(0,function(){
			var node=$(this)
			if(node.is(':visible')){
				$(".map_right").css({"background":"url(images/map_right.png)","right":"206px"});
			}
		});
	});
});

//报警实况的提示的隐藏和显示
$(function(){
	$(".wireless .close").click(function(){
		var right_wh = $(".wireless .tip_alarm_r").width();
		$("#tip_alarm.wireless").animate({right:"-609px"});
	});
	$(".wireless .tip_alarm_l").click(function(){
		$("#tip_alarm.wireless").animate({right:"-2px"});
	});
});

//报警实况的提示的隐藏和显示
$(function(){
	$(".wired .close").click(function(){
		var right_wh = $(".wired .tip_alarm_r").width();
		$("#tip_alarm.wired").animate({right:"-"+right_wh});
	});
	$(".wired .tip_alarm_l").click(function(){
		$("#tip_alarm.wired").animate({right:"-2px"});
	});
});

//左侧导航栏
$(function(){
	$(".content_left li.xitong span").click(function(){
		$(this).parent().toggleClass("hide").siblings("li").removeClass("hide").find("ul").slideUp(0);
		$(this).siblings("ul").find("a").removeClass("active");
		var parameter = $(this).attr("parameter");
		var iframe_table = $("#iframe_table").attr("src", parameter );
		$("#div_table").hide();
		$(".iframe_table").show();
		$(".content_left .fenlei").css('overflow-y','scroll');
		$(".content_left .fenlei").css('overflow-x','hidden');
		var content_right_height = $(window).height()-220;
		$(".content_left .fenlei").css('height',content_right_height-30);
		if($(this).siblings("ul").length > 0){
			$(this).siblings("ul").slideToggle(200);
			return false;
		}else{
			$(this).parent().addClass("hide");
		}
	});
	$(".content_left li.xitong a,.content_left li.addressi a").click(function(){
		$(this).parent("li").siblings("li").find("a").removeClass("active");
		$(this).addClass("active").parents("li.xitong").siblings().find("a").removeClass("active");
		var parameter = $(this).attr("parameter");
		var iframe_table = $("#iframe_table").attr("src", parameter );
		$("#div_table").hide();
		$(".iframe_table").show();
	});
	
	$(".content_left li.addressi").click(function(){
		$(this).find("a").css('color','#3b66cc').parents("li.addressi").siblings("li.addressi").find("a").css('color','#000');
	});
});
