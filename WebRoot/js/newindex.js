var color=['#7cb5ec','#38e4f3'];
var marker_icon = ["images/marker.png","images/marker02.png","images/marker01.png"];
$(document).ready(function() {  
	var h=$(window).height();
	var h_chart=(h)/3-15;
	h_chart=h_chart;
	$('#allmap').css('height',h);
	$('.chart .setheight').css('height',h_chart);
	$('.chart .lunb3').css('height',h_chart-40);
	/*get_data();*/
		$('.body_loading').hide();
		$('#container').show();
		//柱状图	
		$.ajax({
				url: "getFireStatistic.do ",
				dataType: "json",
				type: "GET",
				success: function(data){
					var get_data=data;
					var show_data=[];
					
					x_name=[];//所有设备名称
					y_y=[];//报警类型和报警数
					var get_elemt=[];//获取下标
					var alert_type=[];//报警类型
					var type_data=[];
					var full_data=[];
					for(i=0;i<get_data.length;i++){
						if(JSON.stringify(get_data[i].alarmMap)!="{}"){
							 full_data.push(get_data[i]);
						}
					}
					//获取所有设备名
					for(i=0;i<full_data.length;i++){
						x_name[i]=full_data[i].deviceName;
						y_y[i]=full_data[i].alarmMap;
						get_elemt[i]=Object.keys(y_y[i]);            
					}
					//获取所有报警类型
					for(i=0;i<get_elemt.length;i++){
						get_type=get_elemt[i];
						for(x=0;x<get_elemt[i].length;x++){
							if(!isInArray(alert_type,get_elemt[i][x])){
								alert_type.push(get_elemt[i][x]);
							}
						}
					}
					for(i=0;i<alert_type.length;i++){
						type_data[i]=[];
					}
				   for(i=0;i<y_y.length;i++){
					   for(var x=0;x<alert_type.length;x++){
						  var my_type=alert_type[x];
						  if(y_y[i][my_type]!=undefined){
							  type_data[x][i]=y_y[i][my_type];
						  }
						  else{ type_data[x][i]="";}
					   }
				   }
				   for(i=0;i<alert_type.length;i++){
					   show_data[i]=new Object();
					   show_data[i].name=alert_type[i];
					   show_data[i].data=type_data[i];
					   if(i<2){
					   show_data[i].color=color[i];
					   }
				}
				    var chart = new Highcharts.Chart({
				        chart: {
				            renderTo: 'chart1',
				            type: 'column',
				            backgroundColor: 'rgba(255,255,255,0)',
				            borderColor:"#F00",
				        },
				        credits: {
				            enabled: false
				       },
				        title: {
				            text: '各类设备报警情况',
				            style:{color: '#fff' ,fontSize: '18px'}
				        },
				        legend: {
				        	  // 图例项样式
				        	  itemStyle: {
				        	    color: '#fff'
				        	  },
				        	  // 隐藏的图例项样式
				        	  itemHiddenStyle: {
				        	    color: '#666'
				        	  },
				        	  borderwidth: 0,
				        	},
				        plotOptions: {
				            column: {
				                depth: 25,
				                dataLabels: {
				                    inside: true
				                },
				            },
				            series: {
				                shadow: true,
				                borderColor: '#303030',
				                minPointLength: 5
				            },
				        },
				        xAxis: {
				            lineWidth: 1,
				            lineColor: "#fff",
				            tickWidth: 0,
				            categories: x_name,
				            labels: {
				                y: 20, //x轴刻度往下移动20px
				                style: {
				                    color: '#fff',//颜色
				                    fontSize:'12px'  //字体
				                }
				            },
				        },
				        yAxis: {
				            lineWidth: 0,
				            lineColor: "#38e4f3",
				            tickWidth: 0,
				            max:1000,
				            min:100,
				            gridLineWidth: 0,
				            title: {  
				                text: '次数' ,
				                style: {
				                    color: '#aaa',//颜色
				                    fontSize:'14px'  //字体
				                }
				            }, 
				            labels: {
				                y: 20, //x轴刻度往下移动20px
				                style: {
				                    color: '#fff',//颜色
				                    fontSize:'14px'  //字体
				                }
				            },
				        },
				        series: show_data
				    });
				    
				},
				error: function() {
					console.log("请求失败+++");
				}
			});
	//饼状图
		$.ajax({
			url: "getAreaStatistic.do",
			dataType: "json",
			type: "GET",
			success: function(data){
				var area=data;
				var get_area=[];
				totle_mac=0;//所有设备总数
			    show_area=[];
				for(i=0;i<area.length;i++){
			       		if(area[i].cv.macNum!=0){
			       			get_area.push(area[i]);
			       			totle_mac+=area[i].cv.macNum;
			       			show_area.push(area[i].areaName);
			       		}
				}
				   var data=[];
				   var colors = ['#222','#3181bb','#2a918f','#1e7632','#57549f'];
				   var categories = show_area;//区域名字
				   var color_num=0;
				   for(i=0;i<get_area.length;i++){
					  data[i]=new Object();
					  my_data=get_area[i].cv.macNum/totle_mac;
					  my_data=my_data.toFixed(2)*100;
					  my_alert=get_area[i].cv.ifDealNum/get_area[i].cv.macNum;
					  my_net=get_area[i].cv.netStaterNum/get_area[i].cv.macNum;
					  my_nonet=get_area[i].cv.noNetStater/get_area[i].cv.macNum;
					  my_alert=my_alert*my_data;
					  my_net=my_net*my_data;
					  my_nonet=my_nonet*my_data;
					 show_net=parseFloat(my_net.toFixed(2));
					 show_alert=parseFloat(my_alert.toFixed(2));
					 show_nonet=parseFloat(my_nonet.toFixed(2));
					   data[i].y=my_data;
					   data[i].aid=get_area[i].areaId;
					   data[i].drilldown={
							   name:get_area[i].areaName,
							   categories:[get_area[i].areaName+'--在线', get_area[i].areaName+'--离线', get_area[i].areaName+'--报警'],
							   data:[show_net,show_nonet,show_alert]
					   };
					  if(color_num%5==0){
						 color_num=0;
					  }
					  data[i].color=colors[color_num];
					  color_num++;
				   }
			var browserData = [];
			var versionsData = [];
			var i, j;
			var dataLen = data.length;
			var drillDataLen;
			var brightness;


			// Build the data arrays
			for (i = 0; i < dataLen; i += 1) {
			  // add browser data
			  browserData.push({
			     name: categories[i],
			     y: data[i].y,
			     color: data[i].color,
			     a_id:data[i].aid,
			     
			  });

			  // add version data
			  drillDataLen = data[i].drilldown.data.length;
			  for (j = 0; j < drillDataLen; j += 1) {
				  var pd_color=data[i].drilldown.categories[j].substr(-2);
				  var show_color="";
				  if(pd_color=="在线"){
					  show_color='#3cb758';
				  }
				  else if(pd_color=="报警"){
					  show_color="#e53a0c";
				  }
				  else{show_color='#979797';}
			     brightness = 0.2 - (j / drillDataLen) / 5;
			     versionsData.push({
			        name: data[i].drilldown.categories[j],
			        y: data[i].drilldown.data[j],
			        borderWidth:'2px',
			        color: show_color,
			        area_id:data[i].aid,
			     });
			  }
			}

			var chart = {
			  type: 'pie',
			  backgroundColor:'rgba(0,0,0,0)',
			};
			var credits = {
			    enabled: false
			};
			var title = {
			  text: '地区设备分布情况',
			  style:{color: '#fff'}
			};      
			var yAxis = {
			  title: {
			     text: 'Total percent market share'
			  },
			  style:{color: '#fff'}
			};
			var tooltip = {
			  valueSuffix: '%'
			};
			var plotOptions = {
			  pie: {
			     shadow: true,
			     center: ['50%', '50%']
			  }
			};
			var series= [{
			  name: '区域',
			        data: browserData,
			        size: '60%',
			        dataLabels: {
			            formatter: function () {
			                return this.y > 5 ? this.point.name : null;
			            },
			            color: 'white',
			            distance: -30
			        },
			        events: { 
			            click: function(e) { 
			              show_place(e.point.a_id);
			            }},
			    }, {
			        name: '状态',
			        data: versionsData,
			        size: '80%',
			        innerSize: '60%',
			        dataLabels: {
			            formatter: function () {
			                // display only if larger than 1
			                return this.y > 1 ? '' + this.point.name + ': ' + this.y + '%'  : null;
			            },
			            color: 'white',
			        },
			    events: { 
			        click: function(e) { 
			         show_state(e.point.area_id,e.point.name);
			        }},
			    }   
			];     
			var json = {};   
			json.chart = chart; 
			json. credits=credits
			json.title = title; 
			json.yAxis = yAxis;        
			json.tooltip = tooltip;  
			json.series = series;
			json.plotOptions = plotOptions;
			$('#chart2').highcharts(json);  
			},
			error: function() {
				console.log("请求失败+++");
			}
		});
		$.ajax({
			url: "getElectricStatistic.do",
			dataType: "json",
			type: "GET",
			success: function(data){
				data=eval(data);
				var today=data;
				data_yesterday=[];
				data_today=[];
				for(i=0;i<today.length;i++){
					data_today[i]=today[i];
				}
				    $('#chart3').highcharts({
				        chart: {
				        	  backgroundColor: 'rgba(0,0,0,0)',
				            type: 'line'
				        },
				        credits: {
				            enabled: false
				       },
				        xAxis: {
				        	 title: {  
				 	            text: '时间' ,
				 	           style: {
				                    color: '#aaa',//颜色
				                    fontSize:'14px'  //字体
				                }
				 	        }, 
				            categories: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11','12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22','23'],
				            labels: {      
				                style: {
				                    color: '#fff',//颜色
				                    fontSize:'14px'  //字体
				                },	       
				            },
				        },
				        yAxis: {  
				        	title: {  
				                text: '次数' ,
				                style: {
				                    color: '#aaa',//颜色
				                    fontSize:'14px'  //字体
				                }
				            }, 
				        	labels: {
				               //x轴刻度往下移动20px
				                style: {
				                    color: '#fff',//颜色
				                    fontSize:'14px'  //字体
				                }
				            }
				        },
				        title: {
				            text: '电气24小时报警情况',
				            style:{color: '#fff',fontSize: '18px'}
				        },
				        tooltip: {
				            formatter: function() {
				                return '<b>'+ this.series.name +'</b><br/>'+
				                    this.x +': '+ this.y;
				            }
				        },
				        legend: {
				  		  layout: 'horizontal',
				  		  align: 'center',
				  		  verticalAlign: 'bottom',
				  		  color:'#fff',
				  		 itemStyle: {
				        	    color: '#fff'
				        	  },
				        	  // 隐藏的图例项样式
				        	  itemHiddenStyle: {
				        	    color: '#666'
				        	  },
				  		},
				        plotOptions: {
				        },
				    	series: [{
				  		  name: '今天',
				  		  data: data_today
				  		}],
				    });
			},
			error: function() {
				console.log("请求失败+++");
			}
		});
//水设备表格
		$.ajax({
			url: "getWaterStatistic.do",
			dataType: "json",
			type: "GET",
			success: function(data){
				var water=eval(data);
				water_totle=[];
				var all_water_alert=0;
				var all_water_net=0;
				var all_water_nonet=0;
				var all_water_totle=0;//所有水设备总数
				var water_str="";
				for(i=0;i<water.length;i++){
					water_totle[i]=new Object();
					water_totle[i].name=water[i].deviceName;//设备名
					water_totle[i].num=water[i].deviceNum;//设备总数
					water_totle[i].alert=water[i].cv.ifDealNum;//设备报警数
					water_totle[i].net=water[i].cv.netStaterNum;//设置在线数
					water_totle[i].nonet=water[i].cv.noNetStater;//设置离线数
				water_str+='<div class="w_detail"><dd class="mec_name">'+water_totle[i].name+'</dd><dd class="mec_num">'+water[i].deviceNum+'</dd><div class="clear"></div></div>';
				all_water_totle+=water_totle[i].num;
				all_water_alert+=water_totle[i].alert;
				all_water_net+=water_totle[i].net;
				all_water_nonet+=water_totle[i].nonet;
				}
				ider=all_water_alert+all_water_nonet;
			    state_str='<div class="w_detail"><dd class="mec_name">在线</dd><dd class="mec_num">'+all_water_net+'</dd><div class="clear"></div></div>';
			    state_str+='<div class="w_detail"><dd class="mec_name">异常</dd><dd class="mec_num">'+ider+'</dd><div class="clear"></div></div>';
			   // state_str+='<div class="w_detail"><dd class="mec_name">报警</dd><dd class="mec_num">'+all_water_alert+'</dd><div class="clear"></div></div>';
				$('#watert_type').html(water_str);
				$('#water_state').html(state_str);
				all_water_totle = all_water_totle.toString();
				all_water_alert = all_water_alert.toString();
				totle_num=alert_num="";
				for(i=0;i<5;i++){
					zo=5-all_water_totle.length;
					if(i==zo){
						for(x=0;x<all_water_totle.length;x++){
							totle_num+="<span>"+all_water_totle[x]+"</span>";
						}
						break;
					}
					else{
						totle_num+="<span>0</span>";
					}	
				}
				for(i=0;i<5;i++){
					zo=5-all_water_alert.length;
					if(i==zo){
						for(x=0;x<all_water_alert.length;x++){
							alert_num+="<span>"+all_water_alert[x]+"</span>";
						}
						break;
					}
					else{
						alert_num+="<span>0</span>";
					}	
				}
				totle_num+='<div class="clear"></div>';
				alert_num+='<div class="clear"></div>';
				$('.w_totle').html(totle_num);
				$('.w_alert').html(alert_num);
			},
			error: function() {
				console.log("请求失败+++");
			}
		});
		map_init();
//加载报警信息		
		var urldo = "push.do";
		var alert_data;
		localStorage.removeItem('key');
		$.ajax({
				type : "GET",
				url : urldo,
				dataType : "json",
				async : false,
				beforeSend: function(XMLHttpRequest){
					$("#index_loading").show();   //调用本次ajax请求时传递的options参数
		        },
		        complete: function(XMLHttpRequest){
		        	$("#index_loading").hide();   //调用本次ajax请求时传递的options参数
		        },
				success : function(data) {
					alert_data="";
					datas="";
					console.log("请求成功");
					datas = eval(data);
					alert_data=datas;
					alert_data=JSON.stringify(alert_data);
					localStorage.setItem("key",alert_data);   
					var getstr_data=localStorage.getItem("key");
					var get_data= eval(getstr_data);
					show_alert2(get_data);
				},
				error : function() {
					console.log("请求失败");
					
				}
			});
		
	
	//改变窗口大小函数
	$(window).resize(function() {
		var h=$(window).height();
		var h_chart=(h)/3-15;
		h_chart=h_chart;
		$('#allmap').css('height',h);
		$('.chart li').css('height',h_chart);
		$('.chart').css('height',h);
	});
	//关闭有线主机报警
	$(".guanbiWired").click(function() {
		$("#popupWired,#popup_mask").hide();
	});
});
function isInArray(arr,value){
    for(var i = 0; i < arr.length; i++){
        if(value === arr[i]){
            return true;
        }
    }
    return false;
}
//地图JS
var markerArr;
var map;
function map_init(){
        // 创建地图实例  
	map = new BMap.Map("allmap",{mapType: BMAP_HYBRID_MAP,minZoom:5}); 
	map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);//显示全中国
    // 向地图中添加缩放控件
	var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_LEFT,type:BMAP_NAVIGATION_CONTROL_LARGE});
	//map.addControl(ctrl_nav);
    // 向地图中添加缩略图控件
	var ctrl_ove = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:1});
	//map.addControl(ctrl_ove);
    // 向地图中添加比例尺控件
	var ctrl_sca = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
	//map.addControl(ctrl_sca);
	//切换地图类型的控件  
	/*var opts = {offset: new BMap.Size(150, 5)}
	 var map_type = new BMap.MapTypeControl();  */
	/*    map.addControl(map_type); */ 
	/*	map.addControl(map_type,new BMap.ScaleControl(opts));*/
    map.enableDragging();// 启用地图拖拽事件，默认启用(可不写)
    map.enableScrollWheelZoom();// 启用地图滚轮放大缩小
    map.enableDoubleClickZoom();// 启用鼠标双击放大，默认启用(可不写)
    map.enableKeyboard();// 启用键盘上下左右键移动地图 

}
//点击旭日图改变地图函数
var markers=[];
function show_place(d){
	$("#index_loading").show();
	comName="";
	deviceId=0;  
	addTilesLayer(); 
$.ajax({
		url: "searchMapSmoke.do?&&deviceId="+deviceId+"&&com="+encodeURI(encodeURI(comName)),
		data:"areaId="+d,
		dataType: "json",
		type: "GET",
		success: function(data){
			if(data!=null){
			json = eval(data);	
			console.log(json);
			var point = new BMap.Point(json[0].longitude,json[0].latitude);  
			map.centerAndZoom(point, 19);
			for(var i=0;i<json.length;i++){
				addMarker(json[i],i);
			}	
			$('#chart_detail').show();
			place_detail(json);
			 markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});    
			}
			$("#index_loading").hide();
			$('#chart2').hide();
			/*$('#chart_detail').show();*/
		},
		error: function() {
			console.log("请求失败+++");
		}
	});
}
//点击里面的旭日图,改变旭日图
function place_detail(get_data){
	var totle_mac=get_data.length;//所有设备总数
   var outline=0;var alert_mac=0; var online=0;
	for(i=0;i<totle_mac;i++){
       		if(get_data[i].placeType=='离线'){
       			outline++;
       		 console.log(outline);
       		}
       		else{
       			if(get_data[i].ifAlarm==0){
       				alert_mac++;
       			}
       			else{
       				online++;
       			}
       		}
	}
	   var data=[];
	   var colors = ['#222','#3181bb','#2a918f','#1e7632','#57549f'];
	   var categories = show_area;//区域名字
	   var color_num=0;
		     my_net=online/totle_mac;
			 my_net=my_net*100;
			 my_alert=alert_mac/totle_mac;
			 my_alert=my_alert*100;
			 my_nonet=outline/totle_mac;
			 my_nonet=my_nonet*100;
			 show_net=parseFloat(my_net.toFixed(2));
			 show_alert=parseFloat(my_alert.toFixed(2));
			 show_nonet=parseFloat(my_nonet.toFixed(2));
			 $('#chart_detail').highcharts({
	        chart: {
	        	backgroundColor: 'rgba(0,0,0,0)',
	            plotBackgroundColor: null,
	            plotBorderWidth: null,
	            plotShadow: false,
	            spacing : [100, 0 , 40, 0]
	        },
	        title: {
	            floating:true,
	            useHTML:true,
	            text: '<div>'+get_data[0].areaName+'设备分布情况<div><div style="text-align: center; color:#eee;"><a onclick="back();" style="color:#fff;">返回</a></div>',
	            style:{color: '#fff',fontSize: '14px', cursor: 'pointer'},
	            },
	        tooltip: {
	            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        plotOptions: {
	            pie: {
	                allowPointSelect: true,
	                cursor: 'pointer',
	                dataLabels: {
	                    enabled: true,
	                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                    style: {
	                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                    }
	                },
	            }
	        },
	        series: [{
	            type: 'pie',
	            innerSize: '80%',
	            name: '设备数占比',
	            data: [
	                {name:'在线',   y: show_net, url : 'http://bbs.hcharts.cn'},
	                {name:'离线',   y: show_nonet, url : 'http://bbs.hcharts.cn'},
	                {name:'报警',   y: show_alert, url : 'http://bbs.hcharts.cn'},
	            ]
	        }]
	    }, function(c) {
	        // 环形图圆心
	        var centerY = c.series[0].center[1],
	            titleHeight = parseInt(c.title.styles.fontSize)+10;
	        c.setTitle({
	        	useHTML:true,
	            y:centerY + titleHeight/2,
	        });
	      
	        chart = c;
	    });
	
}
function back(){
	 $('#chart_detail').hide();
	 $('#chart2').show();
	 map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);//显示全中国
	 map.clearOverlays();
}
//点击外面的旭日图改变地图函数
function show_state(aid,state){
	$("#index_loading").show();  
	var area_id=aid;
	var state_id=0;
	area_state=state.substr(-2);
	if(area_state=="离线"){state_id=0;}
	if(area_state=="在线"){state_id=1;}
	if(area_state=="报警"){state_id=2;}
	$.ajax({
		url: "getSmokeList.do?areaId="+area_id+"&state="+state_id,
		dataType: "json",
		type: "GET",
		success: function(data){
			json = eval(data);	
			var point = new BMap.Point(json[0].longitude,json[0].latitude);  
			map.centerAndZoom(point, 19);
			for(var i=0;i<json.length;i++){
				addMarker(json[i],i);
			}	
			 markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});
			 $("#index_loading").hide();  
		},
		error: function() {
			console.log("请求失败+++");
		}
	});
}
var getbox=null;
//普通marker
function addMarker(json,j){  // 创建图标对象   
	point = new BMap.Point(json.longitude,json.latitude);
	var content="";
	var iconImg=creaticon(0);
	if(json.placeType=="离线"){
		iconImg = creaticon(1);
 content = "<p style='margin-left:10px;'><span style='color: #e06e0b; font-size:18px; font-weight:bold;'>设备："
	+json.placeeAddress+"（"+json.mac+"）"+"</span><span class='type'style='background:#999;'>"
	+json.placeType+"</span><br/>所属区域："+json.areaName+"<br/>地址："+json.address+"<br/></p><table><tr><td>负责人1："
	+json.principal1+"<br/>电话："+json.principal1Phone+"</td><td>负责人2："+json.principal2+"<br/>电话："
	+json.principal2Phone+"</td></tr></table><p class='winbtn'></p>";
	}else if(json.placeType=="在线"){		
		if(json.ifAlarm=="0"){
			iconImg = creaticon(2);
			 content = "<p style='margin-left:10px;'><span style='color: #e06e0b;font-size:18px; font-weight:bold;'>设备："
					+json.placeeAddress+"（"+json.mac+"）"+"</span><span class='type' style='background:red'>"
					+"报警</span><br/>所属区域："+json.areaName+"<br/>地址："+json.address+"<br/></p><table><tr><td>负责人1："
					+json.principal1+"<br/>电话："+json.principal1Phone+"</td><td>负责人2："+json.principal2+"<br/>电话："
					+json.principal2Phone+"</td></tr></table><p class='winbtn'></p>";		
		}else if(json.ifAlarm=="1"){
			iconImg = creaticon(0);
			 content = "<p style='margin-left:10px;'><span style='color: #e06e0b; font-size:18px; font-weight:bold;'>设备："
					+json.placeeAddress+"（"+json.mac+"）"+"</span><span class='type'>"
					+json.placeType+"</span><br/>所属区域："+json.areaName+"<br/>地址："+json.address+"<br/></p><table><tr><td>负责人1："
					+json.principal1+"<br/>电话："+json.principal1Phone+"</td><td>负责人2："+json.principal2+"<br/>电话："
					+json.principal2Phone+"</td></tr></table><p class='winbtn'></p>";
		}
	}
	function creaticon(i){
    var myIcon = new BMap.Icon(marker_icon[i], new BMap.Size(31, 47), {    
        // 指定定位位置。   
        // 当标注显示在地图上时，其所指向的地理位置距离图标左上    
        // 角各偏移10像素和25像素。您可以看到在本例中该位置即是   
        // 图标中央下端的尖角位置。    
        anchor: new BMap.Size(10, 25),    
        // 设置图片偏移。   
        // 当您需要从一幅较大的图片中截取某部分作为标注图标时，您   
        // 需要指定大图的偏移位置，此做法与css sprites技术类似。    
        imageOffset: new BMap.Size(0, 0)   // 设置图片偏移    
    });   
     return myIcon;
	}
    // 创建标注对象并添加到地图   
    var marker = new BMap.Marker(point, {icon: iconImg});    
    map.addOverlay(marker);    
    markers.push(marker);  
    var point=point;
	var label = new BMap.Label(json.name,{"offset":new BMap.Size(20,20)});
	label.setStyle({ 
         display:"none" //给label设置样式，任意的CSS都是可以的
    });
	marker.addEventListener("mouseover", function(){  
	    label.setStyle({    //给label设置样式，任意的CSS都是可以的
	        display:"block"
	    });		         
	}); 		 
	marker.addEventListener("mouseout", function(){ 
	    label.setStyle({    //给label设置样式，任意的CSS都是可以的
	        display:"none"
	    }); 
	});
	var infoBoxTemp = null;
	marker.setLabel(label);
	var html = ["<div class='mybox'>"+content+"</div>"];
    var infoBox = new BMapLib.InfoBox(map,html.join(""),{
    	boxStyle:{
    		background:"#0d3737",
    		width:'400px',
    	}
    	,closeIconMargin: "1px 1px 0 0"
    	,enableAutoPan: true
    	,align: INFOBOX_AT_TOP
    	,offset: new BMap.Size(25,25)
    	,closeIconUrl:'images/close.png'
    });
(function(){
var index = i;
var _marker = marker;
_marker.addEventListener("click",function(){
	if(getbox!=null){
		getbox.close();
	}
	infoBox.open(point);
	getbox=infoBox;
});			
label.addEventListener("click",function(){
	if(getbox!=null){
		getbox.close();
	}
	infoBox.open(point);
	getbox=infoBox;

});
if(!!json.isOpen){
label.hide();
 infoBox.open(marker);
}
})();
}    
// 创建InfoWindow
function createInfoWindow(content,title,i){ 
    var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='"+title+"'>"+title+"</b><div class='iw_poi_content'>"+content+"</div>");
    return iw;
}
// 创建infoBox
function creatBox(content,title,i){
	var infoBox = new BMapLib.InfoBox(map,"百度地图api",{boxStyle:{background:"url('tipbox.gif') no-repeatcenter top",width: "200px"},closeIconMargin: "10px 2px 0 0",enableAutoPan: true
        ,alignBottom: false});
}
//创建报警marker
function addalertMarker(json){
	point = new BMap.Point(json.longitude,json.latitude);
	
    var myIcon = new BMap.Icon(marker_icon[2], new BMap.Size(31, 47), {    
        // 指定定位位置。   
        // 当标注显示在地图上时，其所指向的地理位置距离图标左上    
        // 角各偏移10像素和25像素。您可以看到在本例中该位置即是   
        // 图标中央下端的尖角位置。    
        anchor: new BMap.Size(10, 25),    
        // 设置图片偏移。   
        // 当您需要从一幅较大的图片中截取某部分作为标注图标时，您   
        // 需要指定大图的偏移位置，此做法与css sprites技术类似。    
        imageOffset: new BMap.Size(0, 0)   // 设置图片偏移    
    });   
    // 创建标注对象并添加到地图   
    var marker = new BMap.Marker(point, {icon: myIcon});    
    map.addOverlay(marker);    
    markers.push(marker);  
    var point=point;
    var label = new BMap.Label(json.named,{"offset":new BMap.Size(20,20)});
	label.setStyle({ 
         display:"none" //给label设置样式，任意的CSS都是可以的
    });
	marker.addEventListener("mouseover", function(){  
	    label.setStyle({    //给label设置样式，任意的CSS都是可以的
	        display:"block"
	    });		         
	}); 		 
	marker.addEventListener("mouseout", function(){ 
	    label.setStyle({    //给label设置样式，任意的CSS都是可以的
	        display:"none"
	    }); 
	});

	//marker.enableMassClear(); 
	label.setStyle({
		borderColor:"#808080",
		color:"#333",
		cursor:"pointer"
	});
	marker.setLabel(label);
	marker.setAnimation(BMAP_ANIMATION_BOUNCE);
	var html = ["<div class='mybox'><div class='named'>"+json.named+"</div><p style='margin-left:10px;'><span style='color: #e06e0b;font-size:18px; font-weight:bold;'>设备："+json.devName+"（"+json.devMac+"）"
	        	+"</span><span class='type' style='background:#f00'>"+json.alarmType
	        	+"</span><br/>所属区域："+json.areaName+"<br/>地址："+json.alarmAddress+"<br/></p><table><tr><td>负责人1："
	        	+json.principal1+"<br/>电话："+json.principalPhone1+"</td><td>负责人2："+json.principal2+
	        	"<br/>电话："+json.principalPhone2+"</td></tr></table><div class='alert_person'><div class='cl_person'>处理人:</div><div class='cl_content'><input name='person'placeholder='处理人姓名' id='person' type='text' /></div>"
	        	+"<div style='clear:both;'></div></div>" +
	        	"<div class='alert_content'><div class='cl_person'>备注：</div><div class='cl_content'><textarea placeholder='请输入内容'  name='content'  id='polic'></textarea></div></div><div class='clear'></div>" +
	        	'<p class="winbtn"><a onclick="doalert(\''+json.devMac+'\')" class="pmt2">处理警报</a></p></div>'];
	            var infoBox = new BMapLib.InfoBox(map,html.join(""),{
	            	boxStyle:{
	            		background:"#0d3737",
	            		width:'400px',
	            	}
	            	,closeIconMargin: "1px 1px 0 0"
	            	,enableAutoPan: true
	            	,align: INFOBOX_AT_TOP
	            	,offset: new BMap.Size(25,25)
	            	,closeIconUrl:'images/close.png'
	            });
	            infoBox.open(marker);
	(function(){
		var index = i;
		var _marker = marker;
		_marker.addEventListener("click",function(){
			 infoBox.open(marker);
		});			
		label.addEventListener("click",function(){
			 infoBox.open(marker);
		});
		if(!!json.isOpen){
			label.hide();
			 infoBox.open(marker);
		}
	})();
}
//地图切片
var out_src="../";
function addTilesLayer(){
    /*配置切片的位置*/
    var op = {x:732.6,y:185.25};//起始切片索引(左下角)
    var minzoom = 12;//切片起始级别
    var zoomcount = 8;//切片级别个数（zomm文件夹个数）

    /*计算每一个级别中起始位置的切片索引*/
    var maxzoom = minzoom+zoomcount-1;
    var p = map.getMapType().getProjection();
    var zu = 256*Math.pow(2,18-minzoom);
    var opoint = p.pointToLngLat({x:op.x*zu,y:op.y*zu});
    var tileStartIndex = {};
    tileStartIndex[minzoom] = op;
    for (var i = minzoom + 1 ; i <= maxzoom; i++) {
        var s = p.lngLatToPoint(opoint);
        var k = 256*Math.pow(2,18-i);
        tileStartIndex[i] = {x:Math.round(s.x/k),y:Math.round(s.y/k)};
    };

    /*计算每级的切片索引范围,超出范围则显示替代图片*/
    var src_size = {w:20292,h:5822};
    var maxTile = {}
    for(var i=0;i<zoomcount;i++){
        var k = Math.pow(2,(zoomcount-i-1));
        var w = src_size.w / k;
        var h = src_size.h / k;
        maxTile[i] = {x:Math.ceil(w/256)-1,y:Math.ceil(h/256)-1};
    }

    /*创建切片图层*/
    var tileLayer = new BMap.TileLayer({isTransparentPng: true});
    tileLayer.getTilesUrl = function(tileCoord, zoom) {
        var si = tileStartIndex[zoom];
        if(si){
            var z = zoom - minzoom;
            var x = tileCoord.x - si.x;
            var y = tileCoord.y - si.y;
            var maxxy = maxTile[z];
            if(x>maxxy.x || y>maxxy.y || x<0 || y<0){
                return out_src+'mapfiles/no_map.png'
            }
            return out_src+'mapfiles/zoom'+z+'/'+y+'-'+x+'.png';
        }
        return out_src+'mapfiles/no_map.png';
    }
    map.addTileLayer(tileLayer);
}
//报警信息

