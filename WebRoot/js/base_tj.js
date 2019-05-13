//(function(window) {
//    var theUA = window.navigator.userAgent.toLowerCase();
//    if ((theUA.match(/msie\s\d+/) && theUA.match(/msie\s\d+/)[0]) || (theUA.match(/trident\s?\d+/) && theUA.match(/trident\s?\d+/)[0])) {
//        var ieVersion = theUA.match(/msie\s\d+/)[0].match(/\d+/)[0] || theUA.match(/trident\s?\d+/)[0];
//        if (ieVersion < 9) {
//            var str = "你的浏览器版本太low了\n已经和时代脱轨了 :(";
//            var str2 = "推荐使用:<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E8%B0%B7%E6%AD%8C%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>谷歌</a>,"
//            + "<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E7%81%AB%E7%8B%90%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>火狐</a>,"
//            + "<a href='https://www.baidu.com/s?ie=UTF-8&wd=%E7%8C%8E%E8%B1%B9%E6%B5%8F%E8%A7%88%E5%99%A8' target='_blank' style='color:#cc0'>猎豹</a>,其他双核急速模式";
//            document.writeln("<pre style='text-align:center;color:#fff;background-color:#0cc; height:100%;border:0;position:fixed;top:0;left:0;width:100%;z-index:1234'>" + 
//            "<h2 style='padding-top:200px;margin:0'><strong>" + str + "<br/></strong></h2><p>" + 
//            str2 + "</p><h2 style='margin:0'><strong>如果你的使用s是双核浏览器,请切换到极速模式访问<br/></strong></h2></pre>");
//            document.execCommand("Stop");
//        };
//    }
//})(window);

//自适应窗口.css('background-color','#d9dfef')color: #b4b4b4;
$(".content_table tr:even").css('background-color','#f1f2f9');

$(window).load(function(){
	var content_right_width =  $(window).width()-257;
	$(".content_right").css('width',content_right_width);
	//var content_right_height = $(".content_left ul.fenlei").height()+30;
	//$(".content_right,.content_left").css('height',content_right_height);
	if(content_right_width<=1184){
			content_right_width="1184px";
			$(".content_right").css('width',content_right_width);
			$("body").css('overflow-y','auto');
	}
	$(window).resize(function() {
		content_right_width =  $(window).width()-240;
		$(".content_right").css('width',content_right_width);
		if(content_right_width<=1184){
			content_right_width="1184px";
			$(".content_right").css('width',content_right_width);
			$("body").css('overflow-y','auto');
		}
	}); 
});

//左侧导航栏
//$(".content_left li.xitong a.active").parent("li").parent("ul").show().parent("li").addClass("hide");
$(function(){
	$(".content_left li.xitong span").click(function(){
		$(this).parent().toggleClass("hide").siblings("li").removeClass("hide").find("ul").slideUp(0);
		if($(this).siblings("ul").length > 0){
			$(this).siblings("ul").slideToggle(200);
			return false;
		}
	});
	$(".content_left li.xitong a").click(function(){
		$(this).parent("li").siblings("li").find("a").removeClass("active");
		$(this).addClass("active").parents("li.xitong").siblings().find("a").removeClass("active");
		var parameter = $(this).attr("parameter");
		var iframe_table = $("#iframe_table").attr("src", parameter );
		$("#div_table").hide();
		$(".iframe_table").show();
	});

//使用jQuery异步提交表单
$('#itemsForm').submit(function() {

$(".iframe_table").hide();
$("#div_table").show();
var repeaterMac = $("#repeaterMac").val();
var smokeMac = $("#smokeMac").val();
	if(repeaterMac==""&&smokeMac==""){
		alert("查询条件不能为空");
	}else{
		
	jQuery.ajax({
		url: "selectItems.do",
		data: $('#itemsForm').serialize(),
		dataType: "json",
		type: "GET",
		success: function(data){
			var json = eval(data);
			var strTd;
			for(var i=0;i<json.length;i++){
				strTd += "<tr class='tr'><td><input type=\"checkbox\" class='smokeMac' name='"+json[i].smokeMac+"' value='"+json[i].smokeMac+"'  /></td><td>"+(i+1)+"</td><td>"+json[i].smokeMac+"</td><td>"+json[i].smokeName+"</td><td>"+json[i].lineState+"</td><td>"+json[i].repeaterState+"</td><td>"+json[i].heartTime+"</td><td>"+json[i].addTime+"</td><td>"+json[i].address+"</td><td>"+json[i].areaName
				+"</td><td><a href='javascript:void(0);'  onclick=\"javascript:openWin('findSmokeBySmokeMac.do?smokeMacs="+json[i].smokeMac+"','"+json[i].smokeMac+"',620,400);\">编辑</a></td>" ;				
			}
			$("#idData .tr").remove();
			$("#idData").append(strTd);
			$("#idData .tr:even").css('background-color','#f1f2f9');
		}
	});
	
	return false;}

});
});
