package com.cloudfire.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.cloudfire.dao.CompanyDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.LoginHttpRsult;
import com.cloudfire.entity.UserEntity;
import com.cloudfire.mail.MailSenderInfo;
import com.cloudfire.mail.SimpleMailSender;
import com.cloudfire.push.GeTuiConfiguration;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.MD5;
import com.cloudfire.until.Utils;
import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IAliasResult;
import com.gexin.rp.sdk.http.IGtPush;

public class LoginDaoImpl implements LoginDao {

	private static String appkey = "81wzEpKaYp7vEzcFgDEWp2";
	private static String mastersecret = "qGM9DuOZnW51PL6hai9xB5";
	private static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public LoginEntity login(String userId) {
		String loginSql = "select named,privilege,salt,isCanCutEletr from user where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql);
		ResultSet rs = null;
		LoginEntity le = new LoginEntity();
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				le.setName(rs.getString("named"));
				le.setIsCanCutEletr(rs.getString("isCanCutEletr"));
				int privilege = rs.getInt("privilege");
				if (privilege == 6 || privilege == 7) {
					le.setPrivilege(3);// @@权限为6或7，返回3
				} else {
					le.setPrivilege(privilege);
				}
				le.setSalt(rs.getString("salt"));
				// le.setPrivId(rs.getInt("privId"));
				Random rd = new Random();
				String toKen = rd.nextInt() + userId;
				toKen = new MD5().getMD5ofStr(toKen);
				Utils.userMd5.put(userId, toKen);
				le.setErrorCode(0);
				le.setError("登录成功");
				le.setUserId(userId);
				le.setToKen(toKen);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return le;
	}

	public LoginEntity login2(String userId, String psw) {
		String loginSql = "select named,privilege,pwd,privId,salt from user where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql);
		ResultSet rs = null;
		LoginEntity le = new LoginEntity();
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				String soft = rs.getString("salt");
	    		String password = MD5.encrypt(psw + soft);
				if (psw.equals(rs.getString("pwd"))||password.equals(rs.getString("pwd"))) {
					le.setName(rs.getString("named"));
					int privilege = rs.getInt("privilege");
					if (privilege != 4 && privilege > 3) {
						le.setPrivilege(3);// @@权限为6或7，返回3
					} else {
						le.setPrivilege(privilege);
					}
					le.setSalt(rs.getString("salt"));
					le.setErrorCode(0);
					le.setError("登录成功");
					le.setPrivId(rs.getInt("privId"));	//privId==4允许获取token接口否则不允许访问
					if(le.getPrivId()<4){
						le.setToKen("获取token失败：无权限。");
					}else{
						Random rd = new Random();
						String toKen = rd.nextInt() + userId;
						toKen = new MD5().getMD5ofStr(toKen);
						Utils.userMd5.put(userId,toKen );
						le.setToKen(toKen);
					}
					le.setUserId(userId);
				} else {
					le.setErrorCode(1);
					le.setError("账号或密码错误");
				}
			} else {
				le.setErrorCode(2);
				le.setError("账号不存在");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return le;
	}

	// add by lzo at 2017-5-11 user save pwd
	public LoginEntity loginToYooSee(String userId, String pwd) {
		String sql = "UPDATE USER SET pwd = ? WHERE userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, pwd);
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return null;
	}

	@Override
	public boolean unBindAliasAll(String appId, String Alias, String cid) {
		LoginDao loginDao = new LoginDaoImpl();
		loginDao.savePushBindCid(Alias, cid, 0);
		
		appkey = GeTuiConfiguration.appKey_zdst;
		mastersecret = GeTuiConfiguration.masterSecret_zdst;
		appId = GeTuiConfiguration.appId_zdst;
		

		IGtPush push = new IGtPush(host, appkey, mastersecret);
		IAliasResult AliasUnBindAll = push.unBindAliasAll(appId, Alias);

		return AliasUnBindAll.getResult();
	}

	@Override
	public boolean bindAlias(String appId, String Alias, String cid) {
		IGtPush push = new IGtPush(host, appkey, mastersecret);
		IAliasResult bindSCid = push.bindAlias(appId, Alias, cid);
		// System.out.println("绑定结果：" + bindSCid.getResult() + "错误码:" +
		// bindSCid.getErrorMsg());
		LoginDao loginDao = new LoginDaoImpl();
		loginDao.savePushBindCid(Alias, cid, 1);
		return bindSCid.getResult();
	}

