<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link
	href="http://api.map.baidu.com/library/TrafficControl/1.4/src/TrafficControl_min.css"
	rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="http://api.map.baidu.com/api?v=2.0&ak=WAbGUPB8LA7mB2QTy4G0n4e2"></script> 
	<script type="text/javascript" src="js/jquery-1.8.2.js"></script>
	<script type="text/javascript" src="dwr/engine.js"></script>
	<script type="text/javascript" src="dwr/util.js"></script>
	<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
	<script type="text/javascript">
	var get_data;
	var address;
	var sl=[0,0,0,0,0,0,0,0,0,0,0,0];
	dwr.engine.setAsync(false);
   		pushMessageCompont.getCountXYZ("${userId }", function(data) {
		if(data!=null&&data.length>0){
		    get_data=  eval ("(" +  data + ")");
		  /*   var geoc = new BMap.Geocoder()
			var num = 0;
			(function aa(i){
				if(i == get_data.length) {
					console.log('Num:'+num)
					return;
				}
				var point = new BMap.Point(get_data[i].longitude,get_data[i].latitude);
			    geoc.getLocation(point, function(rs){
			        var addComp = rs.addressComponents;
			        if(addComp.district == '黄埔区'){
			        	num++;
			        }
			        i++;aa(i);
			        return;
			    });  
			})(0); */
		}
	});
	dwr.engine.setAsync(true);
</script>
</head>
<body>
<input id="comName" name="Name" type="text"
								placeholder="" />
 <div id="allmap" style="width:800px; height:600px;"></div>
<script type="text/javascript" src="js/map_data.js"></script>
<!-- <script type="text/javascript" src="js/map_new.js"></script> -->
</body>
</html>