// 百度地图API功能

//创建和初始化地图函数：
    function initMap(){
        createMap();//创建地图
        setMapEvent();//设置地图事件
        addMapControl();//向地图添加控件
        addMarker();//向地图中添加marker
    }
    
    //创建地图函数：
    function createMap(){
        var map = new BMap.Map("allmap",{mapType: BMAP_HYBRID_MAP});//在百度地图容器中创建一个地图
        var point = new BMap.Point(113.350587,23.131228);//定义一个中心点坐标
        map.centerAndZoom(point,14);//设定地图的中心点和坐标并将地图显示在地图容器中
        window.map = map;//将map变量存储在全局
    }
    
    //地图事件设置函数：
    function setMapEvent(){
        map.enableDragging();//启用地图拖拽事件，默认启用(可不写)
        map.enableScrollWheelZoom();//启用地图滚轮放大缩小
        map.enableDoubleClickZoom();//启用鼠标双击放大，默认启用(可不写)
        map.enableKeyboard();//启用键盘上下左右键移动地图
    }
    
    //地图控件添加函数：
    function addMapControl(){
        //向地图中添加缩放控件
	var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_LEFT,type:BMAP_NAVIGATION_CONTROL_LARGE});
	map.addControl(ctrl_nav);
        //向地图中添加缩略图控件
	var ctrl_ove = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:1});
	map.addControl(ctrl_ove);
        //向地图中添加比例尺控件
	var ctrl_sca = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
	map.addControl(ctrl_sca);
    }
	//json数据请求
	
	
	
    
    //标注点数组
    var markerArr = [[
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.350614|23.13156",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.35279|23.143217",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.346969|23.127099",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.335974|23.126401",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.34025|23.133248",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
		 ],
		 [
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.362965|23.129491",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.365409|23.132715",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.373188|23.128793",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.368212|23.134543",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.36699|23.124871",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
		 ],
		 [
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.270802|23.135512",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.268866|23.135686",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.26768|23.13601",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.266769|23.134348",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.265533|23.133937",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.346969|23.127299",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.335974|23.126201",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.34025|23.131248",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
		 ]
];
	//注点图片数组
	var marker_icon = [{images:"images/marker.png"},{images:"images/marker01.png"},{images:"images/marker02.png"}];
	//alert(marker_icon[0])
    //创建marker
    function addMarker(){
        for(var i=0;i<markerArr.length;i++){
			for(var j=0;j<markerArr[i].length;j++){
				var json = markerArr[i][j];
				var p0 = json.point.split("|")[0];
				var p1 = json.point.split("|")[1];
				var point = new BMap.Point(p0,p1);
				var iconImg = createIcon(json.icon,marker_icon[i].images);
				var marker = new BMap.Marker(point,{icon:iconImg});
				var iw = createInfoWindow(i,j)
				var label = new BMap.Label(json.title,{"offset":new BMap.Size(json.icon.lb-json.icon.x+10,-20)});
				marker.setLabel(label);
				map.addOverlay(marker);
				label.setStyle({
							borderColor:"#808080",
							color:"#333",
							cursor:"pointer"
				});
				
				(function(){
					var index = j;
					var _iw = createInfoWindow(i,j);
					var _marker = marker;
					_marker.addEventListener("click",function(){
						this.openInfoWindow(_iw);
					});
					_iw.addEventListener("open",function(){
						_marker.getLabel().hide();
					})
					_iw.addEventListener("close",function(){
						_marker.getLabel().show();
					})
					label.addEventListener("click",function(){
						_marker.openInfoWindow(_iw);
					})
					if(!!json.isOpen){
						label.hide();
						_marker.openInfoWindow(_iw);
					}
				})()
			}
        }
    }
    //创建InfoWindow
    function createInfoWindow(i,j){
        var json = markerArr[i][j];
        var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>"+json.content+"</div>");
        return iw;
    }
    //创建一个Icon
    function createIcon(json,marker_icon){
		var icon = new BMap.Icon(marker_icon, new BMap.Size(31,47),{imageOffset: new BMap.Size(-json.l,-json.t),infoWindowOffset:new BMap.Size(json.lb+5,1),offset:new BMap.Size(json.x,json.h)})
        return icon;
    }
    
    initMap();//创建和初始化地图
	map.enableContinuousZoom();
	//城市切换
	var size = new BMap.Size(10, 20);
	map.addControl(new BMap.CityListControl({
		anchor: BMAP_ANCHOR_BOTTOM_LEFT,
		offset: size,
	}));
	
//创建天河区 
//创建经纬度数组  
var secRingCenter = new BMap.Point(113.37011,23.134776);  
var secRing = [new BMap.Point(113.307659,23.155711),new BMap.Point(113.324404,23.158968),new BMap.Point(113.419984,23.162955),new BMap.Point(113.40942,23.116098),new BMap.Point(113.325194,23.120751)];  
//创建多边形  
var secRingPolygon = new BMap.Polygon(secRing, {strokeColor:"blue", strokeWeight:5, strokeOpacity:0.5});  
//添加多边形到地图上  
map.addOverlay(secRingPolygon);  
//给多边形添加鼠标事件  
secRingPolygon.addEventListener("mouseover",function(){  
    secRingPolygon.setStrokeColor("red");  
    map.addOverlay(secRingLabel);  
    map.panTo(secRingCenter);  
});  
secRingPolygon.addEventListener("mouseout",function(){  
    secRingPolygon.setStrokeColor("blue");  
    map.removeOverlay(secRingLabel);  
});  
secRingPolygon.addEventListener("click",function(){  
    map.zoomIn();  
    secRingPolygon.setStrokeColor("red");  
    map.setCenter(secRingCenter);  
});  
//创建标签  
var secRingLabel = new BMap.Label("<b>北京市二环</b>，包括了东城区</br>和西城区。著名旅游景点有</br>天安门、故宫、后海、北海</br>公园、景山、南锣鼓巷等。",{offset: new BMap.Size(-150,0), position: secRingCenter});  
secRingLabel.setStyle({"z-index":"999999", "padding": "10px","width": "140px","border": "1px solid #ccff00"});  
  
var secRingLabel2 = new BMap.Label("二  环",{offset: new BMap.Size(10,-30), position: secRingCenter});  
secRingLabel2.setStyle({"line-height": "20px", "text-align": "center", "width": "80px", "height": "29px", "border": "none", "padding": "2px","background": "url(http://jixingjx.com/mapapi/ac.gif) no-repeat",});  
map.addOverlay(secRingLabel2);  
	
	
	
	