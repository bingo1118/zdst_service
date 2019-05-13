<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<title>设置设备位置</title>
<style type="text/css">
.title_tj {
    padding-left: 10px;
    margin-top: 10px;
}
li{ list-style: none;}
	.clo{height:10px;width:10px;background:#f00;  position: absolute;bottom:-20px;}
/* 	#ic1{left:0px;}#ic2{left:15px;}#ic3{left:30px;}#ic4{left:45px;}#ic0{left:60px;} */
	.sheb{position: relative; width:379px;height:236px; background-repeat: no-repeat; margin:30px auto;clear:both;background-size: 100%;	}
	#btn2{clear:both; float:right; margin-right:50px;}
	.search{margin:20px auto; border-bottom:1px solid #eee;}
	.search li{float:left;}
	.sb_left{width:49%;float:left; min-height: 480px;  border-right: 1px solid #eee;}
	.sb_right{width:49%;float:right; position: relative; min-height:500px;}
	.sb_right img{width:100%;}
	.show_img{width:50%;margin-left:auto;margin-right:auto;}
	#btn_image{ position: absolute; right:10%; bottom:10%;}
	.sb_title{margin-left:20px;}
	.sb_title2{margin-bottom:20px;}
	.imgs{width:60%;margin:0 auto;}
</style>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery.js"></script>
<script src="js/layui.js" charset="utf-8"></script>
</head>
<body>
 <div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智慧云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span> </span>
        	<span id="timer"> </span>
            <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a>
            <a href="loginOut.do" class="zc">注销</a>
        </div>
    </div>
</div>
<div class="title_tj">
  <a class="layui-btn layui-btn-small" style="background:#3b66cc; color:#fff;" href="queryItems_test.do">返回</a> 
</div>
	<div class="contain_tj">
        <div class="search">
            <!-- <form class="layui-form" action="" method="post"> -->
<!-- 				<li>
				  <div class="layui-form-item">
				    <label class="layui-form-label">设备Mac</label>
				    <div class="layui-input-inline">
				      <input type="text" name="title" required  lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">
				    </div>
				  </div>
				</li> -->
				<!-- <li>
				 <div class="layui-form-item">
				    <label class="layui-form-label">类型</label>
				  </div>
				</li> -->
				<%-- <li>
				 <div class="layui-form-item">
				    <label class="layui-form-label">区域</label>
				    <div class="layui-input-block">
				      <select name="" lay-verify="required">
				        <option value=""></option>
				        <c:forEach var = "area"  items ="${areaAndRepeaters }">
				                <option value="${ area.areaId}">${area.areaName }</option>
				        </c:forEach>
				      </select>
				    </div>
				  </div>
			     </li> --%>
		<!-- 	     <li>
				  <div class="layui-form-item">
				    <label class="layui-form-label">设备类型</label>
				  </div>
				 </li> -->
		<!-- 	     <li>
				  <div class="layui-form-item">
				    <label class="layui-form-label">设备状态</label>
				  </div>
				</li> -->
			   <!--   <li>  
			         <div class="layui-form-item">
				    <div class="layui-input-block">
				         <button class="layui-btn" lay-submit lay-filter="formDemo">查询</button> 
				    </div>
				  </div>
				</li>
				  <div style="clear:both;"></div> 
            </form> -->
        </div>
<div class="imgs">
 <form class="layui-form" method="post" enctype="multipart/form-data" id="fileForm" >
	<div class="sb_title2">上传平面图</div>
	<div id="layer-photos-demo" class="layer-photos-demo">  
	<div class="layui-form-item" style="width:400px">
	       <label class="layui-form-label">请选择区域</label>
	       <div class="layui-input-block">
		        <select name="planArea"  id="selectArea"  required="required">
		            <c:forEach var = "area"  items ="${areaAndRepeaters }">
		                 <c:if test="${area.areaId==plan.areaid}">
		                      <option value="${ area.areaId}" selected="selected">${area.areaName }</option>
		                 </c:if>
		                 <c:if test="${area.areaId != plan.areaid }">
                              <option value="${ area.areaId}" >${area.areaName }</option>
                         </c:if>
		            </c:forEach>
		        </select>
		    </div>
		</div>
        <input type="file" name="plan"  style="display:none;" class="hidden" value=""id="images"/>
        <a href="javascript:void(0);" class="file_img layui-btn">选择图片</a>  
        <!-- <a href="javascript:void(0);" class="img_look">预览</a> -->
     <%--    <div class="show_img"> 
             <img src="${plan.planPath} "  layer-src=""  class="img1 fr hidden" alt="" >  
         </div> --%> 
	</div> 
	<!-- 有平面图 -->
	<c:if test="${plan.planPath != ''}">
	   <div class="sheb" id="sb" style="background-image:url(../plans/${plan.planPath }); background-repeat: no-repeat">
	</c:if>
	<!-- 没有平面图 -->
	<c:if test="${plan.planPath == ''}">
	   <div class="sheb" id="sb" style="background-image:url(images/null.jpg); background-repeat: no-repeat">
	</c:if>
	
	   <div id="in_plan">
	       <c:if test="${plan.lstpp!=null}">
	           <c:forEach items="${plan.lstpp }" var="pp">
	               <li class="clo inplan" mac="${pp.mac }" macX="${pp.macX }" macY="${pp.macY }"></li>
	           </c:forEach>
	       </c:if>
	   </div>
		<div id="she_show">
		      <c:if test="${lstSmoke!=null }" >
			      <c:set var="i" value="0" />
			      <c:forEach items="${lstSmoke}" var="smoke">
			          <li id="ic${i}"    class="clo" mac="${smoke.mac}" style="left:${i*15}px"></li>
			          <c:set var="i" value="${i+1}"/>
			      </c:forEach>
		      </c:if>
		</div>
	</div>
	
	 <button class="layui-btn"  id="btn2">保存设置</button>
  </form>

</div>
	</div>
	</div>
</body>
</html>


<script type="text/javascript" src="js/jquery1.42.min.js"></script>

<script type="text/javascript">  
/* var oDiv = document.getElementById("ic1"); 
var oDiv2 = document.getElementById("ic2");  */
	//var num=document.getElementById('she_show').children.length;
	var ops=[];
	var res=[];
	var mac=[];
	var inplan=[];
	mac = document.getElementsByClassName("clo");
	inplan= document.getElementsByClassName("inplan");
	
	for(i=0;i<inplan.length;i++){
	   show(inplan[i]);
	}
	
	var num = mac.length;
	var aLi = document.getElementsByTagName("li");  
	for(i=0;i<num;i++){
	   //idname="ic"+i;
	  // divx=document.getElementById(idname);
	  divx = mac[i];
	   cz(divx,i);
	}
	
	function show(obj){
	    var map_w=document.getElementById("sb").offsetWidth;
        var map_h=document.getElementById("sb").offsetHeight;
        var macx=obj.getAttribute("macx");
        var macy=obj.getAttribute("macy");
        obj.style.left=macx*map_w/100+"px";
        obj.style.top=macy*map_h/100+"px";
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
               var mac=obj.getAttribute("mac");
               var x=pers(obj.style.left,obj.style.top,mac);
               //if(obj.style.left<"0px"||)
               ops[i]={id:i};
               res[i]=Object.assign(ops[i],x);
            }  
        }
     } 


	function pers(x,y,z){
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
	   return {left:w,top:h,mac:z};
	}    
	    
    $(function(){   
       $("#btn2").click(function(){
     // $('.layui-btn').on('click','#btn2', function () {  
        var bg=$('#sb').css("background-image");
        if(bg=='url("")') {
            alert("请选择图片");
            $('#images').attr("required","required");
        } else{
            $('#images').removeAttr("required");
        }
        //console.log(res);
 /*       for(var i =0;i<res.length;i++){
           // console.log(res[i].id);
           alert(res[i].id);
           console.log(res[i]);
        }
        alert("pause"); */
      var smokes=JSON.stringify(res);
      //$('#fileForm').append('<input type="hidden" name="smokes" value="'+smokes+'"/>');
      $('#fileForm').attr('action','savePlan?smokes='+smokes);
      $('#fileForm').submit();
    /*   var planArea=$('#selectArea option:selected').val();
      console.log(smokes+planArea);
      $.ajax({
              type: "post",
              url: "savePlan",
              async: false,
              dataType: "json",
              data: { 'planArea':planArea,'smokes':smokes },
              success : function() {
	                alert("添加平面图成功");
	            }  
       });   */
    });
    
//上传图片   
   $('.file_img').live('click', function (e) {  
	    e.preventDefault();  
	    $('#images').trigger('click');  
    })  
    
	$('#images').live("change",function(){  
	/* $('.file_img').html('已上传');   */
	/* $('.img1').show(); */
		var srcs = getObjectURL(this.files[0]);  
		 $('.img1').attr("src",srcs).attr('layer-src',srcs);  
		$('.img_look').hide().next('.img1').show();
		$('#sb').css("background-image",'url('+srcs+")");
		layui.use('layer', function(){  
		        var $ = layui.jquery  
		        ,layer = layui.layer;  
		        layer.photos({  
		            photos: '#layer-photos-demo',  
		            anim: 1  
		            });  
		  
		  });  
    });
   
	function getObjectURL(file) {  
	        var url = null;  
	        if (window.createObjectURL != undefined) {  
	            url = window.createObjectURL(file)  
	        } else if (window.URL != undefined) {  
	            url = window.URL.createObjectURL(file)  
	        } else if (window.webkitURL != undefined) {  
	            url = window.webkitURL.createObjectURL(file)  
	        }  
	        return url  
	 }
	   
});
</script>  	
<script>
//Demo
    var planArea;
	layui.use('form', function(){
	  var form = layui.form; 
	  form.on('select',function(d){
	      planArea = d.value;
	      window.location.href="getPlan.do?planArea="+planArea;
	  });
	});
	
</script>
      