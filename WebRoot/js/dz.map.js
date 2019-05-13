// JavaScript Document
	var longitude2 = $("#longitude2").text();
	var latitude2 = $("#latitude2").text();
	var map = new BMap.Map("company_map");
	var point = new BMap.Point(longitude2,latitude2);
	map.centerAndZoom(point, 12);
	var marker = new BMap.Marker(point);  // 创建标注
	map.addOverlay(marker);              // 将标注添加到地图中
