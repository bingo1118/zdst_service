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

var to=100;//比例总数
//区域颜色
var color=["#FF0000","#FF3333","#FF8888","#FFDD55","#FFBB00","#FFFF77","#FFFF00","#DDFF77","#BBFF66","#66FF66"];
var arrow;
var myCompOverlay=[];
//获取坐标点
console.log(sl[6]);

//计算比例
function Percentage(num) { 
    return (Math.round(num / to * 10000) / 100.00);// 小数点后两位百分比
   
}
//设置颜色函数
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
				ply = new BMap.Polygon(rs.boundaries[i], {strokeWeight: 2, strokeColor: x, fillColor:x,strokeOpacity: 0.8}); //建立多边形覆盖物
				color_area=maps.addOverlay(ply);  //添加覆盖物
				pointArray = pointArray.concat(ply.getPath());
				var midde=Math.round(pointArray.length/2);
//				console.log(pointArray);
				var lable_lat=pointArray[midde].lat;
				var lable_lng=pointArray[midde].lng;    		 
			}    
		
		});		
	}
//	console.log(get_data);
var a=w=0;
//行政区域名字
var a=0;
var myGeo = new BMap.Geocoder();   
var len=get_data.length/10;
for(i=0;i<len;i++){
 	/* console.log(get_data[i]);  */
	long=get_data[i].longitude; lat=get_data[i].latitude;
	var gc = new BMap.Geocoder();
var point = new BMap.Point(long,lat);
var address;
var gz=['黄埔区','从化区','增城区','南沙区','花都区','番禺区','白云区','天河区','海珠区','荔湾区','越秀区'];
var num=[0,0,0,0,0,0,0,0,0,0,0,0,0,0];
var show_color=[];
gc.getLocation(point, function(rs){
   var addComp = rs.addressComponents;
   /* address = addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber; */
   /*console.log(addComp.city);*/
    if(addComp.city=="广州市"){
       for(x=0;x<gz.length;x++){
    	 
    	  if(addComp.district.toString()==gz[x]){
    		 num[x]++;
    	  }
       }
     } 
});
}

function init(){
   console.log(num);
for(i=0;i<num.length;i++){
if(num[i]<10){
	show_color[i]=color[0];
}
if(num[i]>10&&num[i]<20){
	show_color[i]=color[2];
}
}   
   action_area(gz);
}
setTimeout("init()", 5000); 
//循环执行函数添加区域颜色
function action_area(areaname){
for(var i=0; i<areaname.length;i++){
	getBoundary(areaname[i],show_color[x]);
}
}
window.onload=function(){
	//获取广东省的市名
	/*action_area(gz);*/

}

//滚动函数
var scrollFunc=function(e){
    e=e || window.event;
    var t1=document.getElementById("mapInfo");
    var zoom;
    zoom=maps.getZoom();
      console.log(zoom);
}
/*注册事件*/
if(document.addEventListener){
    document.addEventListener('DOMMouseScroll',scrollFunc,false);
}//W3C
window.onmousewheel=document.onmousewheel=scrollFunc;//IE/Opera/Chrome
function ComplexCustomOverlay(point){
    this._point = point;
    
  }

/*  for(i=0;i<poin.length;i++){
	  lon=poin[i].longitude; lat=poin[i].latitude;
	  var mPoint = new BMap.Point(lon, lat); 
	  maps.enableScrollWheelZoom();
	  maps.centerAndZoom(mPoint,15);
	  circle = new BMap.Circle(mPoint,50,{fillColor:"blue", strokeWeight: 1 ,fillOpacity: 0.3, strokeOpacity: 0.3,enableEditing:false});
	  maps.addOverlay(circle);
	  circle.addEventListener('click',function(){
		    alert('click');
	  });
  }*/

  var show=0; 
  function hide_show(){ 
	  for(var i=0;i<myCompOverlay.length;i++){
      if(show==0){ 
          myCompOverlay[i].hide(); 
         
      }else{ 
          myCompOverlay[i].show(); 
      } 
	  }
  } 
 