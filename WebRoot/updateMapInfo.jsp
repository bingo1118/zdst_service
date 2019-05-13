<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
 <title>Hello, World</title>
 <style type="text/css">
     html{height:100%}
     body{height:100%;margin:0px;padding:0px}
     #Container{height:100%}
 </style>
 <script type="text/javascript" src="js/jquery-1.8.2.js"></script>
 <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=WAbGUPB8LA7mB2QTy4G0n4e2"></script>
</head> 
 <body>
 设备Mac编号：<input type="text" id="smokeMac"/>  安装地址：<input type="text" id="address" style="width:300px"/> 
 <div><div id="lnglat" style="display:inline-block; color:red; width: 550px;">当前坐标：经度<input type="text" id="lng"/> , 维度<input type="text" id="lat"/> </div> <input type="button" id="submit" value="保存"/></div> 
   
<div id="allmap" style="width:1000px; height:600px; overflow:hidden; margin:0;"></div>  
    <script type="text/javascript">  

    $("#smokeMac").blur(function(){
    var smokeMac = $("#smokeMac").val();
    	$.ajax({
				type: "GET",
				url: "updateMapInfo.do?smokeMac="+smokeMac,
				dataType: "json",		
				async : false, 
				success: function (data) {	
					data = eval(data);
					$("#address").val(data[0].address);			
					document.getElementById('lnglat').innerHTML = "当前坐标：经度<input type='text' id='lng' value='"+ data[0].longitude +"'/>,维度<input type='text' id='lat' value='"+ data[0].latitude +"'/>";  
					map.clearOverlays();  
					point = new BMap.Point(data[0].longitude,data[0].latitude);
					map.centerAndZoom(point, 19);
					marker = new BMap.Marker(point);
					map.addOverlay(marker);
					marker.enableDragging();//可拖拽标注点
				 	marker.addEventListener('dragend', function(e){  
				        document.getElementById('lnglat').innerHTML = "当前坐标：经度<input type='text' id='lng' value='"+ e.point.lng +"'/>,维度<input type='text' id='lat' value='"+ e.point.lat +"'/>";  
				    }); 
					return false;						
				},
				error: function() {
					console.log("处理失败");
					return false;
				}
			});
    });
     $("#submit").click(function(){
     var smokeMac = $("#smokeMac").val();
     var smokeMaclng = $("#lng").val();
     var smokeMaclat = $("#lat").val();
    	$.ajax({
				type: "GET",
				url: "saveMapInfo.do?smokeMac="+smokeMac+"&longitude="+smokeMaclng+"&latitude="+smokeMaclat,
				dataType: "json",		
				async : false, 
				success: function (data) {	
					alert("处理成功");	
					return false;						
				},
				error: function() {
					alert("处理失败");
					return false;
				}
			});
    });
/** 
 * 百度地图API功能 
 */  
var map = new BMap.Map("allmap");
	var point = new BMap.Point(116.400244,39.92556);
	map.centerAndZoom(point, 12);
	var marker = new BMap.Marker(point);// 创建标注
	map.addOverlay(marker);             // 将标注添加到地图中
	marker.enableDragging();//可拖拽标注点
 	marker.addEventListener('dragend', function(e){  
        document.getElementById('lnglat').innerHTML = "当前坐标：经度<input type='text' id='lng' value='"+ e.point.lng +"'/>,维度<input type='text' id='lat' value='"+ e.point.lat +"'/>";  
    }); 
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
	map.enableScrollWheelZoom(true);
    }
    addMapControl();
    /*创建地图后，执行以下代码即可叠加新的层*/
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
                return 'mapfiles/zoom'+z+'/'+y+'-'+x+'.png';
            }
            return 'hdmap/no_map.png';
        }
        map.addTileLayer(tileLayer);
    }

    addTilesLayer();  
</script>  
    </body>
    </html>
