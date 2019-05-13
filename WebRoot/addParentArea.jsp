<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>添加区域</title>
<link type="text/css" rel="stylesheet" href="css/style.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript">
	
	$(function(){
		$("#sub").click(function(){
			var province = $("#province").val();
			var city = $("#city").val();
			var town = $("#town").val();
			var parentAreaName = $("#parentAreaName").val().trim();
			//alert("province:"+province + ",city:" + city +",town:" + town + ",areaName:" + areaName);
			if(province==null || province==""){
				alert("请选择省份");
				return false;
			}
			if(city==null || city =="0"){
				alert("请选择城市");
				return false;
			}
			if(town==null || city =="0"){
				alert("请正确选择");
				return false;
			}
			if(parentAreaName==null || parentAreaName==""){
				alert("请输入区域");
				return false;
			}
			$("#addArea").submit();
		});
		
		findAllProvince();  //加载完获取省份列表
	});

     function findAllProvince(){
        $.post("getAllProvince.do",function(data,textStatus){
            if(data!=null &&data.length>0){
                var data=eval(data);
                var $option=$("<option></option>");
                        $option.attr("value",0);
                    $option.text("请选择");
                    $("#province").append($option);
                for(var i=0;i<data.length;i++){
                    var code = data[i].code;
                    var name = data[i].name;
                    //添加到单位名称的下拉菜单中
                    var $option = $("<option></option>");
                    $option.attr("value",code);
                    $option.text(name);
                    $("#province").append($option);
                }
            }
        });
    }
	
	 //ajax的二级联动，使用选择的所属省份，查询该所属单位下对应的城市
    //ajax的二级联动，使用选择的所属单位，查询该所属单位下对应的单位名称列表
    function findCity(o){
    	//货物所属单位的文本内容
    	var provinceCode = $(o).find("option:selected").val();
    	$.post("selectCity.do",{"provinceCode":provinceCode},function(data,textStatus){
	   	    //先删除单位名称的下拉菜单，但是请选择要留下
	   	    $("#city option").remove();
	        if(data!=null && data.length>0){
	          	//添加一个请选择
	          	var data=eval(data);
	          	var $option = $("<option></option>");
	   		       	$option.attr("value",0);
	   		       	$option.text("请选择");
	   		       	$("#city").append($option);
	            for(var i=0;i<data.length;i++){
	   		       	var code = data[i].code;
	   		       	var name = data[i].name;
	   		       	//添加到单位名称的下拉菜单中
	   		       	var $option = $("<option></option>");
	   		       	$option.attr("value",code);
	   		       	$option.text(name);
	   		       	$("#city").append($option);
	   	        }
	        }
        });
    	
    }
    
    
      //ajax的二级联动，使用选择的所，查询该所属单位下对应的单位名称列表
    function findTown(o){
    	//货物所属单位的文本内容
    	var cityCode = $(o).find("option:selected").val();
    	$.post("selectTown.do",{"cityCode":cityCode},function(data,textStatus){
	   	    //先删除单位名称的下拉菜单，但是请选择要留下
	   	    $("#town option").remove();
	        if(data!=null && data.length>0){
	        	var data = eval(data);
	            for(var i=0;i<data.length;i++){
	   		       	var code = data[i].code;
	   		       	var name = data[i].name;
	   		       	//添加到单位名称的下拉菜单中
	   		       	var $option = $("<option></option>");
	   		       	$option.attr("value",code);
	   		       	$option.text(name);
	   		       	$("#town").append($option);
	   	        }
	        }
        });
    	
    }
    
    
</script>
</head>

<body>
	<div class="contain_tj">
    	<div class="title_tj">
        	<a href="addUser.do">返回上一级</a>
        </div>
        <div class="conten_tj">
        	<div class="conten_tj_tit">添加区域</div>
            <div class="conten_txt">
            	<!-- <form action="/fireSystem/addArea.do" id="addArea" method="post"> -->
            	<form action="/zdst/addParentArea.do" id="addArea" method="post">
            		<label>所属省份：
            			<select name="province" id="province" style="width:155px" onchange="findCity(this)">
					<%-- 		<option >请选择</option>
							<c:forEach items="${province }" var="province" >								
								<option value="${province.code }">${province.name }</option>
							</c:forEach> --%>
						</select>
            		</label>
            		<label>城市：
            			<select id="city" name="city" style="width:155px" onchange="findTown(this)">
						</select>
            		</label>
            		<label>县/区：
            			<select id="town" name="town" style="width:155px"></select>
            		</label>
            		           		
                    <label>一级区域名称(大区域)：
                    	<input type="text" name="parentAreaName" id="parentAreaName"/>
                    	<%-- <span><b style="color:#e90000;"><%request.getAttribute("message"); %></span> --%>
                    	 <span><b style="color:#e90000;"><%=request.getAttribute("message")==null? "" : request.getAttribute("message")%></span> 
                    </label>
             		
                    <div style="text-align:left; margin-top:90px;">
                    	<input type="reset" value="取消" id="reset"/>
                    	<input type="submit" value="添加" id="sub"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
