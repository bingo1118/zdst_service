package com.cloudfire.dwr.push;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;


public class PushMessageUtil{
	
	public static final String DEFAULT_MARK = "userId";
	
	public static final String DEFAULT_METHOD_NAME = "showMessage";

	/**
	 * 信息回复
	 * @Title: writeToscript 
	 * @param response
	 * @param msg
	 * @param username
	 * @throws Exception
	 */
	public void writeToscript(HttpServletResponse response,String msg,String username) throws Exception {
		writeScript(response, "<script language='javascript'>parent.replyMsg('"+msg+"','"+username+"')</script>");
	}
	
	
	/**
	 * 写回浏览器
	 * @param response
	 * @param pw
	 * @param script
	 * @throws Exception
	 */
	private void writeScript(javax.servlet.http.HttpServletResponse response,String script) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(script);
		response.flushBuffer();
	}
	
	/**
	 * 向指定人推送消息
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage 发送消息
	 * @param scriptMethodName 方法名称
	 */
	public static void sendMessageToOne(String userId,String sendMessage) {
		sendMessageOneToOne(userId, DEFAULT_METHOD_NAME, sendMessage);
	}
	/**
	 * 向指定人推送消息
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage 发送消息
	 * @param scriptMethodName 方法名称
	 */
	public static void sendMessageToOneCallBack(String userId,String sendMessage,String callBackName) {
		sendMessageOneToOne(userId, DEFAULT_METHOD_NAME, sendMessage,callBackName);
	}
	
	/**
	 * 向指定人推送消息
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage 发送消息
	 * @param scriptMethodName 方法名称
	 */
	public static void sendMessageToOne(String userId,String scriptMethodName,String sendMessage) {
		sendMessageOneToOne(userId, scriptMethodName, sendMessage);
	}
	
	/**
	 * 向指定人推送消息 并调用回调
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage 发送消息
	 * @param callBackName 回调函数名称
	 * @param scriptMethodName 方法名称
	 */
	public static void sendMessageToOneCallBack(String userId,String scriptMethodName,String sendMessage,String callBackName) {
		sendMessageOneToOne(userId, scriptMethodName, sendMessage, callBackName);
	}
	
	
	/**
	 * 向所有的人推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param methodName 方法名称
	 */
	public static void sendMessageToAll(String methodName,String sendMessage) {
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(scriptSessions,  methodName,sendMessage);
	}
	
	/**
	 *  向所有的人推送消息 回调函数
	 * @Title: sendMessageToAllCallBack 
	 * @param sendMessage 发送的消息
	 * @param callBackName 回调方法名称
	 */
	public static void sendMessageToAllCallBack(String sendMessage,String callBackName) {
		List<String> userIds = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(userIds,  DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 *  向所有的人推送消息 回调函数
	 * @Title: sendMessageToAllCallBack 
	 * @param methodName 调用的方法名称
	 * @param sendMessage 发送的消息
	 * @param callBackName 回调方法名称
	 */
	public static void sendMessageToAllCallBack(String methodName,String sendMessage,String callBackName) {
		List<String> userIds = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(userIds,  methodName,sendMessage,callBackName);
	}
	
	/**
	 * 向所有的人推送消息 除了自己
	 * 注意: 主要是由后台管理人员推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param methodName 方法名称
	 */
	private static void sendMessageToAllNotContainCallBack(String methodName,Object ...sendMessage) {
		String currentId = "11";
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		scriptSessions.remove(currentId);
		sendMessageToAll(scriptSessions, methodName,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了自己 回调
	 * 注意: 主要是由后台管理人员推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param callBackName 回调方法名称
	 */
	public static void sendMessageToAllNotContainCallBack(String sendMessage,String callBackName) {
		sendMessageToAllNotContainCallBack(DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 * 向所有的人推送消息 除了自己
	 * 注意: 主要是由后台管理人员推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 */
	public static void sendMessageToAllNotContain(String sendMessage) {
		sendMessageToAllNotContainCallBack(DEFAULT_METHOD_NAME,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了自己
	 * 注意: 主要是由后台管理人员推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param methodName 方法名称
	 */
	public static void sendMessageToAllNotContain(String methodName,String sendMessage) {
		sendMessageToAllNotContainCallBack(methodName,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了指定人
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain 不包含
	 * @param sendMessage 发送消息
	 * @param methodName 前台接受方法名称
	 */
	private static void sendMessageToAllNotContains(List<String> notContain,String methodName,Object ...sendMessage) {
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		if(CollectionUtils.isNotEmpty(notContain)) {
			scriptSessions.removeAll(notContain);
		}
		sendMessageToAll(scriptSessions, methodName,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了指定人
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain 不包含
	 * @param sendMessage 发送消息
	 */
	public static void sendMessageToAllNotContain(List<String> notContain,String sendMessage) {
		sendMessageToAllNotContains(notContain, DEFAULT_METHOD_NAME,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了指定人
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain 不包含
	 * @param sendMessage 发送消息
	 * @param methodName 前台接受方法名称
	 */
	public static void sendMessageToAllNotContain(List<String> notContain,String methodName,String sendMessage ) {
		sendMessageToAllNotContains(notContain, methodName,sendMessage);
	}
	
	/**
	 * 向所有的人推送消息 除了指定人 并回调
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain 不包含
	 * @param sendMessage 发送消息
	 * @param callBackName 回调名称
	 */
	public static void sendMessageToAllNotContainCallBack(List<String> notContain,String sendMessage,String callBackName) {
		sendMessageToAllNotContains(notContain, DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 * 向所有的人推送消息 除了指定人 并回调
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain 不包含
	 * @param sendMessage 发送消息
	 * @param callBackName 回调名称
	 * * @param methodName 前台接受方法名称
	 */
	public static void sendMessageToAllNotContainCallBack(List<String> notContain,String methodName,String sendMessage,String callBackName) {
		sendMessageToAllNotContains(notContain, methodName,sendMessage,callBackName);
	}
	
	/**
	 * 向所有的人推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 */
	public static void sendMessageToAll(String sendMessage) {
		sendMessageToAll(DEFAULT_METHOD_NAME,sendMessage);
	}
	
	
	/**
	 * 向多个用户推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param methodName 前台页面方法名称
	 */
	public static void sendMessageToAll(List<String> userIds,String methodName,String sendMessage) {
		sendMessageToAll(userIds,methodName,sendMessage);
	}
	
	/** 
	 * 向多个用户推送消息 并且回调
	 * @Title: sendMessageToAll 
	 * @param sendMessage 发送消息
	 * @param methodName 前台页面方法名称
	 * @param callBackName 回调函数名称
	 */
	public static void sendMessageToAllCallBack(List<String> userIds,String sendMessage,String callBack) {
		sendMessageToAll(userIds,DEFAULT_METHOD_NAME,sendMessage,callBack);
	}
	
	/**
	 * 向多个用户推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage
	 */
	public static void sendMessageToAll(List<String> userIds,String sendMessage) {
		sendMessageToAll(userIds,DEFAULT_METHOD_NAME,sendMessage);
	}
	
	
	/**
	 * 精确推送
	 * @Title: sendMessageToOne 
	 * @param mark
	 * @param sendMessage
	 */
	private static void sendMessageOneToOne(String userId,String scriptMethodName, Object ...sendMessage) {
		final String tempUserId = userId;
		final Object[] tempSendMessage = sendMessage;
		final String tempScriptMethodName = scriptMethodName;
          Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
			public boolean match(ScriptSession session) {
				String getSessionUserId = (String) session.getAttribute(DEFAULT_MARK);
				if(StringUtils.isNotBlank(getSessionUserId)) {
					boolean result = StringUtils.equals(getSessionUserId, tempUserId);
					return result;
				}
				return false;
			}
		}, new Runnable() {
			private ScriptBuffer scriptBuffer = new ScriptBuffer();
			public void run() {
				appendCurrentSession(scriptBuffer, tempScriptMethodName,tempSendMessage);
			}
		});
	}
	
	/**
	 * 向指定的多个用户推送消息
	 * @Title: sendMessageToAll 
	 * @param sendMessage
	 */
	private static void sendMessageToAll(List<String> userIds,String scriptMethodName,Object ...sendMessage) {
		final List<String> tempUserIds = userIds;
		final Object[] tempSendMessage = sendMessage;
		final String tempScriptMethodName = scriptMethodName;
		 Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
				public boolean match(ScriptSession session) {
					String getSessionUserId = (String) session.getAttribute(DEFAULT_MARK);
					if(StringUtils.isNotBlank(getSessionUserId) && CollectionUtils.isNotEmpty(tempUserIds) && tempUserIds.contains(getSessionUserId)) {
						tempUserIds.remove(getSessionUserId);
						return true;
					}
					return false;
				}
			}, new Runnable() {
				private ScriptBuffer scriptBuffer = new ScriptBuffer();
				public void run() {
					appendCurrentSession(scriptBuffer,tempScriptMethodName,tempSendMessage);
				}
			});
	}
	/**
	 * 循环获取当前已登录的Session
	 * @Title: appendCurrentSession 
	 * @param scriptBuffer
	 * @param sendMessage
	 * @param scriptMethodName
	 */
	private static void appendCurrentSession(ScriptBuffer scriptBuffer, String scriptMethodName, Object ...sendMessage) {
		Map map = MyScriptSessionListener.scriptSessionMap;
		scriptBuffer.appendCall(scriptMethodName, sendMessage);//id=KI6L/ImZn
		Collection<ScriptSession> scriptSessions =  Browser.getTargetSessions();//MyScriptSessionListener.getAllScriptSessions();
		for(ScriptSession scriptSession : scriptSessions) {
			scriptSession.addScript(scriptBuffer);
		}
	}

	
}
