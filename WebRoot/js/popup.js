// JavaScript Documentmenu
center($('#cartogram'));
center($('#popup'));
center($('#popup_mask'));
center($("#popup_chuli"));
center($('#popupWired'));
center($("#chuliBaojing"));
//加载报警数据
function get_alert(){
var urldo = "push.do";
var alert_data;
localStorage.removeItem('key');
	$.ajax({
		type : "GET",
		url : urldo,
		dataType : "json",
		async : false,
		success : function(data) {
			alert_data="";
			datas="";
			console.log("请求成功");
			datas = eval(data);
			console.log(datas);
			alert_data=datas;
		},
		error : function() {
			console.log("请求失败");
			
		}
	});
	alert_data=JSON.stringify(alert_data);
	localStorage.setItem("key",alert_data);   
	var getstr_data=localStorage.getItem("key");
	var get_data= eval(getstr_data);
}	

//弹出层显示方法
function show(){
	showDiv($('#popup_1'));
	showDiv($('#popup_mask'));
}
function showDiv(obj){
 $(obj).fadeIn();
 center(obj);
 $(window).scroll(function(){
  center(obj);
 });
 $(window).resize(function(){
  center(obj);
 }); 
}
//弹出层居中方法
function center(obj){
 var windowWidth = document.documentElement.clientWidth;//网页可见区域宽   
 var windowHeight = document.documentElement.clientHeight;//网页可见区域高   
 var popupHeight = $(obj).height();   
 var popupWidth = $(obj).width();    
 $(obj).css({   
  "position": "absolute",   
  "top": (windowHeight-popupHeight)/2+$(document).scrollTop(),   
  "left": (windowWidth-popupWidth)/2  
 });  
}
//遮罩层方法
function maskDiv(){
	
}

$(function(){
	var areaname=$('.sh li:first-child').html();
	$(".cartogram_close").click(function(){
		$("#cartogram").fadeOut();
	});
	$(".quxiao,.guanbi,.qd,.close_1,.look_close").click(function(){
		$("#popup,#popup_mask,#popup_chuli,#popup_1,#menu_dy1,#menu_dl1,#menu_ldl1,#menu_wd1").fadeOut();
	});
	
	$('.nav li').hover(function(){
		$(this).find('.xiala').toggle(300);
	});
	
//地区选择
	$('#click_l').not('.haves li').click(function(){
		$('.sh').toggle();
		$('.son').hide();
		preventBubble();
	});

$('.haves span').click(function(){
	$('.haves span').html('>');
	$('.haves span').not($(this)).removeClass('opening');
	if($(this).hasClass('opening')){
		$('#show_son').hide();
		$(this).html('>');
		$(this).removeClass('opening');
	}
	else{
	var con=$(this).nextAll('ul').html();
	$('#show_son').show();
	$('#show_son').html(con);
		 $(this).html('-');
		 $(this).addClass('opening');
	}	 

});

$(document).on("click",function(e)
	    { 
	        if($(e.target).parents(".sh").length == 0)
	        {
	            $(".sh").hide();
	            $('.son').hide();
	            $('.haves span').html('>');
	            $('.haves').removeClass('haves_open');
	        }
	});
$('.son').click(function(){event.stopPropagation();});
$(".sh li").not('.fc').not(".haves").click(function(){
	areaname=$(this).text();
	get_val=$(this).text()+'<img style="float:right; margin-top:11px;" src="images/arrow.png" />';
   $('#click_l').html(get_val);
    $('.son').hide();
	  $('.haves span').html('>');
    $('.sh').hide();
    console.log(areaname);
});

$(".haves p").click(function(){
	areaname=$(this).text();
	get_val=$(this).text()+'<img style="float:right; margin-top:11px;" src="images/arrow.png" />';
	$('#click_l').html(get_val);
    $('.son').hide();
    $('.haves span').html('>');
    $('.sh').hide();
    console.log(areaname);
});
});
function change_areaId(x){
	$('#areaId').val(x);
}
function change_son(e){
get_val=e+'<img style="float:right; margin-top:11px;" src="images/arrow.png" />';
$('#click_l').html(get_val);
$('.son').hide();
  $('.haves span').html('>');
$('.haves').removeClass('haves_open');
$('.sh').hide();
}
//表格內容过长隐藏
$(".sm_tr td").not('.not_sm').each(function(index,element){
	if(element.className=='time'){return true;}
	e= $(element);
	div_len=e.width()/12;
	font_num=Math.round(div_len);
	if(e.text().length>font_num){
	 e.text(e.text().substring(0,font_num-2) + '...'); }
	});
//阻止冒泡事件
function preventBubble(event){  
	  var e=arguments.callee.caller.arguments[0]||event; //若省略此句，下面的e改为event，IE运行可以，但是其他浏览器就不兼容  
	  if (e && e.stopPropagation) {  
	    e.stopPropagation();  
	  } else if (window.event) {  
	    window.event.cancelBubble = true;  
	  }  
	}   
$('.iicon').click(function(){
	$('.ii_detail').hide();
	$(this).children('.ii_detail').show();
});
$('.ii_close').click(function(){
	preventBubble();
	$('.ii_detail').css('display','none');
});