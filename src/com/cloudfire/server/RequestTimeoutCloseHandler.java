package com.cloudfire.server;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

public class RequestTimeoutCloseHandler implements KeepAliveRequestTimeoutHandler{

	public void keepAliveRequestTimedOut(KeepAliveFilter filter,IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