//报警信息的添加函数
function show_alert2(get_data){
	var alert_str="";
	for(i=0;i<get_data.length;i++){
		alert_str+="<li>";
		alert_str+='<dt class="tabs2" title="'+get_data[i].devName+'">'+get_data[i].devName+'</dt>';
		alert_str+='<dt class="tabs3" title="'+get_data[i].devMac+'">'+get_data[i].devMac+'</dt>';
		alert_str+='<dt class="tabs4" title="'+get_data[i].alarmType+'">'+get_data[i].alarmType+'</dt>';
		alert_str+='<dt class="tabs5"  title="'+get_data[i].alarmTime+'">'+get_data[i].alarmTime+'</dt>';
		alert_str+='<dt class="tabs6" id="'+get_data[i].devMac+'" alertime="'+get_data[i].alarmTime+'" onclick="lookat(this)">查看</dt>';
		alert_str+="</li>";
	}
	$('#demo1').html(alert_str);
	mytext();
}
//地图查看报警
function lookat(a){
	alert_time=a.getAttribute("alertime");
	alert_id=a.getAttribute("id");
	$.ajax({
		type : "GET",
		url : "searchByDevMac.do?devMac=" + alert_id + "&alarmTime="
				+ alert_time,
		dataType : "json",
		async : true,
		beforeSend: function(XMLHttpRequest){
			$("#index_loading").show();   //调用本次ajax请求时传递的options参数
        },
        complete: function(XMLHttpRequest){
        	$("#index_loading").hide();   //调用本次ajax请求时传递的options参数
        },
		success : function(data) {
			lat2=parseFloat(data[0].latitude);
			lng2=parseFloat(data[0].longitude);
			lat2=lat2+0.001;
			var point2 = new BMap.Point(lng2,lat2);			
			map.centerAndZoom(point2, 19);
			map.clearOverlays();
			addTilesLayer();
			addalertMarker(data[0],0);
		},
		error : function() {
			console.log("请求失败");
		}
	});

}
//处理报警
function doalert(x) {
	w=$('#person').val();
	infomation=$('#polic').val();
	$.ajax({
		type : "GET",
		url : "dealAlarmMsg.do?devMac=" +x+"&dealDetail="+infomation+"&dealPeople="+w,
		dataType : "json",
		async : false,
		beforeSend: function(XMLHttpRequest){
			$("#index_loading").show();   //调用本次ajax请求时传递的options参数
        },
        complete: function(XMLHttpRequest){
        	$("#index_loading").hide();   //调用本次ajax请求时传递的options参数
        },
		success : function(data) {
			console.log("请求成功");
			datas = eval(data);
alert_data=JSON.stringify(data);
localStorage.setItem("key",alert_data);   
var getstr_data=localStorage.getItem("key");
var get_data= eval(getstr_data);
show_alert2(get_data);
map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);//显示全中国
map.clearOverlays();
$('.alert').removeClass('alert_bg_active');
$('.alert_img').hide();
$('.alert_img').removeClass('alert_img_active');
var audio = document.getElementById("audio");
audio.currentTime = 0;
audio.pause();
		},
		error : function() {
			console.log("请求失败");
			var audio = document.getElementById("audio");
			audio.currentTime = 0;
			$('#audio')[0].pause();
		}
	});
}
//处理有线主机报警
function do_alert_yx(json){
	var dealUser = $("#handlePerson_yx").val();
	var dealText = $("#treatmentReasons_yx").val();
	get_time=$(json).attr("time");
	get_araename=$(json).attr("areaname");
	get_devmac=$(json).attr("devmac");
	 $.ajax({
			type : "GET",
			url : "faultAlarm.do?faultCode=" + get_devmac
					+ "&repeaterMac=" + get_araename
					+ "&alarmTime=" + get_time + "&dealUser="
					+ dealUser + "&dealText=" + dealText,
			dataType : "json",
			async : false,
			success : function(data) {
				console.log("处理成功");
				$('#popupWired').hide();
				var audio = document.getElementById("audio");
				audio.currentTime = 0;
				audio.pause();
				return false;
			},
			error : function() {
				console.log("处理失败");
				return false;
			}
		}); 
}
function mytext(){
	//表格內容过长隐藏
	$("#demo1 li dt").not('#demo1 li .tabs5').each(function(index,element){
		e= $(element);
		div_len=e.width()/12;
		font_num=Math.round(div_len);
		if(e.text().length>font_num){
		 e.text(e.text().substring(0,font_num-2) + '...'); }
		});
$('#demo1 li .tabs5').each(function(index,element){
	e= $(element);
	div_len=e.width()/10;
	font_num=Math.round(div_len);
	if(e.text().length>font_num){
	 e.text(e.text().substring(0,font_num-2) + '...'); }
	});
}