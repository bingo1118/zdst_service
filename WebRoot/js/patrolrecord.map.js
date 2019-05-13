// 百度地图API功能

//创建和初始化地图函数：
function initMap() {
	createMap();// 创建地图
	setMapEvent();// 设置地图事件
	addMapControl();// 向地图添加控件
	setTimeout("analysis()", 100);// 向地图中添加marker
	// analysis()
}

// 创建地图函数：
function createMap() {
	var map = new BMap.Map("allmap");// 在百度地图容器中创建一个地图
	var point = new BMap.Point(113.350587, 23.131228);// 定义一个中心点坐标
	map.centerAndZoom(point, 14);// 设定地图的中心点和坐标并将地图显示在地图容器中
	window.map = map;// 将map变量存储在全局
}

// 地图事件设置函数：
function setMapEvent() {
	map.enableDragging();// 启用地图拖拽事件，默认启用(可不写)
	map.enableScrollWheelZoom();// 启用地图滚轮放大缩小
	map.enableDoubleClickZoom();// 启用鼠标双击放大，默认启用(可不写)
	map.enableKeyboard();// 启用键盘上下左右键移动地图
}

// 地图控件添加函数：
function addMapControl() {
	// 向地图中添加缩放控件
	var ctrl_nav = new BMap.NavigationControl({
		anchor : BMAP_ANCHOR_TOP_LEFT,
		type : BMAP_NAVIGATION_CONTROL_LARGE
	});
	map.addControl(ctrl_nav);
	// 向地图中添加缩略图控件
	var ctrl_ove = new BMap.OverviewMapControl({
		anchor : BMAP_ANCHOR_BOTTOM_RIGHT,
		isOpen : 1
	});
	map.addControl(ctrl_ove);
	// 向地图中添加比例尺控件
	var ctrl_sca = new BMap.ScaleControl({
		anchor : BMAP_ANCHOR_BOTTOM_LEFT
	});
	map.addControl(ctrl_sca);
}

// 定义全局变量
var title, content, point_x, point_y, markerArr, marker_icon = [ {
	images : "images/marker.png"
} ];
var icon_image=["images/marker.png","images/marker01.png","images/marker02.png"];
var icon_s=0;
var points = [];
var getline=true;
var mac=netstater=nonet=other=0;
// 解析json
function analysis() {
	var equipmentTxt = "";
	points.length = 0;
	$.ajax({
				type : "GET",
				url : "mapPatrolRecord.do",
				dataType : "json",
				async : false,
				success : function(data) {
					json = eval(data);
					if (data == null || data == "") {
						
					} else {
						setZoom(json);
						for ( var i = 0; i < json.length; i++) {
		//首次查询无数据					
					if(json[i].uid==undefined){
					
						equipmentTxt = "无数据";
						
					map.centerAndZoom(new BMap.Point(103.388611, 35.563611), 5);					
					popupMap(json[json.length - 1].cv.macNum,
							json[json.length - 1].cv.netStaterNum,
							json[json.length - 1].cv.noNetStater,
							json[json.length - 1].cv.otherNum);
					}
		//有数据时				
					else{
						if(json[i].devicestate=="待检"){
							equipmentTxt += "<li point_jd="
									+ json[i].longitude
									+ " point_wd="
									+ json[i].latitude
									+ " mac="
									+ json[i].uid
									+ "><h1 style='background: url(images/marker02.png) no-repeat left center;'>"
									+ json[i].deviceName + "（" + json[i].uid + "）"
									+ "</h1><p>地址："
									+ json[i].address + "（" + json[i].uid + "）"
									+ "</p></li>";
							icon_s=2;
							
							}
						else if(json[i].devicestate=="不合格"){ 
							equipmentTxt += "<li point_jd="
								+ json[i].longitude
								+ " point_wd="
								+ json[i].latitude
								+ " mac="
								+ json[i].uid
								+ "><h1 style='background: url(images/marker01.png) no-repeat left center;'>"
								+ json[i].deviceName + "（" + json[i].uid + "）"
								+ "</h1><p>地址："
								+ json[i].address + "（" + json[i].uid + "）"
								+ "</p></li>";
							icon_s=1;
						}
							else{equipmentTxt += "<li point_jd="
								+ json[i].longitude
								+ " point_wd="
								+ json[i].latitude
								+ " mac="
								+ json[i].uid
								+ "><h1 style='background: url(images/marker.png) no-repeat left center;'>"
								+ json[i].deviceName + "</h1><p>地址："
								+ json[i].address + "（" + json[i].uid + "）"
								+ "</p></li>";
							icon_s=0;
							}
          					point_x = json[i].longitude;
							point_y = json[i].latitude;
							
							addMarker(i, point_x, point_y, json[i],getline,icon_s);
							// 终端总数
							popupMap(json[json.length - 1].cv.macNum,
									json[json.length - 1].cv.netStaterNum,
									json[json.length - 1].cv.noNetStater,
									json[json.length - 1].cv.otherNum);
		
						}
						$(".equipment ul").html(equipmentTxt);
					}
						}
				},
				error : function() {
					console.log("请求失败+++");
				}
			});

	polyline();
}

