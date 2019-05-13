
    var areaName = [];
    var smokeNumber = [];
    var lossSmokeNumber = [];
    var onLineSmokeNumber =[];
    var totalSmokeNumber;
    var totalLossSmokeNumber;
    var totalOnlineSmokeNumber;
    var alarmCount = [];
    var alarmCount193=[];
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
    	
    	//默认加载当前的区域来显示一年的报警数量（查的是故障）
    	function foldline(){
    		//不带条件查询，即默认的加载，故障
    		var areaTitle = $("#areaId option:eq(0)").text()+"";
    		
    		$.ajax({
    			url:"searchAnalysisData36.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount36 = json;
    					titleName=title+"故障统计图 ";
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		})	;	
    		
    		//不带条件查询，即默认的加载，过压
    		$.ajax({
    			url:"searchAnalysisData43.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount43 = json;
    					titleName=title+"";
    	 			}
    	 			foldline1();
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		//不带条件查询，即默认的加载，欠压
    		$.ajax({
    			url:"searchAnalysisData44.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount44 = json;
    					titleName=title+"异常统计图 ";
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认加载时，过流
    		$.ajax({
    			url:"searchAnalysisData45.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount45 = json;
    					titleName=title+"异常统计图 ";
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认的加载，漏电流报警
    		$.ajax({
    			url:"searchAnalysisData46.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount46 = json;
    					titleName=title+"异常统计图 ";
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认的是温度
    		$.ajax({
    			url:"searchAnalysisData47.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount47 = json;
    					titleName=title+"异常统计图 ";
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		/** **********************************下面各个的平均值************************************************** */
    		//不带条件查询，即默认的加载，过压的平均值
    		$.ajax({
    			url:"searchAnalysisData43and44avg.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    	 				//alert(eval(json));
    					var titleName ="电压的平均值";
    					alarmCount43and44avg = json;
    	 			}
    	 			foldline1(titleName);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认加载时，过流的平均值
    		$.ajax({
    			url:"searchAnalysisData45avg.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount45avg = json;  					
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认的加载，漏电流的平均值
    		$.ajax({
    			url:"searchAnalysisData46avg.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount46avg = json;
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		//不带条件查询，即默认的是温度的平均值
    		$.ajax({
    			url:"searchAnalysisData47avg.do",
    			type:"post",
    			dataType:"json",
    			success:function(data){
    	 			var json = eval(data);
    	 			if(json!=null){
    	 				var json = eval(data);
    					var titleName;
    					alarmCount47avg = json;
    	 			}
    	 			foldline1(areaTitle);
    			},
    			error:function(){
    				console.log("请求失败");
    			}
    		});	
    		
    		
    	}
    	
    	//带条件查询
    	$("#theSecondButtonSearch").click(function(){
    		var areaId = $("#areaId").val();

    		var areaTitle = $("#areaId option:selected").text();		
    		var year = $("#year").val();
    		//ajax异步请求数据（故障数据）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear36.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount36 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据过压
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear43.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount43 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据，欠压数据
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear44.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount44 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据（过流数据）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear45.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount45 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据，漏电流数据
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear46.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount46 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据（温度数据）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear47.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount47 = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据 ,下面的是平均值的点击按钮查询
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear43and44avg.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount43and44avg = json;
    				foldline1(areaTitle);
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据（电流测试）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear45avg.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount45avg = json;
    				foldline1(areaTitle );
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		//ajax异步请求数据（低电压数据）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear46avg.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount46avg = json;
    				foldline1(areaTitle );
    			},
    			error:function(){
    				alert("网络连接失败");
    			}
    		});
    		
    		
    		//ajax异步请求数据（低电压数据）
    		$.ajax({
    			type:"post",
    			url:"searchAnalysisByareaIdAndyear47avg.do",
    			data:{areaId:areaId,year:year},
    			dataType:"json",
    			success:function(data){
    				var json = eval(data);
    				alarmCount47avg = json;
    				foldline1(areaTitle );
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
                text: areaId+"设备报警次数"
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
                        enabled: false          // 开启数据标签
                    },
                    enableMouseTracking: true // 关闭鼠标跟踪，对应的提示框、点击事件会失效
                }
            },
            series: [{
                name: '故障',
                data:alarmCount36
            }, {
                name: '过压报警',
                data: alarmCount43
            },{
                name: '欠压报警',
                data: alarmCount44
            },{
                name: '过流报警',
                data: alarmCount45
            },{
                name: '漏电流',
                data: alarmCount46
            },{
                name: '温度',
                data: alarmCount47
            }
            
            
            ]
        });
        
        //电压的平均值
        $('#Voltage').highcharts({
        	 chart: {
                 type: 'line'
             },
             title: {
                 text: areaId+"电压的平均值"
             },        
             xAxis: {
                 categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
             },
             yAxis: {
                 title: {
                     text: '电压平均值(V)'
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
                 name: '电压平均值:V',
                 data:alarmCount43and44avg
             }
             ]
        	
        });
        
      //电流的平均值
        $('#Current').highcharts({
        	 chart: {
                 type: 'line'
             },
             title: {
                 text: areaId+"电流的平均值"
             },        
             xAxis: {
                 categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
             },
             yAxis: {
                 title: {
                     text: '电流值：A'
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
                 name: '电流值:A',
                 data:alarmCount45avg
             }
             ]
        	
        });
        
      //漏电流的平均值
        $('#Leakagecurrent').highcharts({
        	 chart: {
                 type: 'line'
             },
             title: {
                 text: areaId+"漏电流的平均值"
             },        
             xAxis: {
                 categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
             },
             yAxis: {
                 title: {
                     text: '漏电流值:mA'
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
                 name: '漏电流平均值:mA',
                 data:alarmCount46avg
             }]
        	
        });
        
      //温度的平均值
        $('#Temperature').highcharts({
        	 chart: {
                 type: 'line'
             },
             title: {
                 text: areaId+"温度的平均值"
             },        
             xAxis: {
                 categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
             },
             yAxis: {
                 title: {
                     text: '温度值:℃'
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
                 name: '温度平均值：℃',
                 data:alarmCount47avg
             }]
        	
        });
        
        
        
    };   
