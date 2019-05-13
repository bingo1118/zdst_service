// JavaScript Document
$(function(){
	$(".table-responsive .delete").click(function(){
		$(this).parents("tr").remove();
	});
});