<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>无标题文档</title>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
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
		var re_Macs = new Array();
   		var smokeMacs = new Array();
   		$(".smokeMac").each(function(i){
   			if(this.checked){
   				//smokeMacs[i] = $(this).val();
   				smokeMacs.push($(this).val());
   				var a=$(this).parent('td').siblings('#re_mac').html();
   				re_Macs.push(a);
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
     if(smokeMacs.length <1){
     	alert("没有选择执行操作！不能执行该操作");
     	return false;
     }
     else{
   	    var msg="";
     	if(opFlag==0){
     		msg="你确定要进行删除操作吗？mac:"
     	}
     	var confirmflag = window.confirm(msg);
     	if(!confirmflag){
     		return false;
     	}
     	else{
     		/* dwr.engine.setAsync(false);
     		pushMessageCompont.getPrivilege("${userId }", function(data) {
				if(data==1||data==3){
					alert("您的权限不够，如需删除操作请联系管理人员");
					opFlag = 1;
				}
			});
			dwr.engine.setAsync(true); */
     		if(opFlag==0){
     			//alert(smokeMacs);
     			//alert(re_Macs[0]);
 	    		$.ajax({
 	    			type:"get",
 	    			url:"deleteBuyerByIds.do?smokeMacs="+smokeMacs+"&repeater="+re_Macs[0],
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
   				var a=$(this).parent('td').siblings('#re_mac').html();
   				re_Macs.push(a);
   			}
   		});
   		//flag 为1时是恢复 0是删除
	 var smokeMac = document.getElementsByName("smokeMac");
	 var flag = true; 
  
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

<body style="width:100%;background-color:#fff;min-width:100%;">

<%-- <div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智慧云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span>你好！欢迎 ${userId} 登录</span>
        	<span id="timer"> </span>
            <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a>
            <a href="loginOut.do" class="zc">注销</a>
        </div>
    </div>
</div> --%>
<div class="content_table">
	<form id="deleteSmokeByMac" >
            	<div class="dydc">
                    <a href="javascript:void(0);" id="print">打印</a>
                    |
                    <a href="javascript:void(0);" id="daochu_items">导出</a>
                    |
                    <!-- <input type="button" id="BT_Delete" name="BT_Delete" onclick="return deleteAll('0')" value="批量删除" />  --> 
                    <a href="javascript:void(0);" id="BT_Delete" name="BT_Delete" onclick="return deleteAll('0')">批量删除</a>      
           		</div>
            	<table id="idData">
            		
                	<tr>
                		<th width="30px;"><input type="checkbox" name="selectSmokeMacAll" onclick="checkSmokeMac(this)"></th>
                    	<th>序号</th>
                        <th><ul><li>烟感mac</li></ul></th>
                        <th><ul><li>烟感名称</li></ul></th>
                        <th>烟感状态</th>
                        <th>区域管理终端状态</th>
                        <th>终端号</th>
                        <th>负责人1</th>
                        <th>联系方式1</th>
                        <th>添加时间</th>
                        <th>地址</th>
                        <th>所属区域</th>
                        <th>操作</th>
                    </tr>
                     <c:forEach items="${listItems}" var="item" varStatus="status">
	                    <tr>
	                    	<td><input type="checkbox" class="smokeMac" name="smokeMac" value="${item.smokeMac}"  /></td>
	                    	<td>${status.index+1}</td>
	                        <td>${item.smokeMac}</td>
	                        <td>${item.smokeName}</td>
	                        <td>${item.lineState}</td>
	                        <td>${item.repeaterState}</td>
	                        <td id="re_mac">${item.repeaterMac }</td>
	                        <td>${item.principal1}</td>
	                        <td>${item.principal1Phone}</td>
	                        <td>${item.addTime}</td>
	                        <td width="300px">${item.address}</td>
	                        <td>${item.areaName }</td>
	                   		<td> <a href="javascript:void(0);"  onclick="edit_mac('${item.smokeMac}');">编辑</a></td>
<!-- 	                   		<td> <a href="javascript:void(0);"  onclick="javascript: openWin('findSmokeBySmokeMac.do?smokeMacs=${item.smokeMac}','${item.smokeMac}',620,400); ">编辑</a> -->
<!-- 	                   		</td> -->
	    <!--window.open('findSmokeBySmokeMac.do?smokeMacs=${item.smokeMac}','200','200'); -->
	                    </tr>
                     </c:forEach>
                    
                </table>
                <div class="ul_pagenav" style="text-align:center;"> 
                     <ul id="record"></ul> 
                     <ul class="pagination" id="pagination"> 
                            
                     </ul>  
                 </div>
                 
         </form>        
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
<script>
		$(function(){
			$("#daochu_items").click(function(){
				ExportUtil.toExcel("idData");	
			});	
			
			var iframe_table_height = 600;   //$('#objId', parent.document)
			$("#iframe_table",parent.document).css('height',iframe_table_height);
		});
		
		
</script>
<script type="text/javascript">
	function searchPrivilege(){
	$.ajax({
  					type: "post",
	                url : "searchprivilege.do",
					dataType: "json",
					async: true,
					success:function(data){
						alert("请求成功" +eval(data.Code));
					 	console.log("请求成功。");
						var json = eval(data);
						if(json.Code == 4){
							alert(4);
						} else{
							alert("其他权限不能删除")
						}
					},
  					error:function(data){
  						alert("网络错误");
  					}
			});
	}	
</script>
<script src="js/layui.js"></script>
<script>
//一般直接写在一个js文件中
function edit_mac(mac){
layui.use(['layer', 'form'], function(){
 layer.open({
  type: 2, 
  content: 'findSmokeBySmokeMac.do?smokeMacs='+mac, //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
  area:['620px','450px'],
  offset: '80px',
  title: '修改烟感信息',
}); 
});
}
</script> 

