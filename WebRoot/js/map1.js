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
    
    //标注点数组
    var markerArr = [
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.350614|23.13156",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.35279|23.143217",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.346969|23.127099",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.335974|23.126401",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}},
	{title:"广州瀚润",content:"<p style='margin-left:10px;'><span style='color:#3d6dcc; font-weight:bold;'>设备：中继器</span><span class='online'>在线</span><br/>所属区域：天河区<br/>地址：广州市天河区天河北路<br/></p><table><tr><td>负责人1：行行行<br/>电话：12658531566</td><td>负责人2：行行行<br/>电话：12658531566</td></tr></table>",point:"113.34025|23.133248",isOpen:0,icon:{w:21,h:21,l:0,t:0,x:6,lb:5}}
		 ];
	
    //创建marker
    function addMarker(){
        for(var i=0;i<markerArr.length;i++){
            var json = markerArr[i];
            var p0 = json.point.split("|")[0];
            var p1 = json.point.split("|")[1];
            var point = new BMap.Point(p0,p1);
			var iconImg = createIcon(json.icon);
            var marker = new BMap.Marker(point,{icon:iconImg});
			var iw = createInfoWindow(i);
			var label = new BMap.Label(json.title,{"offset":new BMap.Size(json.icon.lb-json.icon.x+10,-20)});
			marker.setLabel(label);
            map.addOverlay(marker);
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
    //创建InfoWindow
    function createInfoWindow(i){
        var json = markerArr[i];
        var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>"+json.content+"</div>");
        return iw;
    }
    //创建一个Icon
    function createIcon(json){
        var icon = new BMap.Icon("images/marker.png", new BMap.Size(31,47),{imageOffset: new BMap.Size(-json.l,-json.t),infoWindowOffset:new BMap.Size(json.lb+5,1),offset:new BMap.Size(json.x,json.h)})
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