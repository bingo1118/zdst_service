package com.cloudfire.dao;

import java.math.BigDecimal;
import java.util.List;

import com.cloudfire.entity.PayRecord;
import com.cloudfire.entity.PayRecords;
import com.cloudfire.entity.Recharge;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokePay;
import com.cloudfire.entity.UserEntity;
import com.cloudfire.entity.query.DevicesFee;
import com.cloudfire.entity.query.SmokeQuery;

public interface FreeDao {
	
	//BigDecimal getMoneyByMacAndType(String mac);
	
	int updateFree(String mac, BigDecimal free, String nowTime, String stopTime);
	
	void addPayRecord(PayRecord payRecord);
	
	//BigDecimal getBeforeFeeByMac(String mac);
	
	//String getStopTimeByMac(String mac);

	Recharge getRechargeByMac(String mac);
	
	List<Recharge> listRecharge();
	
	boolean recordExistMac(String mac);
	
	List<String> getUserIdsByMac(String mac);
	
	int reduceFee(String mac);
	/**
	 * 添加支付订单记录
	 * @param payRecords
	 */
	void addPayRecords(PayRecords payRecords);
	/**
	 * 根据订单号查询订单记录
	 * @param orderId
	 * @return PayRecords
	 */
	PayRecords queryRecordsByOrderId(String orderId);
	/**
	 * 修改设备余额
	 * @param mac
	 * @param cost
	 * @return
	 */
	boolean updateCostByMac(String mac,Double cost);
	/**
	 * 修改订单支付状态
	 * @param payRecords
	 * @return
	 */
	boolean updateRecords(PayRecords payRecords);
	
	Double queryCostByMac(String mac);
	
	List<UserEntity> getUsersByMac(String mac);
	
	//批量需弹窗提醒的，包含弹窗和电话的
	List<SmokePay> getUserAllList();
	
	int countDeviceType(DevicesFee query);
	
	List<DevicesFee> deviceList(DevicesFee query);
	
	int updateMoney(String id, String money);
	
	int countDetail(List<String> areaList, SmokeQuery query);
	
	List<SmartControlEntity> detailList(List<String> areaList, SmokeQuery query);

	
}
