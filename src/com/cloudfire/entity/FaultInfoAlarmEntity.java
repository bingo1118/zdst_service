package com.cloudfire.entity;

public class FaultInfoAlarmEntity {
	private String secret;	// String 是对接接入方用户凭证
	private String appkey;	// String 是接入方appkey
	private String bid;	// int 是物联网建筑物id
	private String pid;		// String 是故障点位id
	private String pointDesc;	// String 否点位描述
	private String devNum;	// String 是点位号
	//故障

	private String happenTime;		// String 是故障发生时间
	private String faultStatus;	// String 是故障状态
	private String maintainTime;	// String 是维修时间
	private String faultType;	// String 是故障类型（事先总结一套故障类型清单）
	private String faultid;		// String 是物联网故障id

	//火警
	private String compartment; 	//String 是防火分区
	private String fireFlow;	// String 是火警流程（整理一套火警流程清单）
	private String fireid;	// String 是物联网商火警id
	private String firstTime;	// String 是火警第一次上报时间
	private String flowendTime;	// String 是火警流程结束时间
	private String lastTime;	// String 是火警最后一次上报时间
	private int points;	// Int 是报警点位个数
	
	
}