// 根据原始数据计算中心坐标和缩放级别，并为地图设置中心坐标和缩放级别。
function setZoom(json) {
	if (json.length > 0) {
		var maxLng = json[0].longitude;
		var minLng = json[0].longitude;
		var maxLat = json[0].latitude;
		var minLat = json[0].latitude;
		var res;
		for ( var i = json.length - 1; i >= 0; i--) {
			res = json[i];
			if (res.lng > maxLng)
				maxLng = res.lng;
			if (res.lng < minLng)
				minLng = res.lng;
			if (res.lat > maxLat)
				maxLat = res.lat;
			if (res.lat < minLat)
				minLat = res.lat;
		}
		;
		var cenLng = (parseFloat(maxLng) + parseFloat(minLng)) / 2;
		var cenLat = (parseFloat(maxLat) + parseFloat(minLat)) / 2;
		var zoom = getZoom(maxLng, minLng, maxLat, minLat);
		map.centerAndZoom(new BMap.Point(cenLng, cenLat), zoom);
	} else {
		// 没有坐标，显示全中国。
		map.centerAndZoom(new BMap.Point(103.388611, 35.563611), 5);
	}
}

// 根据经纬极值计算绽放级别。本例核心代码。
function getZoom(maxLng, minLng, maxLat, minLat) {
	var zoom = [ "50", "100", "200", "500", "1000", "2000", "5000", "10000",
			"20000", "25000", "50000", "100000", "200000", "500000", "1000000",
			"2000000" ]// 级别18到3。
	var pointA = new BMap.Point(maxLng, maxLat); // 创建点坐标A
	var pointB = new BMap.Point(minLng, minLat); // 创建点坐标B
	var distance = map.getDistance(pointA, pointB).toFixed(1); // 获取两点距离,保留小数点后1位
	for ( var i = 0, zoomLen = zoom.length; i < zoomLen; i++) {
		if (zoom[i] - distance > 0) {
			return 18 - i + 3;// 之所以会多3，是因为地图范围常常是比例尺距离的10倍以上。所以级别会增加3。
		}
	}
	;
}

// 创建marker
function addMarker(i, point_x, point_y, json,getline,icon_s) {
	var point = new BMap.Point(point_x, point_y);
	var myIcon = new BMap.Icon(icon_image[icon_s], new BMap.Size(31, 47));  //定义自己的标注
	var marker = new BMap.Marker(point,{icon:myIcon});
	map.addOverlay(marker);
	var label = new BMap.Label(i + 1, {
		offset : new BMap.Size(20, -10)
	});
	marker.setLabel(label);
	if(icon_s!=2){points.push(point);}
	content = "txt";
	addClickHandler(json, marker);
}
function addClickHandler(content, marker) {
	marker.addEventListener("click", function(e) {
		$('.detail').hide(200);
		openInfo(content, e);
	});
}
function openInfo(json, e) {
	var opts = {
		height : 80, // 信息窗口高度
		overflow:"",
		//title : "信息窗口", // 信息窗口标题
		//enableMessage : true
	// 设置允许信息窗发送短息
	};
	
	var p = e.target;
	var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
	content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："
			+  json.deviceName
			+ "（"
			+ json.uid
			+ "）"
			+ "</span><span class='online'>"
			+ json.devicestate
			+ "</span><br/>所属区域："
			+ json.areaName
			+ "<br/>地址："
			+ json.address
			+ "<br/></p><p class='winbtn'><a onclick='detail(this,\""+json.uid+"\")' class='lcxq'>设备详情</a></p>";
	var infoWindow = new BMap.InfoWindow(content);
	map.openInfoWindow(infoWindow, point); // 开启信息窗口
}
initMap();// 创建和初始化地图
// 标注点连线方法
function polyline() {
	var polyline = new BMap.Polyline(points, {
		strokeColor : "blue",
		strokeWeight : 4,
		strokeOpacity : 1
	});
	map.addOverlay(polyline);
	var view = map.getViewport(points);
	// alert(view.zoom);
	// map.centerAndZoom(view.center, view.zoom);
}

