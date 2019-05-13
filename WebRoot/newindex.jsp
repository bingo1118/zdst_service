<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>首页--智能云消防</title>
<style type="text/css">
.title_tj {
    padding-left: 10px;
    margin-top: 10px;
}
</style>
<link rel="shortcut icon" href="images/fire.ico" />
<!-- <link type="text/css" rel="stylesheet" href="css/style.css"> -->
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<link href="css/TrafficControl_min.css" rel="stylesheet" type="text/css" />
<style type="text/css">
 body{overflow:hidden;}
</style>
</head>
<body>
<script type="text/javascript" src="js/jquery1.42.min.js"></script>
<script type="text/javascript">
/** 文档加载完 */
			$(function(){
			onPageLoad();
				/** 获取timer对应的jquery对象 */
				$("#timer").runtime();
			});
</script>
<div class="body_loading" style='display:none;'> 
 <div class="loading_content">
 <div><img src='images/loading.gif' /></div>
 <div class="loading_font" style='color:#999;'>飞速加载中...</div>
  </div>
</div>
 <div id="container" style='display:none;'>
 <!-- 地图 开始-->
 <div class="map2 allmap" style="margin-top:0;">
  <div class="index_head">
    <div class="yetou"><img src="images/yetou_logo.png" />
    <div class='index_logininfo'>
    <div class="user index_user">
        	<span>你好！欢迎 ${userId} 登录</span>
        	<span id="timer"> </span>
    </div>    
    <div class="user index_loginout">
     <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a>
     <a href="loginOut.do" class="zc">注销</a>
    </div>	
    </div>
    </div>
    <div class="more_img"></div>
</div>
 <div id="allmap"></div>
<!-- 信息查询时loading  -->
 <div id="index_loading">
 <div class="loading_content">
 <div><img src='images/loading.gif' /></div>
  <div class="loading_font">信息查询中...</div>
  </div>
</div>
 <!-- 地图结束 -->
 <!-- 左边统计表开始 -->
    <div class="chart">
     <ul>
      <li class="lunb1 setheight click_left"><div id="chart1" style="width: 100%; height: 100%; margin: 0 auto"></div>
      </li>
     <li class="lunb2 setheight water">
     <div class="water_content">
      <div class="water_left">
      <div class="water_totle">
          <p class="today">消防水设备总数</p>
          <div class="num w_totle">
          </div>
       </div>
      <!-- 消防水设备类型和数量 -->
        <ul id="watert_type">
        </ul>
     </div>
     <div class="water_left" style="margin-left: 6%;">
       <div class="water_totle">
          <p class="today">水设备今日报警数</p>
          <div class="num w_alert">
             <span>0</span>
             <span>0</span>
             <span>0</span>
             <span>1</span>
             <span>2</span>
             <div class="clear"></div>
          </div>
       </div>
        <ul id="water_state">
         <div class="w_detail"><dd class='mec_name'>在线</dd><dd class="mec_num">20</dd>
          <div class="clear"></div>
         </div>
         <div class="w_detail"><dd class='mec_name'>异常</dd><dd class="mec_num">20</dd>
          <div class="clear"></div>
         </div>
        <!--  <div class="w_detail"><dd class='mec_name'>报警</dd><dd class="mec_num">20</dd>
          <div class="clear"></div>
         </div> -->
        </ul>
     </div>
     </div>
     </li>
      <li class="lunb3  click_right"><div id="chart3" style="width: 100%; height: 100%; margin: 0 auto"></div></li>
     </ul>
    </div>
 <!-- 左边统计表结束 -->    
 <div style="clear:both;"></div>
  <li class="nav_dw">
 <!-- 右上角的导航 -->
 <div class="right_nav">
   <div class="containers">
  <ul class="navigation">
    <li>
        <a href="infoweb.do" class="to bottom">
          <i class="fa fa-home">
         <div><img alt="" src="images/i1.png"></div>       
         <div>信息管理</div> 
         </i>
          <span><div><img alt="" src="images/i1.png"></div>       
         <div>信息管理</div> </span>
        </a>
    </li>
    <li>
        <a href="deviceState.do" class="to bottom">
          <i class="fa fa-qrcode">
           <div><img alt="" src="images/i2.png"></div>       
           <div>智能监控</div>
          </i>
          <span><div><img alt="" src="images/i2.png"></div>       
           <div>智能监控</div></span>
        </a>
    </li>
    <li>
        <a href="mapInfo.do" class="to bottom">
          <i class="fa fa-qrcode">
          <div><img alt="" src="images/i3.png"></div>
          <div>预警地图</div></i>
          <span><div><img alt="" src="images/i3.png"></div>
          <div>预警地图</div></span>
        </a>
    </li>
    <li>
        <a href="statisticAnalysis.do" class="to bottom">
          <i class="fa fa-pencil">
      <div><img alt="" src="images/i4.png"></div>     
      <div>统计分析</div>
          </i>
          <span><div><img alt="" src="images/i4.png"></div>     
      <div>统计分析</div></span>
        </a>
    </li>
    <li>
        <a href="patrolRecord.do">
          <i class="fa fa-heart">
            <div><img alt="" src="images/i6.png"></div>     
            <div>维保系统</div>
      </i>
          <span> <div><img alt="" src="images/i6.png"></div>     
            <div>维保系统</div></span>
        </a>
    </li>
    <li>
        <a href="videoSurveillance.do" class="to bottom">
          <i class="fa fa-pencil">
           <div><img alt="" src="images/i5.png"></div>     
           <div> 视频监控</div>
         </i>
          <span><div><img alt="" src="images/i5.png"></div>     
           <div> 视频监控</div></span>
        </a>
    </li>
  </ul>
