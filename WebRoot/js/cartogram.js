

$(function () {
	var deviceName="";
	var J_xl_1="";
	var J_xl_2="";
	var errorAlarmNum;//误报数量
	var alarmState;//报火警数量
	var normal;//正常工作数量
	var lossup;//掉线数量
	var faultNub;//故障数量 
	var devNameTitle;//标题
	
	//统计分析设备状态统计数据方法
	$("#analysisSearch").click(function(){
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
	 				circle(errorAlarmNum,alarmState,normal,lossup,faultNub,devNameTitle);
	 				histogram(errorAlarmNum,alarmState,normal,lossup,faultNub,devNameTitle);
	 			}
			},
			error:function(){
				console.log("请求失败");
			}
		})
	}
	
	listContractor();
});


function circle(errorAlarmNum,alarmState,normal,lossup,faultNub,devNameTitle) {
	
    // Create the chart
    Highcharts.chart('columnChart', {
		credits: {
			 enabled: false
		},
        chart: {
            type: 'column'
        },
        title: {
            text: '异常统计图'
        },
        xAxis: {
            categories: [
                
                '火警',
                '正常',
                '掉线',
                '故障'
            ],
            crosshair: true
        },
        yAxis: {
            min: 0,
            allowDecimals:false,
            title: {
                text: '异常次数 (次)'
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
            data: [ alarmState, normal, lossup, faultNub]
        }]
    });
}


function histogram(errorAlarmNum,alarmState,normal,lossup,faultNub,devNameTitle) {
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
            text: '声光报警百分比'
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
                
                ['火警',       alarmState],
                {
                    name: '正常',
                    y: normal,
                    sliced: true,
                    selected: true
                },
                ['掉线',    lossup],
                ['故障',     faultNub]
            ]
        }]
    });
}

