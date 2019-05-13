package com.cloudfire.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.cloudfire.dao.ToolDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.EasyIOT;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.GetTime;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;

public class ToolDaoImpl implements ToolDao {

	@Override
	public int updateSmokePrincipal(String repeater, String principal1,
			String principalTel1, String principal2, String principalTel2) {
		int result = 0;
		String sqlstr = "update smoke set principal1=?,principal1Phone=?,principal2=?,principal2Phone=? where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, principal1);
			ps.setString(2, principalTel1);
			ps.setString(3, principal2);
			ps.setString(4, principalTel2);
			ps.setString(5, repeater);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int updateSmokePrincipal(String repeater, String principal1,
			String principalTel1) {
		int result = 0;
		String sqlstr = "update smoke set principal1=?,principal1Phone=? where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, principal1);
			ps.setString(2, principalTel1);
			ps.setString(3, repeater);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int updateSmokePrincipal2(String repeater, String principal2,
			String principalTel2) {
		int result = 0;
		String sqlstr = "update smoke set principal2=?,principal2Phone=? where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, principal2);
			ps.setString(2, principalTel2);
			ps.setString(3, repeater);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public void insertUserSmoke(String repeater) {
		List<SmokeBean> lists = getSmokeInfo(repeater);
		int result = 0;
		for (SmokeBean smk : lists) {
			System.out.println(smk.getMac() + "======"
					+ smk.getPrincipal1Phone());
			result = insertuseridsmoke(smk.getMac(), smk.getPrincipal1Phone());
			System.out.println(result);
		}
	}

	public int insertuseridsmoke(String mac, String tel) {
		String sqlstr = "insert into useridsmoke values(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int result = 0;
		try {
			ps.setString(2, mac);
			ps.setString(1, tel);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public List<SmokeBean> getSmokeInfo(String repeater) {
		String sqlstr = "SELECT mac,principal1Phone from smoke where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		List<SmokeBean> slist = new ArrayList<SmokeBean>();
		ResultSet rs = null;
		try {
			ps.setString(1, repeater);
			rs = ps.executeQuery();
			while (rs.next()) {
				SmokeBean smk = new SmokeBean();
				smk.setMac(rs.getString(1));
				smk.setPrincipal1Phone(rs.getString(2));
				slist.add(smk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return slist;
	}

	public List<SmokeBean> getSmokeInfo(int areaId) {
		String sqlstr = "SELECT mac,principal1Phone from smoke where areaid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		List<SmokeBean> slist = new ArrayList<SmokeBean>();
		ResultSet rs = null;
		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				SmokeBean smk = new SmokeBean();
				smk.setMac(rs.getString(1));
				smk.setPrincipal1Phone(rs.getString(2));
				slist.add(smk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return slist;
	}

	@Override
	public void insertUserSmoke(int areaId) {
		List<SmokeBean> lists = getSmokeInfo(areaId);
		int result = 0;
		for (SmokeBean smk : lists) {
			System.out.println(smk.getMac() + "====="
					+ smk.getPrincipal1Phone());
			result = insertuseridsmoke(smk.getMac(), smk.getPrincipal1Phone());
			System.out.println(result);
		}
	}

	@Override
	public List<String> getSoundList(String repeaterMac) {
		String sql = "SELECT deviceMac,soundMac from ackdevice where repeaterMac = ?";
		List<String> soundList = new ArrayList<String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				soundList.add(rs.getString("soundMac"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return soundList;
	}

	/**
	 * ͨ��accessTokenע��ص���ַ
	 */
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/subscribe-service-address";

		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("callbackUrl", callbackUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		// ������Ϣͷ
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ע�ᶩ�ĳɹ�");
				} else {
					result = "ע�ᶩ��ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ע�ᶩ��ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ͨ��accessToken�����豸�·�����
	 */
	public String ackAccessToKen(String accessToken, String serverID,
			String devSerial, String method,String key,int value) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String url = "https://www.easy-iot.cn/idev/3rdcap/dev-control/urt-command";
		JSONObject params = new JSONObject();
		params.put(key, value);
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("method", method);
		reqBody.put("params", params);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�豸����ָ�����·���commandId=" + respJson.getString("commandId"));
				} else {
//					System.out.println("�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult"));
					result = "�豸����ָ���·�ʧ�ܣ�������="+ respJson.getString("optResult");
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
//				System.out.println("���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ͨ��accessToken�����豸�·�����
	 */
	public String ackAccessToKens(String accessToken, String serverID,
			String devSerial, String method,Map<String,Integer> param) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String url = "https://www.easy-iot.cn/idev/3rdcap/dev-control/urt-command";
		JSONObject params = new JSONObject();
		for(String key : param.keySet()){
			int value = param.get(key);
			params.put(key, value);
		}
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("method", method);
		reqBody.put("params", params);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�豸����ָ�����·���commandId=" + respJson.getString("commandId"));
				} else {
					System.out.println("�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult"));
					result = "�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult");
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode());
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	@Override
	public String connectionPlatform(String serverID, String accessToken) {
		// TODO Auto-generated method stub
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/service-config/get-iotservers";
		// ��Ϣͷ����serverID
		// String serverID = "����д�û���";
		// ��Ϣͷ����accessToken
		// String accessToken = "����д�ɹ���½���accessToken";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("iotserverList")
							.toJSONString();
					System.out.println(result);
					System.out.println("IOT����ƽ̨�б����£�\n"
							+ respJson.getJSONArray("iotserverList"));
				} else {
					System.out.println("��ȡIOT����ƽ̨�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryServiceMode(String serverID, String accessToken,
			String iotserverId) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/service-config/get-iotservicemode";
		// String iotserverId = "ctc-nanjing-iot-137";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?iotserverId=" + iotserverId);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("serviceModeList")
							.toJSONString();
					System.out.println("��ѯ����ģʽ�б����£�\n"
							+ respJson.getJSONArray("serviceModeList"));
				} else {
					System.out.println("��ѯ����ģʽ�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type) {
		// TODO Auto-generated method stub
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/reg-device";
		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("deviceType", deviceType);
		reqBody.put("connectPointId", connectPointId);
		reqBody.put("serviceMode", serviceMode);
//		reqBody.put("endUserName", endUserName);
//		reqBody.put("endUserInfo", endUserInfo);
//		reqBody.put("location", location);
//		reqBody.put("longitude", longitude);
//		reqBody.put("latitude", latitude);
//		reqBody.put("extend_type", extend_type);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		// ������Ϣͷ
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ע���豸�ɹ�");
				} else {
					result = "ע���豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ע���豸ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String updateDevice(String serverID, String accessToken,
			String devSerial, String name, String longitude, String latitude) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/update-device";
		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("longitude", longitude);
		reqBody.put("latitude", latitude);
		// ���ԷǱ�ѡ�Ĳ���...
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPut httpPut = new HttpPut(url);
		// ������Ϣͷ
		httpPut.setHeader("Content-type", contentType);
		httpPut.setHeader("serverID", serverID);
		httpPut.setHeader("accessToken", accessToken);
		try {
			httpPut.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPut);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�����豸�ɹ�");
				} else {
					result = "�����豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("�����豸ʧ�ܣ�������=" + respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String deleteDevice(String serverID, String accessToken,
			String devSerial) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/del-device";
		// ��Ϣͷ����serverID = "����д�û���";
		// ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
		// �豸���к� = "TEST$_1130_1";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url + "?devSerial=" + devSerial);
		httpDelete.setHeader("Content-type", contentType);
		httpDelete.setHeader("serverID", serverID);
		httpDelete.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpDelete);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ɾ���豸�ɹ�");
				} else {
					result ="ɾ���豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ɾ���豸ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryDevice(String serverID, String accessToken,
			String devSerial) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/query-device-allinfo";
		// ��Ϣͷ����serverID = "����д�û���";
		// ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
		// �豸���к� = "TEST$_1130_1";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("��ѯ�豸��Ϣ���£�\n" + respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸��Ϣʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryDeviceType(String serverID, String accessToken,
			String devType) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/dev-manage/query-devType";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devType=" + devType);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("�豸����(��Ʒ�ͺ�)��Ϣ���£�\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸����(��Ʒ�ͺ�)��Ϣʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("��ѯ�豸����(��Ʒ�ͺ�)��Ϣʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryAllAvailableDeviceTypes(String serverID,
			String accessToken) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/list-devtypes";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?serverID=" + serverID);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("devTypes").toJSONString();
					System.out.println("��ѯ���п����豸���ͣ���Ʒ�ͺţ��ӿ��б����£�\n"
							+ respJson.getJSONArray("devTypes").toJSONString());
				} else {
					System.out.println("��ѯ��ѯ���п����豸���ͣ���Ʒ�ͺţ��ӿ��б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryUserAllDevice(String serverID, String accessToken) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/list-devices";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?serverID=" + serverID);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("devices").toJSONString();
					System.out.println("�豸�б����£�\n"
							+ respJson.getJSONArray("devices").toJSONString());
				} else {
					System.out.println("��ѯ�豸�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryTheDeviceDusinessData(String serverID,
			String accessToken, String devSerial) {
		String result = "";
		String contentType = "application/json";

		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/device-history-data";

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		// ������Ϣͷ
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("��ѯ�豸ҵ��������ʷ��¼������£�\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸ҵ��������ʷ��¼ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public String unSubscribe(String serverID, String accessToken) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/unsubscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url);
		// ������Ϣͷ
		httpDelete.setHeader("Content-type", contentType);
		httpDelete.setHeader("serverID", serverID);
		httpDelete.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpDelete);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ȡ�����ĳɹ�");
				} else {
					result = "ȡ������ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ȡ������ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public String querySubscribe(String serverID, String accessToken) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = "https://www.easy-iot.cn/idev/3rdcap/query-subscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		// ������Ϣͷ
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getString("callbackUrl");
					System.out.println("���ĵ�ַ���£�\n"
							+ respJson.getString("callbackUrl"));
				} else {
					System.out.println("��ȡ���ĵ�ַʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String deviceMessageNotification(String callbackUrl,
			String devSerial, String createTime, String lastMesssageTime,
			String iotEventTime, String serviceData) {

		String result = "";

		return result;
	}

	@Override
	public String registrationCompleted(String serverID,String accessToken,List<EasyIOT> objdev) {
		// TODO Auto-generated method stub
		String result = "";
		String charset="utf-8";
	    String contentType="application/json";
	    
	    //�����ַ
	    String url="https://www.easy-iot.cn/idev/3rdcap/devices/reg-device-batch";
	    //��Ϣͷ����serverID
	    //��Ϣͷ����accessToken
	    //������
	    JSONObject reqBody = new JSONObject();
	    //�������devices
	    JSONArray devices=new JSONArray();
	    
	    //�豸1
	    for (EasyIOT obj:objdev) {
	    	JSONObject dev = new JSONObject();
		    dev.put("devSerial", obj.getDevSerial());
		    dev.put("name", obj.getName());
		    dev.put("deviceType", obj.getDeviceType());
		    dev.put("connectPointId", obj.getConnectPointId());
		    devices.add(dev);
		}
	    reqBody.put("devices", devices);
	    
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    
	    HttpPost httpPost = new HttpPost(url);
	    //������Ϣͷ
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	        	result = "���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode();
	            System.out.println("���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode());
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = "�ɹ�";
	            System.out.println("����ע���豸�ɹ�");
	        }else{
	        	result = "����ע���豸ʧ�ܣ�������="+respJson.getString("optResult");
	            System.out.println("����ע���豸ʧ�ܣ�������="+respJson.getString("optResult"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            httpClient.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		return result;
	}
	
	@Override
	public String queryService(String service) {
		// TODO Auto-generated method stub
		String result = "";
		/*String charset="utf-8";
	    String contentType="application/json";
	    
	    //�����ַ
	    String url="https://www.easy-iot.cn/idev/3rdcap/devices/reg-device-batch";
	    //��Ϣͷ����serverID
	    //��Ϣͷ����accessToken
	    //������
	    JSONObject reqBody = new JSONObject();
	    //�������devices
	    JSONArray devices=new JSONArray();
	    
	    //�豸1
	    for (EasyIOT obj:objdev) {
	    	JSONObject dev = new JSONObject();
		    dev.put("devSerial", obj.getDevSerial());
		    dev.put("name", obj.getName());
		    dev.put("deviceType", obj.getDeviceType());
		    dev.put("connectPointId", obj.getConnectPointId());
		    devices.add(dev);
		}
	    reqBody.put("devices", devices);
	    
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    
	    HttpPost httpPost = new HttpPost(url);
	    //������Ϣͷ
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	            System.out.println("���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode());
	            return false;
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = true;
	            System.out.println("����ע���豸�ɹ�");
	        }else{
	            System.out.println("����ע���豸ʧ�ܣ�������="+respJson.getString("optResult"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            httpClient.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }*/
		return result;
	}

	@Override
	public void addIotEntity(String devSerial, String createTime,
			String iotEventTime, String serviceId, String IOT_key,
			String IOT_value) {

		String sql = "INSERT into easyiot(devSerial,createTime,iotEventTime,serviceId,IOT_key,IOT_value)VALUES(?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, devSerial);
			ps.setString(2, createTime);
			ps.setString(3, iotEventTime);
			ps.setString(4, serviceId);
			ps.setString(5, IOT_key);
			ps.setString(6, IOT_value);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	/**
	 * ͨ��accessTokenע��ص���ַ
	 */
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl,String IotUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/subscribe-service-address";

		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("callbackUrl", callbackUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		// ������Ϣͷ
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ע�ᶩ�ĳɹ�");
				} else {
					result = "ע�ᶩ��ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ע�ᶩ��ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ͨ��accessToken�����豸�·�����
	 */
	public String ackAccessToKen(String accessToken, String serverID,
			String devSerial, String method,String key,int value,String IotUrl) {
		System.out.println("nowTime:"+GetTime.ConvertTimeByLong()+"�{���O������°l����");
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String url = IotUrl+"idev/3rdcap/dev-control/urt-command";
		JSONObject params = new JSONObject();
		params.put(key, value);
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("method", method);
		reqBody.put("params", params);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�豸����ָ�����·���commandId=" + respJson.getString("commandId"));
				} else {
//					System.out.println("�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult"));
					result = "�豸����ָ���·�ʧ�ܣ�������="+ respJson.getString("optResult");
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
//				System.out.println("���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ͨ��accessToken�����豸�·�����
	 */
	public String ackAccessToKens(String accessToken, String serverID,
			String devSerial, String method,Map<String,Integer> param,String IotUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String url = IotUrl+"idev/3rdcap/dev-control/urt-command";
		JSONObject params = new JSONObject();
		for(String key : param.keySet()){
			int value = param.get(key);
			params.put(key, value);
		}
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("method", method);
		reqBody.put("params", params);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�豸����ָ�����·���commandId=" + respJson.getString("commandId"));
				} else {
					System.out.println("�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult"));
					result = "�豸����ָ���·�ʧ�ܣ�������=" + respJson.getString("optResult");
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode());
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	@Override
	public String connectionPlatform(String serverID, String accessToken,String IotUrl) {
		// TODO Auto-generated method stub
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/service-config/get-iotservers";
		// ��Ϣͷ����serverID
		// String serverID = "����д�û���";
		// ��Ϣͷ����accessToken
		// String accessToken = "����д�ɹ���½���accessToken";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("iotserverList")
							.toJSONString();
					System.out.println(result);
					System.out.println("IOT����ƽ̨�б����£�\n"
							+ respJson.getJSONArray("iotserverList"));
				} else {
					System.out.println("��ȡIOT����ƽ̨�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryServiceMode(String serverID, String accessToken,
			String iotserverId,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/service-config/get-iotservicemode";
		// String iotserverId = "ctc-nanjing-iot-137";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?iotserverId=" + iotserverId);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("serviceModeList")
							.toJSONString();
					System.out.println("��ѯ����ģʽ�б����£�\n"
							+ respJson.getJSONArray("serviceModeList"));
				} else {
					System.out.println("��ѯ����ģʽ�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	

	@Override
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type,String IotUrl) {
		// TODO Auto-generated method stub
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/reg-device";
		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("deviceType", deviceType);
		reqBody.put("connectPointId", connectPointId);
		reqBody.put("serviceMode", serviceMode);
//		reqBody.put("endUserName", endUserName);
//		reqBody.put("endUserInfo", endUserInfo);
//		reqBody.put("location", location);
//		reqBody.put("longitude", longitude);
//		reqBody.put("latitude", latitude);
//		reqBody.put("extend_type", extend_type);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		// ������Ϣͷ
		httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("serverID", serverID);
		httpPost.setHeader("accessToken", accessToken);
		try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ע���豸�ɹ�");
				} else {
					result = "ע���豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ע���豸ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String updateDevice(String serverID, String accessToken,
			String devSerial, String name, String longitude, String latitude,String IotUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/update-device";
		// ��װ������Ϣ��
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("longitude", longitude);
		reqBody.put("latitude", latitude);
		// ���ԷǱ�ѡ�Ĳ���...
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPut httpPut = new HttpPut(url);
		// ������Ϣͷ
		httpPut.setHeader("Content-type", contentType);
		httpPut.setHeader("serverID", serverID);
		httpPut.setHeader("accessToken", accessToken);
		try {
			httpPut.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(httpPut);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("�����豸�ɹ�");
				} else {
					result = "�����豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("�����豸ʧ�ܣ�������=" + respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String deleteDevice(String serverID, String accessToken,
			String devSerial,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/del-device";
		// ��Ϣͷ����serverID = "����д�û���";
		// ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
		// �豸���к� = "TEST$_1130_1";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url + "?devSerial=" + devSerial);
		httpDelete.setHeader("Content-type", contentType);
		httpDelete.setHeader("serverID", serverID);
		httpDelete.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpDelete);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ɾ���豸�ɹ�");
				} else {
					result ="ɾ���豸ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ɾ���豸ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryDevice(String serverID, String accessToken,
			String devSerial,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/query-device-allinfo";
		// ��Ϣͷ����serverID = "����д�û���";
		// ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
		// �豸���к� = "TEST$_1130_1";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("��ѯ�豸��Ϣ���£�\n" + respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸��Ϣʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryDeviceType(String serverID, String accessToken,
			String devType,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/dev-manage/query-devType";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devType=" + devType);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("�豸����(��Ʒ�ͺ�)��Ϣ���£�\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸����(��Ʒ�ͺ�)��Ϣʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("��ѯ�豸����(��Ʒ�ͺ�)��Ϣʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryAllAvailableDeviceTypes(String serverID,
			String accessToken,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/list-devtypes";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?serverID=" + serverID);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("devTypes").toJSONString();
					System.out.println("��ѯ���п����豸���ͣ���Ʒ�ͺţ��ӿ��б����£�\n"
							+ respJson.getJSONArray("devTypes").toJSONString());
				} else {
					System.out.println("��ѯ��ѯ���п����豸���ͣ���Ʒ�ͺţ��ӿ��б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryUserAllDevice(String serverID, String accessToken,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/devices/list-devices";

		// ʹ��HttpClient���ʽӿ�
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?serverID=" + serverID);
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getJSONArray("devices").toJSONString();
					System.out.println("�豸�б����£�\n"
							+ respJson.getJSONArray("devices").toJSONString());
				} else {
					System.out.println("��ѯ�豸�б�ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String queryTheDeviceDusinessData(String serverID,
			String accessToken, String devSerial,String IotUrl) {
		String result = "";
		String contentType = "application/json";

		// �����ַ
		String url = IotUrl+"idev/3rdcap/device-history-data";

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		// ������Ϣͷ
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.toJSONString();
					System.out.println("��ѯ�豸ҵ��������ʷ��¼������£�\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("��ѯ�豸ҵ��������ʷ��¼ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public String unSubscribe(String serverID, String accessToken,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/unsubscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url);
		// ������Ϣͷ
		httpDelete.setHeader("Content-type", contentType);
		httpDelete.setHeader("serverID", serverID);
		httpDelete.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpDelete);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = "�ɹ�";
					System.out.println("ȡ�����ĳɹ�");
				} else {
					result = "ȡ������ʧ�ܣ�������=" + respJson.getString("optResult");
					System.out.println("ȡ������ʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "���ʽӿ�ʧ�ܣ�״̬��=" + resp.getStatusLine().getStatusCode();
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public String querySubscribe(String serverID, String accessToken,String IotUrl) {
		String result = "";
		String contentType = "application/json";
		// �����ַ
		String url = IotUrl+"idev/3rdcap/query-subscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		// ������Ϣͷ
		httpGet.setHeader("Content-type", contentType);
		httpGet.setHeader("serverID", serverID);
		httpGet.setHeader("accessToken", accessToken);
		try {
			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getString("callbackUrl");
					System.out.println("���ĵ�ַ���£�\n"
							+ respJson.getString("callbackUrl"));
				} else {
					System.out.println("��ȡ���ĵ�ַʧ�ܣ�������="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("���ʽӿ�ʧ�ܣ�״̬��="
						+ resp.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String deviceMessageNotification(String callbackUrl,
			String devSerial, String createTime, String lastMesssageTime,
			String iotEventTime, String serviceData,String IotUrl) {

		String result = "";

		return result;
	}

	@Override
	public String registrationCompleted(String serverID,String accessToken,List<EasyIOT> objdev,String IotUrl) {
		// TODO Auto-generated method stub
		String result = "";
		String charset="utf-8";
	    String contentType="application/json";
	    
	    //�����ַ
	    String url=IotUrl+"idev/3rdcap/devices/reg-device-batch";
	    //��Ϣͷ����serverID
	    //��Ϣͷ����accessToken
	    //������
	    JSONObject reqBody = new JSONObject();
	    //�������devices
	    JSONArray devices=new JSONArray();
	    
	    //�豸1
	    for (EasyIOT obj:objdev) {
	    	JSONObject dev = new JSONObject();
		    dev.put("devSerial", obj.getDevSerial());
		    dev.put("name", obj.getName());
		    dev.put("deviceType", obj.getDeviceType());
		    dev.put("connectPointId", obj.getConnectPointId());
		    devices.add(dev);
		}
	    reqBody.put("devices", devices);
	    
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    
	    HttpPost httpPost = new HttpPost(url);
	    //������Ϣͷ
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    try {
			httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	        	result = "���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode();
	            System.out.println("���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode());
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = "�ɹ�";
	            System.out.println("����ע���豸�ɹ�");
	        }else{
	        	result = "����ע���豸ʧ�ܣ�������="+respJson.getString("optResult");
	            System.out.println("����ע���豸ʧ�ܣ�������="+respJson.getString("optResult"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            httpClient.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		return result;
	}
	
	@Override
	public String queryService(String service,String IotUrl) {
		// TODO Auto-generated method stub
		String result = "";
		/*String charset="utf-8";
	    String contentType="application/json";
	    
	    //�����ַ
	    String url=IotUrl+"idev/3rdcap/devices/reg-device-batch";
	    //��Ϣͷ����serverID
	    //��Ϣͷ����accessToken
	    //������
	    JSONObject reqBody = new JSONObject();
	    //�������devices
	    JSONArray devices=new JSONArray();
	    
	    //�豸1
	    for (EasyIOT obj:objdev) {
	    	JSONObject dev = new JSONObject();
		    dev.put("devSerial", obj.getDevSerial());
		    dev.put("name", obj.getName());
		    dev.put("deviceType", obj.getDeviceType());
		    dev.put("connectPointId", obj.getConnectPointId());
		    devices.add(dev);
		}
	    reqBody.put("devices", devices);
	    
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    
	    HttpPost httpPost = new HttpPost(url);
	    //������Ϣͷ
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	            System.out.println("���ʽӿ�ʧ�ܣ�״̬��="+resp.getStatusLine().getStatusCode());
	            return false;
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = true;
	            System.out.println("����ע���豸�ɹ�");
	        }else{
	            System.out.println("����ע���豸ʧ�ܣ�������="+respJson.getString("optResult"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            httpClient.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }*/
		return result;
	}

	@Override
	public void addIotEntity(String devSerial, String createTime,
			String iotEventTime, String serviceId, String IOT_key,
			String IOT_value,String IotUrl) {

		String sql = "INSERT into easyiot(devSerial,createTime,iotEventTime,serviceId,IOT_key,IOT_value)VALUES(?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, devSerial);
			ps.setString(2, createTime);
			ps.setString(3, iotEventTime);
			ps.setString(4, serviceId);
			ps.setString(5, IOT_key);
			ps.setString(6, IOT_value);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	@Override
	public void addgasbyhm(String gasMac, int gasalarm, int language,
			int alarmType, int beepsoundlevel, int handonoff) {
		// TODO Auto-generated method stub
		String sql = "REPLACE into gas_infos(gasMac,gasalarm,language,alarmType,beepsoundlevel,handonoff,gastime) VALUES(?,?,?,?,?,?,?)";
		String gastime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, gasMac);
			ps.setInt(2, gasalarm);
			ps.setInt(3, 0);
			ps.setInt(4, alarmType);
			ps.setInt(5, beepsoundlevel);
			ps.setInt(6, handonoff);
			ps.setString(7, gastime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
}
