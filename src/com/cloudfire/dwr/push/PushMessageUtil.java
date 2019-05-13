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
	 * ��Ϣ�ظ�
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
	 * д�������
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
	 * ��ָ����������Ϣ
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage ������Ϣ
	 * @param scriptMethodName ��������
	 */
	public static void sendMessageToOne(String userId,String sendMessage) {
		sendMessageOneToOne(userId, DEFAULT_METHOD_NAME, sendMessage);
	}
	/**
	 * ��ָ����������Ϣ
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage ������Ϣ
	 * @param scriptMethodName ��������
	 */
	public static void sendMessageToOneCallBack(String userId,String sendMessage,String callBackName) {
		sendMessageOneToOne(userId, DEFAULT_METHOD_NAME, sendMessage,callBackName);
	}
	
	/**
	 * ��ָ����������Ϣ
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage ������Ϣ
	 * @param scriptMethodName ��������
	 */
	public static void sendMessageToOne(String userId,String scriptMethodName,String sendMessage) {
		sendMessageOneToOne(userId, scriptMethodName, sendMessage);
	}
	
	/**
	 * ��ָ����������Ϣ �����ûص�
	 * @Title: sendMessageToOne 
	 * @param userId
	 * @param sendMessage ������Ϣ
	 * @param callBackName �ص���������
	 * @param scriptMethodName ��������
	 */
	public static void sendMessageToOneCallBack(String userId,String scriptMethodName,String sendMessage,String callBackName) {
		sendMessageOneToOne(userId, scriptMethodName, sendMessage, callBackName);
	}
	
	
	/**
	 * �����е���������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param methodName ��������
	 */
	public static void sendMessageToAll(String methodName,String sendMessage) {
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(scriptSessions,  methodName,sendMessage);
	}
	
	/**
	 *  �����е���������Ϣ �ص�����
	 * @Title: sendMessageToAllCallBack 
	 * @param sendMessage ���͵���Ϣ
	 * @param callBackName �ص���������
	 */
	public static void sendMessageToAllCallBack(String sendMessage,String callBackName) {
		List<String> userIds = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(userIds,  DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 *  �����е���������Ϣ �ص�����
	 * @Title: sendMessageToAllCallBack 
	 * @param methodName ���õķ�������
	 * @param sendMessage ���͵���Ϣ
	 * @param callBackName �ص���������
	 */
	public static void sendMessageToAllCallBack(String methodName,String sendMessage,String callBackName) {
		List<String> userIds = MyScriptSessionListener.getAllScriptSessionIds();
		sendMessageToAll(userIds,  methodName,sendMessage,callBackName);
	}
	
	/**
	 * �����е���������Ϣ �����Լ�
	 * ע��: ��Ҫ���ɺ�̨������Ա������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param methodName ��������
	 */
	private static void sendMessageToAllNotContainCallBack(String methodName,Object ...sendMessage) {
		String currentId = "11";
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		scriptSessions.remove(currentId);
		sendMessageToAll(scriptSessions, methodName,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ �����Լ� �ص�
	 * ע��: ��Ҫ���ɺ�̨������Ա������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param callBackName �ص���������
	 */
	public static void sendMessageToAllNotContainCallBack(String sendMessage,String callBackName) {
		sendMessageToAllNotContainCallBack(DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 * �����е���������Ϣ �����Լ�
	 * ע��: ��Ҫ���ɺ�̨������Ա������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 */
	public static void sendMessageToAllNotContain(String sendMessage) {
		sendMessageToAllNotContainCallBack(DEFAULT_METHOD_NAME,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ �����Լ�
	 * ע��: ��Ҫ���ɺ�̨������Ա������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param methodName ��������
	 */
	public static void sendMessageToAllNotContain(String methodName,String sendMessage) {
		sendMessageToAllNotContainCallBack(methodName,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ ����ָ����
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain ������
	 * @param sendMessage ������Ϣ
	 * @param methodName ǰ̨���ܷ�������
	 */
	private static void sendMessageToAllNotContains(List<String> notContain,String methodName,Object ...sendMessage) {
		List<String> scriptSessions = MyScriptSessionListener.getAllScriptSessionIds();
		if(CollectionUtils.isNotEmpty(notContain)) {
			scriptSessions.removeAll(notContain);
		}
		sendMessageToAll(scriptSessions, methodName,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ ����ָ����
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain ������
	 * @param sendMessage ������Ϣ
	 */
	public static void sendMessageToAllNotContain(List<String> notContain,String sendMessage) {
		sendMessageToAllNotContains(notContain, DEFAULT_METHOD_NAME,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ ����ָ����
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain ������
	 * @param sendMessage ������Ϣ
	 * @param methodName ǰ̨���ܷ�������
	 */
	public static void sendMessageToAllNotContain(List<String> notContain,String methodName,String sendMessage ) {
		sendMessageToAllNotContains(notContain, methodName,sendMessage);
	}
	
	/**
	 * �����е���������Ϣ ����ָ���� ���ص�
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain ������
	 * @param sendMessage ������Ϣ
	 * @param callBackName �ص�����
	 */
	public static void sendMessageToAllNotContainCallBack(List<String> notContain,String sendMessage,String callBackName) {
		sendMessageToAllNotContains(notContain, DEFAULT_METHOD_NAME,sendMessage,callBackName);
	}
	
	/**
	 * �����е���������Ϣ ����ָ���� ���ص�
	 * @Title: sendMessageToAllNotContain 
	 * @param notContain ������
	 * @param sendMessage ������Ϣ
	 * @param callBackName �ص�����
	 * * @param methodName ǰ̨���ܷ�������
	 */
	public static void sendMessageToAllNotContainCallBack(List<String> notContain,String methodName,String sendMessage,String callBackName) {
		sendMessageToAllNotContains(notContain, methodName,sendMessage,callBackName);
	}
	
	/**
	 * �����е���������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 */
	public static void sendMessageToAll(String sendMessage) {
		sendMessageToAll(DEFAULT_METHOD_NAME,sendMessage);
	}
	
	
	/**
	 * �����û�������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param methodName ǰ̨ҳ�淽������
	 */
	public static void sendMessageToAll(List<String> userIds,String methodName,String sendMessage) {
		sendMessageToAll(userIds,methodName,sendMessage);
	}
	
	/** 
	 * �����û�������Ϣ ���һص�
	 * @Title: sendMessageToAll 
	 * @param sendMessage ������Ϣ
	 * @param methodName ǰ̨ҳ�淽������
	 * @param callBackName �ص���������
	 */
	public static void sendMessageToAllCallBack(List<String> userIds,String sendMessage,String callBack) {
		sendMessageToAll(userIds,DEFAULT_METHOD_NAME,sendMessage,callBack);
	}
	
	/**
	 * �����û�������Ϣ
	 * @Title: sendMessageToAll 
	 * @param sendMessage
	 */
	public static void sendMessageToAll(List<String> userIds,String sendMessage) {
		sendMessageToAll(userIds,DEFAULT_METHOD_NAME,sendMessage);
	}
	
	
	/**
	 * ��ȷ����
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
	 * ��ָ���Ķ���û�������Ϣ
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
	 * ѭ����ȡ��ǰ�ѵ�¼��Session
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
