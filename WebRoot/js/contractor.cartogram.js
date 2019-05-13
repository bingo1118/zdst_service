

$(function () {
    // Create the chart
	var deviceName="";
	var J_xl_1="";
	var J_xl_2="";
	/*var errorAlarmNum;//误报数量
	 * var normal;//正常工作数量
	*/	
	var alarmState;//报火警数量
	var lossup;//掉线数量
	var faultNub;//低电压 
	var devNameTitle;//标题
	//统计分析设备状态统计数据方法
	$("#searchtj").click(function(){
		deviceName = $("#deviceName").val();
		J_xl_1 = $("#J_xl_1").val();
		J_xl_2 = $("#J_xl_2").val();
		if(deviceName==""&&J_xl_1==""&&J_xl_2==""){
			alert("查询条件不能为空，请输入正确的关键字！");
			return false;
		}
		listContractor();
		return false;
	})
	function listContractor(){
	 	$.ajax({
	 		url:"selectContractcount.do?deviceName="+deviceName+"&&J_xl_1="+J_xl_1+"&&J_xl_2="+J_xl_2,
	 		type:"GET",
	 		dataType:"json",
	 		success:function(data){
	 			console.log("请求成功");
	 			var json = eval(data);
	 			if(json!=null){
	 				var strTd;
	 				for(var i = 0;i<json.length;i++){
	 					errorAlarmNum = json[i].errorAlarmNum;
	 					alarmState = json[i].alarmState;
	 					normal = json[i].normal;
	 					lossup = json[i].lossup;
	 					faultNub = json[i].faultNub;
	 					devNameTitle = json[i].devName;
	 				}
	 				CartogramChart	(alarmState,lossup,faultNub,devNameTitle);
	 			}
	 		},
	 		error:function(){
	 			console.log("请求失败");
	 		}
	 	});
	 
	 }
function CartogramChart	(alarmState,lossup,faultNub,devNameTitle){
	Highcharts.chart('columnChart', {
		credits: {
			 enabled: false
		},
        chart: {
            type: 'column'
        },
        title: {
            text: devNameTitle+"异常统计"
        },
        xAxis: {
            categories: [
                '火警',
                '掉线',
                '低电压'
            ],
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: '异常次数 (times)'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">次数: </td>' +
            '<td style="padding:0"><b>{point.y:.1f} 次</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
        	name: "异常统计",
            data: [ alarmState, lossup, faultNub]
        }]
    });
	$('#pieChart').highcharts({
		credits: {
			 enabled: false
		},
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: devNameTitle+'报警百分比'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: '百分比',
            data: [
                ['火警',   alarmState],
                ['掉线',   lossup],
                ['低电压',   faultNub],
            ]
        }]
    });
}
listContractor();
});

