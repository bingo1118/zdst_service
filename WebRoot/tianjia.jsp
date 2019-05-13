<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>添加用户</title>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/drag.js"></script>
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link rel="stylesheet" type="text/css" href="css/css.css" />
<link rel="stylesheet" type="text/css" href="css/alpha.css" />
<script type="text/javascript" src="js/jquery.js"></script>

<script type="text/javascript" src="js/funtype_arr.js"></script>
<script type="text/javascript" src="js/funtype_func.js"></script>
<script type="text/javascript">
$(function(){
	$("#username").blur(function(){
	son_arr.splice(0,son_arr.length);//清除数组
	par_arr.splice(0,par_arr.length);//清除数组	
	showson();
			var username = $("#username").val();
			if(username==null||username==""){
				return false;
			}
			var address = $(".address");
			var addressNums = address.length;
			var getid;
			//alert("账号：" +username);
			$.ajax({
				type:"post",
				url:"findUserByUserId.do",
				data:{username:username},				
				dataType:"json",
				success:function(data){
					data = eval(data);
					if(data.errorCode == 1){
						//alert(data.list.length);
						$("#name").val(data.username);
						console.log(data);	
 for(var i=0; i < data.list.length;i++){
	for(var x in children){
			if(children[x].areaId==data.list[i]) getid=x;
		}
//  if(children[getid].parentId==undefined){
if(!children[getid].hasOwnProperty("parentId")||children[getid].parentId==undefined){
				par_arr.push(data.list[i]);	}
else{son_arr.push(data.list[i]);}
 						 showson();
						 
						} 
						//alert("请求成功:"+data.error+"---->" );
					}else{
						alert("请求成功:"+data.error);
					
					}
					
				},
				error:function(){
					alert("网络错误");
				}
			
			});

	});
//修改区域
$("#findareaidareaid").click(function(){
		if($( "#FuntypeSelected dd" ).children('li').length>1){
 			 alert("只能选中一个地址进行修改");
		}
		if($( "#FuntypeSelected dd").children('li').length< 1){
 			alert("请选择一个地址进行修改");
		}
		if($( "#FuntypeSelected dd").children('li').length== 1){	
          	var address1=$("#FuntypeSelected dd").find('li').attr('class'); 
			address1=address1.substr(1);
			window.open('findAreaByareaId.do?address='+address1,'200','200');
			//openWin('findAreaByareaId.do?address='+address1,'',400,200);
		}
	});
	
	
	
});


