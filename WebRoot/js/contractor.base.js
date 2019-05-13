$(function(){
    $("#iframe_table").load(function(){
		var iframe_table_height =  $("#iframe_table").contents().find(".content_list").height(); 
		$("#iframe_table,.content_right",parent.document).css('height','550px');
    });
    var content_right_width =  $("body").width()-260;
    $("#iframe_table,.content_right").css('width',content_right_width);
});


if($(window).width()<=1200){
	content_right_width="978px";
		$("#iframe_table,.content_right").css('width',content_right_width);
}
$(window).resize(function() {
	var content_right_width =  $("body").width()-250;
	$("#iframe_table,.content_right").css('width',content_right_width);
	if($(window).width()<=1200){
		content_right_width="978px";
		$("#iframe_table,.content_right").css('width',content_right_width);
	}
});