	@Override
	public void savePushBindCid(String userid, String cid, int ifbind) {
		String sql;
		boolean flag = isExits(cid);
		if (flag) {
			sql = "UPDATE pushbindcid SET userid=?,ifbind=? WHERE cid=? ";
		} else {
			sql = "INSERT INTO pushbindcid (userid,ifbind,cid) VALUES (?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, userid);
			ps.setInt(2, ifbind);
			ps.setString(3, cid);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public boolean isExits(String cid) {
		boolean result = false;
		String sql = "SELECT cid,ifbind FROM pushbindcid WHERE cid=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, cid);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean unBindAlias(String appId, String Alias, String cid) {
		LoginDao loginDao = new LoginDaoImpl();
		loginDao.savePushBindCid(Alias, cid, 0);
		
		appkey = GeTuiConfiguration.appKey_zdst;
		mastersecret = GeTuiConfiguration.masterSecret_zdst;
		appId = GeTuiConfiguration.appId_zdst;
	

		IGtPush push = new IGtPush(host, appkey, mastersecret);
		IAliasResult AliasUnBind = push.unBindAlias(appId, Alias, cid);

		return AliasUnBind.getResult();
	}

	@Override
	public void updateUserAppId(String userId, int appId) {
		String sql = "UPDATE USER SET appId=? WHERE userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, appId);
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public int getAppIdByUser(String userId) {
		String sql = "SELECT appId FROM USER WHERE userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return result;
	}

	public int loginUser(String userId, String pwd) {
		int result = 0;
		String sql = "select userId from user where userId = ? and pwd = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, pwd);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public int updateLoginState(String userId, int state, String ipaddress) {
		int loginState = 0;
		String sql = "";
		if (Utils.isNullStr(ipaddress)) {
			sql = "UPDATE user SET loginState = ?,hostAddress=? WHERE userId = ?";
		} else {
			sql = "UPDATE user SET loginState = ? WHERE userId = ?";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, state);
			if (Utils.isNullStr(ipaddress)) {
				ps.setString(2, ipaddress);
				ps.setString(3, userId);
			} else {
				ps.setString(2, userId);
			}
			loginState = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return loginState;
	}

	public int getLoginState(String userId) {
		int loginState = 0;
		String sql = "SELECT loginState FROM USER WHERE userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				loginState = rs.getInt("loginState");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return loginState;
	}

	@Override
	public boolean charactUser(String userId) {
		String sql = "SELECT userId,named,loginState,hostAddress from user where userid = ?";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt("loginState") == 1) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean charactUserName(String userName) {
		String sql = "SELECT userId from user where userid = ?";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userName);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// @@ liangbin 2017.09.28
	@Override
	public boolean addUser(String userId, int privilege, String psw) {
		String getTime = GetTime.ConvertTimeByLong();
		LoginEntity userEntity = login(userId);
		String sql = "";
		if (userEntity.getErrorCode() != 2) {
			privilege = userEntity.getPrivilege();
			sql = "update user set pwd=?,privilege=?,endTime=? where userId = ?";
		} else {
			sql = "insert user (pwd,privilege,endTime,userId) values (?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(4, userId);
			ps.setString(1, psw);
			ps.setInt(2, privilege);
			ps.setString(3, getTime);
			rs = ps.executeUpdate();
			if (rs > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean addUser(String userId, int privilege, String name, String psw, String salt) {
		String getTime = GetTime.ConvertTimeByLong();
		LoginEntity userEntity = login(userId);
		String sql = "";
		if (userEntity.getErrorCode() != 2) {
			privilege = userEntity.getPrivilege();
			sql = "update user set pwd=?,privilege=?,endTime=?,named=?, salt=? where userId = ?";
		} else {
			sql = "insert user (pwd,privilege,endTime,named,salt,userId) values (?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(5, salt);
			ps.setString(4, name);
			ps.setString(1, psw);
			ps.setInt(2, privilege);
			ps.setString(3, getTime);
			ps.setString(6, userId);
			rs = ps.executeUpdate();
			if (rs > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int elecMeterAddUser(String user, String pwd) {
		String haveUserString = elecMeterHaveUser(user);
		if (haveUserString != null)
			return 3;
		int result = -1;
		String sql = "insert elecMeterUser (user,password) values(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);

		try {
			ps.setString(1, user);
			;
			ps.setString(2, pwd);
			if (ps.executeUpdate() > 0)
				result = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			;
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int elecMeterLogin(String user, String pwd) {

		int ret = -1;
		String pwdQueryString = elecMeterHaveUser(user);
		if (pwdQueryString == null) {
			ret = 3;// 用户不存在
		} else if (pwdQueryString.compareTo(pwd) == 0) {
			ret = 0;
		} else {
			ret = 4;// 密码错误
		}
		return ret;
	}

	private String elecMeterHaveUser(String user) {
		String sql = "select password from elecMeterUser where user=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet resultSet;
		String result = null;
		try {
			ps.setString(1, user);
			;

			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				result = resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			;
			DBConnectionManager.close(conn);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cloudfire.dao.LoginDao#elecMeterSendEmail(java.lang.String)
	 */
	@Override
	public int elecMeterSendEmail(String email) {
		String sql = "insert into emailCheck(email,serialCode ) values(?,?) on duplicate key update serialCode =? ";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement preparedStatement = DBConnectionManager.prepare(connection, sql);
		String radomString = getRandomString(15);
		int ret = 0;

		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.sina.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("yzzttt@sina.com");
		mailInfo.setPassword("yzztt348193");// 您的邮箱密码
		mailInfo.setFromAddress("yzzttt@sina.com");
		mailInfo.setToAddress(email);
		mailInfo.setSubject("注册广州瀚润电表客户端邮箱验证");
		StringBuffer content = new StringBuffer();
		content.append("<a href=\"http://127.0.0.1:8080/fireSystem/elecMeterCheckEmail?serialCode=");
		content.append(radomString);
		content.append("&email=");
		content.append(email);
		content.append("\">请点击这里进行邮箱验证</a>");
		mailInfo.setContent(content.toString());
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		if (sms.sendHtmlMail(mailInfo)) { // 发送文体格式
			ret = 1;
		}

		try {
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, radomString);
			preparedStatement.setString(3, radomString);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(preparedStatement);
			DBConnectionManager.close(connection);
		}
		return ret;
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	@Override
	public int elecMeterCheckEmail(String serialCode, String email) {
		String sql = "select serialCode from emailCheck where email=?";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement preparedStatement = DBConnectionManager.prepare(connection, sql);
		ResultSet resultSet;
		int ret = 0;
		String serialCodeQuery = "";
		try {
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.first())
				serialCodeQuery = resultSet.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(preparedStatement);
			DBConnectionManager.close(connection);
		}
		if (serialCodeQuery.equals(serialCode)) {
			ret = 1;
			setCheckEmailCode(email, 1);
		}

		return ret;
	}

	public static void setCheckEmailCode(String email, int code) {
		String sql = "update emailCheck set ifCheck =? where email=?";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement preparedStatement = DBConnectionManager.prepare(connection, sql);
		try {
			preparedStatement.setInt(1, code);
			preparedStatement.setString(2, email);

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(preparedStatement);
			DBConnectionManager.close(connection);
		}
	}

	public static int getCheckEmailCode(String email) {
		String sql = "select ifCheck from emailCheck where email=?";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement preparedStatement = DBConnectionManager.prepare(connection, sql);
		ResultSet resultSet;
		int ret = -1;
		try {
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.first())
				ret = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(preparedStatement);
			DBConnectionManager.close(connection);
		}
		return ret;
	}

	@Override
	public int updateUserInfo(String userId, String privilege) {
		String sqlstr = "update user set privilege = ? where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int result = 0;
		try {
			ps.setString(1, privilege);
			ps.setString(2, userId);
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
	public String getEaseIotAccessToKen(String serverId, String password) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String reqBody = null;// 请求参数json字符串
		String url = "http://www.easy-iot.cn/idev/3rdcap/server/login";// 请求地址
		// String serverId = "gzhr01";//参数serverId用户名
		// String password = "123456aB";//参数password密码
		JSONObject json = new JSONObject();
		json.put("serverId", serverId);
		json.put("password", password);
		reqBody = json.toJSONString();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			StringEntity bodyEntity = new StringEntity(reqBody, charset);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", contentType);
			httpPost.setEntity(bodyEntity);
	
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getString("accessToken");
					System.out.println("登录验证成功,accessToken=" + respJson.getString("accessToken"));
				} else {
					System.out.println("登录验证失败,返回码：" + respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码：" + resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
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
	public String getEaseIotAccessToKen(String serverId, String password, String ioturl) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/json";
		String reqBody = null;// 请求参数json字符串
		String url = ioturl + "idev/3rdcap/server/login";// 请求地址
		// String serverId = "gzhr01";//参数serverId用户名
		// String password = "123456aB";//参数password密码
		JSONObject json = new JSONObject();
		json.put("serverId", serverId);
		json.put("password", password);
		reqBody = json.toJSONString();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			StringEntity bodyEntity = new StringEntity(reqBody, charset);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", contentType);
			httpPost.setEntity(bodyEntity);
	
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getString("accessToken");
					System.out.println("登录验证成功,accessToken=" + respJson.getString("accessToken"));
				} else {
					System.out.println("登录验证失败,返回码：" + respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码：" + resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
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
	public String getNanJinToKen(String serverId, String password) {
		String result = "";
		String charset = "utf-8";
		String contentType = "application/x-www-form-urlencoded";
		String reqBody = null;// 请求参数json字符串
		String url = "https://180.101.147.89:8743/iocm/app/sec/v1.1.0/login";// 请求地址
		// String serverId = "gzhr01";//参数serverId用户名
		// String password = "123456aB";//参数password密码
		JSONObject json = new JSONObject();
		json.put("serverId", serverId);
		json.put("password", password);
		reqBody = json.toJSONString();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			StringEntity bodyEntity = new StringEntity(reqBody, charset);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", contentType);
			httpPost.setEntity(bodyEntity);
		
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() == 200) {
				String responseData = EntityUtils.toString(entity);
				JSONObject respJson = JSONObject.parseObject(responseData);
				if ("0".equals(respJson.getString("optResult"))) {
					result = respJson.getString("accessToken");
					System.out.println("登录验证成功,accessToken=" + respJson.getString("accessToken"));
				} else {
					System.out.println("登录验证失败,返回码：" + respJson.getString("optResult"));
				}
			} else {
				System.out.println("访问接口失败，状态码：" + resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
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
	public int getCompanyByUser(String userId) {
		String sql = "SELECT company FROM USER WHERE userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return result;
	}

	@Override
	public String getSaltByUserId(String userId) {
		String sql = "SELECT salt FROM USER WHERE userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String result = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return result;
	}

	@Override
	public boolean unBindIOS(String alias) {
		String sql = "delete from useridios where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, alias);
			int a=ps.executeUpdate();
			if(a>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return result;
	}

	@Override
	public LoginHttpRsult verify(String userName, String pwd,String privId) {
		UserEntity userEntity = getUserInfo(userName);
		LoginHttpRsult hr = new LoginHttpRsult();
		if(userEntity == null ){
			hr.setError("该用户不存在");
			hr.setErrorCode(1);
			return hr;
		} else {
			int privilege = userEntity.getPrivilege();
			if(privilege!=4&&privilege>3) privilege = 3;
			if(privilege!=3&&privilege!=4){
	        	hr.setError("权限不足,请管理员授权");
	        	hr.setErrorCode(1);
	        	return hr;
			}
			
			int loginState = 1;
			
			if(privId == null){  //后台登陆
	    		if ("gzhr".equals(userName)||"15986438401".equals(userName)){
	    			hr.setError("不是管理员,请与管理员联系");
	            	hr.setErrorCode(1);
	            	return hr;
	    		}
	    	} else { //前台登陆
	    		//再判断用户所选机构是否与本身机构相同
	    		int oripriv = userEntity.getPrivId();
	    		if (privId.equals("1") && oripriv == 1) {  //建筑单位的用户登陆
					CompanyDao cd = new CompanyDaoImpl();
					if (cd.ifExitCompany(userName)) {
						loginState = 1;
					} else {
						loginState = 2;
					} 
				} else if (privId.equals("3")&& oripriv == 3) { //监管部门的用户登陆
					loginState = 3;
				} else {
		        	hr.setError( "您选择的用户类型不匹配！");
		        	hr.setErrorCode(1);
		        	return hr;
				}
	    	} 
			if (MD5.encrypt(pwd+userEntity.getSalt()).equals(userEntity.getPwd())) {  //登陆密码+盐加密后与数据库密码一致
				hr.setError("登陆成功");
				hr.setErrorCode(0);
				hr.setLoginState(loginState);
				hr.setPrivId(userEntity.getPrivilege()+"");
			} else if(pwd.equals(userEntity.getPwd())) {
				hr.setError("登陆成功，但是您的密码存在隐患，请及时在后台重置密码");
				hr.setErrorCode(0);
				hr.setLoginState(loginState);
				hr.setPrivId(userEntity.getPrivilege()+"");
			} else if(MD5.encrypt(pwd).equals(userEntity.getPwd())) {
				hr.setError("登陆成功，但是您的密码存在隐患，请及时在后台重置密码");
				hr.setErrorCode(0);
				hr.setLoginState(loginState);
				hr.setPrivId(userEntity.getPrivilege()+"");
			}else {
				hr.setError("用户名或密码错误，请联系管理员");
				hr.setErrorCode(1);
			}
		}
		return hr;
	}
	
	UserEntity getUserInfo(String userName){
		String sqlstr = "SELECT userid,named,privilege,iftryuser,endtime,privid,istxt,pwd,salt FROM user WHERE userId=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		UserEntity ue = null;
		try {
			ps.setString(1, userName);
			rs = ps.executeQuery();
			while(rs.next()){
				ue = new UserEntity();
				ue.setUserId(rs.getString(1));
				ue.setNamed(rs.getString(2));
				if(rs.getInt(3)!=4&&rs.getInt(3)>3){
					ue.setPrivilege(3);
				}else{
					ue.setPrivilege(rs.getInt(3));
				}
				ue.setIfTryUser(rs.getInt(4));
				ue.setEndTime(rs.getString(5));
				int privId = rs.getInt(6);
				if(privId>2){
					privId = 3;
				}
				ue.setPrivId(privId);
				ue.setIstxt(rs.getInt(7));
				ue.setPwd(rs.getString(8));
				ue.setSalt(rs.getString(9));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ue;
	}
}
