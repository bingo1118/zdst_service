<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>无标题文档</title>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript">
	$(function(){
	
		$("#button_update").click(function(){
			if($("#areaName").val() =="" || $("#areaName").val() ==null){
				alert("烟感名称不能为空");
				return false;
			}			
			
			$("#updateArea").submit();
			
		});
	
	   $("#close").click(function(){
	       window.opener.location.reload(true);
	       window.close();
	   });
	});

</script>


</head>

<body style="width:100%;background-color:#fff;min-width:100%;">
<div class="content_table">
	<form id="updateArea" action="${pageContext.request.contextPath }/updateAreaByareaId.do" method="post">

            	<table id="idData" style="margin:0 auto;">
            		<input type="hidden"  id="areaId" name="areaId" value="${bean.areaId }" />
            		<tr>
            			<td colspan="3"><h3>修改区域</h3></td>
            			
            		</tr>
                    <tr>	
                        <td>区域名称：</td>
                        <td><input type="text"  id="areaName" name="areaName" value="${bean.areaName}" /></td>
                        <td width="50px"><span style="color:green;font-size: 12px;"><%=request.getAttribute("message")==null? "" : request.getAttribute("message")%></span> </td>
                    </tr>                        
                    
                    
                     <tr>
                    	<td colspan="3" align="center">
	                    	<input type="button" id="close" name="" value="关闭" />
	                    	<input type="button" id="button_update" name="button_update" value="修改" />
                    	</td>                    	
                    </tr>  
                    
                </table>              
                 
         </form>        
 </div>
</body>
</html>


