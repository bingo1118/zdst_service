// JavaScript Documentmenu

//弹出层显示方法
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
	$(".close_1").click(function(){
		$("#menu_dy1,#menu_dl1,#menu_ldl1,#menu_wd1,.popup_mask").fadeOut();
	});
	
});