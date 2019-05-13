var areaName = [];
var smokeNumber = [];
var lossSmokeNumber = [];
var onLineSmokeNumber =[];
var totalSmokeNumber;
var totalLossSmokeNumber;
var totalOnlineSmokeNumber;
var alarmCount = [];
var alarmCount193=[];
var alarmCount43=[];
var alarmCount44=[];
var alarmCount45=[];
var alarmCount46=[];
var alarmCount47=[];
var alarmCount48=[];
var alarmCount36=[];
var showname=[];
showname[1]=['烟雾',"低电压"];
showname[2]=['燃气'];
showname[7]=['声光'];
showname[8]=['手报'];
showname[10]=['高升','降低'];
showname[5]=['过压','欠压','过流','漏电流','温度','合闸','故障'];
var titlename=[];
titlename[1]=titlename[7]=titlename[8]="报警统计图";
titlename[2]=titlename[5]=titlename[10]="探测统计图";

var title = $("title").text();
$(function () {
	listContractor();
	foldline();
	
	//根据设备类型查询区域的在线，掉线，总数的情况
	function listContractor(){
		$.ajax({
			url:"searchAnalysisData.do",
			type:"post",
			dataType:"json",
			success:function(data){
				console.log("请求成功");
	 			var json = eval(data);
	 			//alert(json);
	 			if(json!=null){
	 				var json = eval(data);
					var meta = new Array();
					var titleName;
					
					for(var i=0;i<json.length;i++){
						if(json[i] !=null){
							totalSmokeNumber=json[i].smokeNumber;
							totalLossSmokeNumber=json[i].lossNumber;
							totalOnlineSmokeNumber=json[i].onLineNumber;
							for(var j=0;j<json[i].list2.length;j++){
								areaName.push(json[i].list2[j].areaName);
								smokeNumber.push(json[i].list2[j].smokeNumber);
								lossSmokeNumber.push(json[i].list2[j].lossSmokeNumber);
								onLineSmokeNumber.push(json[i].list2[j].onLineSmokeNumber);
							}
						}	
					} 
					titleName=title+"设备数量统计图:总数 "+totalSmokeNumber+"个 ,在线数: "+totalOnlineSmokeNumber+"个 ,掉线数: "+totalLossSmokeNumber+"个";
	 				circle(meta,titleName);
	 			}
			},
			error:function(){
				console.log("请求失败");
			}
		})					
	}
	
	//默认加载当前的区域来显示一年的报警数量（查的是火警）
	function foldline(){
		//查的火警
		var areaTitle = $("#areaId option:selected").text();		
		var areaId = $("#areaId").val();
		var y=$("#year").val();
		if(y==""||y==null){
		    var date=new Date;
		    y=date.getFullYear(); 
		 }
		$.ajax({
			type:"post",
			url:"searchAnalysisByareaIdAndyearlg.do",
			data:{areaId:areaId,year:y},
			dataType:"json",
			success:function(data){				
				var json = eval(data);
//				alarmCount = json;
				areaTitle+=titlename[json[0][0]];
		switch(json[0][0]){
		case 1:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==202){
					 alarmCount = json[i].slice(2);
				 }
				 if(json[i][1]==193){alarmCount193 = json[i].slice(2);}
			 }
			 foldline2(areaTitle,json[0][0]);
			break; 
		case 10:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==217){
					 alarmCount = json[i].slice(2);
				 }
				 if(json[i][1]==210){alarmCount193 = json[i].slice(2);}
			 }
			 foldline2(areaTitle,json[0][0]);
			
			break;
		case 2:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==202){
					 alarmCount = json[i].slice(2);
				 }
			 }
			 foldline1(areaTitle,json[0][0]);
			break;
		case 7:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==203){
					 alarmCount = json[i].slice(2);
				 }
			 }
			 foldline1(areaTitle,json[0][0]);
			break;
		case 8:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==202){
					 alarmCount = json[i].slice(2);
				 }
			 }
			 foldline1(areaTitle,json[0][0]);
			break;
		case 5:
			 for(i=0;i<json.length;i++){
				 if(json[i][1]==43){
					alarmCount43 = json[i].slice(2);
				 }
				 if(json[i][1]==44){
						alarmCount44 = json[i].slice(2);
					 }
				 if(json[i][1]==45){
						alarmCount45 = json[i].slice(2);
					 }
				 if(json[i][1]==46){
						alarmCount46 = json[i].slice(2);
					 }
				 if(json[i][1]==47){
						alarmCount47 = json[i].slice(2);
					 }
				 if(json[i][1]==48){
						alarmCount48 = json[i].slice(2);
					 }
				 if(json[i][1]==36){
						alarmCount36 = json[i].slice(2);
					 }
			 }
			 foldline7(areaTitle,json[0][0]);
			break;
		}
			 
		
				console.log(json);
				
			},
			error:function(){
				alert("网络连接失败");
			}
		});
