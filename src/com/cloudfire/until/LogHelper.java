package com.cloudfire.until;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LogHelper {
	private final static Log log = LogFactory.getLog(LogHelper.class);

	public static void d(String s){
		log.debug(s);
	}
	
	public static void e(String e){
		log.error(e);
	}
	
	public static void w(String w){
		log.warn(w);
	}
}
