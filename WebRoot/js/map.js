// 百度地图API功能

//根据URL获取URL的参数
var point_x,point_y;
function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
var myurllongitude=GetQueryString("longitude");
var longitude  ;
if(myurllongitude !=null && myurllongitude.toString().length>1)
{
	longitude = GetQueryString("longitude");
}
var myurllatitude=GetQueryString("latitude");
var latitude ;	
if(myurllatitude !=null && myurllatitude.toString().length>1)
{
	latitude = GetQueryString("latitude");
}
var myurlsmokeMac=GetQueryString("devMac");
var smokeMac ;
if(myurlsmokeMac !=null && myurlsmokeMac.toString().length>1)
{
	smokeMac = GetQueryString("devMac");
}
var url=location.search; 
	//创建和初始化地图函数：
    function initMap(){
        createMap();// 创建地图
        setMapEvent();// 设置地图事件
        addMapControl();// 向地图添加控件
        //analysis();// 向地图中添加marker
        
    	if(url.indexOf("?")!=-1){//判断是否有参数，调用不同的方法
    		setTimeout("urlParameter()",500);  
    	}else{
    	
    		setTimeout("analysis()",500);
    	}
    }
    
    // 创建地图函数：
    var map;
    function createMap(){
        map = new BMap.Map("allmap",{mapType: BMAP_HYBRID_MAP});// 在百度地图容器中创建一个地图
        var point = new BMap.Point(103.388611,35.563611);// 定义一个中心点坐标
        map.centerAndZoom(point,6);// 设定地图的中心点和坐标并将地图显示在地图容器中
        window.map = map;// 将map变量存储在全局      
    }
    
  // 下面是用到的函数

	// 根据原始数据计算中心坐标和缩放级别，并为地图设置中心坐标和缩放级别。
	function setZoom(json){
		if(json.length>0){
			var maxLng = json[0].longitude;
			var minLng = json[0].longitude;
			var maxLat = json[0].latitude;
			var minLat = json[0].latitude;
			var res;
			for (var i = json.length - 1; i >= 0; i--) {
				res = json[i];
				if(res.lng > maxLng) maxLng =res.lng;
				if(res.lng < minLng) minLng =res.lng;
				if(res.lat > maxLat) maxLat =res.lat;
				if(res.lat < minLat) minLat =res.lat;
			};
			var cenLng =(parseFloat(maxLng)+parseFloat(minLng))/2;
			var cenLat = (parseFloat(maxLat)+parseFloat(minLat))/2;
			var zoom = getZoom(maxLng, minLng, maxLat, minLat);
			map.centerAndZoom(new BMap.Point(cenLng,cenLat), zoom);  
		}else{
			// 没有坐标，显示全中国。
			map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);  
		} 
	}

	// 根据经纬极值计算绽放级别。本例核心代码。
	function getZoom (maxLng, minLng, maxLat, minLat) {
		var zoom = ["50","100","200","500","1000","2000","5000","10000","20000","25000","50000","100000","200000","500000","1000000","2000000"]// 级别18到3。
		var pointA = new BMap.Point(maxLng,maxLat);  // 创建点坐标A
		var pointB = new BMap.Point(minLng,minLat);  // 创建点坐标B
		var distance = map.getDistance(pointA,pointB).toFixed(1);  // 获取两点距离,保留小数点后1位
		for (var i = 0,zoomLen = zoom.length; i < zoomLen; i++) {
			if(zoom[i] - distance > 0){
				return 18-i+3;// 之所以会多3，是因为地图范围常常是比例尺距离的10倍以上。所以级别会增加3。
			}
		};
	}
    
    
    // 地图事件设置函数：
    function setMapEvent(){
        map.enableDragging();// 启用地图拖拽事件，默认启用(可不写)
        map.enableScrollWheelZoom();// 启用地图滚轮放大缩小
        map.enableDoubleClickZoom();// 启用鼠标双击放大，默认启用(可不写)
        map.enableKeyboard();// 启用键盘上下左右键移动地图
    }
    
    // 地图控件添加函数：
    function addMapControl(){
    // 向地图中添加缩放控件
	var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_LEFT,type:BMAP_NAVIGATION_CONTROL_LARGE});
	map.addControl(ctrl_nav);
    // 向地图中添加缩略图控件
	var ctrl_ove = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:1});
	map.addControl(ctrl_ove);
    // 向地图中添加比例尺控件
	var ctrl_sca = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
	map.addControl(ctrl_sca);
	//切换地图类型的控件  
    var map_type = new BMap.MapTypeControl();  
    map.addControl(map_type);  
    }

	function urlParameter(){
		var equipmentTxt = "";
    	if(longitude!="" && latitude !="" && latitude!=null && longitude!=null){
	    	$.ajax({
	    		async: false,
	    		url: "mapSmokeByLongitu.do?longitude="+longitude+"&latitude="+latitude+"&smokeMac="+smokeMac,
	    		data: $('#map_Form').serialize(),
	    		dataType: "json",
	    		type: "GET",
	    		success: function(data){
	    			//$(".equipment ul li").remove();
		    		json = eval(data);
		    		macNum = json[0].cv.macNum;
		    		netStaterNum = json[0].cv.netStaterNum;
					alarmTypeNum = json[0].cv.alarmTypeNum;
					ifDealNum = json[0].cv.ifDealNum;
					noNetStater = json[0].cv.noNetStater;
					popupMap(macNum,netStaterNum,alarmTypeNum,ifDealNum,noNetStater);
		    		if(json!=null){
			    		setZoom(json);
			    		console.log("查询请求成功+++3333 ");
			    		for(var i=0;i<json.length;i++){
			    			if(json[i].placeType=="在线"){
			    				if(json[i].ifAlarm=="0"){
									equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker01.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
								}else if(json[i].ifAlarm=="1"){
									equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
								}
							}else if(json[i].placeType=="离线"){
								equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker02.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
							}
			    			title = json[i].name;
			    			//content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt'>平面图</a><a href='javascript:void(0);' class='lcxq'>楼层详情</a></p>";
			    			content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a></p>";
			    			point_x = json[i].longitude;
			    			point_y = json[i].latitude;
			    			placeTypeStr = json[i].placeType;
			    			markerArr = [
			    				{title:title,content:content,point:point_x+"|"+point_y,isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
			    			];
			    			var b = addMarker(i,placeTypeStr);
			    			markers.push(marker) ;
			    			//警报时,图标跳动
			    			marker.setAnimation(BMAP_ANIMATION_BOUNCE);
			    		}
			    		$(".equipment ul").append(equipmentTxt);
			    		//最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。  
	    		        markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers}); 
		    		}else if(json==null){
		    			alert("请求的经纬度为空");
		    			return false;	
		    		}
		    	},
				error: function() {
				console.log("请求失败+++");
				}
	   		}); 
   		}
	}		
    
	var json,macNum,netStaterNum,alarmTypeNum,ifDealNum,noNetStater;
	// 定义全局变量
	var title,content,placeTypeStr,markerArr,marker_icon = ["images/marker.png","images/marker02.png","images/marker01.png"];
	// 解析json
	function analysis(){
		var equipmentTxt = "";
		$.ajax({
			type: "GET",
			url: "mapSmoke.do",
			dataType: "json",
			async: false,
			success: function (data) {	
				json = eval(data);
				macNum = json[0].cv.macNum;
				netStaterNum = json[0].cv.netStaterNum;
				alarmTypeNum = json[0].cv.alarmTypeNum;
				ifDealNum = json[0].cv.ifDealNum;
				noNetStater = json[0].cv.noNetStater;
				popupMap(macNum,netStaterNum,alarmTypeNum,ifDealNum,noNetStater);
				setZoom(json);
				console.log("请求成功+++");
				for(var i=0;i<json.length;i++){
					if(json[i].placeType=="在线"){
						if(json[i].ifAlarm=="0"){
							equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker01.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
						}else if(json[i].ifAlarm=="1"){
							equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
						}
					}else if(json[i].placeType=="离线"){
						equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker02.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
					}
					title = json[i].name;
					//content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a><a href='javascript:void(0);' class='lcxq'>楼层详情</a></p>";
					content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a></p>";
					point_x = json[i].longitude;
					point_y = json[i].latitude;
					placeTypeStr = json[i].placeType;
					ifAlarm = json[i].ifAlarm;
					markerArr = [
						{title:title,content:content,point:point_x+"|"+point_y,isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
					];
					addMarker(i,placeTypeStr,ifAlarm);
					markers.push(marker);  
				}
				$(".equipment ul").append(equipmentTxt);
				//最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。  
		        markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});    
			},
			error: function() {
				console.log("请求失败+++");
			}
		});

	}
	// 创建marker
	var marker;
	var iconImg;
	var markers = [];  
    var markerClusterer = null;  
    function addMarker(i,placeTypeStr,ifAlarm){
		var json = markerArr[0];
		var p0 = json.point.split("|")[0];
		var p1 = json.point.split("|")[1];
		var point = new BMap.Point(p0,p1);
		if(placeTypeStr=="离线"){
			iconImg = createIcon(json.icon,marker_icon[1]);
		}else if(placeTypeStr=="在线"){		
			if(ifAlarm=="0"){
				iconImg = createIcon(json.icon,marker_icon[2]);
			}else if(ifAlarm=="1"){
				iconImg = createIcon(json.icon,marker_icon[0]);
			}
		}
		marker = new BMap.Marker(point,{icon:iconImg});
		var iw = createInfoWindow(i);
		var label = new BMap.Label(json.title,{"offset":new BMap.Size(json.icon.lb-json.icon.x+10,-20)});
		label.setStyle({ 
	         display:"none" //给label设置样式，任意的CSS都是可以的
	    });
		marker.setLabel(label);
		//console.log(111);
		if(url.indexOf("?")!=-1){
			
		}
		//警报时,图标跳动
		if(ifAlarm=="0"){marker.setAnimation(BMAP_ANIMATION_BOUNCE); }
		
		map.addOverlay(marker);
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
				
		(function(){
			var index = i;
			var _iw = createInfoWindow(i);
			var _marker = marker;
			_marker.addEventListener("click",function(){
				map.openInfoWindow(_iw,point);
			});			
			_iw.addEventListener("open",function(){
				_marker.getLabel().hide();
			});
			_iw.addEventListener("close",function(){
				_marker.getLabel().show();
			});
			label.addEventListener("click",function(){
				_marker.openInfoWindow(_iw);
			});
			if(!!json.isOpen){
				label.hide();
				_marker.openInfoWindow(_iw);
			}
		})();
		return marker;
    }
	
    // 创建InfoWindow
    function createInfoWindow(){
        var json = markerArr[0];
        var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>"+json.content+"</div>");
        return iw;
    }
	
    // 创建一个Icon
    function createIcon(json,marker_icon){
		var icon = new BMap.Icon(marker_icon, new BMap.Size(31,47),{imageOffset: new BMap.Size(-json.l,-json.t),infoWindowOffset:new BMap.Size(json.lb+5,1),offset:new BMap.Size(json.x,json.h)})
        return icon;
    }
    // 创建和初始化地图
    initMap();
    /*创建地图后，执行以下代码即可叠加新的层
    var tileLayer = new BMap.TileLayer({isTransparentPng: true});
    tileLayer.getTilesUrl = function(tileCoord, zoom) {
        var z = zoom-11;
        var k = Math.pow(2,z-5);
        var x = tileCoord.x-Math.floor(93821*k);
        var y = Math.floor(23730*k)-tileCoord.y;
        var max = Math.pow(2,z) -1;
        if(z<=0 || x<0 || y<0 || x>max || y>max){
            return 'hdmap/no_map.png';
        }
        return 'hdmap2/zoom'+z+'/'+y+'-'+x+'.png';//此URL用于指向叠加层的切片
    }
	map.addTileLayer(tileLayer);*/
    var out_src="../";
