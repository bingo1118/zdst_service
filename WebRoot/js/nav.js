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
	$(".close").click(function(){
		$("#tip_alarm").animate({right:"-512px"});
	});
	$(".tip_alarm_l").click(function(){
		$("#tip_alarm").animate({right:"-2px"});
	});
});

//

$(".content_left li.xitong a.active").parent("li").parent("ul").show().parent("li").addClass("hide");
$(function(){
	$(".content_left li.xitong").click(function(){
		$(this).toggleClass("hide").siblings().removeClass("hide").find("ul").slideUp(0);
		if($(this).find("ul").length > 0){
			$(this).find("ul").slideToggle(200);
			return false;
		}
	});
});