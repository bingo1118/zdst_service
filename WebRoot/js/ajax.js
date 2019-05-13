// 实时推送消息报警
setInterval(function(){
	baojing();
//    tableNum();
},18000);

var x=0;
function tableNum(){
	var trNum = $(".tip_alarm_r table tr.td").length;
	if(trNum>6){
		x = trNum-6;
		for(var i=0;i<=x;i++){
			$(".tip_alarm_r table tr.td:eq("+i+")").remove();		
		}
		console.log("------------------------------");
	}
}

//baojing();
function baojing(){
	$.ajax({
		type: "post",
		url: "json.json",
		async: true,
		success: function (data) {
			var tempStr="";
			var json = eval("("+data+")");
			for(var i=0;i<json.length;i++){
				tempStr += "<tr class='td'><td>"+json[i].equipment+"</td><td>"+json[i].time+"</td><td>"+json[i].type+"</td><td>"+json[i].address+"</td><td><a href='javascript:void(0);handle();' >处理</a></td></tr>"
			}
			$(".tip_alarm_r table").append(tempStr);
			console.log($(".tip_alarm_r table").html())
			tableNum();
			console.log($(".tip_alarm_r table").html())
		},
		error: function() {
			console.log("请求失败");
		}
	});
}