//    var out_src="";
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
                    return 'hdmap/no_map.png';
                }
                return out_src+'mapfiles/zoom'+z+'/'+y+'-'+x+'.png';
            }
            return out_src+'hdmap/no_map.png';
        }
        map.addTileLayer(tileLayer);
    }
    addTilesLayer(); 
    	/* 查询事件 */
    	/* 为查询按钮绑定事件 */		
    	// 使用jQuery异步提交表单
    $(function(){
    //$(document).ready(function(){
    	$('#map_Form').submit(function() {
    	//$('#searchBtn').submit(function() {
    		var equipmentTxt = "";
    		if(allOverlay!=0){
	    		var allOverlay = map.getOverlays();
	    		for (var i = 0; i < allOverlay.length; i++){
	    				map.removeOverlay(allOverlay[i]);
	    		}
	    		
	    		markerClusterer.clearMarkers();
    		}
    		var allOverlay = map.getOverlays();
    		var areaId = $("#areaId").val();
    		var deviceId = $("#deviceId option:selected").val();
    		var comName = $("#comName").val();
    		
    		if(areaId==""){
    			alert("查询条件不能为空");
    		}else{
	    		$.ajax({
	    			
	    			async: false,
	    			url: "searchMapSmoke.do?&&deviceId="+deviceId+"&&com="+encodeURI(encodeURI(comName)),
	    			//url: "searchMapSmoke.do",
	    			//data:'{"areaId":"114","deviceId":deviceId,"comName":comName}',
	    			data: $('#map_Form').serialize(),
	    			dataType: "json",
	    			type: "GET",
	    			beforeSend: function(XMLHttpRequest){
	    				$("#loader").show();   //调用本次ajax请求时传递的options参数
	                },
	                complete: function(XMLHttpRequest){
	    				$("#loader").hide();   //调用本次ajax请求时传递的options参数
	                },
	    			success: function(data){
	    				$(".equipment ul").empty();
	    				json = eval(data);
	    				macNum = json[0].cv.macNum;
	    				netStaterNum = json[0].cv.netStaterNum;
	    				alarmTypeNum = json[0].cv.alarmTypeNum;
	    				ifDealNum = json[0].cv.ifDealNum;
	    				noNetStater = json[0].cv.noNetStater;
	    				popupMap(macNum,netStaterNum,alarmTypeNum,ifDealNum,noNetStater);
	    				markers.length = 0;
	    				if(json!=null){
	    					$(".equipment_toggle span").html("隐藏");
	    					$(".equipment_toggle span").css({"right":"0","top":"-22px"});
	    			    	$(".equipment").show();
		    				setZoom(json);
		    				console.log("查询请求成功+++222");
		    				for(var i=0;i<json.length;i++){
		    					if(json[i].placeType=="在线"){		    						
		    						if(json[i].ifAlarm=="0"){
		    							equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker01.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
		    						}else if(json[i].ifAlarm=="1"){
		    							equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
		    						}
		    					}else if(json[i].placeType=="离线"){
		    						equipmentTxt += "<li point_jd="+json[i].longitude+" point_wd="+json[i].latitude+" mac="+json[i].mac+"><h1 style='background: url(images/marker02.png) no-repeat left center;'>"+json[i].name+"</h1><p>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</p></li>";
		    					}
		    					title = json[i].name;
		    					//content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a><a href='javascript:void(0);' class='lcxq'>楼层详情</a></p>";
		    					content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a></p>";
		    				/*	var infoWindow = new BMap.InfoWindow(""+content+"");  // 创建信息窗口对象 
		    					var point = new BMap.Point(point_x,point_y);
		    					map.openInfoWindow(infoWindow,point);
		    					map.centerAndZoom(point,19);// 设定地图的中心点和坐标并将地图显示在地图容器中
		    				}*/
		    					
		    					point_x = json[i].longitude;
		    					point_y = json[i].latitude;
		    					placeTypeStr = json[i].placeType;
		    					ifAlarm = json[i].ifAlarm;
		    					markerArr = [
		    						{title:title,content:content,point:point_x+"|"+point_y,isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
		    					];
		    					addMarker(i,placeTypeStr,ifAlarm);
		    					markers.push(marker);  
		    				}
		    				$(".equipment ul").append(equipmentTxt);
		    				//最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。  
		    		        markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});  
	    				}else if(json==null){
	    					alert("查找的条件没有数据！");
	    					return false;	
	    				}
	    			},
					error: function() {
						console.log("请求失败+++");
					}
	    		});
	    		return false;	
    		}
    	});
    });    	
   