//删除
function deleteareabyid(){
		var areaIds = $( "#FuntypeSelected dd" ).children('li');
		
		if(areaIds.length > 1){
			alert("慎重，只能单个区域删除！");
		}
		if(areaIds.length < 1){
			alert("请选择要删除的区域！");
		}
		if(areaIds.length == 1){
		var confirmflag = window.confirm("确认删除该区域吗？")
		var address1=$("#FuntypeSelected dd").find('li').attr('class'); 
			address1=address1.substr(1);
		if(confirmflag){
			$.ajax({
				type:"get",
				url:"deleteareabyareaid.do?areaId="+address1,
				dataType:"json",
				success:function(){
					alert("删除成功");
					window.location.reload();
				},
				error:function(){
					alert("网络错误");
					window.lacation.reload();
				}
			});
		
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

<body onload="getAreaIds();">
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
	<div class="contain_tj">
		<div class="title_tj">
			<a href="queryItems_test.do">返回上一级</a>
		</div>
		<div class="conten_tj">
			<div class="conten_tj_tit">添加账号</div>
			<div class="conten_txt">
				<form id="add">
					<label>账&nbsp;&nbsp;号：
						<input type="text" name="username" id="username">
							<span><b style="color:#e90000;">*</b> 请填写正确的手机号</span>
					</label>
					<label>用户名称：
						<input type="text" name="name" id="name">
							<span><b style="color:#e90000;">*</b>请填写正确的用户名称</span>
					</label>
					<label>用户权限： 
						<select name="quanxian" id="quanxian">
							<option>管理员</option>
						</select>
						<span><b style="color:#e90000;">*</b>请选择用户权限</span>
					</label>
	 				<div class="label">添加一级区域(注释：添加一级区域是不会出现在下面的列表里面的,但是一级区域就比如说是一个大的区域。)：&nbsp;&nbsp;&nbsp;
	 					<a href="toAdd.do">点击添加一级新区域</a></br></br> 
	 						二级区域(注释：二级区域就是比较小的区域的，比如楼栋、楼层)&nbsp;&nbsp;&nbsp;&nbsp;
						<a href="toAdd2.do">点击添加二级新区域</a></br> </br>
							<input type="button" id="findareaidareaid" name="findareaidareaid" value="修改区域" />
						|| <input type="button" id="bt_delete" name="bt_delete" onclick="return deleteareabyid()" value="删除区域"> <br>
						以下是二级小区域
						<div class="clearfix" style="border:1px solid #ced5e7; background-color:#f1f2f9;">
							<span class="fl">可多选</span>
							<div class="add fl">
<div id="alphadiv">
<div id="drag">
		<div id="drag_con"></div><!-- drag_con end -->
	</div>
</div>
	
</div><!-- maskLayer end -->

<!-- alpha div end -->
<div id="sublist" style="display:none"></div>
<!-- 								<c:forEach items="${lists}" var="item"> -->
<!-- 									<input id="address" name="address" type="checkbox" -->
<!-- 										value="${item.areaId }" class="address" />${item.areaName } -->
<!--                         		 </c:forEach> -->
<!-- 静态代码开始 -->
						<c:forEach items="${plist }" var="item">
							<li class="have"><span>+</span>
								<input id="address1"  name="address1" type="checkbox" value="${item.key }" class="address" />${item.value }黄埔区
								<div class="sm_area">
									<c:forEach items="${lists }" var="areaList">
										<c:if test="${areaList.parentId==item.key && not empty areaList.areaName }">
											<dd><input id="address" name="address" type="checkbox" value="${areaList.areaId }" class="address" />${areaList.areaName }1</dd>
										</c:if>
									</c:forEach>
								</div>
							</li>
						</c:forEach>

</div><!-- maskLayer end -->

<!-- alpha div end -->
<div id="sublist" style="display:none"></div>
<!-- 静态代码结束 -->
							</div>
						</div> </div>
					<div style="text-align:center; margin-top:90px;">
						<input type="reset" value="取消" id="reset" />
						<input type="submit" value="提交" id="sub" />
					</div>
				</form>
			</div>
		</div>
	</div>	
</body>
</html>
<script type="text/javascript">
var getjson=new Array();
var all_c=new Array();
var all_parent=new Array();
var children;
	function getAreaIds(){
		dwr.engine.setAsync(false);
		pushMessageCompont.getAreaIds(function(data){
		//返回json数据 data
			getjson=data;
		});
		dwr.engine.setAsync(true);
		var obj = eval ("(" + getjson + ")");
// 		alert(obj[0].parentIds["1"]);
		 children=obj[0].areaBean;
	for(var i = 0;i < children.length; i++) {
	     if(children[i].parentId==undefined){
	       all_parent[i]=children[i];
	       all_parent[i].parent="null";
	    //   console.log(all_parent[i]);
		   }
else{ 
     
	    var fis=false;
	    var parentname=obj[0].parentIds[children[i].parentId];
	     for(var y = 0;y < all_parent.length; y++) {
	     try 
{ 
 if (typeof(all_parent[y].areaName) !="undefined") {
  if(all_parent[y].areaName==parentname){
	                fis=true;
	                break;
	          } 
 }
} 
catch(err) {} 
	     }
if(!fis){
all_parent[all_parent.length]={areaId:children[i].parentId,areaName:obj[0].parentIds[children[i].parentId]}; 
}

    
}
	}	
funtypeSelect();

}
</script>
<script type="text/javascript" src="js/jquery1.42.min.js"></script>
<script type="text/javascript" src="js/check.js"></script>