//		$.ajax({
//			url:"searchAnalysisData1.do",
//			type:"post",
//			dataType:"json",
//			success:function(data){
//	 			var json = eval(data);
//	 			if(json!=null){
//	 				var json = eval(data);
//					var titleName;
//					alarmCount = json;
//					titleName=title+"异常统计图 ";
//	 			}
//	 			foldline1(areaTitle);
//			},
//			error:function(){
//				console.log("请求失败");
//			}
//		})	;	
		
		//查的是低电压（不带条件查询，即默认的加载）
//		$.ajax({
//			url:"searchAnalysisData2.do",
//			type:"post",
//			dataType:"json",
//			success:function(data){
//	 			var json = eval(data);
//	 			if(json!=null){
//	 				var json = eval(data);
//					var titleName;
//					alarmCount193 = json;
//	 			}
//	 			foldline1();
//			},
//			error:function(){
//				console.log("请求失败");
//			}
//		})	;			
	}
	
	//带条件查询
	$("#theSecondButtonSearch").click(function(){
		foldline();
//		var areaId = $("#areaId").val();
//
//		var areaTitle = $("#areaId option:selected").text()+"报警统计图";		
//		var year = $("#year").val();
//		//ajax异步请求数据（火警数据）
//		$.ajax({
//			type:"post",
//			url:"searchAnalysisByareaIdAndyearlg.do",
//			data:{areaId:areaId,year:year},
//			dataType:"json",
//			success:function(data){
//				var json = eval(data);
//				alarmCount = json;
//				foldline2(areaTitle);
//			},
//			error:function(){
//				alert("网络连接失败");
//			}
//		});
//		
	});
});


function circle(meta,titleName) {
	
    // Create the chart
    Highcharts.chart('columnChart', {
		credits: {
			 enabled: false
		},
        chart: {type: 'column' },
        title: { //标题
        	text: titleName
        },
        xAxis: {  //x轴
            categories: areaName,
            crosshair: true //十字准线，瞄准线
        },
        yAxis: {  //y轴
            min: 0,
            allowDecimals:false,
            title: {
                text: "设备数量（个）"
            }
        },
        
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.1f} 个</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },       
        series: [
            {
        	    name: "设备总数",
                data: smokeNumber
            },
            
            {
            	name:"掉线数量",
            	data:lossSmokeNumber
            },
            {
            	name:"在线数量",
            	data:onLineSmokeNumber
            }
        ]
    }); 
}
//连条2直线的
function foldline2(areaId,devId) {
    $('#container').highcharts({
    	credits: {
			 enabled: false
		},
        chart: {
            type: 'line'
        },
        title: {
            text: areaId
        },        
        xAxis: {
            categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
        },
        yAxis: {
            title: {
                text: '设备报警次数（次）'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true          // 开启数据标签
                },
                enableMouseTracking: false // 关闭鼠标跟踪，对应的提示框、点击事件会失效
            }
        },
        series: [{
            name: showname[devId][0],
            data:alarmCount
        }, {
            name: showname[devId][1],
            data: alarmCount193
        }]
    });
};   
//连一条直线
function foldline1(areaId,devId) {
    $('#container').highcharts({
    	credits: {
			 enabled: false
		},
        chart: {
            type: 'line'
        },
        title: {
            text: areaId
        },        
        xAxis: {
            categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
        },
        yAxis: {
            title: {
                text: '设备报警次数（次）'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true          // 开启数据标签
                },
                enableMouseTracking: false // 关闭鼠标跟踪，对应的提示框、点击事件会失效
            }
        },
        series: [{
            name: showname[devId][0],
            data:alarmCount
        }]
    });
};   
//连7条直线
function foldline7(areaId,devId) {
    $('#container').highcharts({
    	credits: {
			 enabled: false
		},
        chart: {
            type: 'line'
        },
        title: {
            text: areaId
        },        
        xAxis: {
            categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
        },
        yAxis: {
            title: {
                text: '设备报警次数（次）'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true          // 开启数据标签
                },
                enableMouseTracking: true // 关闭鼠标跟踪，对应的提示框、点击事件会失效
            }
        },
        series: [{
            name: showname[devId][0],
            data:alarmCount43
        },{
            name: showname[devId][1],
            data:alarmCount44
        },{
            name: showname[devId][2],
            data:alarmCount45
        },{
            name: showname[devId][3],
            data:[0,0,0,0,0,0,0,0,0,445,0,0]
        },{
            name: showname[devId][4],
            data:alarmCount47
        },{
            name: showname[devId][5],
            data:alarmCount48
        },{
            name: showname[devId][6],
            data:alarmCount36
        }],

    });
}; 
    