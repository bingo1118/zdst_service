
//弹出层显示方法
function show(){
	showDiv($('#popup_1'));
	showDiv($('#popup_mask'));//popup_mask1
	showDiv($('#popup_mask1'));
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
 var windowWidth =  document.body.offsetWidth;//网页可见区域宽   
 var windowHeight =  document.body.offsetHeight;//网页可见区域高   
 
 var popupHeight = $(obj).height();   
 var popupWidth = $(obj).width();    
 
 $(obj).css({   
	  "position": "absolute",   
	  "top": (windowHeight-popupHeight)/2+$(document).scrollTop(),   
	  "left": (windowWidth-popupWidth)/2  
	 });  
 
 if($("#popup_1").height()>$("#popup_mask1").height()){
	 //alert((windowWidth-popupWidth)/2);
	 $("#popup_mask1").css("overflow-y", "scroll");
	 $("#popup_1").css({   
		  "position": "absolute",   
		  "top": "0",   
		  "left": (windowWidth-$("#popup_1").width())/2  
		 }); 
 }
 
 
}

$(function(){

	$(".close_1").click(function(){
		$("#popup_mask,#popup_1,#popup_mask1").fadeOut();
	});
	
});