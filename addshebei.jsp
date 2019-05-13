<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link type="text/css" rel="stylesheet" href="css/style1.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>		
		<title></title>
		
<style type="text/css">
li{ list-style: none;}
	.clo{height:10px;width:10px;background:#f00;  position: absolute;}
	#ic1{bottom:0px;left:0px;}#ic2{bottom:0px;left:15px;}#ic3{bottom:0px;left:30px;}#ic4{bottom:0px;left:45px;}
	.sheb{position: relative; width:380px;height:183px;background:url(images/pmt.jpg); }
	#ic0{bottom:0px;left:60px;}
</style>
		
	</head>
	<body>
 <div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="">
        </div>
        <div class="user fr">
        	<span> </span>
        	<span id="timer"> </span>
            <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm"></a>
            <a href="loginOut.do" class="zc"></a>
        </div>
    </div>
</div>
	<div class="contain_tj">
		<div class="title_tj">
			<a href="queryItems_test.do"></a>
		</div>	
	<div class="sheb" id="sb">
	<div id="she_show">
	<li id="ic0" class="clo"></li>
	<li id="ic1" class="clo"></li>
	<li id="ic2" class="clo"></li>
	<li id="ic3" class="clo"></li>
	<li id="ic4" class="clo"></li>
	</div>
	<div id="btn"></div>
	</div>
	</div>
	</body>
    <script type="text/javascript">  
/* var oDiv = document.getElementById("ic1"); 
var oDiv2 = document.getElementById("ic2");  */
 var num=document.getElementById('she_show').children.length;
var ops=[];
var res=[];
var aLi = document.getElementsByTagName("li");  
for(i=0;i<num;i++){
   idname="ic"+i
   divx=document.getElementById(idname);
   cz(divx,i);
}
function cz(obj,i){
 obj.onmousedown=function(ev)   
           {
            var oEvent = ev;   
            var disX = oEvent.clientX - obj.offsetLeft;  
            var disY = oEvent.clientY - obj.offsetTop;  
            document.onmousemove=function (ev)  
            {  
                oEvent = ev;  
                obj.style.left = oEvent.clientX -disX+"px";  
                obj.style.top = oEvent.clientY -disY+"px";  
            }  
            document.onmouseup=function()  
            {  
               document.onmousemove=null;  
               document.onmouseup=null;  
               var x=pers(obj.style.left,obj.style.top);
               ops[i]={id:i};
               res[i]=Object.assign(ops[i],x);
            }  
        }}


function pers(x,y){
   var map_w=document.getElementById("sb").offsetWidth;
   var map_h=document.getElementById("sb").offsetHeight;
   x=parseInt(x);
   y=parseInt(y);
   w=x/map_w;
   h=y/map_h;
   w=w.toFixed(4);
   h=h.toFixed(4);
   w=w*100;
   h=h*100;
   return {left:w,top:h};
}        
$(function(){
   $('#btn').click(function(){
      console.log(res);
   });
});
    </script>  	
</html>
