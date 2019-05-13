package com.cloudfire.dwr.push;

import java.util.Collection;

import org.directwebremoting.ScriptSession;
import org.directwebremoting.impl.DefaultScriptSessionManager;


public class ScriptSessionManager extends DefaultScriptSessionManager{

	public ScriptSessionManager() {
		this.addScriptSessionListener(new MyScriptSessionListener());
		System.out.println("bind ScriptSessionLitener");
	}

	@Override
	public Collection<ScriptSession> getAllScriptSessions() {
		return MyScriptSessionListener.getAllScriptSessions();
	}
	
}
