var areaName = [];
var smokeNumber = [];
var lossSmokeNumber = [];
var onLineSmokeNumber =[];
var totalSmokeNumber;
var totalLossSmokeNumber;
var totalOnlineSmokeNumber;
var alarmCount = [];
var alarmCount193=[];

$(function () {
	var userid = $("#userid").val();
	listContractor();
//	foldline();
	//根据设备类型查询区域的在线，掉线，总数的情况
	function listContractor(){
		
		$.ajax({
			url:"getAnalysis.do",
			type:"get",
			dataType:"json",
			success:function(data){
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
				titleName="设备数量统计图:总数 "+totalSmokeNumber+"个 ,在线数: "+totalOnlineSmokeNumber+"个 ,掉线数: "+totalLossSmokeNumber+"个";
					circle(meta,titleName);
			}
			
		});
		
		/*pushMessageCompont.getAnalysis(userid,function(data){
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
			titleName="设备数量统计图:总数 "+totalSmokeNumber+"个 ,在线数: "+totalOnlineSmokeNumber+"个 ,掉线数: "+totalLossSmokeNumber+"个";
				circle(meta,titleName);
		});*/
							
	}
	
	//默认加载当前的区域来显示一年的报警数量（查的是火警）
	function foldline(){
		//查的火警
		var areaTitle = $("#areaId option:eq(0)").text()+"火警统计图";
		
		$.ajax({
			url:"searchAnalysisData1.do",
			type:"post",
			dataType:"json",
			success:function(data){
	 			var json = eval(data);
	 			if(json!=null){
	 				var json = eval(data);
					var titleName;
					alarmCount = json;
					titleName=title+"异常统计图 ";
	 			}
	 			foldline1(areaTitle);
			},
			error:function(){
				console.log("请求失败");
			}
		})	;	
		
		//查的是低电压（不带条件查询，即默认的加载）
		$.ajax({
			url:"searchAnalysisData2.do",
			type:"post",
			dataType:"json",
			success:function(data){
	 			var json = eval(data);
	 			if(json!=null){
	 				var json = eval(data);
					var titleName;
					alarmCount193 = json;
	 			}
	 			foldline1();
			},
			error:function(){
				console.log("请求失败");
			}
		})	;			
	}
	
	//带条件查询
	$("#theSecondButtonSearch").click(function(){
		var areaId = $("#areaId").val();

		var areaTitle = $("#areaId option:selected").text()+"报警统计图";		
		var year = $("#year").val();
		//ajax异步请求数据（火警数据）
		$.ajax({
			type:"post",
			url:"searchAnalysisByareaIdAndyear.do",
			data:{areaId:areaId,year:year},
			dataType:"json",
			success:function(data){
				var json = eval(data);
				alarmCount = json;
				foldline1(areaTitle);
			},
			error:function(){
				alert("网络连接失败");
			}
		});
		
		//ajax异步请求数据（低电压数据）
		$.ajax({
			type:"post",
			url:"searchAnalysisByareaIdAndyear1.do",
			data:{areaId:areaId,year:year},
			dataType:"json",
			success:function(data){
				var json = eval(data);
				alarmCount193 = json;
				foldline1(areaTitle);
			},
			error:function(){
				alert("网络连接失败");
			}
		});
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

function foldline1(areaId) {
    $('#container').highcharts({
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
            name: '烟雾',
            data:alarmCount
        }, {
            name: '低电压',
            data: alarmCount193
        }]
    });
};   
    