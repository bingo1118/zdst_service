// 百度地图API功能
	var maps = new BMap.Map("allmap");
	maps.centerAndZoom(new BMap.Point(113.270793,23.135308), 10);
	maps.enableScrollWheelZoom();
	addMapControl();
    function addMapControl(){
        // 向地图中添加缩放控件
    	var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_LEFT,type:BMAP_NAVIGATION_CONTROL_LARGE});
    	maps.addControl(ctrl_nav);
        // 向地图中添加缩略图控件
    	var ctrl_ove = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:1});
    	maps.addControl(ctrl_ove);
        // 向地图中添加比例尺控件
    	var ctrl_sca = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
    	maps.addControl(ctrl_sca);
    	//切换地图类型的控件  
        var map_type = new BMap.MapTypeControl();  
        maps.addControl(map_type);  
        }	
var gz=['黄埔区','从化区','增城区','南沙区','花都区','番禺区','白云区','天河区','海珠区','荔湾区','越秀区'];
var color=["#FF0000","#FF3333","#FF8888","#FFDD55","#FFBB00","#FFFF77","#FFFF00","#DDFF77","#BBFF66","#66FF66"];
var geoc = new BMap.Geocoder();
var num = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
var len=get_data.length/100;
(function aa(i){
	if(i == 500) {
		console.log('Num:'+num);
		init(num);
		return;
	}
	var point = new BMap.Point(get_data[i].longitude,get_data[i].latitude);
    geoc.getLocation(point, function(rs){
        var addComp = rs.addressComponents;
       for(x=0;x<gz.length;x++){
        if(addComp.district == gz[x]){
        	num[x]++;
        }
       }
        i++;aa(i);
        return;
    });  
})(0);
function init(num){
	var gz_data=num;
	var station=[];
	for(i=0;i<gz_data.length;i++){
		/* console.log(i+":"+Percentage(gz_data[i]));*/
		 get_station=Percentage(gz_data[i]);
		 if(get_station<1){get_station=0;}
		 if(get_station>10){get_station=9;}
         station[i]=get_station;
         console.log(station[i]);
	}
action_area(gz);	
//执行绘制	
	function action_area(areaname){
		for(var i=0; i<areaname.length;i++){
			getBoundary(areaname[i],color[station[i]]);
		}
}	
//计算比例
	function Percentage(num) { 
		return  Math.round(num/100*100);
	  /*  return (Math.round(num * 100)/100);// 小数点后两位百分比*/	   
	}
//绘制覆盖物	
	function getBoundary(name,x){       
		var bdary = new BMap.Boundary();
			bdary.get(name, function(rs){       //获取行政区域
				//map.clearOverlays();        //清除地图覆盖物       
				var count = rs.boundaries.length; //行政区域的点有多少个
				if (count === 0) {
					console.log('未能获取当前输入行政区域');
					return ;
				}
	          	var pointArray = [];
				for (var i = 0; i < count; i++) {
					ply = new BMap.Polygon(rs.boundaries[i], {strokeWeight: 2, strokeColor: 'blue', fillColor:x,strokeOpacity: 0.8}); //建立多边形覆盖物
					color_area=maps.addOverlay(ply);  //添加覆盖物
					pointArray = pointArray.concat(ply.getPath());
					var midde=Math.round(pointArray.length/2);
//					console.log(pointArray);
					var lable_lat=pointArray[midde].lat;
					var lable_lng=pointArray[midde].lng;    		 
				}    
			
			});		
		}	
	
}