//饼状统计图的函数
function popupMap(macNum,netStaterNum,alarmTypeNum,ifDealNum,noNetStater){
	$("#macNum").html(macNum);
	$("#netStaterNum").html(netStaterNum);
	//$("#alarmTypeNum").html(alarmTypeNum);
	$("#alarmTypeNum").html(0);
	$("#ifDealNum").html(ifDealNum);
	$("#noNetStater").html(noNetStater);
	$('#map_tab').highcharts({
		credits: {
			 enabled: false
		},
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '设备状态统计图'
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
            name: '数量百分比',
            data: [
                ['正常',     netStaterNum],
                /*['火警',     alarmTypeNum],*/
                /*['误报',    alarmTruthNum],*/
                ['报警',        ifDealNum],
                ['故障',      noNetStater]
            ]
        }]
    });
}

//点击下拉列表，打开标注点上的窗口信息
$(function(){  
    $(".equipment ul").on('click','li',function(){
    	$(this).css('background','#ebedf0').siblings().css('background','#fff');
    	var point_x = $(this).attr("point_jd");
    	var point_y = $(this).attr("point_wd");
    	var mac = $(this).attr("mac");
    	for(var i=0;i<json.length;i++){	
			if(mac == json[i].mac){
				//content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a><a href='javascript:void(0);' class='lcxq'>楼层详情</a></p>";
				content = "<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备："+json[i].placeeAddress+"（"+json[i].mac+"）"+"</span><span class='online'>"+json[i].placeType+"</span><br/>所属区域："+json[i].areaName+"<br/>地址："+json[i].address+"<br/></p><table><tr><td>负责人1："+json[i].principal1+"<br/>电话："+json[i].principal1Phone+"</td><td>负责人2："+json[i].principal2+"<br/>电话："+json[i].principal2Phone+"</td></tr></table><p class='winbtn'><a href='javascript:void(0);pmt(\""+json[i].mac+"\");show();' class='pmt2'>平面图</a></p>";
				var infoWindow = new BMap.InfoWindow(""+json[i].name+content+"");  // 创建信息窗口对象 
				var point = new BMap.Point(point_x,point_y);
				map.openInfoWindow(infoWindow,point);
				map.centerAndZoom(point,19);// 设定地图的中心点和坐标并将地图显示在地图容器中
			}
		}		
    });
    //鼠标经过收缩伸张
    /*$(".equipment").hover(function(){
    	var content_right_height =  $(window).height()-270;
    	$(this).animate({height:content_right_height});
        },function(){    left: -90px;
    top: 210px;
        $(this).animate({height:"85"});
      });*/
    $(".equipment_toggle span").toggle(function(){
    	$(this).html("显示");
    	$(this).css({"right":"416px","top":"210px"});
    	$(".equipment").hide();
    },
        function(){
    	$(this).html("隐藏");
    	$(this).css({"right":"0","top":"-22px"});
    	$(".equipment").show();
    }
    );
});
function pmt(x){
	 $.ajax({ 
	 url: "getPlan2.do", 
	 data:{"mac": x},
    type: "GET",
	  success: function(data){
	  var path="";
	  console.log("data:"+data);
	  if(data==""){
		  $('#iic1').hide();
		  path="images/null.jpg";
	      $('#pmt_sb').css("background-size","auto");
	  } else {
		  var obj = eval('(' + data + ')');
		  $('#iic1').show();
	       $('#pmt_sb').css("background-size","100%");
	       path="../plans/"+obj.planPath;
	       $('#iic1').css("left",obj.lstpp[0].macX+"%");
	       $('#iic1').css("top",obj.lstpp[0].macY+"%");
	  }
      
     $('#pmt_sb').css("background-image","url("+path+")");
 }
 });
	}
   