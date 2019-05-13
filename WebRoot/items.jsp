<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>智能监控</title>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery1.42.min.js"></script>
<script type="text/javascript">
	//用户:全部选中/全部不选中
   function checkSmokeMac(user){
	  var smokeMac = document.getElementsByName("smokeMac");
      for(var i=0;i<smokeMac.length;i++){
     	 smokeMac[i].checked = user.checked;
      }
   }
	
	//批量删除
	function deleteAll(opFlag){
	
   		var smokeMacs = new Array();
   		$(".smokeMac").each(function(i){
   			if(this.checked){
   				//smokeMacs[i] = $(this).val();
   				smokeMacs.push($(this).val());
   			}
   		});
   		//flag 为1时是恢复 0是删除
	 var smokeMac = document.getElementsByName("smokeMac");
	 var flag = false;	
     for(var i=0;i<smokeMac.length;i++){
     	if(smokeMac[i].checked){
     		flag = true;
     	} 
     }
     if(smokeMacs.length <1 ){
     	alert("没有选择执行操作！不能执行该操作");
     	return false;
     }
     else{
   	    var msg="";
     	if(opFlag==0){
     		msg="你确定要进行批量删除吗？mac:";
     	}else{
     		msg="你确定要进行批量恢复吗？";
     	}
     
     	var confirmflag = window.confirm(msg);
     	
     	if(!confirmflag){
     		return false;
     	}
     	else{
     		if(opFlag==0){  
     		alert(smokeMacs);		
 	    		$.ajax({
 	    			type:"get",
 	    			url:"deleteBuyerByIds.do?smokeMacs="+smokeMacs,
 	    			dataType:"json",
 	    			/* data:{
 	    				smokeMacs:smokeMacs
 	    			}, */
 	    			success:function(){
 	    				alert("删除成功");
 	    				window.location.reload();
 	    			},
 	    			error:function(){
 	    				alert("删除失败");
 	    				window.location.reload();
 	    			}
 	    		});
     		}
     		return true;
     	}
     }
   }	
		
	
	//修改设备的信息
	function updateSmokeBySmokeMac(opFlag){
   		var smokeMacs = new Array();
   		$(".smokeMac").each(function(i){
   			if(this.checked){
   				//smokeMacs[i] = $(this).val();
   				smokeMacs.push($(this).val());
   			}
   		});
   		//flag 为1时是恢复 0是删除
	 var smokeMac = document.getElementsByName("smokeMac");
	 var flag = true; 
  		alert("smoke的长度：" +smokeMacs.length);
     if(smokeMacs.length >1 ){
     	alert("只能选择单个设备进行修改")
     	flag = false;
     }
     
     if(smokeMacs.length <= 0){
     	alert("没有选择执行操作！不能执行该操作");
     	return false;
     }
     if(flag == true){
   	    var msg="";
     	if(opFlag==0){
     		msg="你确定要对设备进行修改吗？"
     	}else{
     		msg="你确定要进行批量恢复吗？"
     	}
     	var confirmflag = window.confirm(msg);
     	if(!confirmflag){
     		return false;
     	}
     	else{
     		if(opFlag==0){
 	    		  $.ajax({
 	    			type:"get",
 	    			url:"findSmokeBySmokeMac.do?smokeMacs="+smokeMacs,
 	    			/* data:{smokeMacs:smokeMacs}, */
 	    			dataType:"json",	    			
 	    			success:function(data){
 	    				alert("修改成功");
 	    				window.location.reload();
 	    			},
 	    			error:function(){
 	    				alert("修改失败");
 	    				window.location.reload();
 	    			}
 	    		});  
     		}
     		return true;
     	}
     }
   }	

function openWin(url,name,iWidth,iHeight) { 
            //获得窗口的垂直位置 
            var iTop = (window.screen.availHeight - 30 - iHeight) / 2; 
            //获得窗口的水平位置 
            var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; 
            window.open(url, name, 'height=' + iHeight + ',innerHeight=' + iHeight + ',width=' + iWidth + ',innerWidth=' + iWidth + ',top=' + iTop + ',left=' + iLeft + ',status=no,toolbar=no,menubar=no,location=no,resizable=no,scrollbars=0,titlebar=no'); 
        }

</script>
</head>

<body class="clearfix">
 <div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智慧云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span>你好！欢迎 ${userId} 登录</span>
        	<span id="timer"> </span>
           <!--  <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a> -->
            <a href="loginOutWelcome.do" class="zc">注销</a>
        </div>
    </div>
</div>
<div class="container">
	<input type="hidden" id="userId" name="userId" value="${userId }"/>
    <div class="content clearfix">
    	<div class="content_left fl">
        	<div class="title">设备状态</div>
            <ul class="fenlei">
            <c:forEach items="${itemsList}" var="item">
	            <li class="xitong">
	            	<span>${item.areaName }</span>
	            	<ul>
		            	 <c:forEach items="${item.listMac}" var="lists">
			            	
			            		<li>
		                        	<a href="javascript:void(0);"  parameter="queryItems.do?repeater=${lists}">${lists}</a>
		                        </li>
			            	
		            	</c:forEach>
		            </ul>
	            </li>
            </c:forEach>
            </ul>
        </div>
        <div class="content_right fr">
        	<div class="content_search">
        	<!-- action="${pageContext.request.contextPath }/selectItems.do" method="post" 
            <form >
            	<label>区域管理终端mac：
                	<input type="text" id="repeaterMac" placeholder="请输入" name="repeaterMac"/>
                </label>
                <label>烟感mac：
                	<input type="text" id="smokeMac" placeholder="请输入" name="smokeMac"/>
                </label>
                <label style="position:relative; left:15px; ">
                     <button class="ser" parameter="selectItems.do"></button> 
                </label>
            </form>

            <form id="itemsForm">
            	<label>区域管理终端mac：
                	<input type="text" placeholder="请输入" name="repeaterMac" id="repeaterMac"/>
                </label>
                <label>烟感mac：
                	<input type="text" placeholder="请输入" name="smokeMac"/>
                </label>
                <label style="position:relative; left:15px; ">
                    <input type = "button" class="ser" id="btn" value="tijiao"></input>
                </label>
            </form> -->
            <form id="itemsForm" action="">
            	<label>区域管理终端mac：
                	<input type="text" placeholder="请输入" name="repeaterMac" id="repeaterMac"/>
                </label>
                <label>烟感mac：
                	<input type="text" placeholder="请输入" name="smokeMac" id="smokeMac"/>
                </label>
                <label style="position:relative; left:15px; ">
                    <button class="ser" id="submit" type="submit"></button>
                </label>               
            </form>
            <c:if test="${privilege==4 }">
