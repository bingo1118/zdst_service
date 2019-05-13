package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.LinkAction;

public interface LinkActionDao {

	// 根据设备号查询类型
	LinkAction getTypeByMac(String mac);

	// 添加联动关系近linkaction表
	int addLinkAction(LinkAction linkAction);

	// 分页
	int countLinkAction(LinkAction query);

	List<LinkAction> linkActionList(LinkAction query);

	// 删除
	int removeById(int id);

	// 去重
	int checkMac(String alarmMac, String responseMac);

	List<String> getLoraElectircMacByAlarmMac(String alarmMac);

	List<String> getNBElectircMacByAlarmMac(String alarmMac);

	List<String> getNB7020MacByAlarmMac(String alarmMac);

	String getUseridByMac(String alarmMac, String electricMac);

	String getImeiByNJ(String mac);
	
	String getAreaIdByMac(String mac);

}
