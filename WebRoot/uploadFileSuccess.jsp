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
  �ϴ��ɹ�!
  <br>
  �ļ�����:
  <s:property value=" + title" />
  <br>
  �ļ�Ϊ��
  <img src="<s:property value="'upload/' + uploadFileName"/>" />
  <br>
 </body>
</html>