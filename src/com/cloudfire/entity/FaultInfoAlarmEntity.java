package com.cloudfire.entity;

public class FaultInfoAlarmEntity {
	private String secret;	// String �ǶԽӽ��뷽�û�ƾ֤
	private String appkey;	// String �ǽ��뷽appkey
	private String bid;	// int ��������������id
	private String pid;		// String �ǹ��ϵ�λid
	private String pointDesc;	// String ���λ����
	private String devNum;	// String �ǵ�λ��
	//����

	private String happenTime;		// String �ǹ��Ϸ���ʱ��
	private String faultStatus;	// String �ǹ���״̬
	private String maintainTime;	// String ��ά��ʱ��
	private String faultType;	// String �ǹ������ͣ������ܽ�һ�׹��������嵥��
	private String faultid;		// String ������������id

	//��
	private String compartment; 	//String �Ƿ������
	private String fireFlow;	// String �ǻ����̣�����һ�׻������嵥��
	private String fireid;	// String ���������̻�id
	private String firstTime;	// String �ǻ𾯵�һ���ϱ�ʱ��
	private String flowendTime;	// String �ǻ����̽���ʱ��
	private String lastTime;	// String �ǻ����һ���ϱ�ʱ��
	private int points;	// Int �Ǳ�����λ����
	
	
}
