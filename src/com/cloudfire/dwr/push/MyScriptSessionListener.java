package com.cloudfire.dwr.push;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;


public class MyScriptSessionListener implements ScriptSessionListener{
	
	private Logger logger = Logger.getLogger(getClass());
	
	public static final Map<String, ScriptSession> scriptSessionMap = new HashMap<String, ScriptSession>();
	


	/**
	 * 获取当前的UserID 存放在Session中
	 * @Title: getCurrentUserId 
	 * @return
	 */
	private String getCurrentUserId(String markName) {
		HttpSession httpSession = getHttpSessionByWebComtext();
		Object obj =  httpSession.getAttribute(markName);
		if(obj == null) {
			return null;
		}
		return String.valueOf(obj);
	}
	
	/**
	 * 获取当前创建的Session
	 * @Title: getHttpSessionByWebComtext 
	 * @return
	 */
	private HttpSession getHttpSessionByWebComtext() {
		WebContext webContext = WebContextFactory.get();
		return webContext.getSession();
	}
	/***
	 * 获取所有的ScriptSession
	 * @Title: getAllScriptSessions 
	 * @return
	 */
	public static Collection<ScriptSession> getAllScriptSessions() {
		return scriptSessionMap.values();
	}
	
	/**
	 * 获取所有的当前登录用户ID
	 * @Title: getAllScriptSessionIds 
	 * @return
	 */
	public static List<String> getAllScriptSessionIds() {
		List<String> allUserIds = new ArrayList<String>(scriptSessionMap.keySet());
		return allUserIds;
	}

	public void sessionCreated(ScriptSessionEvent ev) {
		String userId = getCurrentUserId(PushMessageUtil.DEFAULT_MARK);
		//每次访问创建的临时多变 Session
		ScriptSession scriptSession = ev.getSession();
		scriptSessionMap.put(userId, scriptSession);
		ev.getSession().setAttribute(PushMessageUtil.DEFAULT_MARK, userId);
		//logger.warn("user id :"+userId +", scriptSession : "+ scriptSession.getId() +" is created !");
	}

	public void sessionDestroyed(ScriptSessionEvent ev) {
		// TODO Auto-generated method stub
		Object userId = ev.getSession().getAttribute(PushMessageUtil.DEFAULT_MARK);
		if(userId == null) {
			logger.warn(" user id is null ,so The session of failure ! ");
		}else {
			ScriptSession scriptSession = scriptSessionMap.remove(ObjectUtils.toString(userId));
			//logger.warn( "user id: " + userId + " scriptSession : " + scriptSession.getId() + " is destroyed!"); 
		}
	}
	
}