<div class="layui-btn-group" style="margin-top:20px; margin-left:30px;">
  <a href="queryRepeaterInfo.do" style="background:#3b66cc;" class="layui-btn  layui-btn-small">查看主机</a>
  |<a href="bindSmoke.jsp" style="background:#3b66cc;" class="layui-btn  layui-btn-small">绑定烟感</a>
  |<a href="main_list_items.jsp" style="background:#3b66cc;" class="layui-btn  layui-btn-small">绑定声光</a>
  |<a href="bindCamera.jsp" style="background:#3b66cc;" class="layui-btn  layui-btn-small">绑摄像头</a>
  |<a href="bindRepeater.jsp" style="background:#3b66cc;" class="layui-btn  layui-btn-small">绑定主机</a>
  |<a href="updateMapInfo.jsp"  style="background:#3b66cc;" class="layui-btn  layui-btn-small">修改坐标</a>
  |<a href="user_set_privilege.jsp"  style="background:#3b66cc;" class="layui-btn  layui-btn-small">修改权限</a>
  |<a href="updateParend.jsp"  style="background:#3b66cc;" class="layui-btn  layui-btn-small">修改区域</a>
  |<a href="resetTelUser.jsp"  style="background:#3b66cc;" class="layui-btn  layui-btn-small">修改联系人</a>
  |<a href="editPlan.do"  style="background:#3b66cc;" class="layui-btn  layui-btn-small">楼层平面图</a>
  |<a href="updateTxt.jsp" style="background:#3b66cc;" class="layui-btn  layui-btn-small">开/关短信通知</a>

</div>
<!--             	<a href="queryRepeaterInfo.do">查看主机</a> -->
<!--             	|<a href="bindSmoke.jsp">绑定烟感</a> -->
<!--             	|<a href="main_list_items.jsp">绑定声光</a> -->
<!--             	|<a href="updateMapInfo.jsp">修改坐标</a> -->
            </c:if>
            <a href="updateParent.do" class="area"><div class="a_icon"><img src="images/setting.png" /></div><div class="a_font">区域设置</div></a>
            <a href="addUser.do" class="tianjia"></a>
            </div>
            <div id="div_table" style="display:none;">
            	<div class="content_table">
            	<div class="dydc">
                    <a href="javascript:void(0);" id="print">打印</a>
                    |
                    <a href="javascript:void(0);" id="daochu_items">导出</a>
                    |
                    <a href="javascript:void(0);" id="BT_Delete" name="BT_Delete" onclick="return deleteAll('0')">批量删除</a>      
           		</div>
            	<table id="idData" class="idData">
                	<tr>
                    	<th width="30px;"><input type="checkbox" name="selectSmokeMacAll" onclick="checkSmokeMac(this)"></th>
                    	<th>序号</th>
                        <th>烟感mac</th>
                        <th>烟感名称</th>
                        <th>烟感状态</th>
                        <th>区域管理终端状态</th>
                        <th>烟感最近一次心跳</th>
                        <th>添加时间</th>
                        <th>地址</th>
                        <th>所属区域</th>
                        <th>操作</th>
                    </tr>
                </table>
                <div class="ul_pagenav" style="text-align:center;"> 
                     <ul id="record"></ul> 
                     <ul class="pagination" id="pagination"> 
                            
                     </ul>  
                 </div>
            </div>
            </div>
          
         <iframe id="iframe_table2" src="list_items.jsp" class="iframe_table" widxth="100%" frameborder="0">
            	
            </iframe> 

            
        </div>
    </div>
</div>
</body>
</html>

<script type="text/javascript" src="js/base_tj.js"></script>
<script language="javascript" src="js/jquery.jqprint.js"></script>
<script type="text/javascript"> 
		$(document).ready(function() { 
			$("#print").click(function(){ 
				$(".content_table table").jqprint(); 
			}); 
		}); 
</script> 
<script src="js/Blob.js"></script>
<script src="js/FileSaver.min.js"></script>
<script src="js/ExportUtil.js"></script>
<script src="js/layui.all.js"></script>
<script>
		$(function(){
			$("#daochu_items").click(function(){
				ExportUtil.toExcel("idData");	
			});	
			var iframe_table_height = $(window).height();   //$('#objId', parent.document)
			$("#iframe_table2").css('height',iframe_table_height);
		$(".content_left li.xitong a").click(function(){
		$(this).parent("li").siblings("li").find("a").removeClass("active");
		$(this).addClass("active").parents("li.xitong").siblings().find("a").removeClass("active");
		var parameter = $(this).attr("parameter");
		var iframe_table = $("#iframe_table2").attr("src", parameter );
		$("#div_table").hide();
		$(".iframe_table").show();
	});
		});
		
</script>