</div>
   <div style="clear:both;"></div>
 </div>
 </li>
<!--  右边开始 -->
 <div class="right_content">
<!--旭日图  -->
<li  style='height:60%;overflow:hidden;position: relative;'>
<div class="tables" style="background:rgba(0,0,0,0)">
 <div id="chart2" style="width: 100%; height: 100%; margin: 0 auto"></div> 
 <div id='chart_detail' style="width: 100%; height: 100%; margin: 0 auto;"></div>
</div>
</li>
 <!--报警信息开始  -->
<li class='alert_info'>
<div class="alert_img"></div>
<div class="alert">
<div class="a_title">未处理报警信息的数据</div>
<div class="alert_table">
 <div class="table_title">
   <ul>
     <li class="tabs2">设备名称</li>
     <li class="tabs3">设备编号</li>
     <li class="tabs4">报警类型</li>
     <li class="tabs5">报警时间</li>
     <li class="tabs6" style="border:none;">操作</li>
   </ul>
 </div>
  <div style="clear:both;"></div>
 <div class="table_content">
  <div id="demo">
 <ul id="demo1">
 
 </ul>
</div>
 </div>
</div>
</div>
 </li>
 </div>
<!-- 右边结束 --> 
 </div>
 <div class="popup popup_baojing" id="popupWired" style='display:none;'>
	<h1 class='alert_title'>有线系统报警</h1>
	 <div class='popop_content_text'>
	 <div id="info"></div>
	 <div>
	 <div class='cl_person'>处理人：</div>
	 <div class='cl_content'><input name="name"  placeholder='请输入处理人姓名'  type="text" id="handlePerson_yx" /></div>
     </div>
     <div style='clear:both;'></div>
     <div class='wy_content'>
     <div class='cl_person'>备注：</div><div class='cl_content'  placeholder='请输入处理情况' style='height:40px;'><textarea rows="5" cols="" id="treatmentReasons_yx"></textarea></div>
	 </div>
	  <div style='clear:both;'></div>
	</div>
	<div class="popop_btn" style='margin-top:15px;'>
		<a  class="guanbiWired">暂不处理</a> 
		<!-- <a class="chuliWired" >现在处理</a> -->
	</div>
</div> </div> </div>
   <!-- 报警声音 -->
