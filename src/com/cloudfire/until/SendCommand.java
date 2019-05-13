package com.cloudfire.until;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.LinkActionDao;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.LinkActionDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.UserMap;
import com.cloudfire.thread.AckElecContrThread;

public class SendCommand {

	// 标识开或关命令，服务器端1标识开，2表示关，硬件部分0表示开，1表示关。
	// 需传烟感设备号，lora电气设备号，和开关标识
	public static void stopLoraElectric(String alarmMac, String electricMac, int eleState) throws InterruptedException {
		GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
		String repeaterMac = mbrDao.getRepeaterMacBySmokeMac(electricMac);// 根据设备号获取主机号
		LinkActionDao linkActionDao = new LinkActionDaoImpl();
		String userId = linkActionDao.getUseridByMac(alarmMac, electricMac);// 获取用户
		HttpRsult hr = null;
		Object result = null;
		if (electricMac == null || eleState == 0 || userId == null) { // 设备号或标识或用户id为空，则参数错误
			hr = new HttpRsult();
			hr.setError("参数错误");
			hr.setErrorCode(1);
			result = hr;
		} else {
			if (Utils.isNullStr(electricMac)) {// 如果设备号不为空
				RePeaterData mRePeaterData = new RePeaterData();
				mRePeaterData.setRepeatMac(repeaterMac);// 设置主机号
				mRePeaterData.setElectricMac(electricMac);// 设置设备号
				mRePeaterData.setSeqL((byte) 0x01);// 递增识别的
				mRePeaterData.setSeqH((byte) 0x01);
				byte[] ack;
				ack = ClientPackage.ackControlAction(mRePeaterData, eleState);// @@电气设备//count传2就可以关了
				IoSession session = SessionMap.newInstance().getSession(repeaterMac);// 获取主机的session
				if (session == null) {
					session = SessionMap.newInstance().getSession(electricMac);// 为空，通过设备号获取
				}
				if (session != null) {// 不为空
					UserMap.newInstance().addUser(repeaterMac, userId);// 主机号和用户id绑定到UserMap中
					Map<String, Integer> AckEleMap = Utils.AckEleMap;// 实例化hashMap存储键位设备号，值为
					AckEleMap.put(electricMac, eleState);
					System.out.println("开关标识:" + eleState + "lora设备号:" + AckEleMap.get(electricMac));
					AckElecContrThread ackEle = new AckElecContrThread(ack, session, repeaterMac);// 开启线程
					ackEle.start();
					ackEle.join();
					hr = new HttpRsult();
					int state = AckEleMap.get(electricMac);
					for (int i = 0; i < 30; i++) {
						Thread.sleep(1000L);
						state = AckEleMap.get(electricMac);
						NeedSmokeDaoImpl nsd = new NeedSmokeDaoImpl();
						int eletrState = nsd.getElectrState(electricMac);// @@获取当前设备状态
						if (state == 127) {
							hr.setError("失败");
							hr.setErrorCode(1);
							break;
						}
						if (state > eleState || eletrState == (eleState + 1)) {
							hr.setError("操作成功");
							hr.setErrorCode(0);
							// 存储切电记录
							break;
						}
						if (state == eleState && i >= 29) {
							hr.setError("超时");
							hr.setErrorCode(2);
						}
					}
					if (StringUtil.strIsNullOrEmpty(hr.getError())) {
						hr.setError("操作失败");
						hr.setErrorCode(1);
					}
					result = hr;
				} else {
					hr = new HttpRsult();
					hr.setError("设备连接不上，命令失效");
					hr.setErrorCode(3);
					result = hr;
				}
			}
			if (hr == null) {
				hr = new HttpRsult();
				hr.setError("命令发送失败");
				hr.setErrorCode(3);
				result = hr;
			} else {
				result = hr;
			}
		}
		JSONObject jObject = new JSONObject(result);
		System.out.println("下发命令结果：" + jObject.toString());
		// this.response.getWriter().write(jObject.toString());
	}

	// 关闭NB电气
	// 12关闭，13开启
	public static void stopNBElectric(String imeiValue, int devCmd) {
		String devSerial = "";
		ToolNanJinDao tnd = new ToolNanJinDaoImpl();
		devSerial = tnd.getDeviceByImei(imeiValue);// 南京平台上的设备号
		HttpRsult hr = null;
		Object result = null;
		hr = new HttpRsult();
		if (StringUtils.isBlank(imeiValue) || StringUtils.isBlank(devSerial) || devCmd == 0) {
			hr.setError("参数错误");
			hr.setErrorCode(1);
			result = hr;
		} else {
			boolean stateValue = tnd.reSetThread(devSerial, 75, devCmd, null, null, null, null);
			if (stateValue) {
				GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
				mbrDao.chanageElectric(imeiValue, 3);
				hr.setError("成功");
				hr.setErrorCode(0);
			} else {
				hr.setError("失败");
				hr.setErrorCode(2);
			}
			result = hr;
		}
		JSONObject jObject = new JSONObject(result);
		System.out.println(jObject.toString());
	}

	// 关闭NB7020燃气的命令下发
	// state 16开启 17关闭
	public static void stopNB7020(String imeiValue, String deviceState) {
		ToolNanJinDao tnd = new ToolNanJinDaoImpl();
		String deviceId = tnd.getDeviceByImei(imeiValue);
		HttpRsult hr = new HttpRsult();
		Object result = null;
		if (StringUtils.isBlank(imeiValue) || StringUtils.isBlank(deviceState)) {
			hr.setError("参数错误");
			hr.setErrorCode(1);
		} else {
			boolean bool73 = tnd.getCancelCounld(deviceId, 73, deviceState);
			if (bool73) {
				hr.setError("成功");
				hr.setErrorCode(0);
			} else {
				hr.setError("失败");
				hr.setErrorCode(2);
			}
		}
		result = hr;
		JSONObject jObject = new JSONObject(result);
		System.out.println(jObject.toString());
	}

}
