package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.LinkAction;

public interface LinkActionDao {

	// �����豸�Ų�ѯ����
	LinkAction getTypeByMac(String mac);

	// ���������ϵ��linkaction��
	int addLinkAction(LinkAction linkAction);

	// ��ҳ
	int countLinkAction(LinkAction query);

	List<LinkAction> linkActionList(LinkAction query);

	// ɾ��
	int removeById(int id);

	// ȥ��
	int checkMac(String alarmMac, String responseMac);

	List<String> getLoraElectircMacByAlarmMac(String alarmMac);

	List<String> getNBElectircMacByAlarmMac(String alarmMac);

	List<String> getNB7020MacByAlarmMac(String alarmMac);

	String getUseridByMac(String alarmMac, String electricMac);

	String getImeiByNJ(String mac);
	
	String getAreaIdByMac(String mac);

}
