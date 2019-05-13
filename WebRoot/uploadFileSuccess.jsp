<%@ page language="java" pageEncoding="GBK"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
  <meta http-equiv="pragma" content="no-cache">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="expires" content="0">
 </head>
 <body>
  上传成功!
  <br>
  文件标题:
  <s:property value=" + title" />
  <br>
  文件为：
  <img src="<s:property value="'upload/' + uploadFileName"/>" />
  <br>
 </body>
</html>