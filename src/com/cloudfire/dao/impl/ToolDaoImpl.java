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
	 * 通过accessToken注册回调地址
	 */
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/subscribe-service-address";

		// 封装请求消息体
		JSONObject reqBody = new JSONObject();
		reqBody.put("callbackUrl", callbackUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("注册订阅成功");
				} else {
					result = "注册订阅失败，返回码=" + respJson.getString("optResult");
					System.out.println("注册订阅失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
	 * 通过accessToken控制设备下发命令
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
					result = "成功";
					System.out.println("设备控制指令已下发，commandId=" + respJson.getString("commandId"));
				} else {
//					System.out.println("设备控制指令下发失败，返回码=" + respJson.getString("optResult"));
					result = "设备控制指令下发失败，返回码="+ respJson.getString("optResult");
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
//				System.out.println("访问接口失败，状态码=" + resp.getStatusLine().getStatusCode());
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
	 * 通过accessToken控制设备下发命令
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
					result = "成功";
					System.out.println("设备控制指令已下发，commandId=" + respJson.getString("commandId"));
				} else {
					System.out.println("设备控制指令下发失败，返回码=" + respJson.getString("optResult"));
					result = "设备控制指令下发失败，返回码=" + respJson.getString("optResult");
				}
			} else {
				System.out.println("访问接口失败，状态码=" + resp.getStatusLine().getStatusCode());
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/service-config/get-iotservers";
		// 消息头参数serverID
		// String serverID = "请填写用户名";
		// 消息头参数accessToken
		// String accessToken = "请填写成功登陆后的accessToken";

		// 使用HttpClient访问接口
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
					System.out.println("IOT连接平台列表如下：\n"
							+ respJson.getJSONArray("iotserverList"));
				} else {
					System.out.println("获取IOT连接平台列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/service-config/get-iotservicemode";
		// String iotserverId = "ctc-nanjing-iot-137";

		// 使用HttpClient访问接口
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
					System.out.println("查询服务模式列表如下：\n"
							+ respJson.getJSONArray("serviceModeList"));
				} else {
					System.out.println("查询服务模式列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/reg-device";
		// 封装请求消息体
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
		// 设置消息头
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
					result = "成功";
					System.out.println("注册设备成功");
				} else {
					result = "注册设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("注册设备失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/update-device";
		// 封装请求消息体
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("longitude", longitude);
		reqBody.put("latitude", latitude);
		// 忽略非必选的参数...
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPut httpPut = new HttpPut(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("更新设备成功");
				} else {
					result = "更新设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("更新设备失败，返回码=" + respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/del-device";
		// 消息头参数serverID = "请填写用户名";
		// 消息头参数accessToken = "请填写成功登陆后的accessToken";
		// 设备序列号 = "TEST$_1130_1";

		// 使用HttpClient访问接口
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
					result = "成功";
					System.out.println("删除设备成功");
				} else {
					result ="删除设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("删除设备失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/query-device-allinfo";
		// 消息头参数serverID = "请填写用户名";
		// 消息头参数accessToken = "请填写成功登陆后的accessToken";
		// 设备序列号 = "TEST$_1130_1";

		// 使用HttpClient访问接口
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
					System.out.println("查询设备信息如下：\n" + respJson.toJSONString());
				} else {
					System.out.println("查询设备信息失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/dev-manage/query-devType";

		// 使用HttpClient访问接口
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
					System.out.println("设备类型(产品型号)信息如下：\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("查询设备类型(产品型号)信息失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("查询设备类型(产品型号)信息失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/list-devtypes";

		// 使用HttpClient访问接口
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
					System.out.println("查询所有可用设备类型（产品型号）接口列表如下：\n"
							+ respJson.getJSONArray("devTypes").toJSONString());
				} else {
					System.out.println("查询查询所有可用设备类型（产品型号）接口列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/devices/list-devices";

		// 使用HttpClient访问接口
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
					System.out.println("设备列表如下：\n"
							+ respJson.getJSONArray("devices").toJSONString());
				} else {
					System.out.println("查询设备列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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

		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/device-history-data";

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		// 设置消息头
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
					System.out.println("查询设备业务数据历史记录结果如下：\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("查询设备业务数据历史记录失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/unsubscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("取消订阅成功");
				} else {
					result = "取消订阅失败，返回码=" + respJson.getString("optResult");
					System.out.println("取消订阅失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = "https://www.easy-iot.cn/idev/3rdcap/query-subscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		// 设置消息头
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
					System.out.println("订阅地址如下：\n"
							+ respJson.getString("callbackUrl"));
				} else {
					System.out.println("获取订阅地址失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
	    
	    //请求地址
	    String url="https://www.easy-iot.cn/idev/3rdcap/devices/reg-device-batch";
	    //消息头参数serverID
	    //消息头参数accessToken
	    //请求体
	    JSONObject reqBody = new JSONObject();
	    //请求参数devices
	    JSONArray devices=new JSONArray();
	    
	    //设备1
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
	    //设置消息头
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
	        	result = "访问接口失败，状态码="+resp.getStatusLine().getStatusCode();
	            System.out.println("访问接口失败，状态码="+resp.getStatusLine().getStatusCode());
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = "成功";
	            System.out.println("批量注册设备成功");
	        }else{
	        	result = "批量注册设备失败，返回码="+respJson.getString("optResult");
	            System.out.println("批量注册设备失败，返回码="+respJson.getString("optResult"));
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
	    
	    //请求地址
	    String url="https://www.easy-iot.cn/idev/3rdcap/devices/reg-device-batch";
	    //消息头参数serverID
	    //消息头参数accessToken
	    //请求体
	    JSONObject reqBody = new JSONObject();
	    //请求参数devices
	    JSONArray devices=new JSONArray();
	    
	    //设备1
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
	    //设置消息头
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	            System.out.println("访问接口失败，状态码="+resp.getStatusLine().getStatusCode());
	            return false;
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = true;
	            System.out.println("批量注册设备成功");
	        }else{
	            System.out.println("批量注册设备失败，返回码="+respJson.getString("optResult"));
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
	 * 通过accessToken注册回调地址
	 */
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl,String IotUrl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		// 请求地址
		String url = IotUrl+"idev/3rdcap/subscribe-service-address";

		// 封装请求消息体
		JSONObject reqBody = new JSONObject();
		reqBody.put("callbackUrl", callbackUrl);

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("注册订阅成功");
				} else {
					result = "注册订阅失败，返回码=" + respJson.getString("optResult");
					System.out.println("注册订阅失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
	 * 通过accessToken控制设备下发命令
	 */
	public String ackAccessToKen(String accessToken, String serverID,
			String devSerial, String method,String key,int value,String IotUrl) {
		System.out.println("nowTime:"+GetTime.ConvertTimeByLong()+"調用設備控制下發命令");
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
					result = "成功";
					System.out.println("设备控制指令已下发，commandId=" + respJson.getString("commandId"));
				} else {
//					System.out.println("设备控制指令下发失败，返回码=" + respJson.getString("optResult"));
					result = "设备控制指令下发失败，返回码="+ respJson.getString("optResult");
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
//				System.out.println("访问接口失败，状态码=" + resp.getStatusLine().getStatusCode());
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
	 * 通过accessToken控制设备下发命令
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
					result = "成功";
					System.out.println("设备控制指令已下发，commandId=" + respJson.getString("commandId"));
				} else {
					System.out.println("设备控制指令下发失败，返回码=" + respJson.getString("optResult"));
					result = "设备控制指令下发失败，返回码=" + respJson.getString("optResult");
				}
			} else {
				System.out.println("访问接口失败，状态码=" + resp.getStatusLine().getStatusCode());
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/service-config/get-iotservers";
		// 消息头参数serverID
		// String serverID = "请填写用户名";
		// 消息头参数accessToken
		// String accessToken = "请填写成功登陆后的accessToken";

		// 使用HttpClient访问接口
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
					System.out.println("IOT连接平台列表如下：\n"
							+ respJson.getJSONArray("iotserverList"));
				} else {
					System.out.println("获取IOT连接平台列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/service-config/get-iotservicemode";
		// String iotserverId = "ctc-nanjing-iot-137";

		// 使用HttpClient访问接口
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
					System.out.println("查询服务模式列表如下：\n"
							+ respJson.getJSONArray("serviceModeList"));
				} else {
					System.out.println("查询服务模式列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/reg-device";
		// 封装请求消息体
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
		// 设置消息头
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
					result = "成功";
					System.out.println("注册设备成功");
				} else {
					result = "注册设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("注册设备失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/update-device";
		// 封装请求消息体
		JSONObject reqBody = new JSONObject();
		reqBody.put("devSerial", devSerial);
		reqBody.put("name", name);
		reqBody.put("longitude", longitude);
		reqBody.put("latitude", latitude);
		// 忽略非必选的参数...
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPut httpPut = new HttpPut(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("更新设备成功");
				} else {
					result = "更新设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("更新设备失败，返回码=" + respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/del-device";
		// 消息头参数serverID = "请填写用户名";
		// 消息头参数accessToken = "请填写成功登陆后的accessToken";
		// 设备序列号 = "TEST$_1130_1";

		// 使用HttpClient访问接口
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
					result = "成功";
					System.out.println("删除设备成功");
				} else {
					result ="删除设备失败，返回码=" + respJson.getString("optResult");
					System.out.println("删除设备失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/query-device-allinfo";
		// 消息头参数serverID = "请填写用户名";
		// 消息头参数accessToken = "请填写成功登陆后的accessToken";
		// 设备序列号 = "TEST$_1130_1";

		// 使用HttpClient访问接口
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
					System.out.println("查询设备信息如下：\n" + respJson.toJSONString());
				} else {
					System.out.println("查询设备信息失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/dev-manage/query-devType";

		// 使用HttpClient访问接口
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
					System.out.println("设备类型(产品型号)信息如下：\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("查询设备类型(产品型号)信息失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("查询设备类型(产品型号)信息失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/list-devtypes";

		// 使用HttpClient访问接口
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
					System.out.println("查询所有可用设备类型（产品型号）接口列表如下：\n"
							+ respJson.getJSONArray("devTypes").toJSONString());
				} else {
					System.out.println("查询查询所有可用设备类型（产品型号）接口列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/devices/list-devices";

		// 使用HttpClient访问接口
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
					System.out.println("设备列表如下：\n"
							+ respJson.getJSONArray("devices").toJSONString());
				} else {
					System.out.println("查询设备列表失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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

		// 请求地址
		String url = IotUrl+"idev/3rdcap/device-history-data";

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "?devSerial=" + devSerial);
		// 设置消息头
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
					System.out.println("查询设备业务数据历史记录结果如下：\n"
							+ respJson.toJSONString());
				} else {
					System.out.println("查询设备业务数据历史记录失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/unsubscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url);
		// 设置消息头
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
					result = "成功";
					System.out.println("取消订阅成功");
				} else {
					result = "取消订阅失败，返回码=" + respJson.getString("optResult");
					System.out.println("取消订阅失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				result = "访问接口失败，状态码=" + resp.getStatusLine().getStatusCode();
				System.out.println("访问接口失败，状态码="
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
		// 请求地址
		String url = IotUrl+"idev/3rdcap/query-subscribe-service-address";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		// 设置消息头
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
					System.out.println("订阅地址如下：\n"
							+ respJson.getString("callbackUrl"));
				} else {
					System.out.println("获取订阅地址失败，返回码="
							+ respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码="
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
	    
	    //请求地址
	    String url=IotUrl+"idev/3rdcap/devices/reg-device-batch";
	    //消息头参数serverID
	    //消息头参数accessToken
	    //请求体
	    JSONObject reqBody = new JSONObject();
	    //请求参数devices
	    JSONArray devices=new JSONArray();
	    
	    //设备1
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
	    //设置消息头
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
	        	result = "访问接口失败，状态码="+resp.getStatusLine().getStatusCode();
	            System.out.println("访问接口失败，状态码="+resp.getStatusLine().getStatusCode());
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = "成功";
	            System.out.println("批量注册设备成功");
	        }else{
	        	result = "批量注册设备失败，返回码="+respJson.getString("optResult");
	            System.out.println("批量注册设备失败，返回码="+respJson.getString("optResult"));
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
	    
	    //请求地址
	    String url=IotUrl+"idev/3rdcap/devices/reg-device-batch";
	    //消息头参数serverID
	    //消息头参数accessToken
	    //请求体
	    JSONObject reqBody = new JSONObject();
	    //请求参数devices
	    JSONArray devices=new JSONArray();
	    
	    //设备1
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
	    //设置消息头
	    httpPost.setHeader("Content-type", contentType);
	    httpPost.setHeader("serverID", serverID);
	    httpPost.setHeader("accessToken", accessToken);
	    httpPost.setEntity(new StringEntity(reqBody.toJSONString(), charset));
	    try {
	        CloseableHttpResponse resp = httpClient.execute(httpPost);
	        HttpEntity entity = resp.getEntity();
	        if (resp.getStatusLine().getStatusCode()!=200) {
	            System.out.println("访问接口失败，状态码="+resp.getStatusLine().getStatusCode());
	            return false;
	        }
	        String responseData = EntityUtils.toString(entity);
	        JSONObject respJson = JSONObject.parseObject(responseData);
	        if ("0".equals(respJson.getString("optResult"))) {
	        	result = true;
	            System.out.println("批量注册设备成功");
	        }else{
	            System.out.println("批量注册设备失败，返回码="+respJson.getString("optResult"));
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