// 饼状统计图的函数
function popupMap(macNum, netStaterNum, noNetStater, otherNum) {
	$("#macNum").html(macNum);
	$("#netStaterNum").html(netStaterNum);
	$("#noNetStater").html(noNetStater);
	$('#otherNum').html(otherNum);
	$('#map_tab')
			.highcharts(
					{
						credits : {
							enabled : false
						},
						chart : {
							plotBackgroundColor : null,
							plotBorderWidth : null,
							plotShadow : false
						},
						title : {
							text : '设备状态统计图'
						},
						tooltip : {
							pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
						},
						plotOptions : {
							pie : {
								allowPointSelect : true,
								cursor : 'pointer',
								dataLabels : {
									enabled : false
								},
								showInLegend : true
							}
						},
						series : [ {
							type : 'pie',
							name : '数量百分比',
							data : [ [ '合格', netStaterNum ],
									[ '不合格', noNetStater ], [ '待检', otherNum ], ]
						} ]
					});
}

// 使用jQuery异步提交表单
$(function() {
	$('#searchBtn').click(function() {
		var getline=false;
		equipmentTxt="";
		if (allOverlay != 0) {
			var allOverlay = map.getOverlays();
			for ( var i = 0; i < allOverlay.length; i++) {
				map.removeOverlay(allOverlay[i]);
			}
		}
		var allOverlay = map.getOverlays();
		var areaId = $("#areaId option:selected").val();
		var J_xl_1 = $("#J_xl_1").val();
		var J_xl_2 = $("#J_xl_2").val();
		if (areaId == "") {
			alert("区域不能为空");
			return false;
		}
		if (J_xl_1 == "") {
			alert("时间不能为空");
			return false;
		}
		if (J_xl_2 == "") {
			alert("时间不能为空");
			return false;
		}

		$.ajax({
			async : false,
			url : "mapPatrolRecord.do?&&areaId=" + areaId + "&&J_xl_1="
					+ J_xl_1 + "&&J_xl_2=" + J_xl_2, //
			data : $('#map_Form').serialize(),
			dataType : "json",
			type : "GET",
			beforeSend : function(XMLHttpRequest) {
				$("#loader").show(); // 调用本次ajax请求时传递的options参数
			},
			complete : function(XMLHttpRequest) {
				$("#loader").hide(); // 调用本次ajax请求时传递的options参数
			},
success : function(data) {
				json = eval(data);
				if (json != null)
					setZoom(json);	
				points.splice(0,points.length);
				getline=true;
				if(json.length==0) {
					equipmentTxt="无数据";
					map.centerAndZoom(new BMap.Point(103.388611, 35.563611), 5);
					popupMap(0,0,0,0);
					$(".equipment_toggle span").click();
				} else {
for ( var i = 0; i < json.length; i++) {
					
	//查询无数据				
					if(json[i].uid==undefined){
				equipmentTxt="无数据";
				map.centerAndZoom(new BMap.Point(103.388611, 35.563611), 5);
				popupMap(0,
						0,
						0,
						0);
			}
//查询有数据		
			else{
					if(json[i].devicestate=="待检"){
						equipmentTxt += "<li point_jd="
								+ json[i].longitude
								+ " point_wd="
								+ json[i].latitude
								+ " mac="
								+ json[i].uid
								+ "><h1 style='background: url(images/marker02.png) no-repeat left center;'>"
								+ json[i].deviceName +"（" + json[i].uid + "）"
								+ "</h1><p>地址："
								+ json[i].address + "</p></li>";
						icon_s=2;
						}
						else if(json[i].devicestate=="不合格"){
							equipmentTxt += "<li point_jd="
								+ json[i].longitude
								+ " point_wd="
								+ json[i].latitude
								+ " mac="
								+ json[i].uid
								+ "><h1 style='background: url(images/marker01.png) no-repeat left center;'>"
								+ json[i].deviceName + "（" + json[i].uid + "）"
								+ "</h1><p>地址："
								+ json[i].address + "（" + json[i].uid + "）"
								+ "</p></li>";
							icon_s=1;
						}
						else{equipmentTxt += "<li point_jd="
							+ json[i].longitude
							+ " point_wd="
							+ json[i].latitude
							+ " mac="
							+ json[i].uid
							+ "><h1 style='background: url(images/marker.png) no-repeat left center;'>"
							+ json[i].deviceName + "</h1><p>地址："
							+ json[i].address + "（" + json[i].uid + "）"
							+ "</p></li>";
						icon_s=0;
						
						}
  					point_x = json[i].longitude;
					point_y = json[i].latitude;
					
					addMarker(i, point_x, point_y, json[i],getline,icon_s);
					polyline();
					
					popupMap(json[json.length - 1].cv.macNum,
							json[json.length - 1].cv.netStaterNum,
							json[json.length - 1].cv.noNetStater,
							json[json.length - 1].cv.otherNum);
				}	
$(".equipment ul").html(equipmentTxt);
			}
}
},
			error : function() {
				console.log("请求失败+++");
			}
			

			});
});
});
// 点击下拉列表，打开标注点上的窗口信息
$(function() {
	$(".equipment ul").on('click','li',function() {
						$(this).css('background', '#ebedf0').siblings().css(
								'background', '#fff');
						var point_x = $(this).attr("point_jd");
						var point_y = $(this).attr("point_wd");
						var mac = $(this).attr("mac");
						for ( var i = 0; i < json.length; i++) {
							if (mac == json[i].uid) {								
								content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："
									+ json[i].deviceName
									+ "（"
									+ json[i].uid
									+ "）"
									+ "</span><span class='online'>"
									+ json[i].devicestate
									+ "</span><br/>所属区域："
									+ json[i].areaName
									+ "<br/>地址："
									+ json[i].address
									+ "<br/></p><p class='winbtn'><a onclick='detail(this,\""+json[i].uid+"\")' class='lcxq'>设备详情</a></p>";
								var infoWindow = new BMap.InfoWindow(content); // 创建信息窗口对象
								var point = new BMap.Point(point_x, point_y);
								map.openInfoWindow(infoWindow, point);
								map.centerAndZoom(point, 19);// 设定地图的中心点和坐标并将地图显示在地图容器中
							}	
						}
					});
	// 鼠标经过收缩伸张
	/*
	 * $(".equipment").hover(function(){ var content_right_height =
	 * $(window).height()-270; $(this).animate({height:content_right_height});
	 * },function(){ left: -90px; top: 210px; $(this).animate({height:"85"});
	 * });
	 */
	$(".equipment_toggle span").toggle(function() {
		$(this).html("显示");
		$(this).css({
			"right" : "416px",
			"top" : "210px"
		});
		$(".equipment").hide();
	}, function() {
		$(this).html("隐藏");
		$(this).css({
			"right" : "0",
			"top" : "-22px"
		});
		$(".equipment").show();
	});
$('.close').click(function(){
	$('.detail').hide();
});	
})
function detail(obj,id){
	$.ajax({
		async : false,
		url : "mapPatrolInfo.do?uuid="+id,
//		data : $('#map_Form').serialize(),
		dataType : "json",
		type : "GET",
		beforeSend : function(XMLHttpRequest) {
			$("#loader").show(); // 调用本次ajax请求时传递的options参数
		},
		complete : function(XMLHttpRequest) {
			$("#loader").hide(); // 调用本次ajax请求时传递的options参数
		},
success : function(data) {
	de_json = eval(data);
	console.log(de_json);
	info= '<h3>'+de_json[0].deviceName+'('+de_json[0].uid+')设备详情</h3>'
		  +'<li class="fis"><span class="detail_l">设备编号:</span><span>'+returnun(de_json[0].uid)+'</span></li>'
	      +'<li><span class="detail_l">设备名称:</span><span>'+returnun(de_json[0].deviceName)+'</span></li>'
	      +'<li><span class="detail_l">所属区域:</span><span>'+returnun(de_json[0].areaName)+'</span></li>'
	      +'<li><span class="detail_l">设备地址:</span><span>'+returnun(de_json[0].address)+'</span></li>'
	      +'<li><span class="detail_l">生产厂家:</span><span>'+returnun(de_json[0].producer)+'</span></li>'
	      +'<li><span class="detail_l">生产日期:</span><span>'+returnun(de_json[0].makeTime)+'</span></li>'
	      +'<li><span class="detail_l">过期日期:</span><span>'+returnun(de_json[0].overTime)+'</span></li>'
	      +'<li><span class="detail_l">添加日期:</span><span>'+returnun(de_json[0].addTime)+'</span></li>'
	      +'<li><span class="detail_l">维保人员:</span><span>'+returnun(de_json[0].workerName)+'</span></li>'
	      +'<li><span class="detail_l">联系电话:</span><span>'+returnun(de_json[0].workerPhone)+'</span></li>'
	      +'<li><span class="detail_l">最后一次维保巡检时间:</span><span>'+returnun(de_json[0].endTime)+'</span></li>'
	      ;	
$('#macdetail').html(info);
}
	});
$('.detail').show();
}
//当请求为undefined时, 返回空值
function returnun(obj){
	if(obj==undefined){
		obj="";
	}
	return obj;
}