<audio id="audio" src="images/alarm.mp3" controls="controls" preload="auto">您的浏览器不支持 audio 标签。</audio>
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
<script type='text/javascript' src='http://cdn.goeasy.io/goeasy.js'></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=WAbGUPB8LA7mB2QTy4G0n4e2"></script>
<!-- <script type="text/javascript" src="js/TrafficControl_min.js"></script> -->
<!-- <script type="text/javascript" src="js/TextIconOverlay_min.js"></script> -->
<!-- <script type="text/javascript" src="js/infoBox.js"></script> -->
<!-- <script type="text/javascript" src="js/MarkerClusterer.js"></script> -->
<!-- <script type="text/javascript" src="js/jquery.js"></script> -->
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/newindex.js"></script>
<!-- <script type="text/javascript" src="js/jquery.mousewheel.js"></script> -->
<script type="text/javascript" src="js/jquery-timer-1.0.js"></script>
<script type="text/javascript" src="js/LAB.js"></script>
<script src="js/layui.js" charset="utf-8"></script>
<script type="text/javascript">
$LAB
.script("js/infoBox.js", "js/TrafficControl_min.js", "js/TextIconOverlay_min.js","js/MarkerClusterer.js")
.wait();
//推送代码
function onPageLoad() {
	var userid = "${userId}";
	var appkey = "${appkey}";
	if (appkey == "" || appkey == null) {
		dwr.engine.setAsync(false);
		pushMessageCompont.onPageLoad("${userId }", function(data) {
			appkey = data;
		});
		dwr.engine.setAsync(true);
	}
	var channels=null;
	if (channels == null) { //获取订阅的频道：用户所对应的areaIds
	    dwr.engine.setAsync(false);
        pushMessageCompont.getAreaIds("${userId}",function(data) {
            channels = data;
        });
        dwr.engine.setAsync(true);
	}
	var goEasy = new GoEasy({
		appkey : appkey
	});
	for (var i=0;i<channels.length;i++) {
	     goEasy.subscribe({
            //channel : userid,
            channel : channels[i],
            onMessage : function(message) {
                 showMessage(message.content,null);
            }
        });
	}
}
	function showMessage(sendMessages, clickEvent) {
	    var getstr_data=localStorage.getItem("key");
		var strText = sendMessages.charAt(sendMessages.length - 2);
		var u1 = "flashAlarm.do?methods=smoke";
		var u2 = "flashAlarm.do?methods=fault";
		var u4 = "loginLoss.do";
		var u3;
		$("#popupWired .popop_content").empty();
		if (strText == "0") {
			u3 = u1;
		} else if (strText == "2") {
			u3 = u2;
		} else if (strText == "4") {
			u3 = u4;
			alert("你的账号已在其他地方被强制登录，你已被踢下线！");
			window.location.href = u3;
		} else {
			u3 = u1;
		}
//有警报时执行的函数		
		$.ajax({
					type : "GET",
					url : u3,
					dataType : "json",
					async : false,
					success : function(data) {
			      var datas = eval(data);
if (u3 == u1) {			      
	if(datas!=null){
		   var alert_str="";
	for(i=0;i<datas.length;i++){
		alert_str+="<li>";
		alert_str+='<dt class="tabs2" title="'+datas[i].devName+'">'+datas[i].devName+'</dt>';
		alert_str+='<dt class="tabs3" title="'+datas[i].devMac+'">'+datas[i].devMac+'</dt>';
		alert_str+='<dt class="tabs4" title="'+datas[i].alarmType+'">'+datas[i].alarmType+'</dt>';
		alert_str+='<dt class="tabs5" title="'+datas[i].alarmTime+'">'+datas[i].alarmTime+'</dt>';
		alert_str+='<dt class="tabs6" id="'+datas[i].devMac+'" alertime="'+datas[i].alarmTime+'" onclick="lookat(this)">查看</dt>';
		alert_str+="</li>";
		$('.alert').addClass('alert_bg_active');
		$('.alert_img').show();
		$('.alert_img').addClass('alert__img_active');
		var audio = document.getElementById("audio");
				audio.currentTime = 0;
				$('#audio')[0].play();
		setTimeout("$('#audio')[0].pause();",60000);
				}
	}
	$('#demo1').html(alert_str);
	$('#demo').scrollTop(0);
					}
	else if(u3==u2){
	$('#popupWired').show();
	var strTd = "<h2>"+datas[0].named+"</h2>"+
	 "<p>报警原因："+datas[0].alarmType+"</p>"+
	 "<p>报警类型："+datas[0].devName+"</p>"+
     "<p>报警时间："+datas[0].alarmTime+"</p>"+
     "<p>终端（编号）："+datas[0].areaName+"</p>";
     var btn_cl='<a class="chuliWired" time="'+datas[0].alarmTime+'" devmac="'+datas[0].devMac+'" areaname="'+datas[0].areaName+'"  onclick="do_alert_yx(this)" >现在处理</a>';
			$("#info").append(strTd);	
			 $(".popop_btn").append(btn_cl);
				var audio = document.getElementById("audio");
				audio.currentTime = 0;
				$('#audio')[0].play();
		setTimeout("$('#audio')[0].pause();",60000);							
	}				
					},
					error : function() {
						console.log("请求失败");
					}
				});
	}
</script>
</body>
</html>




