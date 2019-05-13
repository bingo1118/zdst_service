package com.cloudfire.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.MeterInfoDaoImp;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.entity.DX_NB_NJ_DataMqttEntity;
import com.cloudfire.entity.EasyIOTCmdEntity;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.ServiceData;
import com.cloudfire.entity.THDevice;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.server.HrNanJingIotServer;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class Tool_NanJin_Actions extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private static HrNanJingIotServer hrServer;
	private static ToolNanJinDao nanJingDao;
	private static MeterInfoDao meterInfoDao; 
	private final static Log log = LogFactory.getLog(Tool_NanJin_Actions.class);
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private String getRequestPostData(HttpServletRequest request) {
		String result = "";
		int contentLength = request.getContentLength()+1;
		System.out.println("���ݵ���getRequestPostData����"+contentLength);
		if(contentLength<0){
			return null;
		}
		byte buffer[] = new byte[contentLength];
		try {
			for (int i = 0; i <contentLength; i++) {
				int len = request.getInputStream().read(buffer,0,contentLength-1);
				if(len == -1){
					break;
				}
				i += len;
			}
			result = new String(buffer,"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		Map<String,Object> map = context.getParameters();
		Set<String> keys = map.keySet();
		for (String key:keys) {
			Object[] obj = (Object[])map.get(key);
			System.out.println(Arrays.toString(obj));
		}
		return NONE;
	}
	
	/**
	 * �Ͼ�ƽ̨����豸�ӿڵ���
	 */
	public void addDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("addDevice���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨�豸��Ϣ�仯�ӿڵ���
	 */
	public void updateDeviceInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("updateDeviceInfo���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨�����¼��ӿڵ���
	 */
	public void RulEevent(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("RulEevent���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨��Ϣȷ�Ͻӿڵ���
	 */
	public void commandConfirmData(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("commandConfirmData���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨�豸�¼��ӿڵ���
	 */
	public void DeviceEvent(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("DeviceEvent���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨ɾ���豸�ӿڵ���
	 */
	public void deletedDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("deletedDevice���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨��Ӧ����ӿڵ���
	 */
	public void commandRspData(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("commandRspData���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨����ӿڵ���
	 */
	public void reportCmdExecResult(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("Tool_NanJin_Actions");
		String result = getRequestPostData(this.request);
		System.out.println("reportCmdExecResult���ݻص������"+result);
	}
	
}
