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

	// ��ʶ����������������1��ʶ����2��ʾ�أ�Ӳ������0��ʾ����1��ʾ�ء�
	// �贫�̸��豸�ţ�lora�����豸�ţ��Ϳ��ر�ʶ
	public static void stopLoraElectric(String alarmMac, String electricMac, int eleState) throws InterruptedException {
		GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
		String repeaterMac = mbrDao.getRepeaterMacBySmokeMac(electricMac);// �����豸�Ż�ȡ������
		LinkActionDao linkActionDao = new LinkActionDaoImpl();
		String userId = linkActionDao.getUseridByMac(alarmMac, electricMac);// ��ȡ�û�
		HttpRsult hr = null;
		Object result = null;
		if (electricMac == null || eleState == 0 || userId == null) { // �豸�Ż��ʶ���û�idΪ�գ����������
			hr = new HttpRsult();
			hr.setError("��������");
			hr.setErrorCode(1);
			result = hr;
		} else {
			if (Utils.isNullStr(electricMac)) {// ����豸�Ų�Ϊ��
				RePeaterData mRePeaterData = new RePeaterData();
				mRePeaterData.setRepeatMac(repeaterMac);// ����������
				mRePeaterData.setElectricMac(electricMac);// �����豸��
				mRePeaterData.setSeqL((byte) 0x01);// ����ʶ���
				mRePeaterData.setSeqH((byte) 0x01);
				byte[] ack;
				ack = ClientPackage.ackControlAction(mRePeaterData, eleState);// @@�����豸//count��2�Ϳ��Թ���
				IoSession session = SessionMap.newInstance().getSession(repeaterMac);// ��ȡ������session
				if (session == null) {
					session = SessionMap.newInstance().getSession(electricMac);// Ϊ�գ�ͨ���豸�Ż�ȡ
				}
				if (session != null) {// ��Ϊ��
					UserMap.newInstance().addUser(repeaterMac, userId);// �����ź��û�id�󶨵�UserMap��
					Map<String, Integer> AckEleMap = Utils.AckEleMap;// ʵ����hashMap�洢��λ�豸�ţ�ֵΪ
					AckEleMap.put(electricMac, eleState);
					System.out.println("���ر�ʶ:" + eleState + "lora�豸��:" + AckEleMap.get(electricMac));
					AckElecContrThread ackEle = new AckElecContrThread(ack, session, repeaterMac);// �����߳�
					ackEle.start();
					ackEle.join();
					hr = new HttpRsult();
					int state = AckEleMap.get(electricMac);
					for (int i = 0; i < 30; i++) {
						Thread.sleep(1000L);
						state = AckEleMap.get(electricMac);
						NeedSmokeDaoImpl nsd = new NeedSmokeDaoImpl();
						int eletrState = nsd.getElectrState(electricMac);// @@��ȡ��ǰ�豸״̬
						if (state == 127) {
							hr.setError("ʧ��");
							hr.setErrorCode(1);
							break;
						}
						if (state > eleState || eletrState == (eleState + 1)) {
							hr.setError("�����ɹ�");
							hr.setErrorCode(0);
							// �洢�е��¼
							break;
						}
						if (state == eleState && i >= 29) {
							hr.setError("��ʱ");
							hr.setErrorCode(2);
						}
					}
					if (StringUtil.strIsNullOrEmpty(hr.getError())) {
						hr.setError("����ʧ��");
						hr.setErrorCode(1);
					}
					result = hr;
				} else {
					hr = new HttpRsult();
					hr.setError("�豸���Ӳ��ϣ�����ʧЧ");
					hr.setErrorCode(3);
					result = hr;
				}
			}
			if (hr == null) {
				hr = new HttpRsult();
				hr.setError("�����ʧ��");
				hr.setErrorCode(3);
				result = hr;
			} else {
				result = hr;
			}
		}
		JSONObject jObject = new JSONObject(result);
		System.out.println("�·���������" + jObject.toString());
		// this.response.getWriter().write(jObject.toString());
	}

	// �ر�NB����
	// 12�رգ�13����
	public static void stopNBElectric(String imeiValue, int devCmd) {
		String devSerial = "";
		ToolNanJinDao tnd = new ToolNanJinDaoImpl();
		devSerial = tnd.getDeviceByImei(imeiValue);// �Ͼ�ƽ̨�ϵ��豸��
		HttpRsult hr = null;
		Object result = null;
		hr = new HttpRsult();
		if (StringUtils.isBlank(imeiValue) || StringUtils.isBlank(devSerial) || devCmd == 0) {
			hr.setError("��������");
			hr.setErrorCode(1);
			result = hr;
		} else {
			boolean stateValue = tnd.reSetThread(devSerial, 75, devCmd, null, null, null, null);
			if (stateValue) {
				GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
				mbrDao.chanageElectric(imeiValue, 3);
				hr.setError("�ɹ�");
				hr.setErrorCode(0);
			} else {
				hr.setError("ʧ��");
				hr.setErrorCode(2);
			}
			result = hr;
		}
		JSONObject jObject = new JSONObject(result);
		System.out.println(jObject.toString());
	}

	// �ر�NB7020ȼ���������·�
	// state 16���� 17�ر�
	public static void stopNB7020(String imeiValue, String deviceState) {
		ToolNanJinDao tnd = new ToolNanJinDaoImpl();
		String deviceId = tnd.getDeviceByImei(imeiValue);
		HttpRsult hr = new HttpRsult();
		Object result = null;
		if (StringUtils.isBlank(imeiValue) || StringUtils.isBlank(deviceState)) {
			hr.setError("��������");
			hr.setErrorCode(1);
		} else {
			boolean bool73 = tnd.getCancelCounld(deviceId, 73, deviceState);
			if (bool73) {
				hr.setError("�ɹ�");
				hr.setErrorCode(0);
			} else {
				hr.setError("ʧ��");
				hr.setErrorCode(2);
			}
		}
		result = hr;
		JSONObject jObject = new JSONObject(result);
		System.out.println(jObject.toString());
	}

}
