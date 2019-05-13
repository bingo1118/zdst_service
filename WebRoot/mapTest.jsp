<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>青曼</title>
    <link rel="shortcut icon" href="images/fire.ico" />
	<link type="text/css" rel="stylesheet" href="css/style.css">
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=WAbGUPB8LA7mB2QTy4G0n4e2"></script>
    
	<script type="text/javascript" src="http://api.map.baidu.com/library/TrafficControl/1.4/src/TrafficControl_min.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
	<script type="text/javascript" src="js/MarkerClusterer.js"></script>
	<script type="text/javascript" src="js/jquery-1.8.2.js"></script>
	<script type="text/javascript" src="js/jquery-timer-1.0.js"></script>

	<style type="text/css">
	        html,body{
	            margin: 0;
	            padding: 0;
	            height: 100%;
	            overflow: hidden;
	            width: 100%;
	        }
	        #allmap{
	            width: 100%;
	            height:100%;
	        }
	    </style>
</head>
<body>
    <div id="allmap"></div>
   
</body>
	
</html>
<script type="text/javascript" src="js/map.js"></script><!-- 地图引用js -->