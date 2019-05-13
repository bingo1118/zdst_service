package com.cloudfire.until;

import java.util.HashMap;
import java.util.Map;



public class IfStopAlarm {

	public static Map<String,Long> map=new HashMap<>();
	public static Map<String,Long> ifPushFault=new HashMap<>();
	
	public static Long cycleTime=60000L;
	public static Map<String,Long> lastAlarmTime=new HashMap<>();
	
	public static Map<String,Integer> electricState=new HashMap<>();
}
